package sample.database;

import sample.database.models.Music;
import sample.database.models.Playlist;
import sample.database.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatabaseAccess {

    private final String DATABASE_NAME = "/pdmusic?";

    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public DatabaseAccess(String dbIP, String dbUser, String dbPassword) throws SQLException {
        connectToDatabase(dbIP, dbUser, dbPassword);
    }

    public void connectToDatabase(String dbIP, String dbUser, String dbPassword) throws SQLException {
        String connectionUrl = "jdbc:mysql://";
        connectionUrl += dbIP + DATABASE_NAME;
        connectionUrl += "user=" + dbUser + "&password=" + dbPassword;

        // Setup the connection with the DB
        connection = DriverManager.getConnection(connectionUrl);
    }

    public synchronized boolean hasUsername(String username) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT username FROM users WHERE UPPER(username)=UPPER(?)");
        preparedStatement.setString(1, username);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public synchronized void addUser(User user) throws SQLException {
        preparedStatement = connection
                .prepareStatement("INSERT INTO users (name, username, password) VALUES (?, ?, ?)");
        preparedStatement.setString(1, user.getName());
        preparedStatement.setString(2, user.getUsername());
        preparedStatement.setString(3, user.getPassword());
        preparedStatement.executeUpdate();
    }

    public synchronized User getUser(String username) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE UPPER(username)=UPPER(?)");
        preparedStatement.setString(1, username);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) return null;
        return new User(resultSet.getString("username"), resultSet.getString("password"));
    }

    public synchronized int getUserIDFromUsername(String username) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT user_id FROM users WHERE UPPER(username)=UPPER(?)");
        preparedStatement.setString(1, username);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized int getUserIDFromMusicName(String musicName) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT user_id FROM musics WHERE UPPER(name)=UPPER(?)");
        preparedStatement.setString(1, musicName);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized String getMusicOwner(String musicName) throws SQLException {
        preparedStatement = connection
                .prepareStatement("SELECT username FROM musics m INNER JOIN users u ON m.user_id=u.user_id "
                        + "WHERE UPPER(m.name)=UPPER(?)");
        preparedStatement.setString(1, musicName);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) return null;
        return resultSet.getString(1);
    }

    public synchronized int getMusicID(String musicName) throws SQLException {
        preparedStatement = connection
                .prepareStatement("SELECT music_id FROM musics WHERE UPPER(name)=UPPER(?)");
        preparedStatement.setString(1, musicName);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized List<Music> getMusics() throws SQLException {
        List<Music> musics = new ArrayList<>();

        preparedStatement = connection
                .prepareStatement("SELECT * FROM musics m INNER JOIN users u ON m.user_id=u.user_id");
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String author = resultSet.getString("author");
            String album = resultSet.getString("album");
            int year = resultSet.getInt("year");
            int duration = resultSet.getInt("duration");
            String genre = resultSet.getString("genre");
            String username = resultSet.getString("username");
            String path = resultSet.getString("path");

            musics.add(new Music(name, author, album, year, duration, genre, username, path));
        }

        return musics;
    }

    public synchronized boolean hasMusic(String musicName) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT * FROM musics WHERE UPPER(name)=UPPER(?)");
        preparedStatement.setString(1, musicName);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public synchronized void addMusic(Music music) throws SQLException {
        preparedStatement = connection
                .prepareStatement("INSERT INTO  musics (user_id, name, author, album, year, duration, genre, path)"
                        + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        preparedStatement.setInt(1, music.getUserID());
        preparedStatement.setString(2, music.getName());
        preparedStatement.setString(3, music.getAuthor());
        preparedStatement.setString(4, music.getAlbum());
        preparedStatement.setInt(5, music.getYear());
        preparedStatement.setInt(6, music.getDuration());
        preparedStatement.setString(7, music.getGenre());
        preparedStatement.setString(8, music.getPath());
        preparedStatement.executeUpdate();
    }

    public synchronized void editMusic(String musicToEdit, Music music) throws SQLException {
        preparedStatement = connection
                .prepareStatement("UPDATE musics SET name=?, author=?, album=?, year=?, duration=?, genre=?, path=?"
                        + " WHERE UPPER(name)=?");
        preparedStatement.setString(1, music.getName());
        preparedStatement.setString(2, music.getAuthor());
        preparedStatement.setString(3, music.getAlbum());
        preparedStatement.setInt(4, music.getYear());
        preparedStatement.setInt(5, music.getDuration());
        preparedStatement.setString(6, music.getGenre());
        preparedStatement.setString(7, music.getPath());
        preparedStatement.setString(8, musicToEdit);
        preparedStatement.executeUpdate();
    }

    public synchronized void removeMusic(String musicName) throws SQLException {
        preparedStatement = connection
                .prepareStatement("DELETE FROM musics WHERE UPPER(name)=UPPER(?)");
        preparedStatement.setString(1, musicName);
        preparedStatement.executeUpdate();
    }

    public synchronized boolean hasPlaylist(String playlistName) throws SQLException {
        preparedStatement = connection.prepareStatement("SELECT * FROM playlists WHERE UPPER(name)=UPPER(?)");
        preparedStatement.setString(1, playlistName);
        resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    public synchronized int getPlaylistID(String playlistName) throws SQLException {
        preparedStatement = connection
                .prepareStatement("SELECT playlist_id FROM playlists WHERE UPPER(name)=UPPER(?)");
        preparedStatement.setString(1, playlistName);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }

    public synchronized List<Playlist> getPlaylists() throws SQLException {
        List<Playlist> playlists = new ArrayList<>();

        preparedStatement = connection
                .prepareStatement("SELECT p.name, username FROM playlists p INNER JOIN users u ON p.user_id=u.user_id");
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString(1);
            String userName = resultSet.getString(2);

            playlists.add(new Playlist(name, userName));
        }

        return playlists;
    }

    public synchronized void addPlaylist(Playlist playlist) throws SQLException {
        preparedStatement = connection
                .prepareStatement("INSERT INTO playlists (user_id, name) VALUES (?, ?)");
        preparedStatement.setInt(1, playlist.getUserID());
        preparedStatement.setString(2, playlist.getName());
        preparedStatement.executeUpdate();
    }

    public synchronized void editPlaylist(String playlistToEdit, String playlistName) throws SQLException {
        preparedStatement = connection
                .prepareStatement("UPDATE playlists SET name=? WHERE UPPER(name)=?");
        preparedStatement.setString(1, playlistName);
        preparedStatement.setString(2, playlistToEdit);
        preparedStatement.executeUpdate();
    }

    public synchronized void removePlaylist(String playlistName) throws SQLException {
        preparedStatement = connection
                .prepareStatement("DELETE FROM playlists WHERE UPPER(name)=UPPER(?)");
        preparedStatement.setString(1, playlistName);
        preparedStatement.executeUpdate();
    }

    public synchronized String getPlaylistOwner(String playlistName) throws SQLException {
        preparedStatement = connection
                .prepareStatement("SELECT username FROM playlists p INNER JOIN users u ON p.user_id=u.user_id"
                        + " WHERE UPPER(p.name)=UPPER(?)");
        preparedStatement.setString(1, playlistName);
        resultSet = preparedStatement.executeQuery();
        if (!resultSet.next()) return null;
        return resultSet.getString(1);
    }

    public synchronized Map<String, List<String>> getMusicsInPlaylist() throws SQLException {
        Map<String, List<String>> musicsInPlaylist = new HashMap<>();

        preparedStatement = connection
                .prepareStatement("SELECT m.name, p.name FROM playlist_musics pm " +
                        "INNER JOIN musics m ON m.music_id=pm.music_id " +
                        "INNER JOIN playlists p ON p.playlist_id=pm.playlist_id ");
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String music = resultSet.getString(1);
            String playlist = resultSet.getString(2);

            if (musicsInPlaylist.containsKey(playlist)) {
                musicsInPlaylist.get(playlist).add(music);
            } else {
                List<String> musics = new ArrayList<>();
                musics.add(music);
                musicsInPlaylist.put(playlist, musics);
            }
        }

        return musicsInPlaylist;
    }

    public synchronized void addMusicToPlaylist(int playlistID, int musicID) throws SQLException {
        preparedStatement = connection
                .prepareStatement("INSERT INTO playlist_musics VALUES (?, ?)");
        preparedStatement.setInt(1, playlistID);
        preparedStatement.setInt(2, musicID);
        preparedStatement.executeUpdate();
    }

    public synchronized void removeMusicFromPlaylist(int playlistID, int musicID) throws SQLException {
        preparedStatement = connection
                .prepareStatement("DELETE FROM playlist_musics WHERE playlist_id=? AND music_id=?");
        preparedStatement.setInt(1, playlistID);
        preparedStatement.setInt(2, musicID);
        preparedStatement.executeUpdate();
    }

    public void closeConnection() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }
}
