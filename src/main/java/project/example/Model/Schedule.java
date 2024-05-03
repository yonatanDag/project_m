package project.example.Model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Schedule {

    // This represents the scheduled tasks for each technician.
    private Map<Technician, ArrayList<Task>> scheduling = new HashMap<>();
    // unscheduledFaults holds a list of tasks that have not been assigned to any technician yet.
    private ArrayList<Task> unscheduledTasks = new ArrayList<>();
    // the fitness value of the Schedule
    private double fitness = -1;

    private static int sameCityTime = 15;
    private static int sameAreaTime = 40;
    private static int difAreaTime = 90;
    private static int maxAttempts = 400;

    public Schedule(ArrayList<Technician> techList) {
        this.scheduling = new HashMap<>();
        for (Technician technician : techList) {
            this.scheduling.put(technician, new ArrayList<Task>());
        }
        this.unscheduledTasks = new ArrayList<>();
    }

    public Schedule(ArrayList<Task> unscheduledTasks, ArrayList<Technician> techList) {
        this.scheduling = new HashMap<>();
        for (Technician technician : techList) {
            this.scheduling.put(technician, new ArrayList<Task>());
        }
        this.unscheduledTasks = unscheduledTasks;
    }

    public void assignTaskToTechnician(Technician technician, Task task) {
        scheduling.computeIfAbsent(technician, k -> new ArrayList<>()).add(task);
    }

    public ArrayList<Task> getTaskAssignedToTechnician(Technician technician) {
        Technician tech = getTechnician(technician);
        return scheduling.getOrDefault(tech, new ArrayList<>());
    }

    public void addUnscheduledTask(Task task) {
        unscheduledTasks.add(task);
    }

    public boolean removeUnscheduledTask(Task task) {
        return unscheduledTasks.remove(task);
    }

    public Map<Technician, ArrayList<Task>> getScheduling() {
        return scheduling;
    }

    public ArrayList<Task> getUnscheduledTasks() {
        return unscheduledTasks;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    // functoin that add Task as is to the ArrayList of the Technician
    public void addTask(Task task) {
        Technician tech = task.getAssignedTechnician();
        if (tech != null) {
            // Check if the list for the technician exists
            ArrayList<Task> tasks = this.scheduling.get(tech);
            if (tasks == null) {
                // If not, initialize it
                tasks = new ArrayList<>();
                this.scheduling.put(tech, tasks);
            }
            // Add the task to the list
            tasks.add(task);
        }
    }
    

    public ArrayList<Technician> getTechnicians() {
        // Return a new ArrayList containing all technicians from the scheduling map
        return new ArrayList<>(scheduling.keySet());
    }

    // function that returns the number of Tasks in the Schedule(include the unScheduledTasks)
    public int numOfTasks(){
        int count = 0;
        // count all scheduled tasks for each technician
        for (ArrayList<Task> tasks : scheduling.values()) {
            count += tasks.size();
        }
        // add the count of all unscheduled tasks
        count += unscheduledTasks.size();
        return count;
    }

    // function that gets a Technician and checks if he is available to get more Task
    public boolean isTechAvailable(Technician tech, Task newTask) {
        // check if the technician has any scheduled tasks
        ArrayList<Task> tasks = scheduling.get(tech);
        if (tasks == null || tasks.isEmpty()) {
            return true;
        }
        // Get the last task in the technician's schedule
        Task lastTask = tasks.get(tasks.size() - 1);
        // Calculate the end time of the last task
        LocalDateTime lastTaskEndTime = lastTask.getScheduledTime().plusMinutes(lastTask.getFault().getDuration());

        // Assume travel time between tasks within the same city is 15 minutes, 40 minutes within the same area but different cities, and 90 minutes between different areas.
        long travelTime = 0;
        if (lastTask.getClient().getCity().getCityID() != newTask.getClient().getCity().getCityID()) {
            if (lastTask.getClient().getCity().getCityArea().getAreaID() == newTask.getClient().getCity().getCityArea().getAreaID()) {
                travelTime = sameAreaTime;
            } else {
                travelTime = difAreaTime;
            }
        } else {
            travelTime = sameCityTime;
        }

        // calculate the prospective start time for the new task, considering travel time
        LocalDateTime newTaskStartTime = lastTaskEndTime.plusMinutes(travelTime);

        // check if the new task, including its duration, would end before the workday ends at 19:30
        LocalDateTime workDayEnd = LocalDateTime.of(newTaskStartTime.toLocalDate(), LocalTime.of(19, 30));
        LocalDateTime newTaskEndTime = newTaskStartTime.plusMinutes(newTask.getFault().getDuration());

        return newTaskEndTime.isBefore(workDayEnd) || newTaskEndTime.equals(workDayEnd);
    }

    // function that get Technician and return his object in the current Schedule
    public Technician getTechnician(Technician tech) {
        if (tech == null) {
            return null;
        }
        
        for (Technician t : this.scheduling.keySet()) {
            if (t.getIdT() == tech.getIdT()) {
                return t; // Found the matching technician by ID
            }
        }
        return null; // Return null if no matching technician is found
    }

    // function that get TechnicianID and return his object in the current Schedule
    public Technician getTechnicianByID(int techID) {
        for (Technician t : this.scheduling.keySet()) {
            if (t.getIdT() == techID) {
                return t; // Found the matching technician by ID
            }
        }
        return null; // Return null if no matching technician is found
    }
    
    // function that Adds a task to the scheduling list of a given technician. 
    public void addScheduledTask(Task task, Technician technician){
        LocalDateTime scheduledTime;
        Technician tech = getTechnician(technician);
        if(tech == null){
            return;
        }

        // Get the list of tasks for the technician, or initialize it if not present
        ArrayList<Task> tasksTemp = this.scheduling.getOrDefault(tech, new ArrayList<Task>());

        // checks if the the tech have ScheduledTask
        if(tasksTemp.isEmpty()){
            task.setAssignedTechnician(tech);
            // Workday starts at 9:00
            LocalTime workStart = LocalTime.of(9, 0);
            // Use LocalDate to get tomorrow's date
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            scheduledTime = LocalDateTime.of(tomorrow, workStart);
            task.setScheduledTime(scheduledTime);
        }
        else{
            // Get the last task
            Task prevTask = tasksTemp.get(tasksTemp.size() - 1);

            int durationInMinutes = prevTask.getFault().getDuration();
            // Create a Duration object representing the total duration in minutes
            Duration duration = Duration.ofMinutes(durationInMinutes);

            // checks if the 2 Tasks are for the same Client
            if(prevTask.getClient().getIdC() == task.getClient().getIdC()){
                scheduledTime = prevTask.getScheduledTime().plus(duration);
            }
            else{
                // checks if the 2 Tasks(the previous Task and the current Task) are in the same City
                if(prevTask.getClient().getCity().getCityID() == task.getClient().getCity().getCityID()){
                    // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                    scheduledTime = prevTask.getScheduledTime().plus(duration).plusMinutes(sameCityTime);
                }
                else{
                    // checks if the 2 Tasks(the previous Task and the current Task) are in the same Area 
                    if(prevTask.getClient().getCity().getCityArea().getAreaID() == task.getClient().getCity().getCityArea().getAreaID()){
                        // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                        scheduledTime = prevTask.getScheduledTime().plus(duration).plusMinutes(sameAreaTime); 
                    }
                    else{
                        scheduledTime = prevTask.getScheduledTime().plus(duration).plusMinutes(difAreaTime);   
                    }
                }
            }
            task.setScheduledTime(scheduledTime);
        }
        assignTaskToTechnician(tech, task);
        task.setAssignedTechnician(tech);
    }

    // function that removes a task to the scheduling list of a given technician. 
    public void removeScheduledTask(int index, Technician tech){
        ArrayList<Task> lst1 = this.getTaskAssignedToTechnician(tech);

        if(lst1 != null && !lst1.isEmpty() && index >= 0 && index < lst1.size()){
            // set the TechnicianID and the ScheduledTime of the Task as null
            lst1.get(index).setAssignedTechnician(null);
            lst1.get(index).setScheduledTime(null);
        
            lst1.remove(lst1.get(index));
        } else {
            // Handle the scenario where the task cannot be removed because the index is invalid
            //System.out.println("Attempted to remove a task at an invalid index or from an empty list.");
        }

        LocalDateTime scheduledTime;
        
        // update the Time for the other Tasks

        // if the Task is the 1st Task and there are more Tasks
        if(index == 0 && !lst1.isEmpty()){
            Task prevTask = lst1.get(index);
            LocalTime workStart = LocalTime.of(9, 0);
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            scheduledTime = LocalDateTime.of(tomorrow, workStart);
            prevTask.setScheduledTime(scheduledTime);
            int durationInMinutes = prevTask.getFault().getDuration();
            scheduledTime = scheduledTime.plusMinutes(durationInMinutes);
            for (int idx = index + 1; idx < lst1.size(); idx++) {
                Task curTask = lst1.get(idx);

                // checks if the 2 Tasks(the previous Task and the current Task) are in the same City
                if(prevTask.getClient().getCity().getCityID() == curTask.getClient().getCity().getCityID()){
                    // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                    scheduledTime = scheduledTime.plusMinutes(sameCityTime);
                }
                else{
                    // checks if the 2 Tasks(the previous Task and the current Task) are in the same Area 
                    if(prevTask.getClient().getCity().getCityArea().getAreaID() == curTask.getClient().getCity().getCityArea().getAreaID()){
                        // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                        scheduledTime = scheduledTime.plusMinutes(sameAreaTime); 
                    }
                    else{
                        scheduledTime = scheduledTime.plusMinutes(difAreaTime);   
                    }
                }
                curTask.setScheduledTime(scheduledTime);
                durationInMinutes = curTask.getFault().getDuration();
                scheduledTime = scheduledTime.plusMinutes(durationInMinutes);
            }
        }
        // Otherwise
        else{
            if(!lst1.isEmpty()){
                Task prevTask = lst1.get(index - 1);
                scheduledTime = prevTask.getScheduledTime();
                int durationInMinutes = prevTask.getFault().getDuration();
                scheduledTime = scheduledTime.plusMinutes(durationInMinutes);
                for (int idx = index; idx < lst1.size(); idx++) {
                    Task curTask = lst1.get(idx);

                    // checks if the 2 Tasks(the previous Task and the current Task) are in the same City
                    if(prevTask.getClient().getCity().getCityID() == curTask.getClient().getCity().getCityID()){
                        // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                        scheduledTime = scheduledTime.plusMinutes(sameCityTime);
                    }
                    else{
                        // checks if the 2 Tasks(the previous Task and the current Task) are in the same Area 
                        if(prevTask.getClient().getCity().getCityArea().getAreaID() == curTask.getClient().getCity().getCityArea().getAreaID()){
                            // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                            scheduledTime = scheduledTime.plusMinutes(sameAreaTime); 
                        }
                        else{
                            scheduledTime = scheduledTime.plusMinutes(difAreaTime);   
                        }
                    }
                    curTask.setScheduledTime(scheduledTime);
                    durationInMinutes = curTask.getFault().getDuration();
                    scheduledTime = scheduledTime.plusMinutes(durationInMinutes);
                }
            }
        }
    }

    // function that generates a random schedule by attempting to assign tasks from a list of unscheduled tasks to suitable technicians.
    public void generateRandomSchedule(ArrayList<SpecializationTechnician> specTechList) {
        SpecializationService specService = new SpecializationService(specTechList);
        int attempts = 0;

        // loop that run on all the unscheduledTasks
        while(!this.unscheduledTasks.isEmpty() && attempts < maxAttempts){
            // gets randomly a Task
            int index = (int) (Math.random() * this.unscheduledTasks.size());
            Task curTask = this.unscheduledTasks.get(index);

            ArrayList<Technician> suitableTechnicians = new ArrayList<>();
            
            // Find suitable technicians for the task
            for (Technician tech : scheduling.keySet()) {
                if (specService.isTechSpec(tech, curTask.getRequiredSpecialization()) && isTechAvailable(tech, curTask)) {
                    suitableTechnicians.add(tech);
                }
            }
            
            // in case of there are suitable Technicians
            if(!suitableTechnicians.isEmpty()){
                // gets randomly a Technician that is suitable for the task
                int index2 = (int) (Math.random() * suitableTechnicians.size());
                Technician curTechnician = suitableTechnicians.get(index2);
                // add the curTask 
                addScheduledTask(curTask, curTechnician);
                // remove from the unscheduledTasks the curTask
                unscheduledTasks.remove(index);
            }
            attempts++;
        }
    }

    // function that retrieves all tasks assigned to a specific client across all technicians, both scheduled and unscheduled.
    public ArrayList<Task> getTasksForClient(int clientId) {
        ArrayList<Task> tasksForClient = new ArrayList<>();

        // Check scheduled tasks for each technician
        for (ArrayList<Task> tasks : scheduling.values()) {
            for (Task task : tasks) {
                if (task.getClient().getIdC() == clientId) {
                    tasksForClient.add(task);
                }
            }
        }

        // Check unscheduled tasks
        for (Task task : unscheduledTasks) {
            if (task.getClient().getIdC() == clientId) {
                tasksForClient.add(task);
            }
        }

        return tasksForClient;
    }

    public void printSchedule() {
        System.out.println("Schedule Overview:");
        for (Map.Entry<Technician, ArrayList<Task>> entry : scheduling.entrySet()) {
            Technician technician = entry.getKey();
            ArrayList<Task> tasks = entry.getValue();
    
            System.out.println("Technician: " + technician.getName() + " (" + technician.getCity().getCityName() + ")");
            if (tasks.isEmpty()) {
                System.out.println("\tNo tasks assigned.");
            } else {
                for (Task task : tasks) {
                    LocalDateTime scheduledTime = task.getScheduledTime();
                    String scheduledTimeStr = scheduledTime != null ? scheduledTime.toString() : "Not scheduled";
                    System.out.println("\tTask ID: " + task.getIdT() + ", Scheduled Time: " + scheduledTimeStr + ", Fault: " + task.getFault().getfDescription() + ", Fault Duration: " + task.getFault().getDuration() + ", FSpecialization: " + task.getFault().getCfSpecialization().getIdS());
                }
            }
        }
    
        if (!unscheduledTasks.isEmpty()) {
            System.out.println("Unscheduled Tasks:");
            for (Task task : unscheduledTasks) {
                System.out.println("\tTask ID: " + task.getIdT() + ", Client: " + task.getClient().getName() + ", Fault: " + task.getFault().getfDescription());
            }
        } else {
            System.out.println("No unscheduled tasks.");
        }
    }

        // Method to get sorted tasks based on the cumulative fitness of tasks for each technician
        public ArrayList<Task> getSortedScheduledTasks(SpecializationService specService) {
            // Create a map to store the total fitness for tasks of each technician
            Map<Technician, Double> technicianFitnessMap = new HashMap<>();
    
            FitnessCalculator fitnessCalculator = new FitnessCalculator();
    
            // Populate the map with total fitness for each technician
            for (Map.Entry<Technician, ArrayList<Task>> entry : scheduling.entrySet()) {
                Technician tech = entry.getKey();
                double totalFitness = 0.0;
                for (Task task : entry.getValue()) {
                    // Assuming calculatetaskFitness method calculates fitness for a given task
                    totalFitness += fitnessCalculator.calculatetaskFitness(task, tech, tech.getCity(), specService);
                }
                technicianFitnessMap.put(tech, totalFitness);
            }
    
            // convert map entries to a list and sort it based on the fitness value
            ArrayList<Map.Entry<Technician, Double>> sortedTechnicians = new ArrayList<>(technicianFitnessMap.entrySet());
            sortedTechnicians.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    
            // Create a new list to hold tasks sorted based on technician fitness
            ArrayList<Task> sortedTasks = new ArrayList<>();
    
            // Add tasks to the list based on the sorted order of technicians
            for (Map.Entry<Technician, Double> entry : sortedTechnicians) {
                ArrayList<Task> tasksForTech = scheduling.get(entry.getKey());
                // Optionally sort tasks within the same technician if needed
                // tasksForTech.sort(comparator);
                sortedTasks.addAll(tasksForTech);
            }
    
            // Add unscheduled tasks at the end
            sortedTasks.addAll(unscheduledTasks);
    
            return sortedTasks;
        }
}
