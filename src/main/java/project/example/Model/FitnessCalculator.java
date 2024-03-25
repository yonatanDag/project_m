package project.example.Model;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

public class FitnessCalculator {

    private int numOfDriving(City city, Task task){
        if(city.equals(task.getClient().getCity())){
            return 15;
        }
        else{
            if(city.getCityArea().getAreaID() == task.getClient().getCity().getCityArea().getAreaID()){
                return 40;
            }
            else{
                return 90;
            }
        }
    }

    private int calculateTaskWaiting(Task task) {
        LocalDateTime now = LocalDateTime.now();

        // execute the number of Days that passed from the Time that the Task reoported
        int daysSinceReported = (int) java.time.temporal.ChronoUnit.DAYS.between(task.getReportedTime(), now);

        return daysSinceReported;
    }

    private double calculatetaskFitness(Task task, Technician tech, City city, SpecializationService specService){
        double taskFitness = 0.0;

        // checks if the Technician is fitting to the Specialization of the task 
        if(!specService.isTechSpec(tech, task.getRequiredSpecialization())){
             return -10.0;
        }

        //checks if the Client and the Technician are not from the same area
        if(task.getClient().getCity().getCityArea().getAreaID() != tech.getCity().getCityArea().getAreaID()){
            taskFitness += -2.0;
        }
        
        else{ // means that they are from the same City
            // checks if the Client and the Technician are from the same City
            if(task.getClient().getCity().equals(tech.getCity())){
                taskFitness += 1.5;
            }
            else{ // means that they are from the same Area but not from the same City
                taskFitness += 0.5;
            }
        }

        // check if the Client is premium
        if(task.getClient().isPremium()){
            taskFitness += 3;
        }

        // Calculate fitness based on urgency
        taskFitness += 0.3 * (11 - task.getFault().getUrgencyLevel());

        // if in the Specialization of the Task, the Technician is the best(from all the Specializations that he specialize)
        taskFitness += 0.5 * specService.level(tech, task.getRequiredSpecialization());

        // multiply the number of days that the Task waited with 0.1
        taskFitness += 0.1 * calculateTaskWaiting(task);

        return taskFitness;
    }

    private double calculateUnscheduledTask(ArrayList<Task> unscheduledTasks){
        double tasksFitness = 0.0;
        
        for(int i = 0; i < unscheduledTasks.size(); i++){
            Task curTask = unscheduledTasks.get(i);

            // Calculate fitness based on urgency
            tasksFitness -= 0.3 * (11 - curTask.getFault().getUrgencyLevel());
            
            // checks if the Client in the current Task is premium
            if(curTask.getClient().isPremium()){
                tasksFitness -= 10;
            }

            // multiply the number of days that the Task waited with 0.1
            tasksFitness -= 0.1 * calculateTaskWaiting(curTask);

        }
        return tasksFitness;
    }

    // Calculates the fitness of a given schedule
    public double calculate(Schedule schedule, SpecializationService specService){
        double fitness = 0.0;

        // Calculate fitness for all scheduled tasks
        Map<Technician, ArrayList<Task>> scheduledTasksMap = schedule.getScheduling();

        for (Entry<Technician, ArrayList<Task>> entry : scheduledTasksMap.entrySet()) {
            double minutesInDrive = 0.0, minutesOfWork = 0.0;
            Technician technician = entry.getKey();
            ArrayList<Task> tasks = entry.getValue();
            City prevCity = technician.getCity();

            for (int i = 0 ; i < tasks.size(); i++) {
                Task curTask = tasks.get(i);
                fitness += calculatetaskFitness(curTask,technician, prevCity, specService);

                // adding the number of minutes that it takes to fix the Task
                minutesOfWork += curTask.getFault().getDuration();

                // adding the number of minutes of drive to the sumMinutesInDrive(wasted minutes)
                minutesInDrive += numOfDriving(prevCity, curTask);

                // update the previous City
                prevCity = curTask.getClient().getCity();
            }

            // execute the efficiency of the Scheduling Tasks of the current Technician(by execute the proportion between minutesOfWork and minutesInDrive) 
            fitness += (minutesOfWork/minutesInDrive)*2;

        }

        ArrayList<Task> unscheduledTasks = schedule.getUnscheduledTasks();
        fitness += calculateUnscheduledTask(unscheduledTasks); 

        return fitness;
    }
}
