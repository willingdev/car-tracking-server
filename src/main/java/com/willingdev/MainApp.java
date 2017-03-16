package com.willingdev;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.FileWriter;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by phanlop on 3/15/2017 AD.
 */
public class MainApp {

    public MainApp(){
        Configurator.defaultConfig()

                .writer(new FileWriter("my_log.txt"))
                .level(Level.INFO)
                .activate();
    }

    private ScheduledExecutorService executor;
    private int port = 8888;
    private ServerController serverController;


    public void init(){
        serverController = new ServerController(port);
        serverController.start();
        executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(() -> readData(), 1000, 1000, TimeUnit.MILLISECONDS);
    }


    private void readData() {
        ByteVector data = serverController.read();
        if(data!=null) {

            Logger.info(new Date() + " Recev:" + new String(data.toByteArray()));
        }

    }

    public static void main(String[] args) throws InterruptedException {
            MainApp mainApp = new MainApp();


            new Thread(() -> mainApp.init()).start();

    }
}
