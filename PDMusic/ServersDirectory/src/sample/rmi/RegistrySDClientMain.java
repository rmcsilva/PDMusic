package sample.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RegistrySDClientMain {

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Sintaxe: java RegistrySDClient <sdIP>");
            return;
        }

        try {
            RegistrySDClient client = new RegistrySDClient(args[0]);
        } catch (RemoteException e) {
            System.out.println("Remote Error -> " + e);
        } catch (NotBoundException e) {
            System.out.println("Unknown Remote Service -> " + e);
        } catch (Exception e) {
            System.out.println("Error -> " + e);
        }

    }

}
