package ca.uwaterloo.redynissvc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants
{
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String CONFIGFILE_PARAM = "ConfigFile";
    public static final Long INDUCED_LATENCY_MILLISEC = 100L;

    public static final String KEY_PARAM = "key";
    public static final String VALUE_PARAM = "value";

    public static final String SERVICE_ENDPOINT = "http://scs555pc:8080/RedynisService/redis";

}
