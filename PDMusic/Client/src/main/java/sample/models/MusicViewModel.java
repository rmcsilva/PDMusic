package sample.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MusicViewModel extends RecursiveTreeObject<MusicViewModel> {
    private StringProperty musicName, author, album, genre, username;
    private IntegerProperty year, duration;

    public MusicViewModel(String musicName, String author, String album, String genre, int year, int duration, String username) {
        this.musicName = new SimpleStringProperty(musicName);
        this.author = new SimpleStringProperty(author);
        this.album = new SimpleStringProperty(album);
        this.genre = new SimpleStringProperty(genre);
        this.year = new SimpleIntegerProperty(year);
        this.duration = new SimpleIntegerProperty(duration);
        this.username = new SimpleStringProperty(username);
    }

    public String getMusicName() {
        return musicName.get();
    }

    public StringProperty musicNameProperty() {
        return musicName;
    }

    public String getAuthor() {
        return author.get();
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getAlbum() {
        return album.get();
    }

    public StringProperty albumProperty() {
        return album;
    }

    public String getGenre() {
        return genre.get();
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public int getYear() {
        return year.get();
    }

    public IntegerProperty yearProperty() {
        return year;
    }

    public int getDuration() {
        return duration.get();
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }
}
