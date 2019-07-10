package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    private class Entry {
        private String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

    private Connection con = null;
    private String username = "root";
    private String password = "Kolokol0";
    private String url = "jdbc:mysql://localhost:3306/mychatusers?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private List<Entry> entries;

    @Override
    public void start() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection established");
        }
        catch (Exception e)
        {
            System.err.println("Cannot connect to database server");
            e.printStackTrace();
        }
        System.out.println("Сервис аутентификации запущен");
    }

    @Override
    public void stop() {
        System.out.println("Сервис аутентификации остановлен");
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BaseAuthService() {
        entries = new ArrayList<>();
    }

    @Override
    public String getNickByLoginPass(String login, String pass) {
        for (Entry o : entries) {
            if (o.login.equals(login) && o.pass.equals(pass)) return o.nick;
        }
        return null;
    }

//    public String getLoginByNickPass(String nick, String pass) {
//        for (Entry o : entries) {
//            if (o.nick.equals(nick) && o.pass.equals(pass)) return o.login;
//        }
//        return null;
//    }

    public boolean checkLogin(String login) {
        for (Entry o : entries) {
            if (o.login.equals(login)) return true;
        }
        return false;
    }

    public void loadUsers() throws SQLException {
        try (Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("select * from users");
            while (rs.next()) {
                entries.add(new Entry(rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
            }
        }
    }

    public void addUserInDB(String login, String password) throws SQLException {
        try (PreparedStatement statement = con.prepareStatement("INSERT INTO `mychatusers`.`users` (`login`, `pass`, `nick`) VALUES (?, ?, ?)")) {
            statement.setString(1, login);
            statement.setString(2, password);
            statement.setString(3, login);
            statement.executeUpdate();
        }
        entries.add(new Entry(login, password, login));
    }

    public void changeNickInDB(String login, String newNick) throws SQLException{
        try (PreparedStatement statement = con.prepareStatement("UPDATE `mychatusers`.`users` SET `nick` = ? WHERE (`login` = ?)")) {
            statement.setString(1, newNick);
            statement.setString(2, login);
            statement.executeUpdate();
        }
    }
}

