package project.example.Model;

public class City {
    private int cityID;
    private String cityName;
    private Area cityArea;
    
    public City(int cityID, String cityName, Area cityArea) {
        this.cityID = cityID;
        this.cityName = cityName;
        this.cityArea = cityArea;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Area getCityArea() {
        return cityArea;
    }

    public void setCityArea(Area cityArea) {
        this.cityArea = cityArea;
    }
}
