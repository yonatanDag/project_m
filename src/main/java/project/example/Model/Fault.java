package project.example.Model;

public class Fault {
    private int fID;// id 0 means that is other 
    private Specialization fSpecialization;// the sepcialization of the fault
    private String fDescription;
    private int duration;// the amount of minutes it's take to fix the Fault in minutes(cfID 0 will have duration of 1 hour)
    private int urgencyLevel; // Urgency level of the fault(1 is the most urgent)

    public Fault(int fID, Specialization fSpecialization, String fDescription, int duration, int urgencyLevel) {
        this.fID = fID;
        this.fSpecialization = fSpecialization;
        this.fDescription = fDescription;
        this.duration = duration;
        this.urgencyLevel = urgencyLevel;// the smaller the number, the more urgent it is(1 is the most urgent)
    }

    public int getfID() {
        return fID;
    }

    public void setfID(int fID) {
        this.fID = fID;
    }

    public Specialization getCfSpecialization() {
        return fSpecialization;
    }

    public void setFSpecialization(Specialization fSpecialization) {
        this.fSpecialization = fSpecialization;
    }

    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(int urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

}
