package project.example.Model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    public ArrayList<Technician> getTechnicians() {
        // Return a new ArrayList containing all technicians from the scheduling map
        return new ArrayList<>(scheduling.keySet());
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
    

    public void addScheduledTask(Task task, Technician tech){
        LocalDateTime scheduledTime;

        // gets the ArrayList of the Scheduling Task of the tech 
        ArrayList<Task> tasksTemp = this.scheduling.get(tech);
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
    }

    public void removeScheduledTask(int index, Technician tech){
        ArrayList<Task> lst1 = this.getTaskAssignedToTechnician(tech);
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
        Random rand = new Random();
        int i = 0;

        // loop that run on all the unscheduledTasks
        while(!this.unscheduledTasks.isEmpty() && i < 100){
            // gets randomly a Task
            int index = rand.nextInt(this.unscheduledTasks.size());
            Task curTask = this.unscheduledTasks.get(index);

            ArrayList<Technician> suitableTechnicians = new ArrayList<>();
            
            // Find suitable technicians for the task
            for (Technician tech : scheduling.keySet()) {
                if (tech.isTechSuit(specTechList, curTask.getRequiredSpecialization())) {
                    suitableTechnicians.add(tech);
                }
            }
            
            // gets randomly a Technician that is suitable for the task
            int index2 = rand.nextInt(suitableTechnicians.size());
            Technician curTechnician = suitableTechnicians.get(index2);
            // add the curTask 
            addScheduledTask(curTask, curTechnician);
            // remove from the unscheduledTasks the curTask
            unscheduledTasks.remove(index);
            i++;
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
    

}
