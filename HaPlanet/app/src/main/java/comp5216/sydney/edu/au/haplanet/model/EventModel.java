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
    private String picture;
    private String title;
    private String description;
    private String startTime;
    private String location;
    private String numberOfPeople;
    private String price;

    //新增活动时长
    private String time;
    //新增分类
    private String category;
    //新增参与用户列表
    private ArrayList<String> uidList;

    public EventModel(String picture, String title, String description,
                      String startTime, String location, String numberOfPeople,
                      String price, String time, String category, ArrayList<String> uidList) {
        this.picture = picture;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.location = location;
        this.numberOfPeople = numberOfPeople;
        this.price = price;

        this.time = time;
        this.category = category;
        this.uidList = uidList;
    }

    public EventModel() {
    }


    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<String> getUidList() {
        return uidList;
    }

    public void setUidList(ArrayList<String> uidList) {
        this.uidList = uidList;
    }

    @Override
    public EventModel clone() {
        EventModel eventModel = new EventModel(this.picture, this.title, this.description, this.startTime,
                this.location, this.numberOfPeople, this.price, this.time, this.category, this.uidList);
        return eventModel;
    }
}
