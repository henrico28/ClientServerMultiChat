import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

/**
 *
 * @author Henrico Leodra & Cristopher
 */

public class Server {
    private Vector<ClientHandler> clients = new Vector<>();
    private HashSet<String> people = new HashSet<>();

    public static void main(String[] args) {
        new Server().process();
    }

    private void process() {
        try {
            ServerSocket server = new ServerSocket(6969);
            out.println("Server dibuka pada localhost dengan port 6969");
            while (true) {
                Socket sock = server.accept();
                ClientHandler ch = new ClientHandler(sock);
                clients.add(ch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void display(String message, String user) {
        for (ClientHandler ch : clients) {
            ch.transmitMessages(user, message);
        }
    }

    private void broadcastEnter(String username) {
        for (ClientHandler ch: clients) {
            ch.transmitAnnouncement("-- " + username + " bergabung dalam chatroom --");
        }
    }

    private void broadcastLeave(String username) {
        for (ClientHandler ch: clients) {
            ch.transmitAnnouncement("-- " + username + " meninggalkan chatroom --");
        }
    }

    class ClientHandler extends Thread {
        BufferedReader in;
        PrintWriter out;
        String name;
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        ClientHandler(Socket c) throws Exception {
            in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            out = new PrintWriter(c.getOutputStream(), true);
            name = in.readLine();
            if (people.contains(name)) {
                out.println("used");
                in.close();
            } else {
                out.println("unused");
                broadcastEnter(name);
                people.add(name);
                start();
            }
        }

        void transmitMessages(String username, String message) {
            out.println("(" + dtf.format(LocalDateTime.now()) + ")" + "<" + username + "> : "  + message);
        }

        void transmitAnnouncement(String anno) {
            out.println(anno);
        }

        @Override
        public void run() {
            String tmp;

            try {
                while (true) {
                    tmp = in.readLine();
                    if (tmp.equals("end")) {
                        broadcastLeave(name);
                        clients.remove(this);
                        people.remove(name);
                        break;
                    }
                    display(tmp, name);
                }
            } catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }
    }
}