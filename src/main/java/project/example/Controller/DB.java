package project.example.Controller;

import project.example.Model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList; // Import the ArrayList class
import java.util.Calendar;

public class DB {
    // the connection to the mySQL
    private Connection connection; 

    public Connection getConnection() {
        return connection;
    }

    public DB() {
        this.connectSql(); // connect the database
    }

    // connect to the mysql database
    public void connectSql() {
        String url = "jdbc:mysql://localhost:3306/project?serverTimezone=Asia/Jerusalem";
        String username = "root";
        String password = "yonatan7";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, username, password);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    // disconnect the sql
    public void disconnectSql() {
        try {
            // Check if the connection is not null
            if (this.connection != null) {
                this.connection.close(); // Close the connection
            }
        } catch (SQLException e) {
            System.out.println("Error closing the database connection: " + e.getMessage());
        }
    }

    public ArrayList<Area> loadAreas() throws SQLException, ClassNotFoundException {
        ArrayList<Area> areaList = new ArrayList<>();
        try (Statement statement = this.connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM project.area")) {

            while (resultSet.next()) {
                Area area = new Area(resultSet.getInt(1), resultSet.getString(2));
                //System.out.println(resultSet.getInt(1) + "  " + resultSet.getString(2));
                areaList.add(area);
            }
        }
        return areaList;
    }

    public Area loadAreaById(int id) throws SQLException, ClassNotFoundException {
        try (Statement statement = this.connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM project.area WHERE areaID=" + id)) {
            if (resultSet.next()) {
                return new Area(resultSet.getInt(1), resultSet.getString(2));
            }
        }
        return null;
    }

    public ArrayList<City> loadCities() throws SQLException, ClassNotFoundException {
        ArrayList<City> cityList = new ArrayList<>();
        try (Statement statement = this.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM project.city")) {

            //ArrayList<Area> areaList = loadAreas();

            while (resultSet.next()) {
                City city = new City(resultSet.getInt(1), resultSet.getString(2), loadAreaById(resultSet.getInt(3)));
                cityList.add(city);
            }
        }
        return cityList;
    }

    public ArrayList<Specialization> loadSpecializations() throws SQLException, ClassNotFoundException {
        ArrayList<Specialization> specializationList = new ArrayList<>();
        try (Statement statement = this.connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM project.specialization")) {
    
            while (resultSet.next()) {
                Specialization specialization = new Specialization(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
                specializationList.add(specialization);
            }
        }
        return specializationList;
    }

    public ArrayList<Client> loadClients()throws SQLException, ClassNotFoundException {
        ArrayList<Client> clientsList = new ArrayList<>();
        ArrayList<City> cities = loadCities();
        try (Statement personStatement = this.connection.createStatement();
             ResultSet personSet = personStatement.executeQuery("SELECT * FROM project.person");
             Statement clientStatement = this.connection.createStatement();
             ResultSet clientSet = clientStatement.executeQuery("SELECT * FROM project.client")) {
    
                while(clientSet.next()){
                    boolean isFound = false;
                    while(!isFound && personSet.next()){
                        if (personSet.getInt(1) == clientSet.getInt(1)) {
                            boolean isPremium = clientSet.getInt(2) == 1;
                            Client c = new Client(personSet.getInt(1), personSet.getString(2) + " " + personSet.getString(3), cities.get(personSet.getInt(5)-1), isPremium);
                            clientsList.add(c); 
                            isFound = true;
                        }
                    }
                    personSet.beforeFirst(); // Reset personSet cursor
                }
        }
        return clientsList;
    }

    public ArrayList<Technician> loadTechnicians()throws SQLException, ClassNotFoundException {
        ArrayList<Technician> techniciansList = new ArrayList<>();
        ArrayList<City> cities = loadCities();
        try (Statement personStatement = this.connection.createStatement();
             ResultSet personSet = personStatement.executeQuery("SELECT * FROM project.person");
             Statement technicianStatement = this.connection.createStatement();
             ResultSet technicianSet = technicianStatement.executeQuery("SELECT * FROM project.technician")) {
    
                while(technicianSet.next()){
                    boolean isFound = false;
                    while(!isFound && personSet.next()){
                        if(personSet.getInt(1) == technicianSet.getInt(1)){
                            Technician t = new Technician(personSet.getInt(1), personSet.getString(2) + " " + personSet.getString(3), cities.get(personSet.getInt(5)-1), technicianSet.getDouble(2));
                            techniciansList.add(t); 
                            isFound = true;
                        }
                    }
                    personSet.beforeFirst(); // Reset personSet cursor
                }
        }
        return techniciansList;
    }

    public ArrayList<SpecializationTechnician> loadSpecializationTechnicians() throws SQLException, ClassNotFoundException {
        ArrayList<SpecializationTechnician> scList = new ArrayList<>();
        ArrayList<Specialization> specializationList = loadSpecializations();
        ArrayList<Technician> technicianList = loadTechnicians();
        try (Statement specTechStatement = this.connection.createStatement();
            ResultSet specTechSet = specTechStatement.executeQuery("select * from project.technicians_specialization")) {
            
            while (specTechSet.next()) {
                int techIndex = -1;
                int specIndex = -1;

                // Loop to find the technician index
                for (int i = 0; (i < technicianList.size()) && (techIndex == -1); i++) {
                    if (specTechSet.getInt(1) == technicianList.get(i).getIdT()) {
                        techIndex = i;
                    }
                }

                // Loop to find the specialization index
                for (int j = 0; (j < specializationList.size()) && (specIndex == -1); j++) {
                    if (specTechSet.getInt(2) == specializationList.get(j).getIdS()) {
                        specIndex = j;
                    }
                }

                if (techIndex != -1 && specIndex != -1) {
                    SpecializationTechnician st = new SpecializationTechnician(technicianList.get(techIndex), specializationList.get(specIndex), specTechSet.getDouble(3), Calendar.getInstance().get(Calendar.YEAR)-specTechSet.getInt(4));
                    scList.add(st);
                }
            }
        }
        return scList;
    }

    public ArrayList<Fault> loadFaults() throws SQLException, ClassNotFoundException {
        ArrayList<Fault> faultList = new ArrayList<>();
        ArrayList<Specialization> specializationList = loadSpecializations();
        try (Statement faultStatement = this.connection.createStatement();
            ResultSet faultSet = faultStatement.executeQuery("select * from project.fault")){
            
            while(faultSet.next()){
                Fault f = new Fault(faultSet.getInt(1), specializationList.get(faultSet.getInt(2)-1), faultSet.getString(3), faultSet.getInt(4), faultSet.getInt(5));
                faultList.add(f); 
           }
        }
        return faultList;
    }

    public ArrayList<Task> loadTasks() throws SQLException, ClassNotFoundException {
        ArrayList<Task> unScheduledTasksList = new ArrayList<>();
        ArrayList<Client> clientList = loadClients(); 
        ArrayList<Fault> faultList = loadFaults(); 
       
        try (Statement taskStatement = this.connection.createStatement();
             ResultSet taskSet = taskStatement.executeQuery("SELECT * FROM project.unscheduletasks;")) {
    
            while (taskSet.next()) {
                int indexClient = 0;
                for (int i = 0; i < clientList.size() && indexClient == 0; i++) {
                    if (taskSet.getInt(2) == clientList.get(i).getIdC())
                        indexClient = i;
                }
                Task t = new Task(taskSet.getInt(1), clientList.get(indexClient), faultList.get(taskSet.getInt(3) - 1),taskSet.getTimestamp(5).toLocalDateTime());
                unScheduledTasksList.add(t);
            }
        }
        return unScheduledTasksList;
    }

    public boolean addTask(int clientId, int faultId) throws SQLException {
        // SQL query to insert a new task into the database
        String insertSQL = "INSERT INTO task (clientID, faultID, description, reportedTime) VALUES (?, ?, NULL, ?);";

        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            // Set the clientID and faultID
            pstmt.setInt(1, clientId);
            pstmt.setInt(2, faultId);
            // Set the reportedTime to the current time
            pstmt.setTimestamp(3, Timestamp.valueOf(now));

            // Execute the update
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding the task: " + e.getMessage());
            // Handle exceptions appropriately
            throw e;
        }
    }

    public int getFaultIdByDescription(String description) throws SQLException {
        String query = "SELECT faultID FROM fault WHERE description = ?";
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
            preparedStatement.setString(1, description);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("faultID");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions
        }
        return -1; // Return -1 if not found or an error occurs
    }

    public ArrayList<Task> getTasksForClientScheduledTomorrow(int clientId) throws ClassNotFoundException {
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT t.taskID, t.description, st.technicianID, st.scheduledTime, st.rating, c.clientID, p.firstName, p.lastName, p.cityID, c.isPremium, " +
                       "f.faultID, f.specializationID, f.description AS faultDescription, f.duration, f.urgencyLevel, " +
                       "tech.visitPrice, tp.firstName AS techFirstName, tp.lastName AS techLastName, tp.cityID AS techCityID " +
                       "FROM task t " +
                       "JOIN scheduled_task st ON t.taskID = st.taskID " +
                       "JOIN client c ON t.clientID = c.clientID " +
                       "JOIN person p ON c.clientID = p.personID " +
                       "JOIN fault f ON t.faultID = f.faultID " +
                       "JOIN technician tech ON st.technicianID = tech.technicianID " +
                       "JOIN person tp ON tech.technicianID = tp.personID " +
                       "WHERE t.clientID = ? AND DATE(st.scheduledTime) = DATE(NOW() + INTERVAL 1 DAY);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                City city = loadCityById(rs.getInt("techCityID"));
                Specialization spec = loadSpecializationById(rs.getInt("specializationID"));
                Client client = new Client(rs.getInt("clientID"), rs.getString("firstName") + " " + rs.getString("lastName"), city, rs.getBoolean("isPremium"));
                Fault fault = new Fault(rs.getInt("faultID"), spec, rs.getString("faultDescription"), rs.getInt("duration"), rs.getInt("urgencyLevel"));
                String techName = rs.getString("techFirstName") + " " + rs.getString("techLastName");
                Technician technician = new Technician(rs.getInt("technicianID"), techName, city, rs.getDouble("visitPrice"));
                Task task = new Task(rs.getInt("taskID"), client, fault, rs.getTimestamp("scheduledTime").toLocalDateTime());
                task.setAssignedTechnician(technician);
                task.setScheduledTime(rs.getTimestamp("scheduledTime").toLocalDateTime());
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return tasks;
    }
    

    public City loadCityById(int cityId) throws SQLException, ClassNotFoundException {
        City city = null;
        String query = "SELECT cityID, cityName, areaID FROM city WHERE cityID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, cityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int areaId = rs.getInt("areaID");
                ArrayList<Area> lst = loadAreas();  // Assuming you have a method to load Area
                Area area = lst.get(areaId-1);
                city = new City(rs.getInt("cityID"), rs.getString("cityName"), area);
            }
        } catch (SQLException e) {
            System.err.println("SQL error when fetching city: " + e.getMessage());
            throw e;
        }
        return city;
    }

    public Specialization loadSpecializationById(int specializationId) throws SQLException {
        Specialization specialization = null;
        String query = "SELECT specializationID, specializationName, descriptionS FROM specialization WHERE specializationID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, specializationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                specialization = new Specialization(
                    rs.getInt("specializationID"),
                    rs.getString("specializationName"),
                    rs.getString("descriptionS")
                );
            }
        } catch (SQLException e) {
            System.err.println("SQL error when fetching specialization: " + e.getMessage());
            throw e;
        }
        return specialization;
    }

    public ArrayList<Task> getTasksForTechnicianScheduledTomorrow(int technicianId) throws ClassNotFoundException {
        ArrayList<Task> tasks = new ArrayList<>();
        String query = "SELECT t.taskID, t.description, st.scheduledTime, st.rating, c.clientID, p.firstName, p.lastName, p.cityID, c.isPremium, " +
                       "f.faultID, f.specializationID, f.description AS faultDescription, f.duration, f.urgencyLevel " +
                       "FROM scheduled_task st " +
                       "JOIN task t ON st.taskID = t.taskID " +
                       "JOIN client c ON t.clientID = c.clientID " +
                       "JOIN person p ON c.clientID = p.personID " +
                       "JOIN fault f ON t.faultID = f.faultID " +
                       "WHERE st.technicianID = ? AND DATE(st.scheduledTime) = DATE(NOW() + INTERVAL 1 DAY);";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, technicianId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                City city = loadCityById(rs.getInt("cityID"));
                Specialization spec = loadSpecializationById(rs.getInt("specializationID"));
                Client client = new Client(rs.getInt("clientID"), rs.getString("firstName") + " " + rs.getString("lastName"), city, rs.getBoolean("isPremium"));
                Fault fault = new Fault(rs.getInt("faultID"), spec, rs.getString("faultDescription"), rs.getInt("duration"), rs.getInt("urgencyLevel"));
                Task task = new Task(rs.getInt("taskID"), client, fault, rs.getTimestamp("scheduledTime").toLocalDateTime());
                task.setScheduledTime(rs.getTimestamp("scheduledTime").toLocalDateTime());
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        }
        return tasks;
    }

    public static void main(String[] args) {
        try {
            DB db = new DB();

            // Load each list using DB methods
            ArrayList<Area> areaList = db.loadAreas();
            ArrayList<City> cityList = db.loadCities();
            ArrayList<Specialization> specializationList = db.loadSpecializations();
            ArrayList<Client> clientList = db.loadClients();
            ArrayList<Technician> technicianList = db.loadTechnicians();
            ArrayList<SpecializationTechnician> specializationTechnicianList = db.loadSpecializationTechnicians();
            ArrayList<Fault> faultList = db.loadFaults();
            ArrayList<Task> taskList = db.loadTasks();

            // Now print each list
            System.out.println("Areas:");
            for (Area area : areaList) {
                System.out.println("ID: " + area.getAreaID() + ", Name: " + area.getAreaName());
            }

            System.out.println("\nCities:");
            for (City city : cityList) {
                System.out.println("ID: " + city.getCityID() + ", Name: " + city.getCityName() + ", Area ID: " + city.getCityArea().getAreaID());
            }

            System.out.println("\nSpecializations:");
            for (Specialization specialization : specializationList) {
                System.out.println("ID: " + specialization.getIdS() + ", Name: " + specialization.getNameS() + ", Description: " + specialization.getDescription());
            }

            System.out.println("\nClients:");
            for (Client client : clientList) {
                System.out.println("ID: " + client.getIdC() + ", Name: " + client.getName() + ", City: " + client.getCity().getCityName() + ", Premium: " + client.isPremium());
            }

            System.out.println("\nTechnicians:");
            for (Technician technician : technicianList) {
                System.out.println("ID: " + technician.getIdT() + ", Name: " + technician.getName() + ", City: " + technician.getCity().getCityName() + ", Visit Price: " + technician.getVisitPrice());
            }

            System.out.println("\nSpecializationTechnicians:");
            for (SpecializationTechnician st : specializationTechnicianList) {
                System.out.println("Technician ID: " + st.getTech().getIdT() + ", Specialization ID: " + st.getType().getIdS() + ", Rating: " + st.getRating() + ", Seniority: " + st.getSeniority());
            }

            System.out.println("\nFaults:");
            for (Fault fault : faultList) {
                System.out.println("ID: " + fault.getfID() + ", Specialization ID: " + fault.getCfSpecialization().getIdS() + ", Description: " + fault.getfDescription() + ", Duration: " + fault.getDuration() + ", Urgency Level: " + fault.getUrgencyLevel());
            }

            System.out.println("\nTasks:");
            for (Task task : taskList) {
                System.out.println("ID: " + task.getIdT() + ", Client ID: " + task.getClient().getIdC() + ", Specialization ID: " + task.getRequiredSpecialization().getIdS() + ", Fault ID: " + task.getFault().getfID() + ", Reported Time: " + task.getReportedTime());
            }
            db.disconnectSql();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
   