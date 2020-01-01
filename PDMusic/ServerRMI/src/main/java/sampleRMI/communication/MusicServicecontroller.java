package sampleRMI.communication;

import sampleRMI.CommandController;
import sampleRMI.communication.interfaces.MonitoringInterface;
import sampleRMI.communication.interfaces.ObserverInfoInterface;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class MusicServicecontroller extends UnicastRemoteObject implements ObserverInfoInterface {

    private ArrayList<Remote> workers;
    private String mServerName;
    private String dbName;
    private ServerSocket listeningSocket = null;

    protected MusicServicecontroller(ArrayList<Remote> workers, String mServerName) throws RemoteException {
        this.workers = workers;
        this.mServerName = mServerName;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Sintax: java MusicServiceController <dsIP> <NIC MultiCast>");
            return;
        }

        System.setProperty("java.rmi.hostname", args[0]);

        ArrayList<Remote> workers = new ArrayList<>();
        String serverName = "PDMusic";

        try {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            System.out.println("Registry started at " + Registry.REGISTRY_PORT + " port!");
        } catch (RemoteException e) {
            System.out.println("Remote Error: " + e);
            System.exit(1);
        }

        try {
            MusicServicecontroller musicService = new MusicServicecontroller(workers, serverName);
            System.out.println("Music Service started!");

            //Regista o servico para que os clientes possam encontrá-lo, ou seja encontrar a sua referência remota
            Naming.rebind(serverName, musicService);
            System.out.println("Service bound to " + serverName + "!");

            CommandController commandManager = new CommandController(musicService);
            commandManager.start();
            System.out.println("Command system online!");

            musicService.ServerStart(args);

        } catch (RemoteException e) {
            System.out.println("Remote Error: " + e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error: " + e);
            System.exit(1);
        }
    }

    public void exit() throws RemoteException {
        try {
            //Remove o registo do servidor no registry
            Naming.unbind(mServerName);

            //Retira o nosso serviço do runtime RMI
            UnicastRemoteObject.unexportObject(this, true);

            System.out.println(mServerName + " server going offline.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void setListenner(MonitoringInterface service) throws RemoteException {
        workers.add(service);
        System.out.println("New Monitoring Service added!");
    }

    @Override
    public ArrayList<clientInfoRmi> getUserOnInfo() throws RemoteException {
        return null;
    }

    public void ServerStart(String[] args) throws RemoteException {
        dbName = args[1];

        try {
            listeningSocket = new ServerSocket(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Listening to requests!");

        while (true) {
            Socket s;

            try {
                s = listeningSocket.accept();
                System.out.println("New client connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
