package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;

public class MainPage extends JPanel {
    private JTabbedPane rolesTabbedPane;
    private JPanel rootPanel;
    private JButton addTrainButton;
    private JButton editTrainButton;
    private JButton deleteTrainButton;
    private JTable trainListTable;
    private JButton sellTicketButton;
    private JButton returnTicketButton;
    private JTable ticketListTable;

    public MainPage() {
        add(rootPanel);
        addTrainButton.addActionListener(this::onAddTrainButtonClick);
        editTrainButton.addActionListener(this::onEditTrainButtonClick);
        deleteTrainButton.addActionListener(this::onDeleteTrainButtonClick);
        refreshTrainListTable();
    }

    private void refreshTrainListTable() {
        String[] columnNames = {"ID", "Поезд", "Тип поезда", "Время отправления", "Станция отправления", "Станция прибытия", "Кол-во вагонов"};
        TableModel model = new DBTableModel("getAllTrainsInfo", columnNames);
        trainListTable.setModel(model);
    }

    private void onAddTrainButtonClick(ActionEvent actionEvent) {
        Train dialog = new Train();
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        refreshTrainListTable();
    }

    private void onEditTrainButtonClick(ActionEvent actionEvent) {
        int row = trainListTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        String id = trainListTable.getModel().getValueAt(row, 0).toString();
        if (id == null) {
            return;
        }

        Train dialog = new Train(id);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        refreshTrainListTable();
    }

    // Нажата кнопка "Удалить поезд"
    private void onDeleteTrainButtonClick(ActionEvent actionEvent) {
        // Активная строка в таблице
        int row = trainListTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        // в 0 столбце id поезда
        String id = trainListTable.getModel().getValueAt(row, 0).toString();
        if (id == null) {
            return;
        }

        // TODO: Проверить вдруг уже проданы билеты на этот поезд, тогда нельзя удалять

        // Удалить поезд из БД
        DBHelper.getInstance().executeFunction("Exec deleteTrain " + id);

        refreshTrainListTable();
    }

}
