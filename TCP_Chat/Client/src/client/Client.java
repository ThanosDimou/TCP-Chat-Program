/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.sun.glass.events.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author dimou
 */
public class Client extends javax.swing.JFrame {

    private static final String CONNECT_REQUEST = "/connect_request";
    private static final String CONNECT = "/connect";
    private static final String DISCONNECT = "/disconnect";
    private static final String MSC = ":"; //Message Separator Character

    String username, address = "127.0.0.1"; //default address
    ArrayList<String> users = new ArrayList();
    int communicationPort = 999;    
    Boolean isConnected = false;

    Socket socket;
    BufferedReader reader;
    PrintWriter writer;

    
    public void ListenThread() {
        Thread IncomingReader = new Thread(new Reader());
        IncomingReader.start();
    }

    
    public void addUser(String data) {
        users.add(data);
    }

    
    public void removeUser(String data) {
        text_area.append(data + " is  offline.\n");

    }

    public void writeUsers() {
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
        for (String token : tempList) {
        }

    }

    // close te socket and announce disconection mode
    public void disconnect() {
        try {
            //the last thing we will do, is let the server know we are disconnecting
            send(DISCONNECT);
            socket.close();
            text_area.append("Now you are disconnected.\n");
        } catch (Exception ex) {
            text_area.append("Disconnection failed.\n");
            ex.printStackTrace();
        }
        isConnected = false;
        nickname_field.setEditable(true);
    }

    public void send_message(){
        if (!send_field.getText().isEmpty()) {
            try {
                writer.println(username + ":" + send_field.getText()); //write the message 
                writer.flush(); // flushes the buffer
            } catch (Exception ex) {
                text_area.append("Message was not sent. \n");   //if message didn't delivered 
            }
        }

        send_field.setText("");
        send_field.requestFocus();
    }
    
    public void send(String message) {
        writer.println(constructMessage(message));
        writer.flush();
    }
    
    public String constructMessage(String message){
        return username + MSC + message;
    }

    
    public Client() {   //new form for client
        initComponents();
    }

    
    public class Reader implements Runnable {

        @Override
        public void run() {
            String stream;
            try {
                while ((stream = reader.readLine()) != null) //read the stream and take action
                {
                    text_area.append(stream);
                    text_area.append("\n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void startNewConnection(String username){
        if (isConnected == false) {
            
            nickname_field.setEditable(false);

            try {
                //first send a connection request so that the server can open
                //the requested port
                Socket comSocket = new Socket(address, communicationPort);
                int requestedPort = Integer.parseInt(port_field.getText());
                BufferedReader comReader = new BufferedReader(new InputStreamReader(comSocket.getInputStream()));
                PrintWriter comWriter = new PrintWriter(comSocket.getOutputStream());
                comWriter.println(constructMessage(CONNECT_REQUEST + " " + requestedPort));
                comWriter.flush();

                while (true) { //wait for any response from the server
                    String result = comReader.readLine();
                    if ("OK".equals(result)) {
                        break;
                    } else if ("FAIL".equals(result)) {
                        throw new IllegalStateException("Server failed to open the requested port.");
                    } else{
                        System.out.println("reading "+result);
                    }
                    sleep(500);
                    //TODO: There is no timeout, might get stuck on loop
                }
                //Or connect to requested by client port
                socket = new Socket(address, requestedPort);
                InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(streamreader);
                writer = new PrintWriter(socket.getOutputStream());
                send(CONNECT);
                isConnected = true;
            } catch (Exception ex) {
                text_area.append("There is a problem with your connection to server. Maybe the server is not operative now. \n");
                nickname_field.setEditable(true);
            }

            ListenThread();

        } else if (isConnected == true) {
            text_area.append("You are already connected\n");
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nickname = new javax.swing.JLabel();
        ipaddress = new javax.swing.JLabel();
        connection_port = new javax.swing.JLabel();
        connect_button = new javax.swing.JButton();
        disconnect_button = new javax.swing.JButton();
        anonimity_button = new javax.swing.JButton();
        nickname_field = new javax.swing.JTextField();
        ipaddress_field = new javax.swing.JTextField();
        port_field = new javax.swing.JTextField();
        send_button = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        text_area = new javax.swing.JTextArea();
        send_field = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        nickname.setText("Username");

        ipaddress.setText("IP address");

        connection_port.setText("Port");

        connect_button.setText("Connect");
        connect_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connect_buttonActionPerformed(evt);
            }
        });

        disconnect_button.setText("Disconnet");
        disconnect_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnect_buttonActionPerformed(evt);
            }
        });

        anonimity_button.setText("Connect with Random Username");
        anonimity_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                anonimity_buttonActionPerformed(evt);
            }
        });

        nickname_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nickname_fieldActionPerformed(evt);
            }
        });

        ipaddress_field.setText("127.0.0.1");
        ipaddress_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipaddress_fieldActionPerformed(evt);
            }
        });

        port_field.setText("1111");
        port_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                port_fieldActionPerformed(evt);
            }
        });

        send_button.setText("Send");
        send_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_buttonActionPerformed(evt);
            }
        });

        text_area.setColumns(20);
        text_area.setRows(5);
        jScrollPane1.setViewportView(text_area);

        send_field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                send_fieldActionPerformed(evt);
            }
        });
        send_field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                send_fieldKeyPressed(evt);
            }
        });

        jLabel5.setText("CLIENT GUI for client/server chat programm");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(connect_button)
                                .addGap(18, 18, 18)
                                .addComponent(disconnect_button))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(nickname)
                                .addGap(18, 18, 18)
                                .addComponent(nickname_field, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(anonimity_button)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(send_button))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipaddress)
                            .addComponent(connection_port))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ipaddress_field, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(port_field, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(127, 127, 127)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(send_field))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 273, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(267, 267, 267))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nickname)
                            .addComponent(nickname_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ipaddress)
                            .addComponent(ipaddress_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(connection_port)
                            .addComponent(port_field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(67, 67, 67)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(connect_button)
                            .addComponent(disconnect_button))
                        .addGap(18, 18, 18)
                        .addComponent(anonimity_button))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(send_button, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(send_field, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(79, 79, 79))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connect_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connect_buttonActionPerformed
        username = nickname_field.getText();
        startNewConnection(username);
    }//GEN-LAST:event_connect_buttonActionPerformed

    private void anonimity_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_anonimity_buttonActionPerformed
        String noname = "User_";
        Random generator = new Random();
        int i = generator.nextInt(100) + 1;
        String is = String.valueOf(i);
        noname = noname.concat(is);
        username = noname;
        
        nickname_field.setText(noname);
        nickname_field.setEditable(false);
        startNewConnection(username);
    }//GEN-LAST:event_anonimity_buttonActionPerformed

    private void disconnect_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnect_buttonActionPerformed

        disconnect();
    }//GEN-LAST:event_disconnect_buttonActionPerformed

    private void send_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_buttonActionPerformed
        send_message();
    }//GEN-LAST:event_send_buttonActionPerformed

    private void port_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_port_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_port_fieldActionPerformed

    private void ipaddress_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipaddress_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ipaddress_fieldActionPerformed

    private void nickname_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nickname_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nickname_fieldActionPerformed

    private void send_fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_send_fieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_send_fieldActionPerformed

    private void send_fieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_send_fieldKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            send_message();
        }
    }//GEN-LAST:event_send_fieldKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton anonimity_button;
    private javax.swing.JButton connect_button;
    private javax.swing.JLabel connection_port;
    private javax.swing.JButton disconnect_button;
    private javax.swing.JLabel ipaddress;
    private javax.swing.JTextField ipaddress_field;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nickname;
    private javax.swing.JTextField nickname_field;
    private javax.swing.JTextField port_field;
    private javax.swing.JButton send_button;
    private javax.swing.JTextField send_field;
    private javax.swing.JTextArea text_area;
    // End of variables declaration//GEN-END:variables
}
