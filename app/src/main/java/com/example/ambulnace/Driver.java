package com.example.ambulnace;

public class Driver {
    private String name;
    private String vehicleNumber;
    private String phoneNumber;
    private String address;

    public Driver(String name, String vehicleNumber, String phoneNumber, String address) {
        this.name = name;
        this.vehicleNumber = vehicleNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }
}
