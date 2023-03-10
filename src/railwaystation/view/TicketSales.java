package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.*;

public class TicketSales extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<DBComboBoxModel.ComboBoxItem> passengerComboBox;
    private JComboBox<DBComboBoxModel.ComboBoxItem> trainComboBox;
    private JTable wagonListTable;

    private final String passengerId;

    public TicketSales(String passengerId) {

        this.passengerId = passengerId;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        refreshPassengerList();
        refreshTrainList();
        refreshWagonList();
    }

    private void refreshPassengerList() {
        ComboBoxModel<DBComboBoxModel.ComboBoxItem> trainTypeModel = new DBComboBoxModel("getAllPassengersName");
        passengerComboBox.setModel(trainTypeModel);
        passengerComboBox.setSelectedIndex(0);
        if (passengerId != null) {
            setComboBoxIndex(passengerComboBox, passengerId);
        } else {
            passengerComboBox.setSelectedIndex(0);
        }
    }

    private void refreshTrainList() {
        ComboBoxModel<DBComboBoxModel.ComboBoxItem> trainTypeModel = new DBComboBoxModel("getAllTrainsName");
        trainComboBox.setModel(trainTypeModel);
        trainComboBox.addActionListener(l -> refreshWagonList());
        trainComboBox.setSelectedIndex(0);
    }

    // Установим у ComboBox текущий элемент
    private void setComboBoxIndex(JComboBox<DBComboBoxModel.ComboBoxItem> comboBox, String id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            DBComboBoxModel.ComboBoxItem item = comboBox.getItemAt(i);
            if (item.getId().equals(id)) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void refreshWagonList() {
        DBComboBoxModel.ComboBoxItem trainComboBoxItem = (DBComboBoxModel.ComboBoxItem) trainComboBox.getModel().getSelectedItem();
        if (trainComboBoxItem == null) {
            return;
        }
        String trainId = trainComboBoxItem.getId();

        String[] columnNames = {"ID", "Номер вагона", "Тип вагона", "Кол-во мест", "Цена"};
        TableModel ticketModel = new DBTableModel("getAllWagonsInfo " + trainId, columnNames);
        wagonListTable.setModel(ticketModel);
        TableColumnModel columnModel = wagonListTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
    }

    private void onCancel() {
        dispose();
    }

    private void onOK() {
        DBComboBoxModel.ComboBoxItem passengerComboBoxItem = (DBComboBoxModel.ComboBoxItem) passengerComboBox.getModel().getSelectedItem();
        if (passengerComboBoxItem == null) {
            return;
        }

        int row = wagonListTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        String wagonId = wagonListTable.getModel().getValueAt(row, 0).toString();
        String passengerId = passengerComboBoxItem.getId();

        // Сформируем SQL строку
        String command = "EXECUTE addTicket " + wagonId + ", " + passengerId;

        // Выполним SQL
        DBHelper.getInstance().executeFunction(command);

        dispose();
    }

}
