package project.example.Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import project.example.Controller.DB;

public class ScheduleGeneticAlgorithm {
    // the Population size of the Genetic Algorithm(the number of Schedules in each Population)
    private int populationSize;
    // the DB of the project
    private DB db;
    // the maximum number of generations
    private int maxGenerations;
    // the mutation Rate
    private double mutationRate;
    public static int countGenerations;
    // the Population
    private Population population;
    private SpecializationService specService;

    public ScheduleGeneticAlgorithm(int populationSize, DB db, int maxGenerations, double mutationRate) throws ClassNotFoundException, SQLException {
        this.populationSize = populationSize;
        this.db = db;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        ScheduleGeneticAlgorithm.countGenerations = 0;
        this.specService = new SpecializationService(db.loadSpecializationTechnicians());
        this.population = generateRandomPopulation();
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getMaxGenerations() {
        return maxGenerations;
    }

    public double getMutationRate() {
        return mutationRate;
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
        ArrayList<SpecializationTechnician> stList = db.loadSpecializationTechnicians();
        for (int i = 0; i < this.populationSize; i++) {
            ArrayList<Task> unscheduledTasks = this.db.loadTasks();
            Schedule temp = new Schedule(unscheduledTasks, db.loadTechnicians());
            temp.generateRandomSchedule(stList);
            population.addSchedule(temp);
        }
        population.updateFitnessForAllSchedules(this.specService); // Update fitness for all schedules
        return population;
    }

    // get 1 Schedule and make random change in it
    public void mutation(Schedule schedule){
        ArrayList<Technician> techList = new ArrayList<>(schedule.getScheduling().keySet());
        int rnd1, rnd2;

        if (techList.size() < 2) return; // Need at least two technicians for mutation
    
        Random random = new Random();
        Technician tech1 = techList.get(random.nextInt(techList.size()));
        ArrayList<Task> tasksTech1 = new ArrayList<>(schedule.getTaskAssignedToTechnician(tech1));
        
        // loop that make sure that tech1 have Scheduled Tasks
        while (tasksTech1.isEmpty()){
            rnd1 = (int)(Math.random() * techList.size());
            tech1 = techList.get(rnd1);
            tasksTech1 = new ArrayList<>(schedule.getTaskAssignedToTechnician(tech1));
        } 
    
        Technician tech2;
        rnd2 = (int)(Math.random() * techList.size());
        tech2 = techList.get(rnd2); 

        // Randomly select a task from tech1
        rnd1 = (int)(Math.random() * tasksTech1.size());
        Task task1 = tasksTech1.get(rnd1);
        
        int attempts = 0;
        
        // loop that make sure that tech2 is able and avaible to fix task1 and they not are the same Technician
        while((((this.specService.isTechSpec(tech2, task1.getRequiredSpecialization()) != true) || (schedule.isTechAvailable(tech2, task1) != true)) || (tech2.getIdT() == tech1.getIdT())) && (attempts < 30)){
            rnd2 = (int)(Math.random() * techList.size());
            tech2 = techList.get(rnd2);
            attempts++;
        }
        if (attempts < 30) {
            return;
        }

        ArrayList<Task> lst2 = schedule.getTaskAssignedToTechnician(tech2);
        
        // case that lst2 has no Tasks
        if(lst2.size() == 0){
            // remove the Task from the tech1 ArrayList and adding it to tech2
            schedule.removeScheduledTask(rnd1, tech1);
            schedule.addScheduledTask(task1, tech2);
        }
        // case that lst2 has also Tasks
        else{
            // the Task of the 2nd Technician 
            rnd2 = (int)(Math.random() * lst2.size());
            Task task2 = lst2.get(rnd2);

            attempts = 0;

            // loop that make sure that tech1 is suit to fix task2
            while(((this.specService.isTechSpec(tech1, task2.getRequiredSpecialization()) != true) || (schedule.isTechAvailable(tech1, task2) != true)) && (attempts < 30)){
                rnd2 = (int)(Math.random() * lst2.size());
                task2 = lst2.get(rnd2);
                attempts++;
            }
            // in case there is a task of Technician2 that Technician1 can fix
            if (attempts < 30) {
                // now we are removing the 2 Tasks, update the ScheduledTime of all of the other
                // ScheduledTasks and then adding the 2 Tasks
                schedule.removeScheduledTask(rnd1, tech1);
                schedule.removeScheduledTask(rnd2, tech2);
                schedule.addScheduledTask(task1, tech2);
                schedule.addScheduledTask(task2, tech1);
            }
            // in case there is no task of Technician2 that Technician1 can fix
            else{
                // remove the Task from the tech1 ArrayList and adding it to tech2
                schedule.removeScheduledTask(rnd1, tech1);
                schedule.addScheduledTask(task1, tech2);
            }
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
                Schedule schedule = this.population.getSchedule(i);
                this.population.removeSchedule(schedule);
                return schedule;
            }
        }
        // In theory, the method should never reach this point because the random number should always be within the range
        // of the cumulative proportions. However, to satisfy the return statement, return the last schedule as a fallback.
        Schedule schedule = this.population.getSchedule(this.population.getPopulationSize() - 1);
        this.population.removeSchedule(schedule);
        return schedule;
    }

    
    public Schedule crossover(Schedule parent1, Schedule parent2){
        Random rand = new Random();
        int i = 0;
        ArrayList<Technician> techList = parent1.getTechnicians();
        Schedule offspring = new Schedule(techList);

        // determine the random crossover point
        int crossoverPoint = (int) (Math.random() * parent1.numOfTasks());
        
        // Set of all the Task IDs that have been assigned to the new Schedule
        Set<Integer> assignedTaskIds = new HashSet<>();

        // get a sorted ArrayList of all the Tasks of parent1 and parent2
        ArrayList<Task> parent1Tasks = parent1.getSortedScheduledTasks(this.specService);
        ArrayList<Task> parent2Tasks = parent2.getSortedScheduledTasks(this.specService);

        // Copy the ScheduledTasks from parent1 up to the crossover point
        for (; i < crossoverPoint; i++) {
            Task curTask = new Task(parent1Tasks.get(i));
            offspring.addTask(curTask);
            assignedTaskIds.add(curTask.getIdT());
        }

        // Copy all the remaining ScheduledTasks from parent2 
        for(i = 0; i < parent2Tasks.size(); i++){
            Task curTask = new Task(parent2Tasks.get(i));

            // check if the curTask is alraedy in offspring
            if(!assignedTaskIds.contains(curTask.getIdT())){
                Technician tech = curTask.getAssignedTechnician();
                // check if the Technician is still available
                if(offspring.isTechAvailable(tech, curTask)){
                    offspring.addScheduledTask(curTask, tech);
                    assignedTaskIds.add(curTask.getIdT());
                }
                // in case that the Technician is unavailable
                else{
                    ArrayList<Technician> suitableTechnicians = new ArrayList<>();

                    // Find suitable technicians for the task
                    for (Technician t : offspring.getScheduling().keySet()) {
                        if ((specService.isTechSpec(t, curTask.getRequiredSpecialization())) && (offspring.isTechAvailable(tech, curTask))) {
                            suitableTechnicians.add(t);
                        }
                    }
                    // in case of there are suitable Technicians
                    if(!suitableTechnicians.isEmpty()){
                        // gets randomly a Technician that is suitable for the task
                        int index = rand.nextInt(suitableTechnicians.size());
                        Technician curTechnician = suitableTechnicians.get(index);
                        // add the curTask 
                        offspring.addScheduledTask(curTask, curTechnician);
                        assignedTaskIds.add(curTask.getIdT());
                    }
                    // in case that there are 0 suitable Technicians
                    else{
                        curTask.setAssignedTechnician(null);
                        curTask.setScheduledTime(null);
                        offspring.addUnscheduledTask(curTask);
                    }
                }
            }
        }
        return offspring;
    }
    
    // function that generate the next generation
    public void generateNextPopulation(){
        // Sort the current population based on fitness
        this.population.sortByFitness();

        // Initialize the next generation
        Population nextGen = new Population(this.populationSize);

        // Directly select the top 10% of Schedules to the next generation
        int eliteSize = (int) (this.populationSize * 0.1);
        for (int i = 0; i < eliteSize; i++) {
            nextGen.addSchedule(this.population.getSchedule(0));
            this.population.removeSchedule(this.population.getSchedule(0));
        }

        // Select 25% of the next generation through roulette wheel selection
        int rouletteSelectionSize = (int) (this.populationSize * 0.25);
        for (int i = 0; i < rouletteSelectionSize; i++) {
            Schedule selected = createRouletteWheel();
            nextGen.addSchedule(selected);
        }

        // Fill the remaining 65% of the next generation with offspring from crossover and mutation
        while(nextGen.getSchedules().size() < this.populationSize) {
            // Select parents
            Schedule parent1 = nextGen.getRandomSchedule();
            Schedule parent2 = nextGen.getRandomSchedule();

            // Perform crossover and mutation
            Schedule newSchedule = crossover(parent1, parent2);
            if (Math.random() < mutationRate) {
                mutation(newSchedule);
            }
            // Add the new offspring to the next generation
            nextGen.addSchedule(newSchedule);
        }

        // Update the fitness of the Scheduled in the nexGen
        nextGen.updateFitnessForAllSchedules(this.specService);

        // Update generation count
        ScheduleGeneticAlgorithm.countGenerations++;

        this.setPopulation(nextGen);
    }

    public void evolutionCycle(){
        for(int i = 1; i < this.getMaxGenerations(); i++){
            System.out.println("Generation number: " + i);
            System.out.println("the Population size: " + this.population.getSchedules().size());
            this.population.calculateAverageFitnessAndPrintHighest();
            this.generateNextPopulation();
        }

        System.out.println("Generation number: " + this.getMaxGenerations());
        System.out.println("the Population size: " + this.population.getSchedules().size());

        // print the best Schedule
        Schedule best = this.population.getFittest();
        System.out.println();
        System.out.println("the fitness is: " + best.getFitness());
        best.printSchedule();
    }

}
