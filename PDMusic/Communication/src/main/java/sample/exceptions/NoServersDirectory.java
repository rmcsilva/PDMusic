package sample.exceptions;

public class NoServersDirectory extends Exception {
    public NoServersDirectory() {
        super("Servers Directory is not running!");
    }
}
