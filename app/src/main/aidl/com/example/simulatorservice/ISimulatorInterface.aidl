// ISimulatorInterface.aidl
package com.example.simulatorservice;

// Declare any non-default types here with import statements

interface ISimulatorInterface {
    float getSpeed();
    float getDistance();
    long getSystemTime();
    boolean isIgnitionOn();
    boolean isDayMode();
    String getUnit();
}