package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;

public class MainPage extends JPanel {
    private JTabbedPane rolesTabbedPane;
    private JPanel rootPanel;

    private JTable trainInfoListTable;
    private JButton addTrainButton; // Добавить поезд
    private JButton editTrainButton; // Редактировать поезд
    private JButton deleteTrainButton; // Удалить поезд

    private JTable trainCompositionListTable;
    private JButton composeTrainButton; // Сформировать поезд

    private JTable ticketListTable;
    private JButton sellTicketButton;
    private JButton returnTicketButton;

    public MainPage() {
        add(rootPanel);
        addTrainButton.addActionListener(this::onAddTrainButtonClick);
        editTrainButton.addActionListener(this::onEditTrainButtonClick);
        deleteTrainButton.addActionListener(this::onDeleteTrainButtonClick);
        composeTrainButton.addActionListener(this::onComposeTrainButtonClick);
        refreshTrainListTables();
    }

    // Обновляем данные в таблицах
    private void refreshTrainListTables() {
        String[] columnNames = {"ID", "Поезд", "Тип поезда", "Время отправления", "Станция отправления", "Станция прибытия", "Кол-во вагонов"};

        TableModel trainInfoModel = new DBTableModel("getAllTrainsInfo", columnNames);
        trainInfoListTable.setModel(trainInfoModel);
        TableColumnModel columnModel1 = trainInfoListTable.getColumnModel();
        columnModel1.removeColumn(columnModel1.getColumn(6));
        columnModel1.removeColumn(columnModel1.getColumn(0));

        TableModel trainCompositionModel = new DBTableModel("getAllTrainsInfo", columnNames);
        trainCompositionListTable.setModel(trainCompositionModel);
        TableColumnModel columnModel2 = trainCompositionListTable.getColumnModel();
        columnModel2.removeColumn(columnModel2.getColumn(3));
        columnModel2.removeColumn(columnModel2.getColumn(0));
    }

    // Нажата кнопка "Добавить поезд"
    private void onAddTrainButtonClick(ActionEvent actionEvent) {
        TrainInfo dialog = new TrainInfo();
        dialog.setSize(700, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        refreshTrainListTables();
    }

    // Нажата кнопка "Редактировать поезд"
    private void onEditTrainButtonClick(ActionEvent actionEvent) {
        int row = trainInfoListTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        String id = trainInfoListTable.getModel().getValueAt(row, 0).toString();
        if (id == null) {
            return;
        }

        TrainInfo dialog = new TrainInfo(id);
        dialog.setSize(700, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        refreshTrainListTables();
    }

    // Нажата кнопка "Удалить поезд"
    private void onDeleteTrainButtonClick(ActionEvent actionEvent) {
        // Активная строка в таблице
        int row = trainInfoListTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        // в 0 столбце id поезда
        String id = trainInfoListTable.getModel().getValueAt(row, 0).toString();
        if (id == null) {
            return;
        }

        // TODO: Проверить вдруг уже проданы билеты на этот поезд, тогда нельзя удалять

        // Удалить поезд из БД
        DBHelper.getInstance().executeFunction("Exec deleteTrain " + id);

        refreshTrainListTables();
    }

    // Нажата кнопка "Сформировать поезд"
    private void onComposeTrainButtonClick(ActionEvent actionEvent) {
        int row = trainCompositionListTable.getSelectedRow();
        if (row < 0) {
            return;
        }

        String id = trainCompositionListTable.getModel().getValueAt(row, 0).toString();
        if (id == null) {
            return;
        }

        TrainComposition dialog = new TrainComposition(id);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        refreshTrainListTables();
    }

}
