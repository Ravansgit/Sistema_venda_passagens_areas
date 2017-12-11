package com.domain.web;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class VehicleStay {
    private int id;
    private String vehicleModel;
    private String vehicleColor;
    private String vehiclePlate;
    private Date begin;
    private Date end;
    private double price;
    
    public static VehicleStay getVehicleStay(int id) throws Exception{
        VehicleStay vs = null;
        String SQL = "SELECT * FROM vehicles_stays WHERE id=?";
        PreparedStatement s = Database.getConnection().prepareStatement(SQL);
        s.setInt(1, id);
        ResultSet rs = s.executeQuery();
        if(rs.next()){
            vs = new VehicleStay(
                    rs.getInt("id")
                    , rs.getString("vehicle_model")
                    , rs.getString("vehicle_color")
                    , rs.getString("vehicle_plate")
                    , rs.getTimestamp("begin_stay")
            );
        }
        rs.close();
        s.close();
        return vs;
    }
    
    public static ArrayList<VehicleStay> getStayList() throws Exception{
        ArrayList<VehicleStay> list = new ArrayList<>();
        Statement s = Database.getConnection().createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM vehicles_stays"
                        + " WHERE end_stay IS NULL");
        while(rs.next()){
            VehicleStay vs = new VehicleStay(
                    rs.getInt("id")
                    , rs.getString("vehicle_model")
                    , rs.getString("vehicle_color")
                    , rs.getString("vehicle_plate")
                    , rs.getTimestamp("begin_stay")
            );
            list.add(vs);
        }
        rs.close();
        s.close();
        return list;
    }
    
    public static ArrayList<VehicleStay> geStayListHistory(String plate, String date) throws Exception{
        ArrayList<VehicleStay> list = new ArrayList<>();
        String SQL = "SELECT * FROM vehicles_stays"
                        + " WHERE end_stay IS NOT NULL";
        if(plate!=null){
            SQL += " AND vehicle_plate = '"+plate+"'";
        }
        if(date!=null){
            SQL += " AND begin_stay >= TIMESTAMP('"+date+"')";
        }
        Statement s = Database.getConnection().createStatement();
        ResultSet rs = s.executeQuery(SQL);
        while(rs.next()){
            VehicleStay vs = new VehicleStay(
                    rs.getInt("id")
                    , rs.getString("vehicle_model")
                    , rs.getString("vehicle_color")
                    , rs.getString("vehicle_plate")
                    , rs.getTimestamp("begin_stay")
                    , rs.getTimestamp("end_stay")
                    , rs.getDouble("price")
            );
            list.add(vs);
        }
        rs.close();
        s.close();
        return list;
    }
    
    public static void addVehicleStay(String model, String color, String plate)throws Exception{
        String SQL = "INSERT INTO vehicles_stays VALUES("
                + "default,?,?,?,?,null,null)";
        PreparedStatement s = Database.getConnection().prepareStatement(SQL);
        s.setString(1, model);
        s.setString(2, color);
        s.setString(3, plate);
        s.setTimestamp(4, new Timestamp(new Date().getTime()));
        s.execute();
        s.close();
    }
    
    public static void finishVehicleStay(int id, double price)throws Exception{
        String SQL = "UPDATE vehicles_stays"
                + " SET end_stay=?, price=?"
                + " WHERE id=?";
        PreparedStatement s = Database.getConnection().prepareStatement(SQL);
        s.setTimestamp(1, new Timestamp(new Date().getTime()));
        s.setDouble(2, price);
        s.setInt(3, id);
        s.execute();
        s.close();
    }

    public VehicleStay(int id, String vehicleModel, String vehicleColor
            , String vehiclePlate, Date begin) {
        this.id = id;
        this.vehicleModel = vehicleModel;
        this.vehicleColor = vehicleColor;
        this.vehiclePlate = vehiclePlate;
        this.begin = begin;
    }

    public VehicleStay(int id, String vehicleModel, String vehicleColor
            , String vehiclePlate, Date begin, Date end, double price) {
        this.id = id;
        this.vehicleModel = vehicleModel;
        this.vehicleColor = vehicleColor;
        this.vehiclePlate = vehiclePlate;
        this.begin = begin;
        this.end = end;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
    
    public double getCalculatedPrice() throws Exception{
        Date now = new Date();
        long diff = Math.abs(now.getTime() - begin.getTime());
        long diffHours = (diff / (60*60*1000))+1;
        return (double)diffHours * Price.getCurrentPrice();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
}