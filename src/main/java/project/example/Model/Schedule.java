package project.example.Model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Schedule {

    // This represents the scheduled tasks for each technician.
    private Map<Technician, ArrayList<Task>> scheduling = new HashMap<>();

    // unscheduledFaults holds a list of tasks that have not been assigned to any technician yet.
    private ArrayList<Task> unscheduledTasks = new ArrayList<>();

    private double fitness = -1;

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

    public Schedule(Map<Technician, ArrayList<Task>> scheduling, ArrayList<Task> unscheduledTasks) {
        this.scheduling = scheduling;
        this.unscheduledTasks = unscheduledTasks;
    }

    public void assignTaskToTechnician(Technician technician, Task task) {
        scheduling.computeIfAbsent(technician, k -> new ArrayList<>()).add(task);
    }

    public ArrayList<Task> getTaskAssignedToTechnician(Technician technician) {
        return scheduling.getOrDefault(technician, new ArrayList<>());
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

    public void setScheduling(Map<Technician, ArrayList<Task>> scheduling) {
        this.scheduling = scheduling;
    }

    public ArrayList<Task> getUnscheduledTasks() {
        return unscheduledTasks;
    }

    public void setUnscheduledTask(ArrayList<Task> unscheduledTasks) {
        this.unscheduledTasks = unscheduledTasks;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    // functoin that add Task as is to the ArrayList of the Technician
    public void addTask(Task task){
        Technician tech = task.getAssignedTechnician();
        if(tech != null){
            this.scheduling.get(tech).add(task);
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

    // function that gets a Technician and checks if he is available to get more Task(if his last Scheduled Task is end before 18:00)
    public boolean isTechAvailable(Technician tech, Task newTask){
        // check if the technician has any scheduled tasks
        ArrayList<Task> tasks = scheduling.get(tech);
        if (tasks == null || tasks.isEmpty()) {
            return true;
        }
        // Get the last task in the technician's schedule
        Task lastTask = tasks.get(tasks.size() - 1);
        // calculate the end time of the last task
        LocalDateTime endTime = lastTask.getScheduledTime().plusMinutes(lastTask.getFault().getDuration() + newTask.getFault().getDuration());
        // checks if the 2 Tasks(the previous Task and the current Task) are in the same City
        if(lastTask.getClient().getCity().equals(newTask.getClient().getCity())){
            // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
            endTime = endTime.plusMinutes(15);
        }
        else{
            // checks if the 2 Tasks(the previous Task and the current Task) are in the same Area 
            if(lastTask.getClient().getCity().getCityArea().getAreaID() == newTask.getClient().getCity().getCityArea().getAreaID()){
                // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                endTime = endTime.plusMinutes(40); 
            }
            else{
                endTime = endTime.plusMinutes(90);   
            }
        }
        
        // define the end of the workday
        LocalTime workDayEnd = LocalTime.of(19, 30);
        // convert the end time of the last task to LocalTime for comparison
        LocalTime lastTaskEndTime = endTime.toLocalTime();
        // check if the last task ends before the end of the workday
        return lastTaskEndTime.isBefore(workDayEnd);
    }

    // Retrieves all tasks in this schedule, both scheduled and unscheduled, as copies.
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();

        // Iterate through each technician's scheduled tasks and add copies to the list
        for (ArrayList<Task> tasks : scheduling.values()) {
            for (Task task : tasks) {
                allTasks.add(new Task(task)); // Add a copy of each task
            }
        }

        // Add copies of all unscheduled tasks to the list
        for (Task task : unscheduledTasks) {
            allTasks.add(new Task(task)); // Add a copy of each task
        }

        return allTasks;
    }

    // function that get Technician and return his object in the current Schedule
    public Technician getTechnician(Technician tech) {
        for (Technician t : this.scheduling.keySet()) {
            if (t.getIdT() == tech.getIdT()) {
                return t; // Found the matching technician by ID
            }
        }
        return null; // Return null if no matching technician is found
    }
    

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
            if(prevTask.getClient().equals(task.getClient())){
                scheduledTime = prevTask.getScheduledTime().plus(duration);
            }
            else{
                // checks if the 2 Tasks(the previous Task and the current Task) are in the same City
                if(prevTask.getClient().getCity().equals(task.getClient().getCity())){
                    // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                    scheduledTime = prevTask.getScheduledTime().plus(duration).plusMinutes(15);
                }
                else{
                    // checks if the 2 Tasks(the previous Task and the current Task) are in the same Area 
                    if(prevTask.getClient().getCity().getCityArea().getAreaID() == task.getClient().getCity().getCityArea().getAreaID()){
                        // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                        scheduledTime = prevTask.getScheduledTime().plus(duration).plusMinutes(40); 
                    }
                    else{
                        scheduledTime = prevTask.getScheduledTime().plus(duration).plusMinutes(90);   
                    }
                }
            }
            task.setScheduledTime(scheduledTime);
        }
        assignTaskToTechnician(tech, task);
        task.setAssignedTechnician(tech);
    }

    public void removeScheduledTask(int index, Technician tech){
        ArrayList<Task> lst1 = this.getTaskAssignedToTechnician(tech);

        // set the TechnicianID and the ScheduledTime of the Task as null
        lst1.get(index).setAssignedTechnician(null);
        lst1.get(index).setScheduledTime(null);
        
        lst1.remove(lst1.get(index));

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
                if(prevTask.getClient().getCity().equals(curTask.getClient().getCity())){
                    // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                    scheduledTime = scheduledTime.plusMinutes(15);
                }
                else{
                    // checks if the 2 Tasks(the previous Task and the current Task) are in the same Area 
                    if(prevTask.getClient().getCity().getCityArea().getAreaID() == curTask.getClient().getCity().getCityArea().getAreaID()){
                        // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                        scheduledTime = scheduledTime.plusMinutes(40); 
                    }
                    else{
                        scheduledTime = scheduledTime.plusMinutes(90);   
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
                        scheduledTime = scheduledTime.plusMinutes(15);
                    }
                    else{
                        // checks if the 2 Tasks(the previous Task and the current Task) are in the same Area 
                        if(prevTask.getClient().getCity().getCityArea().getAreaID() == curTask.getClient().getCity().getCityArea().getAreaID()){
                            // define the scheduleTime as the Time of the previous ScheduledTime + the Duration that it takes to fix the Fault and 15 minutes of Driving
                            scheduledTime = scheduledTime.plusMinutes(40); 
                        }
                        else{
                            scheduledTime = scheduledTime.plusMinutes(90);   
                        }
                    }
                    curTask.setScheduledTime(scheduledTime);
                    durationInMinutes = curTask.getFault().getDuration();
                    scheduledTime = scheduledTime.plusMinutes(durationInMinutes);
                }
            }
        }
    }

    public void generateRandomSchedule(ArrayList<SpecializationTechnician> specTechList) {
        SpecializationService specService = new SpecializationService(specTechList);
        Random rand = new Random();
        int attempts = 0;

        // loop that run on all the unscheduledTasks
        while(!this.unscheduledTasks.isEmpty() && attempts < 150){
            // gets randomly a Task
            int index = rand.nextInt(this.unscheduledTasks.size());
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
                int index2 = rand.nextInt(suitableTechnicians.size());
                Technician curTechnician = suitableTechnicians.get(index2);
                // add the curTask 
                addScheduledTask(curTask, curTechnician);
                // remove from the unscheduledTasks the curTask
                unscheduledTasks.remove(index);
            }
            attempts++;
        }
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
                    System.out.println("\tTask ID: " + task.getIdT() + ", Scheduled Time: " + scheduledTimeStr + ", Client: " + task.getClient().getName() + ", Fault: " + task.getFault().getfDescription());
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

    // function to return a sorted list of all scheduled tasks
    public ArrayList<Task> getSortedScheduledTasks() {
        ArrayList<Task> sortedTasks = new ArrayList<>();

        // adding all tasks
        for (Map.Entry<Technician, ArrayList<Task>> entry : scheduling.entrySet()) {
            sortedTasks.addAll(entry.getValue());
        }

        // sort the Tasks by TechnicianID and then by ScheduledTime
        Collections.sort(sortedTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                // Handle potential null assigned technicians
                if (t1.getAssignedTechnician() == null && t2.getAssignedTechnician() == null) {
                    return 0; // Both tasks have no technician assigned, consider equal
                }
                if (t1.getAssignedTechnician() == null) {
                    return -1; // Consider t1 less than t2, to sort tasks with no technician to the beginning
                }
                if (t2.getAssignedTechnician() == null) {
                    return 1; // Consider t2 less than t1, to sort tasks with no technician to the beginning
                }
        
                // If both tasks have technicians assigned, compare by technician ID
                int techCompare = Integer.compare(t1.getAssignedTechnician().getIdT(), t2.getAssignedTechnician().getIdT());
                if (techCompare == 0) {
                    // If TechnicianIDs are equal, compare by ScheduledTime
                    // Additional null check for scheduled time may be necessary depending on your implementation
                    if (t1.getScheduledTime() == null && t2.getScheduledTime() == null) {
                        return 0; // Both tasks have no scheduled time, consider equal
                    }
                    if (t1.getScheduledTime() == null) {
                        return -1; // Consider t1 less than t2, sort tasks with no scheduled time to the beginning
                    }
                    if (t2.getScheduledTime() == null) {
                        return 1; // Consider t2 less than t1, sort tasks with no scheduled time to the beginning
                    }
        
                    return t1.getScheduledTime().compareTo(t2.getScheduledTime());
                }
                return techCompare;
            }
        });

        // Appending unscheduled tasks at the end
        sortedTasks.addAll(unscheduledTasks);

        return sortedTasks;
    }
    

}
