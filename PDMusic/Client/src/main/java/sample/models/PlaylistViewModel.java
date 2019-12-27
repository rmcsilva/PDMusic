package sample.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlaylistViewModel extends RecursiveTreeObject<PlaylistViewModel> {
    private StringProperty name, username;

    public PlaylistViewModel(String name, String username) {
        this.name = new SimpleStringProperty(name);
        this.username = new SimpleStringProperty(username);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public boolean predicate(String text) {
        return getName().contains(text)
                || getUsername().contains(text);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof PlaylistViewModel)) {
            return false;
        }

        PlaylistViewModel playlistViewModel = (PlaylistViewModel) obj;

        return getName().equals(playlistViewModel.getName());
    }
}
