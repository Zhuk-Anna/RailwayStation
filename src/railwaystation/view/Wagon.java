package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import java.awt.event.*;

public class Wagon extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField wagonNameTextField;
    private JComboBox wagonTypeComboBox;
    private JSpinner seatsCountSpinner;
    private JTextField ticketPriceTextField;

    private String trainId;

    public Wagon(String trainId) {
        this.trainId = trainId;

        setTitle("Добавить вагон");

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

        refreshWagonTypeList();
    }

    // Получим из БД список типов вагонов в ComboBox
    private void refreshWagonTypeList() {
        ComboBoxModel wagonTypeModel = new DBComboboxModel("getAllWagonTypes");
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
        command.append(trainId + ", "); // ID поезда
        command.append("\"" + wagonNameTextField.getText() + "\", "); // Номер вагона
        DBComboboxModel.ComboBoxItem wagonTypeComboBoxItem = (DBComboboxModel.ComboBoxItem) wagonTypeComboBox.getModel().getSelectedItem();
        command.append(wagonTypeComboBoxItem.getId() + ", "); // Тип вагона
        command.append("\"" + ticketPriceTextField.getText() + "\", "); // Стоимость билета
        command.append(seatsCountSpinner.getModel().getValue().toString()); // Колво мест в вагоне

        // Выполним SQL
        DBHelper.getInstance().executeFunction(command.toString());
    }


}
