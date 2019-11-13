package sample.exceptions;

public class CountExceededException extends Exception {

    private int counter = 0;
    private int limit;

    public CountExceededException(int limit) {
        super("Count exceeded " + limit);
        this.limit = limit;
    }

    public int getCounter() {
        return counter+1;
    }

    public int getLimit() {
        return limit;
    }

    public void incrementCounter() throws CountExceededException {
        if (counter > limit) throw this;
        ++counter;
    }
}
