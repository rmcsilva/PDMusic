package sample.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RegistrySDClientMain {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Sintaxe: java RegistrySDClient <sdIP>");
            return;
        }

        RegistrySDClient registrySDClient = null;

        try {
            registrySDClient = new RegistrySDClient();
            registrySDClient.setupRegistrySD(args[0]);
            registrySDClient.registrySDInterface();
        }  catch (RemoteException e) {
            System.out.println("Remote Error -> " + e);
        } catch (NotBoundException e) {
            System.out.println("Unknown Remote Service -> " + e);
        } catch (Exception e) {
            System.out.println("Error -> " + e);
        } finally {
            if (registrySDClient != null) {
                registrySDClient.shutdown();
            }
        }
    }

}
