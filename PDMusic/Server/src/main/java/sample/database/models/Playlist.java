package sample.database.models;

public class Playlist {
    int userID;
    String name, username;

    public Playlist(String name, String username) {
        this.name = name;
        this.username = username;
    }

    public Playlist(int userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "userID=" + userID +
                ", name='" + name + '\'' +
                '}';
    }
}
