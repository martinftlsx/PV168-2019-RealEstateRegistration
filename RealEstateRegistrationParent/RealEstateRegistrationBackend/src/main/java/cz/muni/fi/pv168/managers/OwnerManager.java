package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.exceptions.ValidationException;

import java.util.List;

/**
 * @author Martin Podhora
 */
public interface OwnerManager {
    void createOwner(Owner owner) throws IllegalEntityException, ServiceFailureException, ValidationException;
    Owner retrieveOwnerById(Long id) throws ServiceFailureException;
    List<Owner> retrieveOwnersByIdCardOrCorpNumber(String idCardOrCorpNumber);
    List<Owner> retrieveAllOwners() throws ServiceFailureException;
    void updateOwner(Owner owner) throws ServiceFailureException, ValidationException, IllegalEntityException;;
    void deleteOwner(Owner owner) throws ServiceFailureException, IllegalEntityException;
}
