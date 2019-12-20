package sample.communication;

import org.json.JSONObject;
import sample.models.ServerInformation;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class MulticastResponseHandler extends Thread {

    private final int waitSeconds = 10;

    private ServerCommunication serverCommunication;

    private int requestID;

    private HashSet<ServerInformation> receivedServers;

    private boolean isRunning = true;

    private JSONObject request;

    public MulticastResponseHandler(ServerCommunication serverCommunication, int requestID, JSONObject request) {
        this.serverCommunication = serverCommunication;
        this.requestID = requestID;
        this.request = request;
        receivedServers = new HashSet<>();
        setDaemon(true);
    }

    public int getRequestID() {
        return requestID;
    }

    @Override
    public void run() {
        while (isRunning) {
            receivedServers.clear();
            serverCommunication.sendNotificationToAllServers(request);
            System.out.println("Sent Multicast Request ID -> " + requestID + "\n");
            try {
                TimeUnit.SECONDS.sleep(waitSeconds);
            } catch (InterruptedException e) {
                System.out.println("Received all responses for multicast request ID -> " + requestID + "\n");
            }
        }
    }

    public synchronized void addResponseFromServer(ServerInformation server) {
        receivedServers.add(server);
        //Check if server got all the responses
        if (receivedServers.equals(serverCommunication.getServers())) {
            isRunning = false;
            interrupt();
            serverCommunication.removeMulticastResponseHandler(this);
        }
    }

}
