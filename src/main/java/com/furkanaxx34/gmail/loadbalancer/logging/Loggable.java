    package com.furkanaxx34.gmail.loadbalancer.logging;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public interface Loggable {

    default void readConfiguration() {
        try {
            InputStream stream = getClass().getClassLoader().
                    getResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(stream);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    default Logger logger() {
        readConfiguration();
        return Logger.getLogger(getClass().getName());
    }
}