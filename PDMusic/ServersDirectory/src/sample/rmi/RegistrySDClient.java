package sample.rmi;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static sample.rmi.RegistrySDService.REGISTRY_SERVICE_NAME;

public class RegistrySDClient {

    private RegistrySDInterface registrySDService;

    private Scanner in;

    public RegistrySDClient(String sdIP) throws RemoteException, NotBoundException, MalformedURLException {
        setupRegistrySD(sdIP);
        registrySDInterface();
    }

    private void setupRegistrySD(String sdIP) throws RemoteException, NotBoundException, MalformedURLException {
        String objectURL = "rmi://" + sdIP + "/" + REGISTRY_SERVICE_NAME;
        System.out.println("Connecting to " + objectURL);

        registrySDService = (RegistrySDInterface) Naming.lookup(objectURL);
    }

    public void registrySDInterface() throws RemoteException {
        boolean isRunning = true;
        in = new Scanner(System.in);

        final int LIST_SERVERS_INFORMATION = 1;
        final int SHUTDOWN_SERVER = 2;
        final int SHUTDOWN = 3;

        while (isRunning) {

            System.out.println(LIST_SERVERS_INFORMATION + " -> List Servers Information");
            System.out.println(SHUTDOWN_SERVER + " -> Shutdown Server");
            System.out.println(SHUTDOWN + " -> Shutdown");
            System.out.print("Select Option -> ");

            try {
                int option = in.nextInt();

                switch (option) {
                    case LIST_SERVERS_INFORMATION:
                        listServersInformation(registrySDService.listServersInformation());
                        break;
                    case SHUTDOWN_SERVER:
                        shutdownServer();
                        break;
                    case SHUTDOWN:
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid Option!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Option needs to be an Integer!");
            }
        }
    }

    private void listServersInformation(List<String> servers) {
        if (servers.isEmpty()) {
            System.out.println("There are no servers available!");
            return;
        }
        System.out.println("Servers List: <IP>:<TcpPort>");
        for (String server : servers) {
            System.out.println(server);
        }
    }

    private void shutdownServer() throws RemoteException {
        System.out.print("Server IP -> ");
        String ip = in.next().trim();
        try {
            //Check if address is valid
            InetAddress.getByName(ip);

            System.out.print("Server Port -> ");
            int port = in.nextInt();

            if (registrySDService.shutdownServer(ip, port)) {
                System.out.println("Shutdown Server Successfully!");
            } else {
                System.out.println("Could not shutdown request server!");
            }

        } catch (UnknownHostException e) {
            System.out.println("Invalid IP Address!");
        }
    }
}