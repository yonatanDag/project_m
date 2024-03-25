package project.example.Model;


import java.util.ArrayList;

import project.example.Controller.DB;


public class Population {

    private ArrayList<Schedule> schedules;
    private int populationSize;
    private double popFitness = -1;


    public Population(int populationSize) {
        this.populationSize = populationSize;
        this.schedules = new ArrayList<Schedule>(populationSize); // Initialize the schedules list
    }
    
    // Constructor to initialize the population with a given size
    public Population(int populationSize, ArrayList<Technician> techList) {
        this.populationSize = populationSize;
        this.schedules = new ArrayList<Schedule>(populationSize); // Initialize the schedules list
        for (int i = 0; i < populationSize; i++) {
            this.schedules.add(new Schedule(techList)); // Add a new Schedule object to the list
        }
    }

    // Get a schedule from the population
    public Schedule getSchedule(int index) {
        return schedules.get(index);
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }
    

    // Get the size of the population
    public int size() {
        return schedules.size();
    }

    // Getters and setters
    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void printAllSchedules() {
        // Iterate over each Schedule in the population
        for (int i = 0; i < schedules.size(); i++) {
            System.out.println("Schedule #" + (i + 1));
    
            // Retrieve the current Schedule
            Schedule currentSchedule = schedules.get(i);
            currentSchedule.printSchedule();
        }
    }

    // // Get the fittest schedule in the population
    // public Schedule getFittest() throws ClassNotFoundException, SQLException{
    //     FitnessCalculator fitnessCalculator = new FitnessCalculator();
    //     Schedule fittest = schedules.get(0);
    //     double maxFitness = fitnessCalculator.calculate(fittest);

    //     for (int i = 1; i < populationSize; i++) {
    //     Schedule currentSchedule = schedules.get(i);    
    //     double currentFitness = fitnessCalculator.calculate(currentSchedule);
    //         if (currentFitness > maxFitness) {
    //             fittest = currentSchedule;
    //             maxFitness = currentFitness;
    //         }
    //     }
    //     return fittest;
    // }

    // public Schedule selectIndividual() throws ClassNotFoundException, SQLException{
    //     FitnessCalculator fitnessCalculator = new FitnessCalculator();
    //     double totalFitness = 0;
    //     for(Schedule schedule : schedules){
    //         totalFitness+= fitnessCalculator.calculate(schedule);
    //     }

    //     // Generate a random number between 0 and totalFitness
    //     double randNum = rand.nextDouble() * totalFitness;

    //     // Select individual based on random number
    //     double runningSum = 0.0;
    //     for (Schedule schedule : schedules) {
    //         runningSum += new FitnessCalculator().calculate(schedule);
    //         if (runningSum >= randNum) {
    //             return schedule;
    //         }
    //     }

    //     // Fallback in case of rounding errors, should not happen
    //     return schedules.get(0);
    // }

    // // Simplified crossover function
    // public Schedule crossover(Schedule parent1, Schedule parent2) {
    //     // Initialize child schedule with an empty map and list
    //     Schedule child = new Schedule(new HashMap<>(), new ArrayList<>());

    //     // Decide on a crossover point, which in this case could be a random number of technicians
    //     int crossoverPoint = rand.nextInt(parent1.getScheduling().size());

    //     List<Technician> technicians = new ArrayList<>(parent1.getScheduling().keySet());
    //     for (int i = 0; i < technicians.size(); i++) {
    //         Technician tech = technicians.get(i);
    //         if (i < crossoverPoint) {
    //             // Copy tasks from parent1 to child for technicians before the crossover point
    //             child.getScheduling().put(tech, new ArrayList<>(parent1.getTaskAssignedToTechnician(tech)));
    //         } else {
    //             // Copy tasks from parent2 to child for technicians after the crossover point, if they exist in parent2
    //             if (parent2.getScheduling().containsKey(tech)) {
    //                 child.getScheduling().put(tech, new ArrayList<>(parent2.getTaskAssignedToTechnician(tech)));
    //             }
    //         }
    //     }

    //     // This is a simplified logic; you might need to handle unscheduled tasks and ensure no task is lost or duplicated
    //     // Also, ensure the child's schedule adheres to all constraints (e.g., technician availability, task prerequisites)

    //     return child;
    // }

    //  // Method to generate a new population
    // public void generate() throws ClassNotFoundException, SQLException {
    //     ArrayList<Schedule> newGeneration = new ArrayList<>(populationSize);
    //     Random rand = new Random();

    //     while (newGeneration.size() < populationSize) {
    //         // Selection
    //         Schedule parent1 = selectIndividual();
    //         Schedule parent2 = selectIndividual();

    //         // Crossover
    //         Schedule child1 = crossover(parent1, parent2);
    //         Schedule child2 = crossover(parent1, parent2);

    //         // Assuming a simple crossover based on tasks; more sophisticated methods may be needed
    //         // Here you need to define how to combine schedules
            
    //         // Mutation - with a low probability, mutate a child
    //         if (rand.nextDouble() < 0.05) { // Assuming a 5% mutation rate
    //             // Implement mutation
    //             // This could involve swapping tasks, adding/removing tasks, etc.
    //         }

    //         newGeneration.add(child1);
    //         if (newGeneration.size() < populationSize) {
    //             newGeneration.add(child2);
    //         }
    //     }

    //     // Replace the old generation with the new one
    //     this.schedules = newGeneration;
    // }

}
