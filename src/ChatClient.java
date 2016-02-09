import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;




/**
 * Created by martingabrielsson on 2016-02-04.
 */
public class ChatClient extends Main {



    private Socket socket;
    public PrintWriter out;
    public BufferedReader in;
    private String nickName;

    public ChatClient(){

    }


    public boolean connect(String nick){
        this.nickName = nick;
        try {
            socket = new Socket("chat.linkura.se", 1337);
            out = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            socket.setKeepAlive(true);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            return false;
        } catch (IOException e) {
            System.out.println("I/O Exception");
            return false;
        }

        ((Runnable) () -> {



            Runnable callReceive = () -> receive();

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(callReceive, 0, 10, TimeUnit.SECONDS);

        }).run();
        return true;
    }

    public void receive() {
        try {
            String t = "";
            while ((t = in.readLine()) != null) {
                String trimmed = "";

                if(Objects.equals(t, "NICK?")){
                    out.write("NICK " + nickName + "\r\n");
                    out.flush();
                }else if(t == "PING"){

                }
                else {

                    if(t.contains(" ")){
                        trimmed = t.substring(0, t.indexOf(" "));
                    }else{
                        trimmed = t;
                    }
                    String result = t.substring(trimmed.length()+1, t.length());

                    Date d1 = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm");
                    String timeStamp = df.format(d1);
                    System.out.println("OUTPUT: " + t);
                    switch (trimmed) {
                        case "ERROR":
                            if(t.equals("ERROR nick already taken")){
                                disconnect();
                                Platform.runLater(new DialogHelper("Nickname already taken"));
                            }
                            break;
                        case "JOINED":
                            users.add(result);
                            userList.setItems(users);
                            messages.add("<COLOR:green><"+timeStamp + "> " + result  + " joined the server.");
                            break;
                        case "QUIT":
                            users.remove(result);
                            userList.setItems(users);
                            messages.add("<COLOR:red><"+timeStamp + "> " + result + " left the server.");
                            break;
                        case "MESSAGE":
                            messages.add("<COLOR:black><"+timeStamp + "> "  + result);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
        }
    }

    public void send(String message){
        try{
            out.write(message + "\r\n");
            out.flush();
        }catch (Exception e){
            System.out.println("Exception caught sending message: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            out.close();
            in.close();
            socket.close();
            messages.clear();
            messageList.setItems(messages);
            users.clear();
            userList.setItems(users);
            connected = false;
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());

        }
    }
}
