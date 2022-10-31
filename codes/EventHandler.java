/*
  E/17/379
  S.P.D.D.S Weerasinghe
  This class is to create a run() method to build TCP thread connections using the passed data from Monitor objects
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

// implementing the run() method inherited from the Runnable interface
public class EventHandler implements Runnable {
    private Monitor monitor;

    public EventHandler(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            //for a tcp connection VitalMonitor will act as a server
            Socket serverSocket = new Socket(monitor.getIp(), monitor.getPort());
            BufferedReader br = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            System.out.println("TCP Connection Establised! " + monitor.monitor_str());
            while (true) {
                String message = br.readLine();
                //With this a Monitor can close the TCP connection without closing the program (by sending the string "end")
                if (message.equals("end")){
                    //Socket will be romoved and program will jump to SocketException 
                    serverSocket.close();
                }
                // "Hello from Vital Monitor" message will be printed
                System.out.println(message);
            }
        //Will Handle Connection Exception
        } catch (ConnectException e) {
            e.printStackTrace();
        //whenever a Vitalmonitor thread is terminated this Exception will run
        } catch (SocketException e) {
            System.out.println("Disconnected! " + monitor.monitor_str());
            //Monitor ID will be removed from the list
            Gateway.removeMonitorIDfromList(monitor.getMonitorID());
        //handle input output exceptions ( Port already in use)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
