package models;

import cz.muni.fi.pv168.entities.Owner;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OwnersTableModel extends AbstractTableModel {
    private List<Owner> owners = new ArrayList<>();

    public void clearTable() {
        int count = getRowCount();
        owners.clear();
        if (getRowCount() != 0) fireTableRowsDeleted(0, count - 1);
    }

    @Override
    public int getRowCount() {
        return owners.size();
    }

    public Owner getOwnerAt(int i) {
        return owners.get(i);
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    public void addOwners(List<Owner> owners) {
        this.owners.addAll(owners);
        int lastRow = owners.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void addOwner(Owner owner) {
        this.owners.add(owner);
        int lastRow = owners.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Owner owner = owners.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return owner.getName();
            case 1:
                return owner.getIdCardOrCorpNumber();
            case 2:
                return owner.getCorp();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private ResourceBundle res = ResourceBundle.getBundle("MyBundle");

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return res.getString("owner_table1");
            case 1:
                return res.getString("owner_table2");
            case 2:
                return res.getString("owner_table3");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return Boolean.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
}
