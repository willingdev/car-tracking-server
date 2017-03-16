package com.willingdev;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by phanlop on 3/15/2017 AD.
 */
public class ClientTest {
    public static void  main(String[] args) throws IOException {
        Socket echoSocket = new Socket("127.0.0.1", 8888);
        DataOutputStream outToServer = new DataOutputStream(echoSocket.getOutputStream());
        outToServer.writeBytes("Hellow\n");
        outToServer.flush();
        echoSocket.close();
    }
}
