package project.example.Model;

public class Area {
    private int areaID;
    private String areaName;
    
    public Area(int areaID, String areaName) {
        this.areaID = areaID;
        this.areaName = areaName;
    }

    public int getAreaID() {
        return areaID;
    }

    public void setAreaID(int areaID) {
        this.areaID = areaID;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
