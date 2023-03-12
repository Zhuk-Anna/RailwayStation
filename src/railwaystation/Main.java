package railwaystation;

import railwaystation.view.Auth;
import railwaystation.view.MainPage;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {

        Auth authDialog = new Auth();
        authDialog.setSize(500, 200);
        authDialog.setLocationRelativeTo(null);
        authDialog.setVisible(true);

        String login = authDialog.getLogin();
        String password = authDialog.getPassword();

        if (login == null || password == null || login.isEmpty() || password.isEmpty()) {
            return;
        }

        final DBHelper dbHelper = DBHelper.getInstance();
        String result = dbHelper.executeFunctionWithResult("EXEC getUserRole " + login + ", " + password);
        int role = Integer.parseInt(result);

        if (role == -1) {
            dbHelper.close();
            JOptionPane.showMessageDialog(null,
                    "Неверный логин или пароль",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame mainPage = new JFrame("Железнодорожный вокзал международного сообщения");
        mainPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPage.setContentPane(new MainPage(role));
        mainPage.pack();
        mainPage.setLocationRelativeTo(null);
        mainPage.setVisible(true);

        mainPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dbHelper.close();
            }
        });
    }
}