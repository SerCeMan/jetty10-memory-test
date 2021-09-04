package me.serce;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

    public static void main(String[] args) throws Exception {
        var server = new Server();
        var handler = new ServletContextHandler();
        handler.addServlet(new ServletHolder(new WebSocketServlet()), "/ws");
        server.insertHandler(handler);
        var connector = new ServerConnector(server);
        connector.setPort(8083);
        server.addConnector(connector);
        server.start();
        server.join();
    }
}
