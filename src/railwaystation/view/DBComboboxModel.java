package railwaystation.view;

import railwaystation.DBHelper;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DBComboboxModel extends DefaultComboBoxModel<DBComboboxModel.ComboBoxItem> {

    public DBComboboxModel(String storedProcedureName) {
        String result = DBHelper.getInstance().selectFunction("EXECUTE " + storedProcedureName, 2);
        List<ComboBoxItem> data = new ArrayList<>();
        if (result != null && !result.isEmpty()) {
            data = Arrays.stream(result.split("\n")).map(i -> {
                String[] str = i.split(";");
                return new ComboBoxItem(str[0], str[1]);
            }).collect(Collectors.toList());
        }
        super.addAll(data);
    }

    public static class ComboBoxItem {
        private final String id;
        private final String name;

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
