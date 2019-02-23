package com.example.iopd;


import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.Calendar;

public class BookmarkQueue {

    private String url ="http://iopd.ml/?function=";
    private int appointmentId, doctorId, patientId;
    public BookmarkQueue(int id) {
        patientId = id;
        try{
            appointmentId = checkAppointment(id);
            URL link = new URL(url+"getAppointment");
            HttpURLConnection urlConnection = (HttpURLConnection) link.openConnection();

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private int checkAppointment(int id){
        int appointmentId =0;

        return appointmentId;
    }

    class Appointment{
        int appointmentId;
        int patientId;
        int doctorId;
        Date appointmentDate;

        public Appointment(int aid, int pid, int did, Date ad){
            appointmentId = aid;
            patientId = pid;
            doctorId = did;
            appointmentDate = ad;
        }

        public boolean haveAppointment(){
            boolean appointment = false;



            return appointment;
        }

    }


}

