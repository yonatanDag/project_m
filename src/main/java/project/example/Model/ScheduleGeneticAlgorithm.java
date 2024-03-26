package project.example.Model;

import java.sql.SQLException;
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
    private SpecializationService specService;

    public ScheduleGeneticAlgorithm(int populationSize, DB db, int maxGenerations, double mutationRate, double crossoverRate, int eliteCount) throws ClassNotFoundException, SQLException {
        this.populationSize = populationSize;
        this.db = db;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.eliteCount = eliteCount;
        ScheduleGeneticAlgorithm.countGenerations = 0;
        this.specService = new SpecializationService(db.loadSpecializationTechnicians());
        this.population = generateRandomPopulation();
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

    // generate random population
    public Population generateRandomPopulation() throws ClassNotFoundException, SQLException {
        Population population = new Population(this.populationSize);
        for (int i = 0; i < this.populationSize; i++) {
            ArrayList<Task> unscheduledTasks = this.db.loadTasks();
            Schedule temp = new Schedule(unscheduledTasks, db.loadTechnicians());
            temp.generateRandomSchedule(db.loadSpecializationTechnicians());
            population.addSchedule(temp);
        }
        population.updateFitnessForAllSchedules(this.specService); // Update fitness for all schedules
        return population;
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
        while((this.specService.isTechsSuit(tech1, tech2) == false && tech2.getIdT() != tech1.getIdT()) && i < 20){
            rnd2 = (int)(Math.random() * techList.size());
            tech2 = techList.get(rnd2);
        }

        ArrayList<Task> lst2 = schedule.getTaskAssignedToTechnician(tech2);

        // the Task of the 1st Technician 
        rnd1 = (int)(Math.random() * lst1.size());
        Task task1 = lst1.get(rnd1);
        i = 0;
        // loop that make sure that tech2 is suit to fix task1
        while((this.specService.isTechSpec(tech2, task1.getRequiredSpecialization()) != true) && i < 20){
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
            while((this.specService.isTechSpec(tech1, task2.getRequiredSpecialization()) != true) && i < 20){
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

    public Schedule createRouletteWheel() {
        // sum of all the fitness of the schedules in the population
        double sum = this.population.sumFitness();
        // array of the proportions of the schedules in the population
        double proportions[] = new double[this.population.getPopulationSize()];
        // array of cumulative proportions
        double cumulativeProportions[] = new double[this.population.getPopulationSize()];
        double cumulativeTotal = 0; // total of the cumulative proportions
    
        for (int i = 0; i < this.population.getPopulationSize(); i++) {
            // calculate the proportion of the Schedule in the population (fitness / sum)
            proportions[i] = this.population.getSchedule(i).getFitness() / sum;
        }
        // sum all the proportions and add them to the cumulative proportions array
        for (int i = 0; i < this.population.getPopulationSize(); i++) {
            cumulativeTotal += proportions[i];
            // add the proportion to the cumulative total
            cumulativeProportions[i] = cumulativeTotal;
        }
        // generate a random number between 0 and the cumulative total
        double random = Math.random() * cumulativeTotal;
        // find the schedule that corresponds to the random number
        for (int i = 0; i < this.population.getPopulationSize(); i++) {
            // if the random number is less than the cumulative proportion of the schedule
            if (random < cumulativeProportions[i]) {
                return this.population.getSchedule(i);
            }
        }
        // In theory, the method should never reach this point because the random number should always be within the range
        // of the cumulative proportions. However, to satisfy the return statement, return the last schedule as a fallback.
        return this.population.getSchedule(this.population.getPopulationSize() - 1);
    }

    public Schedule crossover(Schedule parent1, Schedule parent2){
        ArrayList<Technician> techList = parent1.getTechnicians();
        Schedule offspring = new Schedule(techList);


        return offspring;
    }
    

}
