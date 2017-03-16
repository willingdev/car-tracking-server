package com.willingdev;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by phanlop on 3/15/2017 AD.
 */
public class ServerDataFlowHandler implements Runnable{

    private InputStream in;
    private OutputStream out;
    private int readDelay = 300;
    private Socket socket;
    private Thread T;
    private Vector<ByteVector> buffer = new Vector<ByteVector>();
    private long timeOut = 4000;
    private long lastReceived = 0;


    public ServerDataFlowHandler(Socket socket)
    {
        this.socket = socket;
        init();
    }

    private void init()
    {
        try
        {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e)
        {
        }
    }
    public Socket getSocket()
    {
        return socket;
    }
    public ByteVector read()
    {
        if (buffer.size() <= 0)
            return null;
        return buffer.remove(0);
    }
    public void run()
    {
        int available;
        long currentTime;
        lastReceived = System.currentTimeMillis();

        while (T != null)
        {
            currentTime = System.currentTimeMillis();
            if(currentTime-lastReceived>timeOut){
                closeSocket();
                break;

            }
            try
            {
                available = in.available();
                if (available <= 0)
                {
                    try
                    {
                        Thread.sleep(readDelay);
                    } catch (InterruptedException e)
                    {
                    }
                    continue;
                }
                byte[] b = new byte[available];
                in.read(b);
                buffer.add(new ByteVector(b));
                response();

                try
                {
                    Thread.sleep(1);
                } catch (InterruptedException e)
                {
                }
            } catch (Exception ex)
            {

                closeSocket();
            }

        }
    }

    private void response() {
        try {
            out.write(new ByteVector("OK").toByteArray());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket()
    {

        try
        {
            in.close();
        } catch (Exception e)
        {
        }
        try
        {
            out.close();
        } catch (Exception e)
        {
        }
        in = null;
        out = null;
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket = null;

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        stop();
        buffer.clear();
        buffer = null;
        if(socket != null){
            try
            {
                socket.close();
            } catch (IOException e)
            {
            }
            socket = null;
        }
    }
    public void setReadDelay(int readDelay)
    {
        this.readDelay = readDelay;
    }

    public boolean start()
    {
        if (socket == null || T != null)
            return false;
        if (in == null || out == null)
            init();

        T = new Thread(this);
        T.start();
        return true;
    }
    public boolean stop()
    {
        if (T == null)
            return false;
        Thread tt = T;
        T = null;
        while (tt.isAlive())
        {
            try
            {
                Thread.sleep(50);
            } catch (InterruptedException e)
            {
            }
        }
        closeSocket();
        return true;
    }
}
