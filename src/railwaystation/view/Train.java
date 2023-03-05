package railwaystation.view;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.*;

public class Train extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField trainNameTextField;
    private JComboBox arrivalStationComboBox;
    private JComboBox departureStationComboBox;
    private JComboBox trainTypeComboBox;
    private JTable wagonListTable;
    private JButton addWagonButton;
    private JButton deleteWagonButton;
    private JTextField departureTimeTextField;

    private String trainId;

    public Train() {
        this(null);
    }

    public Train(String trainId) {
        this.trainId = trainId;

        if (trainId != null) {
            setTitle("Изменить поезд");
        } else {
            setTitle("Добавить поезд");
        }

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
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        refreshTrainInfo();
    }

    private void refreshTrainInfo() {
        refreshTrainTypeList();
        refreshStationList();

        if (trainId == null) {
            trainTypeComboBox.setSelectedIndex(0);
            arrivalStationComboBox.setSelectedIndex(0);
            departureStationComboBox.setSelectedIndex(1);
            return;
        }

        String[] columnNames = {"ID", "Поезд", "Тип поезда", "Время отправления", "Станция отправления", "Станция прибытия"};
        TableModel model = new DBTableModel("getTrainInfo " + trainId, columnNames);
        if (model == null) {
            return;
        }

        trainNameTextField.setText(model.getValueAt(0, 1).toString());
        departureTimeTextField.setText(model.getValueAt(0, 3).toString());

        setComboboxIndex(trainTypeComboBox, model.getValueAt(0, 2).toString());
        setComboboxIndex(departureStationComboBox, model.getValueAt(0, 4).toString());
        setComboboxIndex(arrivalStationComboBox, model.getValueAt(0, 5).toString());

        refreshWagonListTable();
    }

    private void setComboboxIndex(JComboBox combobox, String id) {
        for (int i = 0; i < combobox.getItemCount(); i++) {
            DBComboboxModel.ComboBoxItem item = (DBComboboxModel.ComboBoxItem) combobox.getItemAt(i);
            if (item.getId().equals(id)) {
                combobox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void refreshTrainTypeList() {
        ComboBoxModel trainTypeModel = new DBComboboxModel("getAllTrainTypes");
        trainTypeComboBox.setModel(trainTypeModel);
    }

    private void refreshStationList() {
        ComboBoxModel arrivalStationModel = new DBComboboxModel("getAllStations");
        ComboBoxModel departureStationModel = new DBComboboxModel("getAllStations");
        arrivalStationComboBox.setModel(arrivalStationModel);
        departureStationComboBox.setModel(departureStationModel);
    }

    private void refreshWagonListTable() {
        String[] columnNames = {"ID", "Номер вагона", "Тип вагона", "Кол-во мест", "Цена"};
        TableModel model = new DBTableModel("getAllWagonsInfo " + trainId, columnNames);
        wagonListTable.setModel(model);
    }

    private void onOK() {
        if (trainId != null) {
            // TODO edit
        } else {
            // TODO add
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

}
