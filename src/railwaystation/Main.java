package railwaystation;

import railwaystation.view.MainPage;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {

        JFrame mainPage = new JFrame("Железнодорожный вокзал международного сообщения");
        mainPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPage.setContentPane(new MainPage());
        mainPage.pack();
        mainPage.setLocationRelativeTo(null);
        mainPage.setVisible(true);

        final DBHelper dbHelper = DBHelper.getInstance();

        mainPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dbHelper.close();
            }
        });
    }
}