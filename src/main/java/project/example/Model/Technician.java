    package project.example.Model;

import java.util.ArrayList;

// Technician can has more than 1 Specialization
public class Technician {
    private int idT;
    private String name;
    private City city;
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

    public void setIdT(int idT) {
        this.idT = idT;
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

    public double getVisitPrice() {
        return visitPrice;
    }

    public void setVisitPrice(double visitPrice) {
        this.visitPrice = visitPrice;
    }

    public boolean isTechSuit(ArrayList<SpecializationTechnician> stList, Specialization spec) {
        int low = 0;
        int high = stList.size() - 1;
    
        while (low <= high) {
            int mid = low + (high - low) / 2;
            SpecializationTechnician curMid = stList.get(mid);
    
            if (curMid.getTech().getIdT() < this.idT) {
                low = mid + 1;
            } 
            else 
            if (curMid.getTech().getIdT() > this.idT) {
                high = mid - 1;
            } 
            // this is when it is the same Technician
            else {
                // now check for Specialization
                if (curMid.getType().getIdS() == spec.getIdS()) {
                    return true; // Found the Technician with the required Specialization
                } else if (curMid.getType().getIdS() < spec.getIdS()) {
                    // Move to the next index as list is sorted first by technicianID and then by specializationID
                    low = mid + 1;
                } else {
                    // Move to the previous index
                    high = mid - 1;
                }
            }
        }
        return false;
    }
    



    @Override
    public String toString() {
        return name + " (" + city.getCityName() + ")"; 
    }

}
