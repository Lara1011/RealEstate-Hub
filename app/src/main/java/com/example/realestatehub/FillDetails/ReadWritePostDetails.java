package com.example.realestatehub.FillDetails;

import java.util.HashMap;
import java.util.UUID;

public class ReadWritePostDetails {
    private static ReadWritePostDetails instance;
    private HashMap<String, HashMap<String, String>> postDetailsMap;
    private String ID;

    public ReadWritePostDetails() {
        postDetailsMap = new HashMap<>();
    }

    public void addPost(String id, String name, String location, String streetName, String floor, String totalFloors, String homeNumber, String type, String view){
        if(!postDetailsMap.containsKey(id)){
            HashMap<String, String> detailsMap = new HashMap<>();
            detailsMap.put("name", name);
            detailsMap.put("location", location);
            detailsMap.put("streetName", streetName);
            detailsMap.put("floor", floor);
            detailsMap.put("totalFloors", totalFloors);
            detailsMap.put("homeNumber", homeNumber);
            detailsMap.put("type", type);
            detailsMap.put("view", view);
            postDetailsMap.put(id, detailsMap);
        }
    }

    public static synchronized ReadWritePostDetails getInstance() {
        if (instance == null) {
            instance = new ReadWritePostDetails();
        }
        return instance;
    }

    public void addPostInformation(String id, String key, String value){
        if(postDetailsMap.containsKey(id)){
            postDetailsMap.get(id).put(key, value);
        }
    }

    public HashMap<String, String> getPostDetails(String id){
        if(!postDetailsMap.containsKey(id)){
            return null;
        }
        return postDetailsMap.get(id);
    }

    public String getPostInformation(String id, String key){
        if(!postDetailsMap.containsKey(id)){
            return "null";
        }
        return postDetailsMap.get(id).get(key);
    }

    public void setAdType(String id, String adType){
        if(postDetailsMap.containsKey(id)){
            postDetailsMap.get(id).put("adType", adType);
        }
    }

    public String getAdID(){
        return ID;
    }

    public void setAdID(){
        ID = UUID.randomUUID().toString();
    }

}
