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
        ScheduleGeneticAlgorithm sga = new ScheduleGeneticAlgorithm(15,db, 50, 0.03, 0.9, 2);
        // Initialize population
        Population population = sga.getPopulation();
        //population.printAllSchedules();

        // check the mutation function

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
        

        // Close the database connection
        db.disconnectSql();
    }

}
