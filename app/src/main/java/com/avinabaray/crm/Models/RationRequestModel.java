package com.avinabaray.crm.Models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class RationRequestModel {

    public static final Long PENDING = 0L;
    public static final Long APPROVED = 1L;
    public static final Long DELIVERED = 2L;
    public static final Long REJECTED = 3L;

    private String id;
    private String userName;
    private String userId;
    /**
     * userRole: user, admin, superAdmin
     */
    private String userRole;
    private ArrayList<String> itemNames = new ArrayList<>();
    private ArrayList<String> itemUnits = new ArrayList<>();
    private ArrayList<Long> itemQtys = new ArrayList<>();
    private Long pinCode;
    private Timestamp requestTime;
    private Timestamp responseTime;

    /**
     * 0 = PENDING<br>
     * 1 = APPROVED<br>
     * 2 = DELIVERED<br>
     * 3 = REJECTED
     */
    private Long requestStatus = PENDING;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public ArrayList<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(ArrayList<String> itemNames) {
        this.itemNames = itemNames;
    }

    public ArrayList<String> getItemUnits() {
        return itemUnits;
    }

    public void setItemUnits(ArrayList<String> itemUnits) {
        this.itemUnits = itemUnits;
    }

    public ArrayList<Long> getItemQtys() {
        return itemQtys;
    }

    public void setItemQtys(ArrayList<Long> itemQtys) {
        this.itemQtys = itemQtys;
    }

    public Long getPinCode() {
        return pinCode;
    }

    public void setPinCode(Long pinCode) {
        this.pinCode = pinCode;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public Timestamp getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Timestamp responseTime) {
        this.responseTime = responseTime;
    }

    public Long getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Long requestStatus) {
        this.requestStatus = requestStatus;
    }
}
