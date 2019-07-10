package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;
    private String login;

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            new Thread(() -> {
                try {
                    authentication();
                    readMessages();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при создании обработчика клиента");
        }
    }

    public void authentication() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/auth")) {
                String[] parts = str.split("\\s");
                String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                if (nick != null) {
                    if (!myServer.isNickBusy(nick)) {
                        sendMsg("/authok " + nick);
                        name = nick;
                        login = parts[1];
                        myServer.broadcastMsg(name + " зашел в чат");
                        myServer.subscribe(this);
                        return;
                    } else {
                        sendMsg("Учетная запись уже используется");
                    }
                } else {
                    sendMsg("Неверные логин/пароль");
                }
            }
            else if (str.startsWith("/reg")) {
                String[] parts = str.split("\\s");
                if (myServer.getAuthService().checkLogin(parts[1])) {
                    sendMsg("Логин занят");
                }
                else {
                    myServer.getAuthService().start();
                    try {
                        myServer.getAuthService().addUserInDB(parts[1], parts[2]);
                        sendMsg("Пользователь зарегестрирован");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    myServer.getAuthService().stop();
                }
            }
        }
    }

    public void readMessages() throws IOException {
        while (true) {
            String str = in.readUTF();
            if (str.startsWith("/w ")) {
                String[] tokens = str.split("\\s");
                String nick = tokens[1];
                String msg = str.substring(4 + nick.length());
                myServer.sendMsgToClient(this, nick, msg);
            }
            else if(str.startsWith("/newnick ")) {
                String[] tokens = str.split("\\s");
                name = tokens[1];
                myServer.getAuthService().start();
                try {
                    myServer.getAuthService().changeNickInDB(login, name);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                myServer.getAuthService().stop();
                sendMsg("/newnickok " + name);
                sendMsg("Новый ник: " + name);
            }
            else {
                myServer.broadcastMsg(name + ": " + str);
            }
        }
    }

    public void closeConnection() {
        myServer.unsubscribe(this);
        myServer.broadcastMsg(name + " вышел из чата");
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

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
