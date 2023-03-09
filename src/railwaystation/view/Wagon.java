package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import java.awt.event.*;

public class Wagon extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField wagonNameTextField;
    private JComboBox<DBComboBoxModel.ComboBoxItem> wagonTypeComboBox;

    private final String trainId;

    public Wagon(String trainId) {
        this.trainId = trainId;

        setTitle("Добавить вагон");

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

        refreshWagonTypeList();
    }

    // Получим из БД список типов вагонов в ComboBox
    private void refreshWagonTypeList() {
        ComboBoxModel<DBComboBoxModel.ComboBoxItem> wagonTypeModel = new DBComboBoxModel("getAllWagonTypes");
        wagonTypeComboBox.setModel(wagonTypeModel);
        wagonTypeComboBox.setSelectedIndex(0);
    }

    // Нажата кнопка "Отмена"
    private void onCancel() {
        dispose();
    }

    // Нажата кнопка "OK"
    private void onOK() {
        addWagon();
        dispose();
    }

    // Добавление нового вагона в БД
    private void addWagon() {
        // Сформируем SQL строку
        StringBuilder command = new StringBuilder("EXECUTE addWagon ");
        command.append(trainId).append(", "); // ID поезда
        command.append("\"").append(wagonNameTextField.getText()).append("\", "); // Номер вагона
        DBComboBoxModel.ComboBoxItem wagonTypeComboBoxItem = (DBComboBoxModel.ComboBoxItem) wagonTypeComboBox.getModel().getSelectedItem();
        command.append(wagonTypeComboBoxItem.getId()); // Тип вагона

        // Выполним SQL
        DBHelper.getInstance().executeFunction(command.toString());
    }

}
