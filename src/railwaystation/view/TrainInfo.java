package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TrainInfo extends JDialog {
    private JPanel contentPane;

    private JTextField trainNameTextField;
    private JComboBox trainTypeComboBox;
    private JSpinner departureTimeSpinner;
    private JComboBox departureStationComboBox;
    private JComboBox arrivalStationComboBox;

    private JButton buttonOK;
    private JButton buttonCancel;
//    private JTextField departureTimeTextField;

    private String trainId;

    public TrainInfo() {
        this(null);
    }

    public TrainInfo(String trainId) {
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

        initDepartureTimeSpinner();
        refreshTrainInfo();
    }

    private void initDepartureTimeSpinner() {
        departureTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor dateTimeEditor = new JSpinner.DateEditor(departureTimeSpinner, "dd.MM.yyyy HH:mm");
        departureTimeSpinner.setEditor(dateTimeEditor);
        departureTimeSpinner.setValue(new Date());
    }

    private String getDepartureTime() {
        Date date = (Date) departureTimeSpinner.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return formatter.format(calendar.getTime());
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
//        departureTimeTextField.setText(model.getValueAt(0, 3).toString()); // TODO: String -> Date

        setComboboxIndex(trainTypeComboBox, model.getValueAt(0, 2).toString());
        setComboboxIndex(departureStationComboBox, model.getValueAt(0, 4).toString());
        setComboboxIndex(arrivalStationComboBox, model.getValueAt(0, 5).toString());
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
        command.append("\"" + getDepartureTime() + "\" "); // Время отправления TODO: String -> Date

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
        command.append("\"" + getDepartureTime() + "\" "); // Время отправления TODO: String -> Date

        DBHelper.getInstance().executeFunction(command.toString());
    }

}
