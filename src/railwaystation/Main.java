package railwaystation;

import railwaystation.view.StartPage;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {

        JFrame startPage = new JFrame("Железнодорожный вокзал");
        startPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startPage.setContentPane(new StartPage());
        startPage.pack();
        startPage.setLocationRelativeTo(null);
        startPage.setVisible(true);

        final DBHelper dbHelper = DBHelper.getInstance();

        startPage.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dbHelper.close();
            }
        });
    }
}