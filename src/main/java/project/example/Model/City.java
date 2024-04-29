package project.example.Model;

public class City {
    // the ID of the City
    private int cityID;
    // the name of the City
    private String cityName;
    // the Area that the city is belong to
    private Area cityArea;
    
    public City(int cityID, String cityName, Area cityArea) {
        this.cityID = cityID;
        this.cityName = cityName;
        this.cityArea = cityArea;
    }

    public int getCityID() {
        return cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public Area getCityArea() {
        return cityArea;
    }

}
