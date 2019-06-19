package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Window extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private JButton pushButton;
    private JDialog authDialog;
    private JTextField loginField;
    private JPasswordField passwordField;
    private Authorization authorization;

    public Window() throws HeadlessException{
        setTitle("MyChat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(getStandardLayout());
        setBounds(100, 100, 600, 500);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier", Font.BOLD, 10));
//        textArea.setBackground(new Color(45, 55, 70, 45));

        JPanel panel = new JPanel(getStandardLayout());

        textField = new JTextField();
        textField.setFont(new Font("Courier", Font.BOLD, 10));
        textField.addActionListener(getActionListener());
        panel.add(textField, BorderLayout.CENTER);

        panel.add(getPanel(), BorderLayout.WEST);
        panel.add(getPanel(), BorderLayout.SOUTH);

        pushButton = new JButton("Авторизация");
        pushButton.setFont(new Font("Courier", Font.BOLD, 12));
        pushButton.addActionListener(getActionListener());

        JPanel gapPanel = new JPanel(getStandardLayout());
        panel.add(gapPanel, BorderLayout.EAST);
        gapPanel.add(pushButton, BorderLayout.CENTER);
        gapPanel.add(getPanel(), BorderLayout.EAST);

        JMenuBar mainMenu = new JMenuBar();
        JMenu options = new JMenu("Меню");
        options.setFont(new Font("Courier", Font.BOLD, 12));
        JMenuItem itemExit = new JMenuItem("Выход");
        itemExit.setFont(new Font("Courier", Font.BOLD, 10));
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        options.add(itemExit);
        mainMenu.add(options);

        setJMenuBar(mainMenu);
        add(getPanel(), BorderLayout.NORTH);
        add(getPanel(), BorderLayout.EAST);
        add(getPanel(), BorderLayout.WEST);
        add(textArea, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        initAuthorizationDialog();

        setVisible(true);
        textField.grabFocus();

        authorization = new Authorization(this);
        authorization.openConnection();
    }

    private BorderLayout getStandardLayout() {
        BorderLayout layout = new BorderLayout();
        layout.setHgap(5);
        layout.setVgap(5);
        return layout;
    }

    private JPanel getPanel() {
        return new JPanel(getStandardLayout());
    }

    private ActionListener getActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authorization.getAuthorized()) {
                    authorization.sendMessage();
                }
                else {
                    authDialog.setVisible(true);
                }
            }
        };
    }

    private void initAuthorizationDialog() {
        authDialog = new JDialog();
        authDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        authDialog.setTitle("Авторизация");
        authDialog.setBounds(this.getX() + this.getWidth() / 2 - 105, this.getY() + this.getHeight() / 2 - 85,
                210, 175);
        authDialog.setResizable(false);
        authDialog.setLayout(new FlowLayout());

        JLabel loginLabel = new JLabel("          Логин:          ");
        loginField = new JTextField(15);

        JLabel passwordLabel = new JLabel("          Пароль:          ");
        passwordField = new JPasswordField(15);

        JButton authButton = new JButton("Авторизация");

        authButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authorization.onAuthClick();
                authDialog.setVisible(false);
            }
        });

        authDialog.add(loginLabel);
        authDialog.add(loginField);
        authDialog.add(passwordLabel);
        authDialog.add(passwordField);
        authDialog.add(authButton);
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public JTextField getTextField() {
        return textField;
    }

    public JTextField getLoginField() {
        return loginField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getPushButton() {
        return pushButton;
    }
}
