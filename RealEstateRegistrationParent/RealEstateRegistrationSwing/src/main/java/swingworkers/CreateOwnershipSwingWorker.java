package swingworkers;

import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.managers.OwnershipManager;

import javax.swing.*;

public class CreateOwnershipSwingWorker extends SwingWorker<Void, Void> {
    private OwnershipManager ownershipManager;
    private Ownership ownership;
    private Runnable callback;

    public CreateOwnershipSwingWorker(OwnershipManager ownershipManager, Ownership ownership, Runnable callback) {
        this.ownershipManager = ownershipManager;
        this.ownership = ownership;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground() throws Exception {
        ownershipManager.createOwnership(ownership);
        return null;
    }

    @Override
    protected void done() {
        callback.run();
    }
}
