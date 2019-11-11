package sample;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class echoThread extends Thread {
    private Socket socket;

    echoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        String jsonRec;
        BufferedReader br;
        try {
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while ((jsonRec = br.readLine()) != null) {
                JSONObject json = new JSONObject(jsonRec);
                String message;

                switch (json.getString("tipo")) {
                    case "login":
                        System.out.println(json.getString("tipo") +
                                "\n" + json.getString("username") +
                                "\n" + json.getString("password") + "\n");

                        json = new JSONObject();
                        json.put("tipo", "resposta");
                        json.put("sucesso", "sim");
                        json.put("mensagem", "Autenticação realizada com sucesso!\n");
                        message = json.toString();
                        pw.println(message);
                        break;
                    case "newMusic":
                        System.out.println(json.getString("tipo") +
                                "\n" + json.getString("artist") +
                                "\n" + json.getString("name") + "\n");

                        json = new JSONObject();
                        json.put("tipo", "resposta");
                        json.put("sucesso", "sim");
                        json.put("mensagem", "Música Adicionada com sucesso!\n");
                        message = json.toString();
                        pw.println(message);
                        System.out.println("Adicionada com sucesso!\n");
                        break;
                    case "newPlaylist":
                        json = new JSONObject();
                        json.put("tipo", "resposta");
                        json.put("sucesso", "sim");
                        json.put("mensagem", "Playlist adicionada com sucesso!\n");
                        message = json.toString();
                        pw.println(message);
                        System.out.println("Playlist adicionada com sucesso!\n");
                        break;
                    case "logout":
                        System.out.println(json.getString("tipo") +
                                "\n" + json.getString("username") + "\n");

                        json = new JSONObject();
                        json.put("tipo", "resposta");
                        json.put("sucesso", "sim");
                        json.put("mensagem", "Logout com sucesso!\n");
                        message = json.toString();
                        pw.println(message);
                        System.out.println("Logout com sucesso!\n");
                        break;
                    case "register":
                        System.out.println(json.getString("tipo") +
                                "\n" + json.getString("username") + "\n");
                        String user = json.getString("username");

                        json = new JSONObject();
                        json.put("tipo", "resposta");
                        json.put("sucesso", "sim");
                        json.put("mensagem", "User: " + user + " registado com sucesso!\n");
                        message = json.toString();
                        pw.println(message);
                        System.out.println("User: " + user + " registado com sucesso!\n");
                        break;
                    default:
                        break;
                }
            }
            pw.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}