package sample.springboot.dto;

public class MusicDto {
    private String name;
    private String author;
    private String album;
    private int year;
    private int duration;
    private String genre;
    private String username;

    public MusicDto(String name, String author, String album, int year, int duration, String genre, String username) {
        this.name = name;
        this.author = author;
        this.album = album;
        this.year = year;
        this.duration = duration;
        this.genre = genre;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
