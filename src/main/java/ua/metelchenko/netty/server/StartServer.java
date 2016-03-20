package ua.metelchenko.netty.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartServer extends Thread {

    private static int port = 11111;
    private static ServerConfig serverConfig;

    public static void main(String[] args) throws Exception {

        new StartServer().start();
        System.out.println("Server started. Port " + port);
        System.out.println("Enter \"s\" to stop the server");
        try {
            serverConfig = new ServerConfig(port);
            serverConfig.start();
        } catch (Exception ex) {
            System.out.println("The port is busy");
            System.exit(1);
        }
    }

    @Override
    public void run() {
        while(true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = null;
            try {
                input = reader.readLine();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
            if (input != null) {
                if (input.equals("s")) {
                    serverConfig.stop();
                    System.exit(0);
                }
            }
        }
    }
}