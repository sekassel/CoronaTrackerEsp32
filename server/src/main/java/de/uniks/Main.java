package de.uniks;

import de.uniks.cwa.CwaDataInterpreter;
import de.uniks.spark.SparkRequestHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        SparkRequestHandler.handleRequests();
        scheduler.scheduleAtFixedRate(CwaDataInterpreter::checkForInfectionsHourlyTask, 100, 60, TimeUnit.MINUTES);
        //TODO: cleanup for login entries in verificationUser DB like once in a day

        try {
            Thread springThread = new Thread(SpringBoot::startSpring);
            springThread.start();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "UI Vaadin Spring Boot Thread crashed!", e);
        }
    }
}