package com.example.routing_backend.utility;

public class BoundingBoxUtility {

    public static boolean isWithinBoundingBox(
            double pointLatitude,
            double pointLongitude,
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2
    ) {

        boolean isWithinLatitude = pointLatitude <= latitude1 && pointLatitude >= latitude2;
        boolean isWithinLongitude = pointLongitude >= longitude1 && pointLongitude <= longitude2;

        return isWithinLatitude && isWithinLongitude;
    }

}
