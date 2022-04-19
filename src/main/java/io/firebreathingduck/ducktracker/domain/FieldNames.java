package io.firebreathingduck.ducktracker.domain;

/**
 * If this was a serious project, I would either go for domain classes
 * Duck, Pond, DuckTravel  or at a minimum go for enums for the field names.
 */
public class FieldNames {

    public static final String DUCK_ID = "id";
    public static final String DUCK_NAME = "duck_name";
    public static final String DUCK_TAGGED = "tagged";

    public static final String POND_ID = "id";
    public static final String POND_NAME = "pond_name";
    public static final String POND_LOCATION = "pond_location";

    public static final String DUCK_TRAVEL_ID = "id";
    public static final String DUCK_TRAVEL_DUCK_ID = "duck_id";
    public static final String DUCK_TRAVEL_POND_ID = "pond_id";
    public static final String DUCK_TRAVEL_ARRIVAL = "arrival";
    public static final String DUCK_TRAVEL_DEPARTURE = "departure";

}
