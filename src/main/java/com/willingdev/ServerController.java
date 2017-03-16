package com.willingdev;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

/**
 * Created by phanlop on 3/15/2017 AD.
 */
public class ServerController implements Runnable {
    private int port;
    private ServerConnectionHandler serverConnection;
    private ServerSocket serverSocket;
    private int readDelay = 300;
    protected Thread T;
    protected Vector<ByteVector> buffer = new Vector<ByteVector>();

    public ServerController(int port) {
        this.port = port;
        init();
        start();
    }

    private void init() {

        try {
            serverSocket = new ServerSocket(port);
            serverConnection = new ServerConnectionHandler(serverSocket);
        } catch (IOException e) {
        }
    }

    public boolean start() {
        if (serverSocket == null && serverConnection == null)
            init();

        if (T != null)
            return false;
        T = new Thread(this);
        T.start();
        serverConnection.start();
        return true;
    }

    public int getReadDelay() {
        return readDelay;
    }

    public void write(ByteVector data){

    }
    public void run() {
        Vector<ServerDataFlowHandler> outOfDate = new Vector<ServerDataFlowHandler>();
        ByteVector data = null;
        while (T != null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            Vector<ServerDataFlowHandler> client = serverConnection.getClient();
            synchronized (client) {
                ServerDataFlowHandler serverTalk;
                for (int i = 0; i < client.size(); i++) {
                    serverTalk = client.get(i);
                    serverTalk.setReadDelay(getReadDelay());
                    if (serverTalk.getSocket() == null) {
                        outOfDate.add(serverTalk);
                        continue;
                    }

                    data = serverTalk.read();

                    if (data != null) {
                        buffer.add(data);
                    }
                }
                for (int i = 0; i < outOfDate.size(); i++)
                    client.remove(outOfDate.elementAt(i));
            }
            outOfDate.clear();
        }
    }
    public ByteVector read()
    {
        if (buffer.size() <= 0)
            return null;
        return buffer.remove(0);
    }
}