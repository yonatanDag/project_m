package project.example.Model;

public class Client {
    private int idC;
    private String name;
    private City city;
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

    public void setIdC(int idC) {
        this.idC = idC;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
    
}
