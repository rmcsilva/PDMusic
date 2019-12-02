package sample.communication;

import org.json.JSONObject;
import sample.Communication;

import java.io.*;
import java.net.Socket;

public class ClientCommunication implements Runnable, Communication {

    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;

    private boolean isRunning = true;

    private JSONObject response;

    private String username = "undefined";

    public ClientCommunication(Socket clientSocket) throws IOException {
        this.socket = clientSocket;
        pw = new PrintWriter(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        String jsonRequest;

        try {
            while (isRunning) {
                jsonRequest = br.readLine();
                //Check null
                JSONObject request = new JSONObject(jsonRequest);

                switch (request.getString(REQUEST)) {
                    case REQUEST_LOGIN:
                        String username = request.getString(USERNAME);
                        String password = request.getString(PASSWORD);

                        System.out.println("Login -> Username: " + username);

                        login(username, password);
                        break;
                    case REQUEST_REGISTER:
                        String name = request.getString(NAME);
                        username = request.getString(USERNAME);
                        password = request.getString(PASSWORD);

                        System.out.println("Register -> Username: " + username + " Name: " + name);

                        register(name, username, password);
                        break;
                    case REQUEST_ADD_MUSIC:
                        String musicName = request.getString(MUSIC_NAME);
                        String author = request.getString(AUTHOR);
                        String album = request.getString(ALBUM);
                        int year = request.getInt(YEAR);
                        int duration = request.getInt(DURATION);
                        String genre = request.getString(GENRE);

                        System.out.println("Add Music -> Username: " + this.username +
                                " MusicName: " + musicName + " Author: " + author + " Year: " + year +
                                " Duration: " + duration + " Genre: " + genre);

                        addMusic(musicName, author, album, year, duration, genre);
                        break;
                    case REQUEST_ADD_PLAYLIST:
                        String playlistName = request.getString(PLAYLIST_NAME);

                        System.out.println("Add Playlist -> Username: " + this.username +
                                " Playlist Name: " + playlistName);

                        addPlaylist(playlistName);
                        break;
                    case REQUEST_LOGOUT:
                        System.out.println("Logout -> Username: " + this.username);

                        logout();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            //TODO: Warn SeversDirectory that Client logged out
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    private synchronized void sendResponse(JSONObject response) {
        pw.println(response.toString());
        pw.flush();
    }

    @Override
    public void login(String username, String password) throws IOException {
        //TODO: Apply restrictions
        this.username = username;

        response = new JSONObject();
        response.put(RESPONSE, REQUEST_LOGIN);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, LOGIN_SUCCESS);
        response.put(USERNAME, username);

        sendResponse(response);
        System.out.println(LOGIN_SUCCESS);
    }

    @Override
    public void register(String name, String username, String password) throws IOException {
        response = new JSONObject();
        response.put(RESPONSE, REQUEST_REGISTER);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, username + " " + REGISTER_SUCCESS);

        sendResponse(response);
        System.out.println(username + REGISTER_SUCCESS);
    }

    @Override
    public void addMusic(String name, String author, String album, int year, int duration, String genre) {
        response = new JSONObject();
        response.put(RESPONSE, REQUEST_ADD_MUSIC);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, ADD_MUSIC_SUCCESS);
        //Put music details
        response.put(USERNAME, username);
        response.put(MUSIC_NAME, name);
        response.put(AUTHOR, author);
        response.put(ALBUM, album);
        response.put(YEAR, year);
        response.put(DURATION, duration);
        response.put(GENRE, genre);

        sendResponse(response);
        System.out.println(ADD_MUSIC_SUCCESS);
    }

    @Override
    public void addPlaylist(String name) {
        response = new JSONObject();
        response.put(RESPONSE, REQUEST_ADD_PLAYLIST);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, ADD_PLAYLIST_SUCCESS);
        //Put playlist details
        response.put(USERNAME, username);
        response.put(PLAYLIST_NAME, name);

        sendResponse(response);
        System.out.println(ADD_PLAYLIST_SUCCESS);
    }

    @Override
    public void logout() {
        response = new JSONObject();
        response.put(RESPONSE, REQUEST_LOGOUT);
        response.put(STATUS, APPROVED);
        response.put(DETAILS, LOGOUT_SUCCESS);

        sendResponse(response);
        System.out.println(LOGOUT_SUCCESS);

        shutdown();
    }

    private void shutdown() {
        isRunning = false;

        if (pw != null) {
            pw.close();
        }

        if (br != null) {
            try {
                br.close();
            } catch (IOException ignored) {
            }
        }

        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}