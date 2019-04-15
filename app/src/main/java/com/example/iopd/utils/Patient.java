package com.example.iopd.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Patient {

    private int id;
    private String name, surname;
    private int state;
    private int queueNo;
    private String appointment,timeStart,timeEnd;
    private int doctor, appointmentId,workflowId;

    public Patient(int mId, String mName, String mSurname){
        id = mId;
        name = mName;
        surname = mSurname;
        appointment = "";
        timeStart = "";
        timeEnd = "";
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

    public String getAppointment(){
        return appointment;
    }

    public void setState(int now){
        state = now;
    }

    public void setAppointmentDate(String date){
        appointment = date;
    }

    public boolean haveAppointment(){
        Date current = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(current);
        if(appointment.equals(formattedDate)){
            return true;
        }
        return false;
    }

    public void setAppointment(int doc,int ap){
        doctor = doc;
        appointmentId = ap;
    }

    public void setTime(String s, String e){
        timeStart = s;
        timeEnd = e;
    }

    public void setWorkflowId(int id){
        workflowId = id;
    }

    public int getWorkflowId(){
        return workflowId;
    }

    public String getTimeStart(){
        return timeStart;
    }

    public String getTimeEnd(){
        return timeEnd;
    }

    public int getDoctor(){
        return doctor;
    }

    public int getAppointmentId(){
        return appointmentId;
    }
}
