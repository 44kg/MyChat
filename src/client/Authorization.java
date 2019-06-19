package client;

import utils.Properties;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Authorization {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authorized;

    private String myNick;

    private Window window;


    public Authorization(Window window) {
        this.window = window;
        socket = null;
        myNick = "";
        authorized = false;
    }

    public void openConnection() {
        try {
            socket = new Socket(Properties.HOST, Properties.PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authok ")) {
                                myNick = str.split("\\s")[1];
                                authorized = true;
                                window.getPushButton().setText("Отправить");
                                break;
                            }
                            window.getTextArea().append(str + "\n");
                        }
                        while (true) {
                            String strFromServer = in.readUTF();
                            window.getTextArea().append(strFromServer + "\n");
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        closeConnection();
                    }
                }
            });
            t.start();
            window.getTextArea().append("Сервер доступен. Ожидание авторизации." + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            window.getTextArea().append("Сервер не доступен" + "\n");
        }
    }

    public void closeConnection() {
        myNick = "";
        authorized = false;
        window.getPushButton().setText("Авторизация");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onAuthClick() {
        if (socket == null || socket.isClosed()) {
            openConnection();
        }
        try {
            out.writeUTF( "/auth " + window.getLoginField().getText() + " " + window.getPasswordField().getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        if (!window.getTextField().getText().trim().isEmpty()) {
            try {
                out.writeUTF(window.getTextField().getText());
                window.getTextField().setText("");
                window.getTextField().grabFocus();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Ошибка отправки сообщения");
            }
        }
    }

    public boolean getAuthorized() {
        return authorized;
    }
}
