package models;

import javax.swing.*;

public class SingleSelectionModel extends DefaultListSelectionModel {

    public SingleSelectionModel() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void clearSelection() {
    }

    @Override
    public void removeSelectionInterval(int index0, int index1) {
    }

}