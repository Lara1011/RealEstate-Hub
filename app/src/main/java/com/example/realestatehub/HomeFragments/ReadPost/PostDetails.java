package com.example.realestatehub.HomeFragments.ReadPost;

// Import the Serializable interface to allow instances of this class to be serialized.
import java.io.Serializable;

// Declare the PostDetails class which implements Serializable for object serialization.
public class PostDetails implements Serializable {
    // Public fields that can be accessed directly, typically used for user identification and contact.
    public String userName;
    public String phoneNumber;

    // Private fields to encapsulate the property details. Accessible via getter and setter methods.
    private String name;
    private String location;
    private String streetName;
    private String floor;
    private String totalFloors;
    private String homeNumber;
    private String type; // e.g., apartment, villa
    private String view; // e.g., sea view, city view
    private String adType; // e.g., sale, rent
    private String additionalInformation;

    // Boolean-like fields represented as Strings for additional features of the property.
    // These could be changed to boolean for better practice.
    public String airConditioner = "false";
    public String kosherKitchen = "false";
    public String waterHeater = "false";
    public String accessForDisabled = "false";
    public String renovated = "false";
    public String storage = "false";
    public String furniture = "false";
    public String elevators = "false";

    // Field for storing the URL of the property photo.
    private String photoUrl;

    // Default constructor required for data fetching and serialization, e.g., when using Firebase.
    public PostDetails() {
        // No arguments constructor
    }

    // Getter and setter methods for private fields, following JavaBean standard naming conventions.
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getTotalFloors() {
        return totalFloors;
    }

    public void setTotalFloors(String totalFloors) {
        this.totalFloors = totalFloors;
    }

    public String getHomeNumber() {
        return homeNumber;
    }

    public void setHomeNumber(String homeNumber) {
        this.homeNumber = homeNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getAccessForDisabled() {
        return accessForDisabled;
    }

    public void setAccessForDisabled(String accessForDisabled) {
        this.accessForDisabled = accessForDisabled;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getAirConditioner() {
        return airConditioner;
    }

    public void setAirConditioner(String airConditioner) {
        this.airConditioner = airConditioner;
    }

    public String getKosherKitchen() {
        return kosherKitchen;
    }

    public void setKosherKitchen(String kosherKitchen) {
        this.kosherKitchen = kosherKitchen;
    }

    public String getWaterHeater() {
        return waterHeater;
    }

    public void setWaterHeater(String waterHeater) {
        this.waterHeater = waterHeater;
    }

    public String getPhoneNumber() {
        return phoneNumber; 
    }
}
