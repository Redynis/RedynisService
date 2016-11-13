package ca.uwaterloo.redynissvc.utils;

import ca.uwaterloo.redynissvc.beans.ServiceConfig;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

public class ConfigHelper
{
    private static ConfigHelper instance;
    @Getter private ServiceConfig serviceConfig;

    private ConfigHelper(ServiceConfig serviceConfig)
    {
        this.serviceConfig = serviceConfig;
    }

    public static ConfigHelper getInstance(String configFilePath)
        throws IOException
    {
        if (null == instance)
        {
            ServiceConfig serviceConfig = Constants.MAPPER.readValue(new File(configFilePath), ServiceConfig.class);
            instance = new ConfigHelper(serviceConfig);
        }

        return instance;
    }
}
