// ISimulatorInterface.aidl
package com.example.simulatorservice;

// Declare any non-default types here with import statements

interface ISimulatorInterface {
    void getData(out Bundle data);
    float getSpeed();
    float getDistance();
    String getTime();
}