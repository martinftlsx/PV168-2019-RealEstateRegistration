package swingworkers;

import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.managers.RealEstateManager;

import javax.swing.*;

public class CreateRealEstateSwingWorker extends SwingWorker<Void, Void> {
    private RealEstateManager realEstateManager;
    private RealEstate realEstate;
    private Runnable callback;

    public CreateRealEstateSwingWorker(RealEstateManager realEstateManager, RealEstate realEstate, Runnable callback) {
        this.realEstateManager = realEstateManager;
        this.realEstate = realEstate;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground() throws Exception {
        realEstateManager.createRealEstate(realEstate);
        return null;
    }

    @Override
    protected void done() {
        callback.run();
    }
}
