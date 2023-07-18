package ru.practicum.explorewithme.constant;

import java.time.format.DateTimeFormatter;

public class Constant {

    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    public static final String SERVICE_ID = "ewm-main-service";
    public static final String EVENT_URI = "/events/";
}
