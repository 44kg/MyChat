package client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
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

    private JDialog regDialog;
    private JTextField regLoginField;
    private JPasswordField regPasswordField;
    private JPasswordField confirmPasswordField;

    private JDialog changeNickDialog;
    private JTextField newNickField;

    private Authorization authorization;

    public Window() throws HeadlessException{
        setTitle("MyChat");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(getStandardLayout());
        setBounds(100, 100, 600, 500);
        setMinimumSize(new Dimension(300, 200));

        textArea = new JTextArea();

        textArea.setEditable(false);
        textArea.setFont(new Font("Courier", Font.BOLD, 10));
        textArea.setLineWrap(true);
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
        JMenuItem itemReg = new JMenuItem("Регистрация");
        itemReg.setFont(new Font("Courier", Font.BOLD, 10));
        itemReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!authorization.getAuthorized()) {
                    regDialog.setBounds(getX() + getWidth() / 2 - 105, getY() + getHeight() / 2 - 85,
                            210, 175);
                    regDialog.setVisible(true);
                }
            }
        });
        JMenuItem itemChangeNick = new JMenuItem("Сиенить ник");
        itemChangeNick.setFont(new Font("Courier", Font.BOLD, 10));
        itemChangeNick.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authorization.getAuthorized()) {
                    changeNickDialog.setBounds(getX() + getWidth() / 2 - 105, getY() + getHeight() / 2 - 85,
                            210, 175);
                    changeNickDialog.setVisible(true);
                }
            }
        });

        options.add(itemReg);
        options.add(itemChangeNick);
        options.add(itemExit);
        mainMenu.add(options);

        setJMenuBar(mainMenu);
        add(getPanel(), BorderLayout.NORTH);
        add(getPanel(), BorderLayout.EAST);
        add(getPanel(), BorderLayout.WEST);
        add(textArea, BorderLayout.CENTER);
        add(new JScrollPane(textArea));
        add(panel, BorderLayout.SOUTH);

        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        initAuthorizationDialog();
        initRegistrationDialog();
        initChangeNickDialog();

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
                    authDialog.setBounds(getX() + getWidth() / 2 - 105, getY() + getHeight() / 2 - 85,
                            210, 175);
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

    private void initRegistrationDialog() {
        regDialog = new JDialog();
        regDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        regDialog.setTitle("Регистрация");
        regDialog.setBounds(this.getX() + this.getWidth() / 2 - 105, this.getY() + this.getHeight() / 2 - 85,
                210, 175);
        regDialog.setResizable(false);
        regDialog.setLayout(new FlowLayout());

        JLabel loginLabel = new JLabel("          Логин:          ");
        regLoginField = new JTextField(15);

        JLabel passwordLabel = new JLabel("          Пароль:          ");
        regPasswordField = new JPasswordField(15);

        JButton regButton = new JButton("Регистрация");

        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authorization.onRegClick();
                regDialog.setVisible(false);
            }
        });

        regDialog.add(loginLabel);
        regDialog.add(regLoginField);
        regDialog.add(passwordLabel);
        regDialog.add(regPasswordField);
        regDialog.add(regButton);
    }

    private void initChangeNickDialog() {
        changeNickDialog = new JDialog();
        changeNickDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        changeNickDialog.setTitle("Смена ника");
        changeNickDialog.setBounds(this.getX() + this.getWidth() / 2 - 105, this.getY() + this.getHeight() / 2 - 85,
                210, 175);
        changeNickDialog.setResizable(false);
        changeNickDialog.setLayout(new FlowLayout());

        JLabel changeNickLabel = new JLabel("          Новый ник:          ");
        newNickField = new JTextField(15);

        JButton newNickButton = new JButton("Сменить");

        newNickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authorization.onChangeNickClick();
                changeNickDialog.setVisible(false);
            }
        });

        changeNickDialog.add(changeNickLabel);
        changeNickDialog.add(newNickField);
        changeNickDialog.add(newNickButton);
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

    public JTextField getRegLoginField() {
        return regLoginField;
    }

    public JPasswordField getRegPasswordField() {
        return regPasswordField;
    }

    public JTextField getNewNickField() {
        return newNickField;
    }

    public JButton getPushButton() {
        return pushButton;
    }
}
