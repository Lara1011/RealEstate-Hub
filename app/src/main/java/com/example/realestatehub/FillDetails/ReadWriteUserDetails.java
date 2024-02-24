package com.example.realestatehub.FillDetails;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ReadWriteUserDetails {
    private static final String PREF_NAME = "user_details";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_BIRTHDAY = "birthday";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_PURPOSE = "purpose";
    private static ReadWriteUserDetails instance;
    private SharedPreferences sharedPreferences;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String birthday;
    private String phoneNumber;
    private String address;
    private String gender;
    private String purpose;
    private Context context;

    private ReadWriteUserDetails(){
    }
    private ReadWriteUserDetails(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        firstName = sharedPreferences.getString(KEY_FIRST_NAME, "");
        lastName = sharedPreferences.getString(KEY_LAST_NAME, "");
        email = sharedPreferences.getString(KEY_EMAIL, "");
        password = sharedPreferences.getString(KEY_PASSWORD, "");
        birthday = sharedPreferences.getString(KEY_BIRTHDAY, "");
        phoneNumber = sharedPreferences.getString(KEY_PHONE_NUMBER, "");
        address = sharedPreferences.getString(KEY_ADDRESS, "");
        gender = sharedPreferences.getString(KEY_GENDER, "");
        purpose = sharedPreferences.getString(KEY_PURPOSE, "");
    }

    public static synchronized ReadWriteUserDetails getInstance(Context context) {
        if (instance == null) {
            instance = new ReadWriteUserDetails(context);
        }
        return instance;
    }

    public void saveUserDetails() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_BIRTHDAY, birthday);
        editor.putString(KEY_PHONE_NUMBER, phoneNumber);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_PURPOSE, purpose);
        editor.apply();
        Log.i("ReadWriteUserDetails", firstName);
        Log.i("ReadWriteUserDetails", lastName);
        Log.i("ReadWriteUserDetails", email);
        Log.i("ReadWriteUserDetails", password);
        Log.i("ReadWriteUserDetails", birthday);
        Log.i("ReadWriteUserDetails", phoneNumber);
        Log.i("ReadWriteUserDetails", address);
        Log.i("ReadWriteUserDetails", gender);
        Log.i("ReadWriteUserDetails", purpose);
    }
    public void clearUserDetails() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all stored data
        editor.apply();
    }
    // Getters and setters for all fields
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
