package com.example.routing_backend.utility;

import com.example.routing_backend.enums.DeliveryRange;

public class DeliveryRangeUtility {

    public static boolean isInRange(DeliveryRange dispatch, DeliveryRange vehicle) {
        boolean isInRange = false;
        if (dispatch == DeliveryRange.MORNING && vehicle == DeliveryRange.MORNING) {
            isInRange = true;
        } else if (dispatch == DeliveryRange.MORNING && vehicle == DeliveryRange.MIDMORNING) {
            isInRange = true;
        } else if (dispatch == DeliveryRange.MIDMORNING && vehicle == DeliveryRange.MORNING) {
            isInRange = true;
        } else if (dispatch == DeliveryRange.MIDMORNING && vehicle == DeliveryRange.MIDMORNING) {
            isInRange = true;
        } else if (dispatch == DeliveryRange.AFTERNOON && vehicle == DeliveryRange.AFTERNOON) {
            isInRange = true;
        } else if (dispatch == DeliveryRange.AFTERNOON && vehicle == DeliveryRange.EVENING) {
            isInRange = true;
        } else if (dispatch == DeliveryRange.EVENING && vehicle == DeliveryRange.AFTERNOON) {
            isInRange = true;
        } else if (dispatch == DeliveryRange.EVENING && vehicle == DeliveryRange.EVENING) {
            isInRange = true;
        }
        return isInRange;
    }
}
