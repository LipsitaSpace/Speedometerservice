// ISimulatorInterface.aidl
package com.example.simulatorservice;

// Declare any non-default types here with import statements

interface ISimulatorInterface {
    float getSpeed();
    float getDistance();
    String getTime();
    boolean getMode();
    String getUnit();
    float getTotalDistance();
    float getTotalTime();
}