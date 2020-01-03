package sample.database.models;

public class Music {
    private String name, author, album, genre, username, path;
    private int userID, year, duration;

    public Music(String name, String author, String album, int year, int duration, String genre) {
        this.name = name;
        this.author = author;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.duration = duration;
    }

    public Music(String name, String author, String album, int year, int duration, String genre, String username, String path) {
        this.name = name;
        this.author = author;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.duration = duration;
        this.username = username;
        this.path = path;
    }

    public Music(int userID, String name, String author, String album, int year, int duration, String genre, String path) {
        this.userID = userID;
        this.name = name;
        this.author = author;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.duration = duration;
        this.path = path;
    }

    public int getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getUsername() {
        return username;
    }

    public String getPath() {
        return path;
    }

    public int getYear() {
        return year;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "Music{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", album='" + album + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                ", genre='" + genre + '\'' +
                ", username='" + username + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
