package swingworkers;

import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.managers.OwnershipManager;

import javax.swing.*;

public class UpdateOwnershipSwingWorker extends SwingWorker<Void, Void> {
    private OwnershipManager ownershipManager;
    private Ownership ownership;
    private Runnable callback;

    public UpdateOwnershipSwingWorker(OwnershipManager ownershipManager, Ownership ownership, Runnable callback) {
        this.ownershipManager = ownershipManager;
        this.ownership = ownership;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground() {
        ownershipManager.updateOwnership(ownership);
        return null;
    }

    @Override
    protected void done() {
        callback.run();
    }
}
