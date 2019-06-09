package models;

import cz.muni.fi.pv168.entities.RealEstate;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RealEstatesTableModel extends AbstractTableModel {
    private List<RealEstate> realEstates = new ArrayList<>();

    @Override
    public int getRowCount() {
        return realEstates.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    public RealEstate getRealEstateAt(int i) {
        return realEstates.get(i);
    }

    public void clearTable() {
        int count = getRowCount();
        realEstates.clear();
        if (getRowCount() != 0) fireTableRowsDeleted(0, count - 1);
    }

    public void addRealEstates(List<RealEstate> realEstates) {
        this.realEstates.addAll(realEstates);
        int lastRow = realEstates.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void addRealEstate(RealEstate realEstate) {
        this.realEstates.add(realEstate);
        int lastRow = realEstates.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RealEstate realEstate = realEstates.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return realEstate.getCadastralArea();
            case 1:
                return realEstate.getParcelNumber();
            case 2:
                return realEstate.getAreaInMetersSquared();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private ResourceBundle res = ResourceBundle.getBundle("MyBundle");

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return res.getString("real_estate_table1");
            case 1:
                return res.getString("real_estate_table2");
            case 2:
                return res.getString("real_estate_table3");
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
                return Double.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
}
