package project.example.Model;

public class Client {
    // the ID of the Client
    private int idC;
    // the name of the Client
    private String name;
    // the city that the Client live at
    private City city;
    // if the Client is premium
    private boolean premium;

    public Client(int idC, String name, City city, boolean premium) {
        this.idC = idC;
        this.name = name;
        this.city = city;
        this.premium = premium;
    }
 
    public int getIdC() {
        return idC;
    }

    public String getName() {
        return name;
    }

    public City getCity() {
        return city;
    }

    public boolean isPremium() {
        return premium;
    }
}
