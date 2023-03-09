package railwaystation.view;

import javax.swing.*;
import java.awt.event.*;

public class TicketSales extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<DBComboBoxModel.ComboBoxItem> passengerComboBox;
    private JComboBox<DBComboBoxModel.ComboBoxItem> trainComboBox;
    private JTable table1;

    public TicketSales() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        refreshPassengerList();
        refreshTrainList();
    }

    private void refreshPassengerList() {
        ComboBoxModel<DBComboBoxModel.ComboBoxItem> trainTypeModel = new DBComboBoxModel("getAllPassengersName");
        passengerComboBox.setModel(trainTypeModel);
    }

    private void refreshTrainList() {
        ComboBoxModel<DBComboBoxModel.ComboBoxItem> trainTypeModel = new DBComboBoxModel("getAllTrainsName");
        trainComboBox.setModel(trainTypeModel);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
