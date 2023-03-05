package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.table.DefaultTableModel;
import java.util.Arrays;

public class DBTableModel extends DefaultTableModel {

    public DBTableModel(String storedProcedureName, Object[] columnNames) {
        String result = DBHelper.getInstance().selectFunction("Exec " + storedProcedureName, columnNames.length);
        if (result != null && !result.isEmpty()) {
            Object[][]  data = Arrays.stream(result.split("\n")).map(i -> i.split(";")).toArray(Object[][]::new);
            super.setDataVector(data, columnNames);
        } else {
            super.setColumnIdentifiers(columnNames);
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
