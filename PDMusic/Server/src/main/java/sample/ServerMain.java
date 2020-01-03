package sample;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import sample.database.DatabaseAccess;
import sample.exceptions.NoServersDirectory;

import java.io.IOException;
import java.sql.SQLException;

@SpringBootApplication
public class ServerMain {

    public static void main(String[] args) throws NoServersDirectory, IOException, SQLException {
        System.out.println("Server main!");

        if (args.length != 5) {
            System.out.println("Sintaxe: java Server <sdIP> <dbIP> <dbUsername> <dbPassword> <NIC Multicast>");
            return;
        }

        DatabaseAccess databaseAccess = new DatabaseAccess(args[1], args[2], args[3]);

        ServerController serverController = new ServerController(args[0], args[4], databaseAccess);
        serverController.start();
    }
}
