package com.willingdev;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by phanlop on 3/15/2017 AD.
 */
public class ServerConnectionHandler implements Runnable{
    private ServerSocket serverSocket;
    private Thread T;
    private Vector<ServerDataFlowHandler> acceptList = new Vector<ServerDataFlowHandler>();
    private boolean autoAcceptConn = true ;
    public ServerConnectionHandler(ServerSocket serverSocket){
        this.serverSocket = serverSocket;

    }
    public synchronized Vector<ServerDataFlowHandler> getClient()
    {
        return acceptList;
    }
    public void run()
    {
        while (T != null) {
            try {
                Socket socket = serverSocket.accept();
                if (socket != null) {
                    if(autoAcceptConn){
                        addAndInvoleNewConnection(socket);
                    }else{
                   //     involveAndWaitNewConnection(socket);
                    }
                }
            } catch (IOException ex) {
            }
        }
    }
    public boolean start() {
        if (serverSocket == null || T != null)
            return false;
        T = new Thread(this);
        T.start();
        for (int i = 0; i < acceptList.size(); i++)
            acceptList.elementAt(i).start();
        return true;
    }


    private void addAndInvoleNewConnection(Socket socket) {
        if(socket == null)
            return;
        ServerDataFlowHandler st = new ServerDataFlowHandler(socket);

        synchronized (acceptList)
        {
            acceptList.add(st);
        }
        st.start();
    }
}
