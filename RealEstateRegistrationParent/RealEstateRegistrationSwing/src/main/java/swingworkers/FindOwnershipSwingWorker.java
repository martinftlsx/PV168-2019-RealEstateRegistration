package swingworkers;

import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.managers.OwnershipManager;
import models.OwnershipsTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FindOwnershipSwingWorker extends SwingWorker<List<Ownership>, Void> {
    private Logger log = LoggerFactory.getLogger(FindOwnershipSwingWorker.class);
    private OwnershipManager ownershipManager;
    private OwnershipsTableModel ownershipsTableModel;
    private String idCardOrCorpNumber;
    private String parcelNumber;

    public FindOwnershipSwingWorker(OwnershipManager ownershipManager, OwnershipsTableModel ownershipsTableModel, String idCardOrCorpNumber, String parcelNumber) {
        this.ownershipManager = ownershipManager;
        this.ownershipsTableModel = ownershipsTableModel;
        this.idCardOrCorpNumber = idCardOrCorpNumber;
        this.parcelNumber = parcelNumber;
    }

    @Override
    protected List<Ownership> doInBackground() throws InterruptedException {
        return ownershipManager
                .retrieveAllOwnerships()
                .stream()
                .filter((ownership)
                        -> ownership.getOwner().getIdCardOrCorpNumber().equals(idCardOrCorpNumber)
                        && ownership.getRealEstate().getParcelNumber().equals(parcelNumber)
                        && ownership.getOwnershipRemoved() == null)
                .collect(Collectors.toList());
    }

    @Override
    protected void done() {
        try {
            ownershipsTableModel.clearTable();
            ownershipsTableModel.addOwnerships(this.get());
        } catch (InterruptedException e) {
            log.error("Process was interrupted.", e);
        } catch (ExecutionException e) {
            log.error("Execution exception.", e);
        }
    }
}
