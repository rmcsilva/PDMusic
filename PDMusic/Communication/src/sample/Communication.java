package sample;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Communication {
    private String ip = "127.0.0.1";
    private int port = 8080;
    private Socket socket;

    public void geraJSON(JSONObject options) throws IOException{
        String jsonString = null;
        try {
            jsonString = options.toString();
            System.out.println(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            socket = new Socket(ip, port);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(jsonString);
        }catch(IOException io){
            System.out.println("Error: " + io.getMessage());
        }
    }
    public void leJSON(){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            String jsonRec = bufferedReader.readLine();
            JSONObject json = new JSONObject(jsonRec);

            System.out.println(json.getString("tipo") +
                    "\n" + json.getString("sucesso") +
                    "\n" + json.getString("mensagem"));

            printWriter.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
