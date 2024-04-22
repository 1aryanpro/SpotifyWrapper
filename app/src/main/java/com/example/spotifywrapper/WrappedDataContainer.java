package com.example.spotifywrapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class WrappedDataContainer {
    public String userID;
    public String user;
    public String date;
    public int epoch;

    WrappedDataContainer(String userID, String user, int epoch) {
        this.userID = userID;
        this.user = user + "'s Wrapped";
        this.epoch = epoch;

        LocalDateTime localDate = Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy - h:mm a");

        this.date = localDate.format(formatter);
    }
}
