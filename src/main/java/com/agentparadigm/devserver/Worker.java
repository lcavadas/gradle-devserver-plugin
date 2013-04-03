package com.agentparadigm.devserver;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

class Worker implements HttpConstants, Runnable {

    private BlockingQueue<Socket> requests;
    private int timeout;
    private static Map<String, String> map = new HashMap<String, String>() {{
        put(".zip", "application/zip");
        put(".gif", "image/gif");
        put(".jpg", "image/jpeg");
        put(".png", "image/png");
        put(".jpg", "image/jpeg");
        put(".jpeg", "image/jpeg");
        put(".htm", "text/html");
        put(".html", "text/html");
        put(".text", "text/plain");
        put(".txt", "text/plain");
        put(".js", "text/javascript");
        put(".css", "text/css");
    }};
    private final String root;

    Worker(BlockingQueue<Socket> requests, String root, int timeout) {
        this.requests = requests;
        this.timeout = timeout;
        this.root = root;
    }

    public synchronized void run() {
        while (true) {
            try {
                Socket socket = requests.poll(timeout, TimeUnit.MILLISECONDS);
                if (socket != null)
                    handleClient(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void handleClient(Socket socket) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintStream ps = new PrintStream(socket.getOutputStream());

        socket.setSoTimeout(timeout);
        socket.setTcpNoDelay(true);

        try {
            StringBuilder request = new StringBuilder();

            String line = is.readLine();
            String method = line.substring(0, line.indexOf(" "));
            String file = line.substring(line.indexOf(" ") + 1, line.indexOf(" ", line.indexOf(" ") + 1));

            while (!(line = is.readLine()).equalsIgnoreCase("")) {
                request.append(line);
            }

            if (method.equals("GET")) {
                send(HTTP_OK, ps, file);
            } else {
                send(HTTP_UNSUPPORTED_TYPE, ps, null);
            }
        } finally {
            socket.close();
        }
    }

    void send(int httpResponse, PrintStream out, String requestedFile) throws IOException {
        if (requestedFile == null) {
            out.println("HTTP/1.1 " + httpResponse + " OK");
            out.println("Date: Fri, 31 Dec 1999 23:59:59 GMT");
            out.println("Server: Apache/0.8.4");
            out.println("Expires: Sat, 01 Jan 2000 00:59:59 GMT");
            out.println("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT");
        } else {
            File file = new File(root + "/" + requestedFile);
            if (!file.exists() || file.isDirectory()) {
                send(HTTP_NOT_FOUND, out, null);
            } else {
                BufferedReader in = new BufferedReader(new FileReader(file));
                StringBuilder str = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    str.append(line).append("\r\n");
                }
                in.close();
                byte[] bytes = str.toString().getBytes();

                System.out.println("Serving " + file.getAbsolutePath());
                out.println("HTTP/1.1 " + httpResponse + " OK");
                out.println("Date: Fri, 31 Dec 1999 23:59:59 GMT");
                out.println("Server: lcavadas-simple/0.0.1");
                out.println("Content-Type: " + map.get(requestedFile.substring(requestedFile.lastIndexOf("."))));
                out.println("Content-Length: " + bytes.length);
                out.println("Expires: Sat, 01 Jan 2000 00:59:59 GMT");
                out.println("Last-modified: Fri, 09 Aug 1996 14:21:40 GMT");
                out.println("");
                out.write(bytes);
                out.println("");
                out.flush();
            }
        }
    }
}