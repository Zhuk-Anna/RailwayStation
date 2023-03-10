package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Passenger extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField fioTextField;
    private JSpinner birthdaySpinner;
    private JComboBox<DBComboBoxModel.ComboBoxItem> documentTypeComboBox;
    private JTextField documentNumberTextField;
    private JComboBox<DBComboBoxModel.ComboBoxItem> benefitTypeComboBox;

    public Passenger() {

        setTitle("Добавить пассажира");

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

        refreshPassengerInfo();
    }

    private void refreshPassengerInfo() {
        birthdaySpinner.setModel(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(birthdaySpinner, "dd.MM.yyyy");
        birthdaySpinner.setEditor(dateEditor);
        birthdaySpinner.setValue(new Date());

        ComboBoxModel<DBComboBoxModel.ComboBoxItem> documentTypeModel = new DBComboBoxModel("getAllDocumentTypes");
        documentTypeComboBox.setModel(documentTypeModel);
        documentTypeComboBox.setSelectedIndex(0);

        ComboBoxModel<DBComboBoxModel.ComboBoxItem> benefitTypeModel = new DBComboBoxModel("getAllBenefitTypes");
        benefitTypeComboBox.setModel(benefitTypeModel);
        benefitTypeComboBox.setSelectedIndex(0);
    }

    private String getBirthday() {
        Date date = (Date) birthdaySpinner.getValue();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(date);
    }

    private void onCancel() {
        dispose();
    }
    private void onOK() {
        // Сформируем SQL строку
        StringBuilder command = new StringBuilder("EXECUTE addPassenger ");

        command.append("\"").append(fioTextField.getText()).append("\", "); // ФИО
        command.append("\"").append(getBirthday()).append("\", "); // Дата рождения
        DBComboBoxModel.ComboBoxItem documentTypeComboBoxItem = (DBComboBoxModel.ComboBoxItem) documentTypeComboBox.getModel().getSelectedItem();
        command.append(documentTypeComboBoxItem.getId()).append(", "); // Тип документа
        command.append("\"").append(documentNumberTextField.getText()).append("\", "); // Номер документа
        DBComboBoxModel.ComboBoxItem benefitTypeComboBoxItem = (DBComboBoxModel.ComboBoxItem) benefitTypeComboBox.getModel().getSelectedItem();
        command.append(benefitTypeComboBoxItem.getId()); // Льгота

        // Выполним SQL
        DBHelper.getInstance().executeFunction(command.toString());

        dispose();
    }

}
