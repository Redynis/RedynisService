package ca.uwaterloo.redynissvc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Constants
{
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String CONFIGFILE_PARAM = "ConfigFile";
    public static final Long INDUCED_LATENCY_MILLISEC = 100L;
}
