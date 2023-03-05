package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DBComboboxModel extends DefaultComboBoxModel<DBComboboxModel.ComboBoxItem> {

    public DBComboboxModel(String storedProcedureName) {
        String[] columnNames = {"ID", "Название"};
        String result = DBHelper.getInstance().selectFunction("Exec " + storedProcedureName, columnNames.length);
        List<ComboBoxItem> data = new ArrayList<>();
        if (result != null && !result.isEmpty()) {
            data = Arrays.stream(result.split(";")).map(i -> {
                String[] str = i.split("_");
                return new ComboBoxItem(str[0], str[1]);
            }).collect(Collectors.toList());
        }
        super.addAll(data);
    }

    public class ComboBoxItem {
        private String id;
        private String name;

        public ComboBoxItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
