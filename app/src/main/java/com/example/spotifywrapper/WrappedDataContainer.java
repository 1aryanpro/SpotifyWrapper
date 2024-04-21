package com.example.spotifywrapper;

import java.time.Instant;
import java.time.LocalDate;
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

        LocalDate localDate = Instant.ofEpochSecond(epoch).atZone(ZoneOffset.UTC).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        this.date = localDate.format(formatter);
    }
}
