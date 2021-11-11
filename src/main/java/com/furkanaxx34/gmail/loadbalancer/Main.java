package com.furkanaxx34.gmail.loadbalancer;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.choice.RangeArgumentChoice;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        readLogConfig();

        ArgumentParser parser = ArgumentParsers.newFor("loadbalancer").build()
                .defaultHelp(true)
                .description("Creates new instance for loadbalancer");

        parser.addArgument("--port", "-p")
                .type(Integer.class)
                .required(true)
                .help("the listen port for load balancer")
                .choices(new RangeArgumentChoice<>(1, 65535));

        parser.addArgument("--servers", "-s")
                .type(BackendServer.class)
                .required(true)
                .nargs("*")
                .help("the server(s) to be load balance");


        Namespace namespace = null;
        try {
            namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        int port = namespace.getInt("port");
        getLogger().info("Load balancer port: " + port);
        List<BackendServer> servers = namespace.get("servers");
        getLogger().info("Servers: " + servers.stream()
                .map(BackendServer::getAddress)
                .collect(Collectors.joining(", ")));

        try {
            new Server(port, servers);
        } catch (IOException exception) {
            getLogger().warning(String.format("Error when attempt to start server: %s", exception));
        }
    }

    static void readLogConfig() {
        try {
            InputStream stream = Main.class.getClassLoader().
                    getResourceAsStream("logging.properties");
            LogManager.getLogManager().readConfiguration(stream);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return Logger.getLogger(Main.class.getName());
    }
}
