package swingworkers;

import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.managers.OwnershipManager;

import javax.swing.*;

public class DeleteOwnershipSwingWorker extends SwingWorker<Void, Void> {
    private Ownership ownership;
    private OwnershipManager ownershipManager;
    private Runnable callback;

    public DeleteOwnershipSwingWorker(OwnershipManager ownershipManager, Ownership ownership, Runnable callback) {
        this.ownershipManager = ownershipManager;
        this.ownership = ownership;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground() throws Exception {
        ownershipManager.updateOwnership(ownership);
        return null;
    }

    @Override
    protected void done() {
        callback.run();
    }
}
