package com.greencloud.dto;

import com.greencloud.entity.App;
import com.greencloud.entity.Hotel;
import com.greencloud.entity.HotelGroup;
import com.greencloud.entity.User;
import com.greencloud.entity.WorkStation;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by zg on 2017-5-12.
 */
public class UserDto implements Serializable {
  private App app;
  private String authCode;
  private String hessianJump;
  private String alert;
  private String localString;
  private String loginType;
  private HotelGroup hotelGroup;
  private String hotelRange;
  private WorkStation workStation;
  private String version;
  private String buildingStr;
  private String idle;
  private Date loginTimeClient;
  private Date currentDate;
  private Hotel hotel;
  private Date bizDate;
  private String bizDateStr;
  private User user;
  private Date loginTimeAppServer;
  private int cashier;

  public App getApp() {
    return app;
  }

  public void setApp(App app) {
    this.app = app;
  }

  public String getAuthCode() {
    return authCode;
  }

  public void setAuthCode(String authCode) {
    this.authCode = authCode;
  }

  public String getHessianJump() {
    return hessianJump;
  }

  public void setHessianJump(String hessianJump) {
    this.hessianJump = hessianJump;
  }

  public String getAlert() {
    return alert;
  }

  public void setAlert(String alert) {
    this.alert = alert;
  }

  public String getLocalString() {
    return localString;
  }

  public void setLocalString(String localString) {
    this.localString = localString;
  }

  public String getLoginType() {
    return loginType;
  }

  public void setLoginType(String loginType) {
    this.loginType = loginType;
  }

  public HotelGroup getHotelGroup() {
    return hotelGroup;
  }

  public void setHotelGroup(HotelGroup hotelGroup) {
    this.hotelGroup = hotelGroup;
  }

  public String getHotelRange() {
    return hotelRange;
  }

  public void setHotelRange(String hotelRange) {
    this.hotelRange = hotelRange;
  }

  public WorkStation getWorkStation() {
    return workStation;
  }

  public void setWorkStation(WorkStation workStation) {
    this.workStation = workStation;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getBuildingStr() {
    return buildingStr;
  }

  public void setBuildingStr(String buildingStr) {
    this.buildingStr = buildingStr;
  }

  public String getIdle() {
    return idle;
  }

  public void setIdle(String idle) {
    this.idle = idle;
  }

  public Date getLoginTimeClient() {
    return loginTimeClient;
  }

  public void setLoginTimeClient(Date loginTimeClient) {
    this.loginTimeClient = loginTimeClient;
  }

  public Date getCurrentDate() {
    return currentDate;
  }

  public void setCurrentDate(Date currentDate) {
    this.currentDate = currentDate;
  }

  public Hotel getHotel() {
    return hotel;
  }

  public void setHotel(Hotel hotel) {
    this.hotel = hotel;
  }

  public Date getBizDate() {
    return bizDate;
  }

  public void setBizDate(Date bizDate) {
    this.bizDate = bizDate;
  }

  public String getBizDateStr() {
    return bizDateStr;
  }

  public void setBizDateStr(String bizDateStr) {
    this.bizDateStr = bizDateStr;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Date getLoginTimeAppServer() {
    return loginTimeAppServer;
  }

  public void setLoginTimeAppServer(Date loginTimeAppServer) {
    this.loginTimeAppServer = loginTimeAppServer;
  }

  public int getCashier() {
    return cashier;
  }

  public void setCashier(int cashier) {
    this.cashier = cashier;
  }

}
