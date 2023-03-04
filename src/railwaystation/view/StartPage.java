package railwaystation.view;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class StartPage extends JPanel {
    private JPanel rootPanel;
    private JButton headRoleButton;
    private JButton tellerRoleButton;
    private JButton passengerRoleButton;

    public StartPage() {
        add(rootPanel);
        headRoleButton.addActionListener(this::onHeadRoleSelected);
        tellerRoleButton.addActionListener(this::onTellerRoleSelected);
        passengerRoleButton.addActionListener(this::onPassengerRoleSelected);
    }

    private void onPassengerRoleSelected(ActionEvent actionEvent) {
        System.out.println("onPassengerRoleSelected");
    }

    private void onTellerRoleSelected(ActionEvent actionEvent) {
        System.out.println("onTellerRoleSelected");
    }

    private void onHeadRoleSelected(ActionEvent actionEvent) {
        System.out.println("onHeadRoleSelected");
    }

}
