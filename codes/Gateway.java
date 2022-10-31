/*
 E/17/379
 Gateway class to descover all the vital monitors
*/

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Gateway {
    //Array list to save monitorIDS
    static List<String> monitorIDs = new ArrayList<String>();

    public static void main(String[] args) {
        //UDP packet receiving port
        int BROADCAST_PORT = 6000;
        //byte array for receiving packets
        byte[] data = new byte[1024];
        
        try {
            //creating a datagramsocket with the broadcast port
            DatagramSocket datagramsock = new DatagramSocket(BROADCAST_PORT);
            // creates a new datagram packet to receive data from the UDP socket
            DatagramPacket packet = new DatagramPacket(data, data.length);
            System.out.println("Waiting for a Vital Monitor to connect...");
            try {
                while (true) {
                    // process will wait here until it gets a new UDP packet
                    datagramsock.receive(packet);
                    //convert the incoming data to a monitor object
                    Monitor monitor = convertToMonitorObject(packet.getData());
                    // check if the monitor is already in the list
                    if (!monitorIDs.contains(monitor.getMonitorID())) {
                        monitorIDs.add(monitor.getMonitorID());
                        System.out.println("UDP BROADCAST RECEIVED! " + monitor.monitor_str());
                        //build a thread with incoming monitor data
                        buildThreads(monitor);
                        
                    }
                }
            //Handling Input/output exeptions of datagram receiving
            } catch (IOException e) {
                e.printStackTrace();
            //after getting the wanted monitor data close the datagram socket
            } finally {
                datagramsock.close();
            }
        //handing exeptions of binding with the port
        } catch (BindException e) {
            e.printStackTrace();
        //handing exeptions of closing socket
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    //creating a thread with the monitor object data
    public static void buildThreads  (Monitor monitor){
        EventHandler eventHandler = new EventHandler(monitor);
        Thread eventHandlerThread = new Thread(eventHandler);
        eventHandlerThread.start();
    }
    //function will called when a threa is closed 
    //class is synchronized so multiple threads can acces this at the same time
    public static synchronized void removeMonitorIDfromList(String monitorid) {
        try {
            monitorIDs.remove(monitorid);
            System.out.println("Removed monitor from list --> " + monitorid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //convert a byte array object to a Monitor object
    private static Monitor convertToMonitorObject(byte[] data) {
        Monitor monitor = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = null;
            ois = new ObjectInputStream(bis);
            monitor = (Monitor) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return monitor;
    }
}
