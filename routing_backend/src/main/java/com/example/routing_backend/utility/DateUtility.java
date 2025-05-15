package com.example.routing_backend.utility;

import com.example.routing_backend.enums.DeliveryRange;

import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

public class DateUtility {

    public static DeliveryRange getDeliveryRange(Long departureTime) {
        Date timeD = new Date(departureTime * 1000);
        if (timeD.getHours() >= 7 && timeD.getHours() < 13) {
            return DeliveryRange.MORNING;
        } else if (timeD.getHours() >= 10 && timeD.getHours() < 13) {
            return DeliveryRange.MIDMORNING;
        } else if (timeD.getHours() >= 13 && timeD.getHours() < 16) {
            return DeliveryRange.AFTERNOON;
        } else if (timeD.getHours() >= 13 && timeD.getHours() < 19) {
            return DeliveryRange.EVENING;
        } else {
            throw new IllegalArgumentException("Geçersiz saat: " + timeD.getHours());
        }
    }

    public static List<Long> getDeliveryRangeHours(DeliveryRange deliveryRange) {

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        long startTime;
        long endTime;

        switch (deliveryRange) {
            case MIDMORNING, MORNING -> {

                startTime = today.withHour(7).atZone(ZoneId.systemDefault()).toEpochSecond();
                endTime = today.withHour(12).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            case AFTERNOON, EVENING -> {

                startTime = today.withHour(13).atZone(ZoneId.systemDefault()).toEpochSecond();
                endTime = today.withHour(18).atZone(ZoneId.systemDefault()).toEpochSecond();
            }
            default ->
                throw new IllegalArgumentException("Geçersiz teslimat aralığı: " + deliveryRange);
        }

        return Arrays.asList(startTime, endTime);
    }
}
