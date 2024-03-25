package project.example.Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import project.example.Controller.DB;

public class MainGeneticAlgorithm {

    // ArrayList<Area> areaList;
    // ArrayList<City> cityList;
    // ArrayList<Specialization> specializationList;
    // ArrayList<Client> clientList;
    // ArrayList<Technician> technicianList;
    // ArrayList<SpecializationTechnician> specializationTechnicianList;
    // ArrayList<Fault> faultList;
    // //ArrayList<Task> taskList;
    // private DB db;

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //MainGeneticAlgorithm main = new MainGeneticAlgorithm();
        // Connect to the database
        DB db = new DB();
        db.connectSql();
        // Create GA object
        ScheduleGeneticAlgorithm sga = new ScheduleGeneticAlgorithm(15,db, 50, 0.03, 0.9, 2);
        // Initialize population
        Population population = sga.getPopulation();
        //population.printAllSchedules();

        population.getSchedule(0).printSchedule();
        //population.getSchedule(1).printSchedule();
        sga.mutation(population.getSchedule(0));
        //sga.mutation(population.getSchedule(1));
        System.out.println("now after mutation");
        population.getSchedule(0).printSchedule();
        //population.getSchedule(1).printSchedule();

        

        // Close the database connection
        db.disconnectSql();
    }

}
