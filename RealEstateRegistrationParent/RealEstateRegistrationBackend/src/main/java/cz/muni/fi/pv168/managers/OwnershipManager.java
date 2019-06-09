package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.entities.RealEstate;

import java.util.List;

/**
 * @author Martin Podhora
 */
public interface OwnershipManager {
    void createOwnership(Ownership ownership);
    Ownership retrieveOwnershipById(Long id);
    List<Ownership> retrieveAllOwnerships();
    List<Ownership> retrieveOwnershipsForOwner(Owner owner);
    List<Ownership> retrieveOwnershipsForRealEstate(RealEstate realEstate);
    void updateOwnership(Ownership ownership);
    void deleteOwnership(Ownership ownership);
}
