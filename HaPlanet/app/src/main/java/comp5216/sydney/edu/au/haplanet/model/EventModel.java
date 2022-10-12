package comp5216.sydney.edu.au.haplanet.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anupamchugh on 11/02/17.
 */

public class EventModel implements Serializable {
    private String description;
    private String location;
    private String numberOfPeople;
    private String picture;
    private String price;
    private String startTime;
    private String title;

    //新增分类
//    private String category;

    //新增参与用户列表
    private ArrayList<String> uidList;

    public EventModel(String picture, String title, String description,
                      String startTime, String location, String numberOfPeople,
                      String price, ArrayList<String> uidList) {
        this.description = description;
        this.location = location;
        this.numberOfPeople = numberOfPeople;
        this.picture = picture;
        this.price = price;
        this.startTime = startTime;
        this.title = title;

//        this.category = category;

        this.uidList = uidList;
    }
    public EventModel(){}


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(String numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getUidList() {
        return uidList;
    }

    public void setUidList(ArrayList<String> uidList) {
        this.uidList = uidList;
    }

//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
}
