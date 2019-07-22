package client;

import java.io.*;
import java.util.ArrayList;

public class UserChatHistory {
    private String login;

    public UserChatHistory(String login) {
        this.login = login;

        try {
            File file = new File("history_" + login + ".txt");
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveMessage(String text) {
        try (BufferedWriter writer = new BufferedWriter( new FileWriter( "history_" + login + ".txt", true))) {
            writer.write(text + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readHistory() {
        ArrayList<String> arrayList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader( new FileReader( "history_" + login + ".txt" ))) {
            String string;
            while ((string = reader.readLine()) != null) {
                arrayList.add(string);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
