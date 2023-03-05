package railwaystation.view;

import railwaystation.DBHelper;

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

        addWagonButton.addActionListener(e -> onAddWagon());
        deleteWagonButton.addActionListener(e -> onDeleteWagon());

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

    // Получим из БД информацию о поезде и обновим компоненты на форме
    private void refreshTrainInfo() {
        refreshTrainTypeList();
        refreshStationList();

        // Если создание нового поезда - установим в ComboBox значения по умолчанию
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

    // Установим у ComboBox текущий элемент
    private void setComboboxIndex(JComboBox combobox, String id) {
        for (int i = 0; i < combobox.getItemCount(); i++) {
            DBComboboxModel.ComboBoxItem item = (DBComboboxModel.ComboBoxItem) combobox.getItemAt(i);
            if (item.getId().equals(id)) {
                combobox.setSelectedIndex(i);
                break;
            }
        }
    }

    // Получим из БД список типов поездов в ComboBox
    private void refreshTrainTypeList() {
        ComboBoxModel trainTypeModel = new DBComboboxModel("getAllTrainTypes");
        trainTypeComboBox.setModel(trainTypeModel);
    }

    // Получим из БД список станций в ComboBox
    private void refreshStationList() {
        ComboBoxModel arrivalStationModel = new DBComboboxModel("getAllStations");
        ComboBoxModel departureStationModel = new DBComboboxModel("getAllStations");
        arrivalStationComboBox.setModel(arrivalStationModel);
        departureStationComboBox.setModel(departureStationModel);
    }

    // Обновим список вагонов (таблицу)
    private void refreshWagonListTable() {
        String[] columnNames = {"ID", "Номер вагона", "Тип вагона", "Кол-во мест", "Цена"};
        TableModel model = new DBTableModel("getAllWagonsInfo " + trainId, columnNames);
        wagonListTable.setModel(model);
    }

    // Нажата кнопка "Отмена"
    private void onCancel() {
        dispose();
    }

    // Нажата кнопка "OK"
    private void onOK() {
        if (trainId != null) {
            editTrain(); // Редактируем поезд
        } else {
            addTrain(); // Создаем новый поезд
        }
        dispose();
    }

    // Добавление нового поезда в БД
    private void addTrain() {
        // Сформируем SQL строку
        StringBuilder command = new StringBuilder("EXECUTE addTrain ");
        command.append("\"" + trainNameTextField.getText() + "\", "); // Номер поезда
        DBComboboxModel.ComboBoxItem trainTypeComboBoxItem = (DBComboboxModel.ComboBoxItem) trainTypeComboBox.getModel().getSelectedItem();
        command.append(trainTypeComboBoxItem.getId() + ", "); // Тип поезда
        DBComboboxModel.ComboBoxItem departureStationComboBoxItem = (DBComboboxModel.ComboBoxItem) departureStationComboBox.getModel().getSelectedItem();
        command.append(departureStationComboBoxItem.getId() + ", "); // Станция отправления
        DBComboboxModel.ComboBoxItem arrivalStationComboBoxItem = (DBComboboxModel.ComboBoxItem) arrivalStationComboBox.getModel().getSelectedItem();
        command.append(arrivalStationComboBoxItem.getId() + ", "); // Станция прибытия
        command.append("\"" + departureTimeTextField.getText() + "\" "); // Время отправления TODO: String -> Date

        // Выполним SQL и получим результат (ID новой записи в БД)
        String id = DBHelper.getInstance().insertFunctionWithResult(command.toString());
        if (id != null && !id.isEmpty()) {
            trainId = id; // Теперь у нас есть поезд в БД
        }
    }

    // Изменение существующего поезда в БД
    private void editTrain() {
        StringBuilder command = new StringBuilder("EXECUTE editTrain ");
        command.append(trainId + ", "); // ID поезда
        command.append("\"" + trainNameTextField.getText() + "\", "); // Номер поезда
        DBComboboxModel.ComboBoxItem trainTypeComboBoxItem = (DBComboboxModel.ComboBoxItem) trainTypeComboBox.getModel().getSelectedItem();
        command.append(trainTypeComboBoxItem.getId() + ", "); // Тип поезда
        DBComboboxModel.ComboBoxItem departureStationComboBoxItem = (DBComboboxModel.ComboBoxItem) departureStationComboBox.getModel().getSelectedItem();
        command.append(departureStationComboBoxItem.getId() + ", "); // Станция отправления
        DBComboboxModel.ComboBoxItem arrivalStationComboBoxItem = (DBComboboxModel.ComboBoxItem) arrivalStationComboBox.getModel().getSelectedItem();
        command.append(arrivalStationComboBoxItem.getId() + ", "); // Станция прибытия
        command.append("\"" + departureTimeTextField.getText() + "\" "); // Время отправления TODO: String -> Date

        DBHelper.getInstance().executeFunction(command.toString());
    }

    // Нажата кнопка "Добавить вагон"
    private void onAddWagon() {
        // Если мы в режиме создания нового поезда, то перед добавлением вагонов добавим в БД сам поезд
        // TODO: предупредить пользователя, что перед добавлением вагона поезд будет сохранен (создан) в БД
        if (trainId == null) {
            addTrain();
        }

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
