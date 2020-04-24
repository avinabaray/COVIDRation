package com.avinabaray.crm.Models;

import com.google.firebase.Timestamp;

public class UserModel {

    private String id;
    private String phone;
    private Timestamp timestamp;
    private String name;
    private String address;
    private Long pinCode;
    private Long monthlyIncome;
    private String occupation;
    private Long adultMembers;
    private Long childMembers;
    private Long earningMembers;
    private Boolean activeStatus = true;

    /**
     * userRole: user, admin, adminPending, superAdmin
     * <br><br>
     *     admin is displayed as Volunteer
     * <br>
     *     adminPending(s) are Volunteers to be approved by superAdmin
     */
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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
