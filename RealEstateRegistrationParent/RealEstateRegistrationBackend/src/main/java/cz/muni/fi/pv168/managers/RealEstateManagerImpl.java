package cz.muni.fi.pv168.managers;


import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.exceptions.ValidationException;
import cz.muni.fi.pv168.utils.DBUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Branislav Smolicek
 */
public class RealEstateManagerImpl implements RealEstateManager {
    private final DataSource dataSource;

    private final static Logger log = LoggerFactory.getLogger(RealEstateManagerImpl.class);

    public RealEstateManagerImpl(DataSource dataSource) { this.dataSource = dataSource; }

    @Override
    public void createRealEstate(RealEstate realEstate) {
        validateRealEstate(realEstate);
        if (realEstate.getId() != null) throw new IllegalEntityException("Real estate doesn't have assigned id");
        log.info("Creating real estate " + realEstate.toString());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement("INSERT INTO REALESTATE (CADASTRALAREA, PARCELNUMBER, AREAINMETERSSQUARED) VALUES (?, ?, ?)"
                , Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, realEstate.getCadastralArea());
            preparedStatement.setString(2, realEstate.getParcelNumber());
            preparedStatement.setDouble(3, realEstate.getAreaInMetersSquared());
            preparedStatement.executeUpdate();
            realEstate.setId(DBUtils.getId(preparedStatement.getGeneratedKeys()));
            log.info("Created real estate: " + realEstate.toString());
        } catch (SQLException e) {
            log.error("Error occurred while creating new "+ realEstate.toString() + "record in the database", e);
            throw new ServiceFailureException("Error occurred while creating new "+ realEstate.toString() +"record in the database", e);
        }
    }

    @Override
    public RealEstate retrieveRealEstateById(Long id) {
        if (id == null) throw new IllegalArgumentException("Real estate's id is null");
        log.info("Retrieving real estate with id: " + id);
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM REALESTATE WHERE ID=?")) {
            preparedStatement.setLong(1, id);
            log.info("Real estate with id: " + id + " retrieved");
            return executeQueryForSingleRealEstate(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving Real estate with id = " + id + " from the database", e);
            throw new ServiceFailureException("Error occurred while retrieving Real estate with id = " + id + " from the database", e);
        }
    }

    @Override
    public List<RealEstate> retrieveRealEstatesByParcelNumber(String parcelNumber) {
        if (parcelNumber == null) throw new IllegalArgumentException("Real estate's parcel number is null");
        log.info("Retrieving real estate with parcel number: " + parcelNumber);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM REALESTATE WHERE PARCELNUMBER=?")) {
            preparedStatement.setString(1, parcelNumber);
            log.info("Real estate with parcel number: " + parcelNumber + " retrieved");
            return executeQueryForMultipleOwners(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving Real estate with id = " + parcelNumber + " from the database", e);
            throw new ServiceFailureException("Error occurred while retrieving Real estate with id = " + parcelNumber + " from the database", e);
        }    }

    @Override
    public List<RealEstate> retrieveAllRealEstates() {
        log.info("Retrieving all real estates");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM REALESTATE")) {
            log.info("Retrieved all real estates");
            return executeQueryForMultipleOwners(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving all Real estates from the database", e);
            throw new ServiceFailureException("Error occurred while retrieving all Real estates from the database", e);
        }
    }

    @Override
    public void updateRealEstate(RealEstate realEstate) {
        validateRealEstate(realEstate);
        if (realEstate.getId() == null) throw new IllegalEntityException("Real estate doesn't have assigned id");
        log.info("Updating real estate " + realEstate.toString());
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE REALESTATE SET CADASTRALAREA=?, PARCELNUMBER=?, AREAINMETERSSQUARED=? WHERE ID=?")) {
            preparedStatement.setString(1, realEstate.getCadastralArea());
            preparedStatement.setString(2, realEstate.getParcelNumber());
            preparedStatement.setDouble(3, realEstate.getAreaInMetersSquared());
            preparedStatement.setLong(4, realEstate.getId());
            int count = preparedStatement.executeUpdate();
            if (count != 1) throw new IllegalEntityException("Updated " + count + " instead of 1 Real estate");
            log.info("Updated real estate: " + realEstate.toString());
        } catch (SQLException e) {
            log.error("Error occurred while updating " + realEstate.toString() + " in the database", e);
            throw new ServiceFailureException("Error occurred while updating " + realEstate.toString() + " in the database", e);
        }
    }

    @Override
    public void deleteRealEstate(RealEstate realEstate) {
        if (realEstate == null) throw new IllegalArgumentException("Real estate is null");
        if (realEstate.getId() == null) throw new IllegalEntityException("Real estate's id is null");
        log.info("Deleting real estate " + realEstate.toString());
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM REALESTATE WHERE ID=?")) {
            preparedStatement.setLong(1, realEstate.getId());
            int count = preparedStatement.executeUpdate();
            if (count != 1) throw new IllegalEntityException("Deleted " + count + " instead of 1 Real estate");
            log.info("Deleted real estate: " + realEstate.toString());
        } catch (SQLException e) {
            log.error("Error occurred when deleting Real estate from the database", e);
            throw new ServiceFailureException("Error occurred when deleting Real estate from the database", e);
        }
    }

    private RealEstate rowToRealEstate(ResultSet resultSet) throws SQLException {
        RealEstate result = new RealEstate();
        result.setId(resultSet.getLong("ID"));
        result.setCadastralArea(resultSet.getString("CADASTRALAREA"));
        result.setParcelNumber(resultSet.getString("PARCELNUMBER"));
        result.setAreaInMetersSquared(resultSet.getDouble("AREAINMETERSSQUARED"));
        return result;
    }

    private RealEstate executeQueryForSingleRealEstate(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return rowToRealEstate(resultSet);
            } else {
                return null;
            }
        }
    }

    private List<RealEstate> executeQueryForMultipleOwners(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<RealEstate> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowToRealEstate(resultSet));
            }
            return result;
        }
    }

    private void validateRealEstate(RealEstate realEstate) {
        if (realEstate == null) {
            log.error("Real estate is null");
            throw new IllegalArgumentException("Real estate is null");
        }
        if (realEstate.getAreaInMetersSquared() == null) {
            log.error("Real estate's area is null");
            throw new ValidationException("Real estate's area is null");
        }
        if (realEstate.getAreaInMetersSquared() < 0) {
            log.error("Real estate's area is below 0");
            throw new ValidationException("Real estate's area is below 0");
        }
        if (realEstate.getCadastralArea() == null) {
            log.error("Real estate's cadastral area is null");
            throw new ValidationException("Real estate's cadastral area is null");
        }
        if (realEstate.getCadastralArea().isEmpty()) {
            log.error("Real estate's cadastral area is empty");
            throw new ValidationException("Real estate's cadastral area is empty");
        }
        if (realEstate.getParcelNumber() == null) {
            log.error("Real estate's parcel number is null");
            throw new ValidationException("Real estate's parcel number is null");
        }
        if (realEstate.getParcelNumber().isEmpty()) {
            log.error("Real estate's parcel number is empty");
            throw new ValidationException("Real estate's parcel number is empty");
        }
    }
}
