package sample;

import java.io.IOException;

public interface Communication extends JSONConstants {

    void login(String username, String password) throws IOException;
    void register(String name, String username,String password) throws IOException;
    void addMusic(String name, String author, String album, int year, int duration, String genre);
    void addPlaylist(String name);
    void logout();

}
