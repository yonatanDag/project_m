package project.example.Model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class FitnessCalculator {

    private static int sameCityTime = 15;
    private static int sameAreaTime = 40;
    private static int difAreaTime = 90;
    private static double notSuit = -10;
    private static double sameCity = 2.5;
    private static double sameArea = 1.5;
    private static double difArea = -2;
    private static double premiumScore = 1;
    private static double urgencyWeight = 0.1;
    private static double zero = 0;
    private static double techLevelWeight = 0.1;
    private static double waitingWeight = 0.05;

    // Method that calculate the number of minutes it will take to come from the City to the Task
    private int numOfDriving(City city, Task task){
        int time = difAreaTime;
        if(city.getCityID() == task.getClient().getCity().getCityID()){
            time =  sameCityTime;
        }
        else{
            if(city.getCityArea().getAreaID() == task.getClient().getCity().getCityArea().getAreaID()){
                time =  sameAreaTime;
            }
        }
        return time;
    }

    // Method that calculate the number of days from the ReportedTime of the Task
    private int calculateTaskWaiting(Task task) {
        LocalDateTime now = LocalDateTime.now();

        // execute the number of Days that passed from the Time that the Task reoported
        int daysSinceReported = (int) java.time.temporal.ChronoUnit.DAYS.between(task.getReportedTime(), now);

        return daysSinceReported;
    }

    // Method that calculate the fitness of the Task
    public double calculatetaskFitness(Task task, Technician tech, City city, SpecializationService specService){
        double taskFitness = zero;

        // checks if the Technician is fitting to the Specialization of the task 
        if(!specService.isTechSpec(tech, task.getRequiredSpecialization())){
             return notSuit;
        }

        // Create a LocalDateTime for the deadline time on the same day as the task
        LocalDateTime deadline = LocalDateTime.of(task.getScheduledTime().toLocalDate(), LocalTime.of(19, 30));

        // check if the Task (including its duration) finishes before 19:30
        if (task.getScheduledTime().plusMinutes(task.getFault().getDuration()).isAfter(deadline)) {
            return notSuit;
        }

        //checks if the Client and the Technician are not from the same area
        if(task.getClient().getCity().getCityArea().getAreaID() != city.getCityArea().getAreaID()){
            taskFitness += difArea;
        }
        else{ // means that they are from the same City
            // checks if the Client and the Technician are from the same City
            if(task.getClient().getCity().getCityID() == city.getCityID()){
                taskFitness += sameCity;
            }
            else{ // means that they are from the same Area but not from the same City
                taskFitness += sameArea;
            }
        }

        // check if the Client is premium
        if(task.getClient().isPremium()){
            taskFitness += premiumScore;
        }

        // Calculate fitness based on urgency
        taskFitness += urgencyWeight * (11 - task.getFault().getUrgencyLevel());

        // if in the Specialization of the Task, the Technician is the best(from all the Specializations that he specialize)
        taskFitness += techLevelWeight * specService.level(tech, task.getRequiredSpecialization());

        // multiply the number of days that the Task waited with 0.05
        taskFitness += waitingWeight * calculateTaskWaiting(task);

        return taskFitness;
    }

    private double calculateUnscheduledTask(ArrayList<Task> unscheduledTasks){
        double tasksFitness = zero;
        
        for(int i = 0; i < unscheduledTasks.size(); i++){
            Task curTask = unscheduledTasks.get(i);

            // Calculate fitness based on urgency
            tasksFitness -= urgencyWeight * (11 - curTask.getFault().getUrgencyLevel());
            
            // checks if the Client in the current Task is premium
            if(curTask.getClient().isPremium()){
                tasksFitness -= premiumScore;
            }

            // multiply the number of days that the Task waited with 0.05
            tasksFitness -= waitingWeight * calculateTaskWaiting(curTask);

        }
        return tasksFitness;
    }

    // Calculates the fitness of a given schedule
    public double calculate(Schedule schedule, SpecializationService specService){
        double fitness = zero;

        // Calculate fitness for all scheduled tasks
        Map<Technician, ArrayList<Task>> scheduledTasksMap = schedule.getScheduling();

        for (Entry<Technician, ArrayList<Task>> entry : scheduledTasksMap.entrySet()) {
            double minutesInDrive = zero, minutesOfWork = zero;
            Technician technician = entry.getKey();
            ArrayList<Task> tasks = entry.getValue();
            City prevCity = technician.getCity();
            Task prevTask = null;

            for (int i = 0 ; i < tasks.size(); i++) {
                Task curTask = tasks.get(i);
                fitness += calculatetaskFitness(curTask,technician, prevCity, specService);

                // checks if the Client in the pervious Task is the Client in the current Task
                if((prevTask != null) && (prevTask.getClient().getIdC() == curTask.getClient().getIdC())){
                    fitness++;
                }

                // adding the number of minutes that it takes to fix the Task
                minutesOfWork += curTask.getFault().getDuration();

                // adding the number of minutes of drive to the sumMinutesInDrive(wasted minutes)
                minutesInDrive += numOfDriving(prevCity, curTask);

                // update the previous City
                prevCity = curTask.getClient().getCity();

                // update the previous Task
                prevTask = curTask;
            }

            // execute the efficiency of the Scheduling Tasks of the current Technician(by execute the proportion between minutesOfWork and minutesInDrive) 
            if(minutesInDrive != 0){
                fitness += (minutesOfWork/minutesInDrive)*2;
            }

        }

        ArrayList<Task> unscheduledTasks = schedule.getUnscheduledTasks();
        fitness += calculateUnscheduledTask(unscheduledTasks); 

        return fitness;
    }
}
