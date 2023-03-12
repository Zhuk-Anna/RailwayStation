package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TrainInfo extends JDialog {
    private JPanel contentPane;

    private JTextField trainNameTextField;
    private JComboBox<DBComboBoxModel.ComboBoxItem> trainTypeComboBox;
    private JSpinner departureTimeSpinner;
    private JComboBox<DBComboBoxModel.ComboBoxItem> destinationStationComboBox;
    private JSpinner basePriceSpinner;

    private JButton buttonOK;
    private JButton buttonCancel;


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
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        initSpinners();
        refreshTrainInfo();
    }

    private void initSpinners() {
        departureTimeSpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor dateTimeEditor = new JSpinner.DateEditor(departureTimeSpinner, "dd.MM.yyyy HH:mm");
        departureTimeSpinner.setEditor(dateTimeEditor);

        basePriceSpinner.setModel(new SpinnerNumberModel(0.0f, null, null, 0.1f));
        JSpinner.NumberEditor numberEditor = new JSpinner.NumberEditor(basePriceSpinner, "0.00");
        numberEditor.getTextField().setHorizontalAlignment(JTextField.LEFT);
        basePriceSpinner.setEditor(numberEditor);
    }

    private String getDepartureTime() {
        Date date = (Date) departureTimeSpinner.getValue();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return formatter.format(calendar.getTime());
    }

    // Получим из БД информацию о поезде и обновим компоненты на форме
    private void refreshTrainInfo() {
        refreshTrainTypeList();
        refreshStationList();

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date();

        // Если создание нового поезда - установим значения по умолчанию
        if (trainId == null) {
            trainTypeComboBox.setSelectedIndex(0);
            destinationStationComboBox.setSelectedIndex(0);
            basePriceSpinner.setValue(1.0f);
            departureTimeSpinner.setValue(date);
            return;
        }

        String[] columnNames = {"ID", "Поезд", "Тип поезда", "Отправление", "Станция назначения", "Базовая стоимость билета"};
        TableModel model = new DBTableModel("getTrainInfo " + trainId, columnNames);

        trainNameTextField.setText(model.getValueAt(0, 1).toString());
        setComboBoxIndex(trainTypeComboBox, model.getValueAt(0, 2).toString());

        try {
            date = formatter.parse(model.getValueAt(0, 3).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        departureTimeSpinner.setValue(date);

        setComboBoxIndex(destinationStationComboBox, model.getValueAt(0, 4).toString());
        basePriceSpinner.setValue(Float.valueOf(model.getValueAt(0, 5).toString()));
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

    // Получим из БД список типов поездов в ComboBox
    private void refreshTrainTypeList() {
        ComboBoxModel<DBComboBoxModel.ComboBoxItem> trainTypeModel = new DBComboBoxModel("getAllTrainTypes");
        trainTypeComboBox.setModel(trainTypeModel);
    }

    // Получим из БД список станций в ComboBox
    private void refreshStationList() {
        ComboBoxModel<DBComboBoxModel.ComboBoxItem> destinationStationModel = new DBComboBoxModel("getAllStations");
        destinationStationComboBox.setModel(destinationStationModel);
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
        createSQLCommand(command);

        // Выполним SQL и получим результат (ID новой записи в БД)
        String id = DBHelper.getInstance().executeFunctionWithResult(command.toString());
        if (id != null && !id.isEmpty()) {
            trainId = id; // Теперь у нас есть поезд в БД
        }
    }

    // Изменение существующего поезда в БД
    private void editTrain() {
        StringBuilder command = new StringBuilder("EXECUTE editTrain ");
        command.append(trainId).append(", "); // ID поезда
        createSQLCommand(command);
        System.out.println(command);
        DBHelper.getInstance().executeFunction(command.toString());
    }

    private void createSQLCommand(StringBuilder command) {
        command.append("\"").append(trainNameTextField.getText()).append("\", "); // Номер поезда
        DBComboBoxModel.ComboBoxItem trainTypeComboBoxItem = (DBComboBoxModel.ComboBoxItem) trainTypeComboBox.getModel().getSelectedItem();
        command.append(trainTypeComboBoxItem.getId()).append(", "); // Тип поезда
        DBComboBoxModel.ComboBoxItem departureStationComboBoxItem = (DBComboBoxModel.ComboBoxItem) destinationStationComboBox.getModel().getSelectedItem();
        command.append(departureStationComboBoxItem.getId()).append(", "); // Станция назначения
        command.append("\"").append(getDepartureTime()).append("\", "); // Отправление
        command.append((float)basePriceSpinner.getModel().getValue()); // Базовая стоимость билета
    }

}
