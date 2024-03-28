package project.example.Model;

import java.sql.SQLException;

import project.example.Controller.DB;

public class MainGeneticAlgorithm {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //MainGeneticAlgorithm main = new MainGeneticAlgorithm();
        // Connect to the database
        DB db = new DB();
        db.connectSql();


        // Create GA object/ Genearte Population
        ScheduleGeneticAlgorithm sga = new ScheduleGeneticAlgorithm(30,db, 500, 0.05);
        // Initialize population
        Population population = sga.getPopulation();
        int i;
        
        for(i = 1; i < sga.getMaxGenerations(); i++){
            System.out.println("Generation number: " + i);
            System.out.println("the Population size: " + population.getSchedules().size());
            population.calculateAverageFitnessAndPrintHighest();
            sga.generateNextPopulation();
            population = sga.getPopulation();
        }

        System.out.println("Generation number: " + i);
        System.out.println("the Population size: " + population.getSchedules().size());
        
        // print the best Schedule
        Schedule best = population.getFittest();
        System.out.println();
        System.out.println("the fitness is: " + best.getFitness());
        best.printSchedule();
        
        // // check the sortByFitness
        // population.sortByFitness();
        // // Iterate over each Schedule in the population
        // for (int i = 0; i < population.getPopulationSize(); i++) {
        //     // Retrieve the current Schedule
        //     Schedule currentSchedule = population.getSchedule(i);
        //     // Print the fitness of the current Schedule
        //     System.out.println("Schedule #" + (i + 1) + " Fitness: " + currentSchedule.getFitness());
        // }

        // Schedule sch1 = population.getSchedule(0);
        // Schedule sch2 = population.getSchedule(1);

        // System.out.println("1:");
        // sch1.printSchedule();
        // System.out.println();
        // System.out.println();
        // System.out.println();
        // System.out.println("2:");
        // sch2.printSchedule();
        // System.out.println();
        // System.out.println();
        // System.out.println();

        // // crossover function
        // System.out.println("After crossover:");
        // Schedule sch3 = sga.crossover(sch1, sch2);
        // sch3.printSchedule();
        // FitnessCalculator fitnessCalculator = new FitnessCalculator();
        // double fitness = fitnessCalculator.calculate(sch3, sga.getSpecService());
        // sch3.setFitness(fitness);
        // System.out.println("1 fitness: " + sch1.getFitness());
        // System.out.println("2 fitness: " + sch2.getFitness());
        // System.out.println("3 fitness: " + sch3.getFitness());


        // // check getSortedScheduledTasks function
        // Schedule sch = population.getRandomSchedule();
        // ArrayList<Task> tasksLst = sch.getSortedScheduledTasks();
        // // Printing each Task in the sorted list ", Technician ID: " + task.getAssignedTechnician().getId() +
        // for (Task task : tasksLst) {
        //     Integer technicianId = task.getAssignedTechnician() != null ? task.getAssignedTechnician().getIdT() : null; // Use null or a placeholder value if no technician assigned
        //     String scheduledTimeStr = task.getScheduledTime() != null ? task.getScheduledTime().toString() : "Not Scheduled";
        //     System.out.println("Task ID: " + task.getIdT() + 
        //                ", Technician ID: " + (technicianId != null ? technicianId : "No Technician Assigned") +
        //                ", Scheduled Time: " + scheduledTimeStr + 
        //                ", Client: " + task.getClient().getName() + ", Fault: " + task.getFault().getfDescription());
        // }

        // // check the mutation function

        // population.getSchedule(0).printSchedule();
        // //population.getSchedule(1).printSchedule();
        // sga.mutation(population.getSchedule(0));
        // //sga.mutation(population.getSchedule(1));
        // System.out.println();
        // System.out.println();
        // System.out.println("now after mutation");
        // System.out.println();
        // System.out.println();
        // population.getSchedule(0).printSchedule();
        //population.getSchedule(1).printSchedule();

        // check the fitnessCalculator

        // int i = 1;
        // for (Schedule schedule : population.getSchedules()) {
        //     System.out.println("Schedule " + i++ + " Fitness: " + schedule.getFitness());
        // }

        // Schedule best = population.getFittest();
        // best.printSchedule();

        // // check getTechniciansForSpecialization function
        // Specialization spec = db.loadSpecializations().get(0);
        // SpecializationService sps = new SpecializationService(db.loadSpecializationTechnicians());
        // ArrayList<Technician> lst = sps.getTechniciansForSpecialization(spec);
        // for (Technician technician : lst) {
        //     System.out.println(technician);
        // }
        

        // Close the database connection
        db.disconnectSql();
    }

}
