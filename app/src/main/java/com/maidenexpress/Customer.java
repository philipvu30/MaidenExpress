package com.maidenexpress;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Customer {
    private String displayName,email,password,phone,address,country;
    private Boolean active;
    private long createdDay, birthDay, zipCode, balance;
    private GeoPoint geoPoint;




    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }



    public Customer() {

    }


    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        active = active;
    }

    public long getCreatedDay() {
        return createdDay;
    }

    public void setCreatedDay(long createdDay) {
        this.createdDay = createdDay;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public long getZipCode() {
        return zipCode;
    }

    public void setZipCode(long zipCode) {
        this.zipCode = zipCode;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public Customer(String displayName, String email
            , String password, String phone, Boolean active, long createdDay
            , long birthDay, String address, String country, long zipCode, GeoPoint geoPoint, long balance) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.active = active;
        this.createdDay = createdDay;
        this.birthDay = birthDay;
        this.address = address;
        this.country = country;
        this.zipCode  = zipCode;
        this.geoPoint = geoPoint;
        this.balance = balance;
    }
}
