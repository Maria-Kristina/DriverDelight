package com.example.driverdelight;

import com.mbientlab.metawear.data.Acceleration;
import java.util.ArrayList;

class DataProcessor {
    // Used to calculate current value of G
    private float gX = 0f, gY = 0f, gZ = 0f;
    private boolean hasG;
    private ArrayList<Acceleration> accelerationArrayList;

    // Creates new values
    DataProcessor() {
        this.accelerationArrayList = new ArrayList<>();
        this.hasG = false;
    }

    // Fetches the different acceleration data that metawear sends
    double newData(Acceleration acceleration) {
        if (!hasG) {
            accelerationArrayList.add(acceleration);
            gX += acceleration.x();
            gY += acceleration.y();
            gZ += acceleration.z();
            // Calculate value of G based on the average of 5 most consistence data
            if (accelerationArrayList.size() == 5) {
                gX /= 5;
                gY /= 5;
                gZ /= 5;
                for (Acceleration acc : accelerationArrayList) {
                    if (Math.abs(acc.x() - gX) > 0.01
                            || Math.abs(acc.y() - gY) > 0.01
                            || Math.abs(acc.z() - gZ) > 0.01) {
                        // Clear value and restart the process if data inconsistent
                        accelerationArrayList.clear();
                        gX = 0f;
                        gY = 0f;
                        gZ = 0f;
                        return newData(acceleration);
                    }
                }
                hasG = true;
                return 0;
            }
            return 0;
        } else {
            // Return value of acceleration vector
            // Just math and/or physics
            return Math.sqrt(
                    (gX - acceleration.x()) * (gX - acceleration.x())
                            + (gY - acceleration.y()) * (gY - acceleration.y())
                            + (gZ - acceleration.z()) * (gZ - acceleration.z())
            );
        }
    }
}
