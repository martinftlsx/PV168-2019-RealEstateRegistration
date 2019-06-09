package swingworkers;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.managers.OwnerManager;

import javax.swing.*;

public class CreateOwnerSwingWorker extends SwingWorker<Void, Void> {
    private OwnerManager ownerManager;
    private Owner owner;
    private Runnable callback;

    public CreateOwnerSwingWorker(OwnerManager ownerManager, Owner owner, Runnable callback) {
        this.ownerManager = ownerManager;
        this.owner = owner;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground() {
        ownerManager.createOwner(owner);
        return null;
    }

    @Override
    protected void done() {
        callback.run();
    }
}
