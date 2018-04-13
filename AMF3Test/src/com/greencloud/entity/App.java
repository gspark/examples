package com.greencloud.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zg on 2017-5-12.
 */
public class App implements Serializable {
    private String isHalt;
    private int id;
    private String createUser;
    private String remark;
    private String modifyUser;
    private String descriptEn;
    private Date createDatetime;
    private String descript;
    private Date modifyDatetime;
    private String code;
    private String exeName;
    private String appType;
    private int listOrder;

    public String getIsHalt() {
        return isHalt;
    }

    public void setIsHalt(String isHalt) {
        this.isHalt = isHalt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getDescriptEn() {
        return descriptEn;
    }

    public void setDescriptEn(String descriptEn) {
        this.descriptEn = descriptEn;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public Date getModifyDatetime() {
        return modifyDatetime;
    }

    public void setModifyDatetime(Date modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExeName() {
        return exeName;
    }

    public void setExeName(String exeName) {
        this.exeName = exeName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public int getListOrder() {
        return listOrder;
    }

    public void setListOrder(int listOrder) {
        this.listOrder = listOrder;
    }
}
