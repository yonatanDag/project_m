package project.example.Model;

import java.util.HashMap;
import java.util.Map;

public class SpecializationTechnician {
    private Technician tech;
    private Specialization type;
    private double rating;
    private int seniority;
    // costPerFault represents the cost that the technician charges for fixing each specific type of fault.
    private Map<Fault, Double> costPerFault = new HashMap<>();

    public SpecializationTechnician(Technician tech, Specialization type, double rating, int seniority, Map<Fault, Double> costPerFault) {
        this.tech = tech;
        this.type = type;
        this.rating = rating;
        this.seniority = seniority;
        this.costPerFault = costPerFault;
    }

    public Technician getTech() {
        return tech;
    }

    public void setTech(Technician tech) {
        this.tech = tech;
    }

    public Specialization getType() {
        return type;
    }

    public void setType(Specialization type) {
        this.type = type;
    }
    
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getSeniority() {
        return seniority;
    }
    public void setSeniority(int seniority) {
        this.seniority = seniority;
    }

    public Map<Fault, Double> getcostPerFault() {
        return costPerFault;
    }

    public void setCostPerFix(Map<Fault, Double> costPerFault) {
        this.costPerFault = costPerFault;
    }

    //return the cost that the technician charge for fixing specific fault, if the technician is don't know how to fix the fault - return -1
    public Double getPrice(Fault fault){
        return costPerFault.get(fault);
    }
}
