package com.example.iopd;

import java.util.Date;

public class Patient {

    private int id;
    private String name, surname;
    private int state;
    private int queueNo;
    private Date appointment;

    public Patient(int mId, String mName, String mSurname){
        id = mId;
        name = mName;
        surname = mSurname;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getSurname(){
        return surname;
    }

    public String getFullname(){
        return name+" "+surname;
    }

    public Date getAppointment(){
        return appointment;
    }

    public void setState(int now){
        state = now;
    }

    public void setAppointment(Date date){
        appointment = date;
    }
}
