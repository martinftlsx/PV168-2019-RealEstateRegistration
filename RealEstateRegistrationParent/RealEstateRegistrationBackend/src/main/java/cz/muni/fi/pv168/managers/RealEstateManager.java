package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.exceptions.ValidationException;

import java.util.List;

/**
 * @author Martin Podhora
 */
public interface RealEstateManager {
    void createRealEstate(RealEstate realEstate) throws IllegalEntityException, ServiceFailureException, ValidationException;
    RealEstate retrieveRealEstateById(Long id) throws ServiceFailureException;
    List<RealEstate> retrieveRealEstatesByParcelNumber(String parcelNumber);
    List<RealEstate> retrieveAllRealEstates() throws ServiceFailureException;
    void updateRealEstate(RealEstate realEstate) throws ServiceFailureException, ValidationException, IllegalEntityException;
    void deleteRealEstate(RealEstate realEstate) throws ServiceFailureException, IllegalEntityException;
}
