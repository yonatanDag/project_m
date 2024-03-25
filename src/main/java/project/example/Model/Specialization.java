package project.example.Model;


public class Specialization {
    private int idS;
    private String nameS;
    private String descriptionS;
    // private Map<Specialization, List<Technician>> techniciansMap;

    public Specialization(int idS, String nameS, String descriptionS) {
        this.idS = idS;
        this.nameS = nameS;
        this.descriptionS = descriptionS;
        // this.techniciansMap = new HashMap<>();
    }

    public int getIdS() {
        return idS;
    }

    public void setIdS(int idS) {
        this.idS = idS;
    }
    
    public String getNameS() {
        return nameS;
    }

    public void setNameS(String nameS) {
        this.nameS = nameS;
    }

    public String getDescription() {
        return descriptionS;
    }


    public void setDescription(String description) {
        this.descriptionS = description;
    }

    // public Map<Specialization, List<Technician>> getTechniciansMap() {
    //     return techniciansMap;
    // }

    // public void setTechniciansMap(Map<Specialization, List<Technician>> techniciansMap) {
    //     this.techniciansMap = techniciansMap;
    // }

   
}
