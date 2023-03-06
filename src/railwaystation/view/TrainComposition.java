package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.*;

public class TrainComposition extends JDialog {
    private JPanel contentPane;

    private JTextField trainNameTextField;
    private JTextField trainTypeTextField;
    private JTextField routeTextField;

    private JTable wagonListTable;
    private JButton addWagonButton;
    private JButton deleteWagonButton;

    private JButton buttonClose;

    private String trainId;

    public TrainComposition(String trainId) {
        this.trainId = trainId;

        setTitle("Сформировать поезд");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonClose);

        buttonClose.addActionListener(l -> onClose());

        addWagonButton.addActionListener(e -> onAddWagon());
        deleteWagonButton.addActionListener(e -> onDeleteWagon());

        // call onClose() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onClose() on ESCAPE
        contentPane.registerKeyboardAction(l -> onClose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        refreshTrainInfo();
    }

    // Получим из БД информацию о поезде и обновим компоненты на форме
    private void refreshTrainInfo() {
        String[] columnNames = {"ID", "Поезд", "Тип поезда", "Время отправления", "Станция отправления", "Станция прибытия"};
        TableModel model = new DBTableModel("getTrainInfo " + trainId, columnNames);
        if (model == null) {
            return;
        }

        trainNameTextField.setText(model.getValueAt(0, 1).toString());

        String trainTypeId = model.getValueAt(0, 2).toString();
        String departureStationId = model.getValueAt(0, 4).toString();
        String arrivalStationId = model.getValueAt(0, 5).toString();

        ComboBoxModel trainTypeModel = new DBComboboxModel("getAllTrainTypes");
        ComboBoxModel stationModel = new DBComboboxModel("getAllStations");

        String trainType = "";
        for (int i = 0; i < trainTypeModel.getSize(); i++) {
            DBComboboxModel.ComboBoxItem item = (DBComboboxModel.ComboBoxItem) trainTypeModel.getElementAt(i);
            if (item.getId().equals(trainTypeId)) {
                trainType = item.toString();
                break;
            }
        }
        trainTypeTextField.setText(trainType);

        String departureStation = "";
        String arrivalStation = "";
        for (int i = 0; i < stationModel.getSize(); i++) {
            DBComboboxModel.ComboBoxItem item = (DBComboboxModel.ComboBoxItem) stationModel.getElementAt(i);
            if (item.getId().equals(departureStationId)) {
                departureStation = item.toString();
            }
            if (item.getId().equals(arrivalStationId)) {
                arrivalStation = item.toString();
            }
            if (!departureStation.isEmpty() && !arrivalStation.isEmpty()) {
                break;
            }
        }
        routeTextField.setText(departureStation + " - " + arrivalStation);

        refreshWagonListTable();
    }

    // Обновим список вагонов (таблицу)
    private void refreshWagonListTable() {
        String[] columnNames = {"ID", "Номер вагона", "Тип вагона", "Кол-во мест", "Цена"};
        TableModel model = new DBTableModel("getAllWagonsInfo " + trainId, columnNames);
        wagonListTable.setModel(model);
        TableColumnModel columnModel = wagonListTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(0));
    }

    private void onClose() {
        dispose();
    }

    // Нажата кнопка "Добавить вагон"
    private void onAddWagon() {

        // Диалог создания вагона
        Wagon dialog = new Wagon(trainId);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        // Обновим список (возможно добавился новый вагон)
        refreshWagonListTable();
    }

    // Нажата кнопка "Удалить вагон"
    private void onDeleteWagon() {
        // Активная строка в таблице
        int row = wagonListTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        // в 0 столбце id вагона
        String id = wagonListTable.getModel().getValueAt(row, 0).toString();
        if (id == null) {
            return;
        }

        // TODO: Проверить вдруг уже проданы билеты в этот вагон, тогда нельзя удалять

        // Удалить вагон из БД
        DBHelper.getInstance().executeFunction("Exec deleteWagon " + id);

        refreshWagonListTable();
    }

}
