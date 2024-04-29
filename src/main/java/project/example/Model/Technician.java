    package project.example.Model;

// Technician can has more than 1 Specialization
public class Technician {
    // the ID of the Technician
    private int idT;
    // the name of the Technician
    private String name;
    // the City that the Technician is from
    private City city;
    // the price that he takes for visit
    private double visitPrice;

    public Technician(int idT, String name, City city, double visitPrice) {
        this.idT = idT;
        this.name = name;
        this.city = city;
        this.visitPrice = visitPrice;
    }

    public int getIdT() {
        return idT;
    }

    public String getName() {
        return name;
    }

    public City getCity() {
        return city;
    }

    public double getVisitPrice() {
        return visitPrice;
    }

    @Override
    public String toString() {
        return name + " (" + city.getCityName() + ")"; 
    }

}
