package com.agentparadigm.devserver;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DevServer implements HttpConstants {

    private List<Worker> threads = new ArrayList<Worker>();
    private final BlockingQueue<Socket> requests = new ArrayBlockingQueue<Socket>(1000);

    public DevServer() throws IOException {
        this(System.getProperty("user.dir"));
    }

    public DevServer(String rootPath) throws IOException {
        this(8080, rootPath, 5, 5000);
    }

    public DevServer(int port, String rootPath, int workers, int timeout) throws IOException {
        File root = new File(rootPath);

        if (!root.exists()) {
            throw new Error(root + " doesn't exist as server root");
        }
        System.out.println("Serving files from " + rootPath);

        for (int i = 0; i < workers; ++i) {
            Worker w = new Worker(requests, rootPath, timeout);
            (new Thread(w, "worker #" + i)).start();
            threads.add(w);
        }

        ServerSocket ss = new ServerSocket(port);
        while (true) {
            requests.offer(ss.accept());
        }
    }


    public static void main(String[] a) throws Exception {
        new DevServer();
    }
}
