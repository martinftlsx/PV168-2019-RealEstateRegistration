package models;

import cz.muni.fi.pv168.entities.Ownership;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OwnershipsTableModel extends AbstractTableModel {

    private List<Ownership> ownerships = new ArrayList<>();

    @Override
    public int getRowCount() {
        return ownerships.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    public Ownership getOwnershipAt(int i) {
        return ownerships.get(i);
    }

    public void clearTable() {
        int count = getRowCount();
        ownerships.clear();
        if (getRowCount() != 0) fireTableRowsDeleted(0, count - 1);
    }

    public void deleteOwnershipAt(int i) {
        ownerships.remove(i);
        fireTableRowsDeleted(i, i);
    }

    public void addOwnership(Ownership ownership) {
        this.ownerships.add(ownership);
        int lastRow = ownerships.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void addOwnerships(List<Ownership> ownerships) {
        this.ownerships.addAll(ownerships);
        int lastRow = ownerships.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Ownership ownership = ownerships.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return ownership.getOwner().getName();
            case 1:
                return ownership.getOwner().getIdCardOrCorpNumber();
            case 2:
                return ownership.getOwner().getCorp();
            case 3:
                return ownership.getRealEstate().getCadastralArea();
            case 4:
                return ownership.getRealEstate().getParcelNumber();
            case 5:
                return ownership.getRealEstate().getAreaInMetersSquared();
            case 6:
                return ownership.getShareNumerator() + "/" + ownership.getShareDenominator();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private ResourceBundle res = ResourceBundle.getBundle("MyBundle");

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return res.getString("ownership_table1");
            case 1:
                return res.getString("ownership_table2");
            case 2:
                return res.getString("ownership_table3");
            case 3:
                return res.getString("ownership_table4");
            case 4:
                return res.getString("ownership_table5");
            case 5:
                return res.getString("ownership_table6");
            case 6:
                return res.getString("ownership_table7");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Ownership ownership = ownerships.get(rowIndex);
        switch (columnIndex) {
            case 6:
                String[] values;
                try {
                    values = ((String) aValue).split("/");
                    ownership.setShareNumerator(Integer.parseInt(values[0]));
                    ownership.setShareDenominator(Integer.parseInt(values[1]));
                } catch (Exception ex) {
//                    JOptionPane.showMessageDialog(, "Invalid input");
                    throw new IllegalArgumentException("Invalid input", ex);
                }
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return false;
            case 3:
                return false;
            case 4:
                return false;
            case 5:
                return false;
            case 6:
                return true;
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
            case 3:
                return String.class;
            case 4:
                return String.class;
            case 5:
                return Double.class;
            case 6:
                return String.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
}