package com.furkanaxx34.gmail.loadbalancer;

import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {

    private final ServerSocket socket;
    private final List<BackendServer> servers;

    public Server(int port, List<BackendServer> servers) throws IOException {
        this.socket = new ServerSocket(port);
        Main.getLogger().info(String.format("Listening port %s", port));
        this.servers = servers;
        while (true) {
            Socket accept = this.socket.accept();
            new ClientHandler(accept).start();
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }

    @RequiredArgsConstructor
    final class ClientHandler extends Thread {

        private final Socket socket;

        @Override
        public void run() {
            try {
                List<BackendServer> servers = Server.this.servers;
                BackendServer backend = servers.get((int) ((System.nanoTime() / 100) % servers.size()));
                Socket backendServer = new Socket(backend.getIp(), backend.getPort());

                Thread inputThread = new Thread(() -> {
                    try {
                        ByteStreams.copy(this.socket.getInputStream(), backendServer.getOutputStream());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                Thread outputThread = new Thread(() -> {
                    try {
                        ByteStreams.copy(backendServer.getInputStream(), this.socket.getOutputStream());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                inputThread.start();
                outputThread.start();
            } catch (IOException ex) {
                Main.getLogger().warning(String.format("Error when handle client: %s", ex));
            }
        }
    }
}
