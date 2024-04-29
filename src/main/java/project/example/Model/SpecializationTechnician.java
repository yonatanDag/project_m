package project.example.Model;

public class SpecializationTechnician {
    // the Technician
    private Technician tech;
    // the Specialization
    private Specialization type;
    // the rating of the Technician is the Specialization
    private double rating;
    // the seniority of the Technician is the Specialization
    private int seniority;

    public SpecializationTechnician(Technician tech, Specialization type, double rating, int seniority) {
        this.tech = tech;
        this.type = type;
        this.rating = rating;
        this.seniority = seniority;
    }

    public Technician getTech() {
        return tech;
    }

    public Specialization getType() {
        return type;
    }

    public double getRating() {
        return rating;
    }

    public int getSeniority() {
        return seniority;
    }
}
