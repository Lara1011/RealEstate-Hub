package com.example.realestatehub.FillDetails;

public class ReadWriteUserDetails {
    private static ReadWriteUserDetails instance;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String birthday;
    private String phoneNumber;
    private String address;
    private String gender;
    private String purpose;

    public ReadWriteUserDetails() {
        firstName = "";
        lastName = "";
        email = "";
        password = "";
        birthday = "";
        phoneNumber = "";
        address = "";
        gender = "";
        purpose = "";
    }

    public ReadWriteUserDetails(String firstName, String lastName, String email, String password, String birthday, String phoneNumber, String address, String gender, String purpose) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.purpose = purpose;
    }

    public static synchronized ReadWriteUserDetails getInstance() {
        if (instance == null) {
            instance = new ReadWriteUserDetails();
        }
        return instance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}