package com.example.myBaiduOffline;

/**
 * 城市实体 2015/5/25 14:22
 */
public class CityEntity {

    private int cityID;
    private String cityName;
    private boolean isChildCities;
    private int size;
    private int ratio;

    public int getCityID() {
        return cityID;
    }

    public CityEntity setCityID(int cityID) {
        this.cityID = cityID;
        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public CityEntity setCityName(String cityName) {
        this.cityName = cityName;
        return this;
    }

    public boolean isChildCities() {
        return isChildCities;
    }

    public CityEntity setChildCities(boolean isChildCities) {
        this.isChildCities = isChildCities;
        return this;
    }

    public int getSize() {
        return size;
    }

    public CityEntity setSize(int size) {
        this.size = size;
        return this;
    }

    public int getRatio() {
        return ratio;
    }

    public CityEntity setRatio(int ratio) {
        this.ratio = ratio;
        return this;
    }
}
