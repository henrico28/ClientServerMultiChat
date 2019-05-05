import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

/**
 *
 * @author Henrico Leodra & Cristopher
 */

public class MultiChatServer{
    Vector<ClientHandler> clients = new Vector<ClientHandler>();
    Vector<String> people = new Vector<String>();

    public static void main(String ... args) throws Exception{
        new MultiChatServer().process();
    }

    public void process() throws Exception{
        ServerSocket srvr = new ServerSocket(9999, 10);
        out.println("Server Initialized!");
        while(true){
            Socket c = srvr.accept();
            ClientHandler ch = new ClientHandler(c);
            clients.add(ch);
        }
    }

    public void display(String msg, String usr){
        for(ClientHandler ch : clients){
            if(!ch.getUName().equals(usr)){
                ch.transmitMessages(msg, usr);
            }
        }
    }

    class ClientHandler extends Thread{
        BufferedReader in;
        PrintWriter out;
        String name ="";

        public ClientHandler(Socket c) throws Exception{
            in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            out = new PrintWriter(c.getOutputStream(), true);
            name = in.readLine();
            people.add(name);
            start();
        }

        public void transmitMessages(String uname, String message){
            out.println(uname + " : " + message);
        }

        public String getUName(){
            return name;            
        }

        public void run(){
            String tmp;
            try{
                while(true){
                    tmp = in.readLine();
                    if(tmp.equals("end")){
                        clients.remove(this);
                        people.remove(name);
                        break;
                    }
                    display(tmp, name);
                }
            }
            catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}