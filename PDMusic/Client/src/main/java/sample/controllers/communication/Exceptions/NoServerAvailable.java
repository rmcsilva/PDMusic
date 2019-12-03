package sample.controllers.communication.Exceptions;

public class NoServerAvailable extends Exception {
    public NoServerAvailable() {
        super("No available servers!");
    }
}
