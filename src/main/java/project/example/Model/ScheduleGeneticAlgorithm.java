package project.example.Model;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import project.example.Controller.DB;

public class ScheduleGeneticAlgorithm {
    private int populationSize;
    private DB db;
    private int maxGenerations;
    private double mutationRate;
    private double crossoverRate;
    private int eliteCount;
    public static int countGenerations;
    private Population population;
    private ArrayList<SpecializationTechnician> stList;

    public ScheduleGeneticAlgorithm(int populationSize, DB db, int maxGenerations, double mutationRate, double crossoverRate, int eliteCount) throws ClassNotFoundException, SQLException {
        this.populationSize = populationSize;
        this.db = db;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.eliteCount = eliteCount;
        ScheduleGeneticAlgorithm.countGenerations = 0;
        this.population = generateRandomPopulation();
        this.stList = db.loadSpecializationTechnicians();
    }

    public int getPopulationSize() {
        return populationSize;
    }


    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }

    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public int getEliteCount() {
        return eliteCount;
    }

    public void setEliteCount(int eliteCount) {
        this.eliteCount = eliteCount;
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    // helper function that find the start index of a technician's specializations in the sorted list
    private int findFirstIndex(Technician tech) {
        int low = 0;
        int high = stList.size() - 1;
    
        while (low <= high) {
            int mid = (low + high) / 2;
            SpecializationTechnician midVal = stList.get(mid);
        
            if (midVal.getTech().getIdT() < tech.getIdT()) {
                low = mid + 1;
            }

            else{ 
                if (midVal.getTech().getIdT() > tech.getIdT()) {
                    high = mid - 1;
                } 
                else {
                    // Found the technician, now move backwards to find the start index of their specializations
                    while (mid > low && stList.get(mid - 1).getTech().getIdT() == tech.getIdT()) {
                        mid--;
                    }
                    return mid;
                }
            }
        }
        return -1;
    }

    // fucntion that gets Technician and Specialization and return true if the Technician is specialize in the Specialization that gets
    public boolean isTechSpec(Technician tech, Specialization spec) {
        int startIndex = findFirstIndex(tech);
        
        // check if Technician not found
        if (startIndex == -1) {
            return false;
        }
    
        // iterate through the technician's specializations starting from the startIndex
        while (startIndex < stList.size() && stList.get(startIndex).getTech().getIdT() == tech.getIdT()) {
            SpecializationTechnician currentSpecTech = stList.get(startIndex);
            if (currentSpecTech.getType().getIdS() == spec.getIdS()) {
                // found the matching specialization
                return true;
            }
            startIndex++;
        }
    
        // no matching specialization found
        return false;
    }

    // fucntion that gets 2 Technicians and return true if both specialize in the Specialization
    public boolean isTechsSuit(Technician tech1, Technician tech2){
        // find the start index of each technician's specializations
        int startIndexTech1 = findFirstIndex(tech1);
        int startIndexTech2 = findFirstIndex(tech2);
    
        if (startIndexTech1 == -1 || startIndexTech2 == -1) {
            return false;
        }
    
        // compare the specializations of both technicians from the start indexes found
        while (startIndexTech1 < stList.size() && stList.get(startIndexTech1).getTech().getIdT() == tech1.getIdT() &&
               startIndexTech2 < stList.size() && stList.get(startIndexTech2).getTech().getIdT() == tech2.getIdT()) {
                
            int specId1 = stList.get(startIndexTech1).getType().getIdS();
            int specId2 = stList.get(startIndexTech2).getType().getIdS();
        
            if (specId1 == specId2) {
                // both technicians share at least one specialization
                return true;
            } 
            else{ 
                if (specId1 < specId2) {
                    startIndexTech1++;
                }   
                else {
                    startIndexTech2++;
                }
            }
        }
        // No common specializations were found
        return false;
    }

    // generate random population
    public Population generateRandomPopulation() throws ClassNotFoundException, SQLException {
        Population population = new Population(this.populationSize);
        for (int i = 0; i < this.populationSize; i++) {
            ArrayList<Task> unscheduledTasks = this.db.loadTasks();
            Schedule temp = new Schedule(unscheduledTasks, db.loadTechnicians());
            temp.generateRandomSchedule(db.loadSpecializationTechnicians());
            population.addSchedule(temp);
        }
        return population;
    }

    // function that calculate the duration in LocalDateTime format from minutes
    public LocalDateTime subtractMinutesFromScheduledTime(LocalDateTime scheduledTime, int minutes) {
        return scheduledTime.minusMinutes(minutes);
    }
    

    // get 1 Schedule and make random change in it
    public void mutation(Schedule schedule){
        ArrayList<Technician> techList = new ArrayList<>();
        Map<Technician, ArrayList<Task>> map = schedule.getScheduling();

        // add all technicians from the map's keys to the techList
        techList.addAll(map.keySet());

        // the 1st Technician 
        int rnd1 = (int)(Math.random() * techList.size());
        // the 2nd Technician 
        int rnd2 = (int)(Math.random() * techList.size());

        // objects of the 2 random Technicians
        Technician tech1 = techList.get(rnd1);
        Technician tech2 = techList.get(rnd2);

        ArrayList<Task> lst1 = schedule.getTaskAssignedToTechnician(tech1);

        // counter for every loop
        int i = 0;

        // loop that make sure that tech1 have Scheduled Tasks
        while(lst1.size() == 0 && i < 20){
            rnd1 = (int)(Math.random() * techList.size());
            tech1 = techList.get(rnd1);
            lst1 = schedule.getTaskAssignedToTechnician(tech1);
            i++;
        }

        i = 0;
        // loop that make sure that tech1 and tech2 are specialize in at least 1 same Specialization and they are the same Technician
        while((isTechsSuit(tech1, tech2) == false && tech2.getIdT() != tech1.getIdT()) && i < 20){
            rnd2 = (int)(Math.random() * techList.size());
            tech2 = techList.get(rnd2);
        }

        ArrayList<Task> lst2 = schedule.getTaskAssignedToTechnician(tech2);

        // the Task of the 1st Technician 
        rnd1 = (int)(Math.random() * lst1.size());
        Task task1 = lst1.get(rnd1);
        i = 0;
        // loop that make sure that tech2 is suit to fix task1
        while((isTechSpec(tech2, task1.getRequiredSpecialization()) != true) && i < 20){
            rnd1 = (int)(Math.random() * lst1.size());
            task1 = lst1.get(rnd1);
        }
        
        // case that lst2 has no Tasks
        if(lst2.size() == 0){
            // set the new Schedule of the Task
            task1.setAssignedTechnician(tech2);
            LocalTime workStart = LocalTime.of(9, 0);
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            LocalDateTime scheduledTime = LocalDateTime.of(tomorrow, workStart);
            task1.setScheduledTime(scheduledTime);
            // add the new Task to tech2 ArrayList
            schedule.assignTaskToTechnician(tech2, task1);

            // update the Time for the other Tasks
            schedule.removeScheduledTask(rnd1, tech1);
            
        }
        // case that lst2 has also Tasks
        else{
            // the Task of the 2nd Technician 
            rnd2 = (int)(Math.random() * lst2.size());
            Task task2 = lst2.get(rnd2);

            i = 0;
            // loop that make sure that tech1 is suit to fix task2
            while((isTechSpec(tech1, task2.getRequiredSpecialization()) != true) && i < 20){
                rnd2 = (int)(Math.random() * lst2.size());
                task2 = lst2.get(rnd2);
            }
            
            // now we are removing the 2 Tasks, update the ScheduledTime of all of the other ScheduledTasks and then adding the 2 Tasks
            schedule.removeScheduledTask(rnd1, tech1);
            schedule.removeScheduledTask(rnd2, tech2);  
            schedule.addScheduledTask(task1, tech2);
            schedule.addScheduledTask(task2, tech1);
            
        }
    }

}
