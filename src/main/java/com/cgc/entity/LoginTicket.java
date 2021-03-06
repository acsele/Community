package com.cgc.entity;

import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

public class LoginTicket implements Serializable {
    private static final long serialVersionUID = 438342979500803230L;
    private Integer id;
    private Integer userId;
    private String ticket;
    /**
     * 0-有效; 1-无效;
     */
    private Integer status;
    private Date expired;

    public LoginTicket() {
    }

    public LoginTicket(Integer userId, String ticket, Integer status, Date expired) {
        this.userId = userId;
        this.ticket = ticket;
        this.status = status;
        this.expired = expired;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", userId=" + userId +
                ", ticket='" + ticket + '\'' +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}