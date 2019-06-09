package swingworkers;

import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.managers.RealEstateManager;
import models.RealEstatesTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FindRealEstateSwingWorker extends SwingWorker<List<RealEstate>, Void> {
    private RealEstateManager realEstateManager;
    private RealEstatesTableModel realEstatesTableModel;
    private String parcelNumber;
    private Logger log = LoggerFactory.getLogger(FindRealEstateSwingWorker.class);

    public FindRealEstateSwingWorker(RealEstateManager realEstateManager, RealEstatesTableModel realEstatesTableModel, String parcelNumber) {
        this.realEstateManager = realEstateManager;
        this.realEstatesTableModel = realEstatesTableModel;
        this.parcelNumber = parcelNumber;
    }

    @Override
    protected List<RealEstate> doInBackground() {
        return realEstateManager.retrieveRealEstatesByParcelNumber(parcelNumber);
    }

    @Override
    protected void done() {
        try {
            realEstatesTableModel.clearTable();
            realEstatesTableModel.addRealEstates(get());
        } catch (InterruptedException e) {
            log.error("Process was interrupted.", e);
        } catch (ExecutionException e) {
            log.error("Execution exception.", e);
        }
    }
}
