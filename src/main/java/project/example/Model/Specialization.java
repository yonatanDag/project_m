package project.example.Model;


public class Specialization {
    // the ID of the Specialization
    private int idS;
    // the name of the Specialization
    private String nameS;
    // the description of the Specialization
    private String descriptionS;

    public Specialization(int idS, String nameS, String descriptionS) {
        this.idS = idS;
        this.nameS = nameS;
        this.descriptionS = descriptionS;
    }

    public int getIdS() {
        return idS;
    }

    public String getNameS() {
        return nameS;
    }

    public String getDescription() {
        return descriptionS;
    }
}
