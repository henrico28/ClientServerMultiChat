import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Client extends JFrame implements ActionListener {
    private String username;
    private PrintWriter pw;
    private BufferedReader br;
    private JTextArea messages;
    private JTextField input;
    private JButton send;
    private JButton exit;
    private Socket client;

    public Client(String servername) throws Exception {
        super("Chatroom - localhost");
        while (true) {
            String name = JOptionPane.showInputDialog(null,"Username : ", "Join Chatroom",
                    JOptionPane.PLAIN_MESSAGE);
            this.client  = new Socket(servername,6969);
            this.br = new BufferedReader( new InputStreamReader( client.getInputStream()) ) ;
            this.pw = new PrintWriter(client.getOutputStream(),true);
            this.pw.println(name);
            String resp = br.readLine();
            if (!resp.equals("used")) {
                this.username = name;
                break;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Username sudah dipakai, silahkan pilih nama lain",
                        "Error!",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        this.buildInterface();

        Thread messageThread = new MessagesThread();
        messageThread.start();
    }

    public void buildInterface() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        send = new JButton("Send");
        exit = new JButton("Exit");
        messages = new JTextArea();
        messages.setRows(10);
        messages.setColumns(50);
        messages.setEditable(false);
        messages.append("-- Berhasil bergabung kedalam chatroom pada " + dtf.format(LocalDateTime.now()) + " --\n");
        input = new JTextField(50);
        JScrollPane sp = new JScrollPane(messages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(sp,"Center");
        JPanel bp = new JPanel( new FlowLayout());
        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    send.doClick();
                }
            }
        });
        bp.add(input);
        bp.add(send);
        bp.add(exit);
        add(bp,"South");
        send.addActionListener(this);
        exit.addActionListener(this);
        setSize(500,300);
        setVisible(true);
        pack();
    }

    public void actionPerformed(ActionEvent evt) {
        if ( evt.getSource() == exit ) {
            pw.println("end");
            System.exit(1);
        } else {
            pw.println(input.getText());
            input.setText("");
        }
    }

    public static void main(String[] args) {
        try {
            new Client("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MessagesThread extends Thread {
        public void run() {
            String msg;
            try {
                while(true) {
                    try {
                        msg = br.readLine();
                        messages.append(msg + "\n");
                    } catch (SocketException e) {
                        messages.append("-- Koneksi dengan server terputus, harap membuka ulang aplikasi\n");
                        input.setEditable(false);
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}