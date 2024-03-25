package project.example.Model;

//import project.example.Controller.DB;

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

    // Calculates the fitness of a given schedule
    public double calculate(Schedule schedule, ArrayList<SpecializationTechnician> stList){
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
                fitness += calculatetaskFitness(curTask,technician, prevCity, stList);

                // adding the number of minutes that it takes to fix the Task
                minutesOfWork += curTask.getFault().getDuration();

                // adding the number of minutes of drive to the sumMinutesInDrive(wasted minutes)
                minutesInDrive += numOfDriving(prevCity, curTask);

                // update the previous City
                prevCity = curTask.getClient().getCity();
            }

            // execute the efficiency of the Scheduling Tasks of the current Technician(by execute the proportion between minutesOfWork and minutesInDrive) 
            fitness += (minutesOfWork/minutesInDrive)*3;

        }

        // You might also want to include some penalty or consideration for unscheduled tasks
        // For example, reducing the fitness score for each unscheduled task
        ArrayList<Task> unscheduledTasks = schedule.getUnscheduledTasks();
        fitness += calculateUnscheduledTask(unscheduledTasks); 

        return fitness;
    }

    private double calculatetaskFitness(Task task, Technician tech, City city, ArrayList<SpecializationTechnician> stList){
        double taskFitness = 0.0;

        // checks if the Technician is fitting to the Specialization of the task 
        if(isTechSpec()){
             return -10.0;
        }

        //checks if the Client and the Technician are from the same area
        if(!task.getClient().getCity().getCityArea().equals(technician.getCity().getCityArea())){
            return -5.0;
        }

        // checks if the Client and the Technician are from the same city
        if(task.getClient().getCity().equals(technician.getCity())){
            taskFitness+=0.7;
        }

        // check if the Client is premium
        if(task.getClient().isPremium()){
            taskFitness++;
        }

        // Calculate fitness based on urgency
        taskFitness += 1 - (double)task.getFault().getUrgencyLevel()/10;

        // Calculate fitness based on technician's specialization match
        // Assuming you have a way to check if a technician's specialization matches the task's requirements
        // if (task.getRequiredSpecialization().equals(technician.getSpecialization())) {
        //     taskFitness += 10; // Adjust value as needed
        // }

        // Calculate fitness based on technician's rating
        //taskFitness += technician.getRating();

        // Additional fitness calculations can be added here (like cost, time, etc.)

        return taskFitness;
    }

    private double calculateUnscheduledTask(ArrayList<Task> unscheduledTasks){
        double tasksFitness = 0.0;
        
        for(int i = 0; i < unscheduledTasks.size(); i++){
            Task curTask = unscheduledTasks.get(i);
            tasksFitness -= 0.5 * (11 - curTask.getFault().getUrgencyLevel());
            // checks if the Client in the current Task is premium
            if(curTask.getClient().isPremium()){
                tasksFitness -= 10;
            }
        }


        return tasksFitness;
    }

    // Additional helper methods as required
}
