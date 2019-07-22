package client;

import utils.Properties;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Authorization {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean authorized;
    private UserChatHistory history;

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
                                String[] tokens = str.split("\\s");
                                myNick = tokens[1];
                                authorized = true;
                                history = new UserChatHistory(tokens[2]);
                                window.getPushButton().setText("Отправить");
                                ArrayList<String> arr = history.readHistory();
                                if (arr.size() > 100) {
                                    for (int i = arr.size() - 100; i < arr.size(); i++) {
                                        window.getTextArea().append(arr.get(i) + "\n");
                                    }
                                }
                                else {
                                    for (String o : arr) {
                                        window.getTextArea().append(o + "\n");
                                    }
                                }
                                break;
                            }
                            window.getTextArea().append(str + "\n");
                        }
                        while (true) {
                            String strFromServer = in.readUTF();
                            if (strFromServer.startsWith("/newnickok ")) {
                                myNick = strFromServer.split("\\s")[1];
                            }
                            else {
                                window.getTextArea().append(strFromServer + "\n");
                                history.saveMessage(strFromServer);
                            }
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

    public void onChangeNickClick() {
        try {
            out.writeUTF("/newnick " + window.getNewNickField().getText() + " " + myNick);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRegClick() {
        if (socket == null || socket.isClosed()) {
            openConnection();
        }
        try {
            out.writeUTF("/reg " + window.getRegLoginField().getText() + " " + window.getRegPasswordField().getText());
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
