package swingworkers;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.managers.OwnerManager;
import models.OwnersTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FindOwnerSwingWorker extends SwingWorker<List<Owner>, Void> {
    private OwnerManager ownerManager;
    private OwnersTableModel ownersTableModel;
    private String idOrCorpNumber;
    private Logger log = LoggerFactory.getLogger(FindOwnerSwingWorker.class);

    public FindOwnerSwingWorker(OwnerManager ownerManager, OwnersTableModel ownersTableModel, String idOrCorpNumber) {
        this.ownerManager = ownerManager;
        this.ownersTableModel = ownersTableModel;
        this.idOrCorpNumber = idOrCorpNumber;
    }

    @Override
    protected List<Owner> doInBackground() {
        return ownerManager.retrieveOwnersByIdCardOrCorpNumber(idOrCorpNumber);
    }

    @Override
    protected void done() {
        try {
            ownersTableModel.clearTable();
            ownersTableModel.addOwners(get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
