package com.avinabaray.crm.Models;

import com.google.firebase.Timestamp;

public class UserModel {

    String id;
    String phone;
    Timestamp timeStamp;
    String name;
    String address;
    Long pinCode;
    Long monthlyIncome;
    String occupation;
    Long adultMembers;
    Long childMembers;
    Long earningMembers;
    Boolean activeStatus = true;
    String userRole = "user";

    public Boolean getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(Boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPinCode() {
        return pinCode;
    }

    public void setPinCode(Long pinCode) {
        this.pinCode = pinCode;
    }

    public Long getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Long monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Long getAdultMembers() {
        return adultMembers;
    }

    public void setAdultMembers(Long adultMembers) {
        this.adultMembers = adultMembers;
    }

    public Long getChildMembers() {
        return childMembers;
    }

    public void setChildMembers(Long childMembers) {
        this.childMembers = childMembers;
    }

    public Long getEarningMembers() {
        return earningMembers;
    }

    public void setEarningMembers(Long earningMembers) {
        this.earningMembers = earningMembers;
    }
}
