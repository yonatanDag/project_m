package project.example.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Population {

    private ArrayList<Schedule> schedules;
    private int populationSize;


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

    // function that get a Schedule an it remove it from the Population
    public boolean removeSchedule(Schedule schedule) {
        if(schedules.remove(schedule)){
            this.populationSize--;
            return true;
        }
        return false;
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

    public void updateFitnessForAllSchedules(SpecializationService specService) {
        FitnessCalculator fitnessCalculator = new FitnessCalculator();
        for (Schedule schedule : schedules) {
            double fitness = fitnessCalculator.calculate(schedule, specService);
            schedule.setFitness(fitness);
        }
    }
    

    // function that execute the sum of the fitness of all the Schedules in the Population 
    public double sumFitness(){
        double sum = 0;
        for (Schedule schedule : this.schedules) {
            sum += schedule.getFitness(); // Use the updated fitness value
        }
        return sum;
    }

    // function that return random Schedule from the Population
    public Schedule getRandomSchedule(){
        int randomIdx = (int) (Math.random() * this.schedules.size());
        return this.schedules.get(randomIdx);
    }

    // Get the fittest schedule in the population
    public Schedule getFittest(){
        Schedule fittest = schedules.get(0);
        double maxFitness = fittest.getFitness();
        for (int i = 1; i < populationSize; i++) {
        Schedule currentSchedule = schedules.get(i);    
        double currentFitness = currentSchedule.getFitness();
            if (currentFitness > maxFitness) {
                fittest = currentSchedule;
                maxFitness = currentFitness;
            }
        }
        return fittest;
    }

    // Function to sort the schedules based on fitness
    public void sortByFitness() {
        Collections.sort(this.schedules, new Comparator<Schedule>() {
            @Override
            public int compare(Schedule s1, Schedule s2) {
                // Use Double.compare because we're dealing with double values for fitness
                // We want to sort in descending order (higher fitness is better)
                return Double.compare(s2.getFitness(), s1.getFitness());
            }
        });
    }

    public void calculateAverageFitnessAndPrintHighest() {
        double totalFitness = 0;
        double highestFitness = Double.MIN_VALUE; // Initialize with the smallest possible value
    
        for (Schedule schedule : this.schedules) {
            double fitness = schedule.getFitness();
            totalFitness += fitness;
            if (fitness > highestFitness) {
                highestFitness = fitness; // Update the highest fitness found so far
            }
        }
    
        double averageFitness = totalFitness / this.schedules.size();
        System.out.println("Average Fitness: " + averageFitness);
        System.out.println("Highest Fitness: " + highestFitness);
    }
    
}
