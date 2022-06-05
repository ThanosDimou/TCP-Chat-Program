/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author dimou
 */
public class Server extends javax.swing.JFrame {

    Set<String> users = new HashSet();
    List<PrintWriter> writers = new ArrayList<>();
            

    //This will keep a pool of 10 threads, so we can run multiple socket servers.
    Executor executor = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    int communicationPort = 999;   //first and by default we connect to this port
                                   //for first communication

    private String getConnectedUsersString() {
        StringBuilder sb = new StringBuilder();    //for efficiency
        sb.append("Connected users:").append("\n");
        for (String user : users) {
            sb.append(user).append("\n");
        }

        return sb.toString();
    }

    public class Handler implements Runnable {

        private static final String CONNECT_REQUEST = "/connect_request";
        private static final String CONNECT = "/connect";
        private static final String DISCONNECT = "/disconnect";
        private static final String LIST = "/list";
        private static final String MSC = ":"; //Message Separator Character

        Socket socket;
        SocketServer server;
        PrintWriter writer;
        BufferedReader reader;

        public Handler(SocketServer server, Socket clientSocket, PrintWriter writer) {
            this.server = server;
            this.writer = writer;
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                text_area.append("Something is going wrong. \n");
            }

        }

        @Override
        public void run() {
            
            String input;     

            try {
                while ((input = reader.readLine()) != null) {
                    //Our message comes in the format of "user:message"
                    int splitPoint = input.indexOf(MSC);
                    String user = input.substring(0, splitPoint);
                    String message = input.substring(splitPoint + 1);

                    System.out.println(user + " " + message);

                    //if message is a command, treat accordingly
                    if (message.startsWith(CONNECT_REQUEST)) {
                        try {
                            String[] data = message.split(" ");
                            Integer port = null;
                            if (data.length == 2) {
                                port = Integer.parseInt(data[1]);
                            }
                            openSocket(port);
                            reply("OK");
                        } catch (Exception e) {
                            reply("FAIL");
                            System.out.println("Error serving connection request: " + input);
                            e.printStackTrace();
                        }
                    } else if (message.startsWith(CONNECT)) {
                        boolean connected = addUser(user);
                        if (connected) {
                            broadcast((user + " has just connected"));
                        }
                    } else if (message.equals(DISCONNECT)) {
                        boolean disconnected = removeUser(user);
                        if (disconnected) {
                            broadcast((user + " has disconnected"));
                        }
                    } else if (message.equals(LIST)) {
                        reply(getConnectedUsersString());
                    } else {
                        broadcast((user + ": " + message));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                writers.remove(writer);
            }
        }

        public void reply(String message) {
            try {
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                text_area.append("Error sending message: " + ex.getMessage());
            };
        }

        private void openSocket(Integer port) throws IOException {
            executor.execute(new SocketServer(port));
            text_area.append("Started new server at port " + port + "\n");
        }
    }

    /**
     * Creates new form server
     */
    public Server() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        start_server = new javax.swing.JButton();
        close_server = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        text_area = new javax.swing.JTextArea();
        connected_users = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        start_server.setText("Start Server");
        start_server.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                start_serverActionPerformed(evt);
            }
        });

        close_server.setText("Close Server");
        close_server.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_serverActionPerformed(evt);
            }
        });

        jLabel1.setText("SERVER GUI for client/server chat programm");

        text_area.setColumns(20);
        text_area.setRows(5);
        jScrollPane1.setViewportView(text_area);

        connected_users.setText("connected users");
        connected_users.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connected_usersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(start_server)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(close_server))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(179, 179, 179)
                                .addComponent(connected_users))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(122, 122, 122)
                                .addComponent(jLabel1)))
                        .addGap(0, 125, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(close_server)
                    .addComponent(start_server))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(connected_users)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void start_serverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_start_serverActionPerformed
        executor.execute(new SocketServer(communicationPort));
        text_area.append("Server is now online...\n");

    }//GEN-LAST:event_start_serverActionPerformed

    private void close_serverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_serverActionPerformed

        serverClose();

    }//GEN-LAST:event_close_serverActionPerformed

    private void connected_usersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connected_usersActionPerformed
        text_area.append(getConnectedUsersString());
    }//GEN-LAST:event_connected_usersActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

   
    public class SocketServer implements Runnable {

        int port;
        
        SocketServer(int port){
            this.port = port;
        }
        
        @Override
        public void run() {
            startNewSocketServer(port);
        }

        //server start at specific communicationPort
        //TODO: How to close socket servers
        public void startNewSocketServer(int port) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    // The program will wait here until a connection is made.
                    Socket clientSocket = serverSocket.accept(); //server will accept the connection.
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                    writers.add(writer);

                    Thread listener = new Thread(new Handler(this, clientSocket, writer));
                    listener.start();

                    text_area.append("New client connected at port " + port + "\n");
                }
            } catch (Exception ex) {
                text_area.append("Error: " + ex.getMessage() + "\n");
            }
        }
    }

    
    public void serverClose() {
        broadcast(("\nSERVER CLOSED"));

        try {

            Thread.sleep(1000);                 //waiting for 1 second
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        System.exit(0);

    }

//----------------------------------------------------------//
    public boolean addUser(String name) {
        return users.add(name);
    }

    public boolean removeUser(String name) {
        return users.remove(name);
    }

    public void broadcast(String message) {

        for (PrintWriter writer : writers) {
            try {
                writer.println(message);
                writer.flush();
            } catch (Exception ex) {
                text_area.append("Error telling everyone. \n");
            }
        }

        text_area.append("Broadcasting: " + message + "\n");
        text_area.setCaretPosition(text_area.getDocument().getLength());
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton close_server;
    private javax.swing.JButton connected_users;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton start_server;
    private javax.swing.JTextArea text_area;
    // End of variables declaration//GEN-END:variables
}
