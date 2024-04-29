package project.example.Model;

public class Area {
    // the ID of the Area
    private int areaID;
    // the name of the Area
    private String areaName;
    
    public Area(int areaID, String areaName) {
        this.areaID = areaID;
        this.areaName = areaName;
    }

    public int getAreaID() {
        return areaID;
    }

    public String getAreaName() {
        return areaName;
    }
}
