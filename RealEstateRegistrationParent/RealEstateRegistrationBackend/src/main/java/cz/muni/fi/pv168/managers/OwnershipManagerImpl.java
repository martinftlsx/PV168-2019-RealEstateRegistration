package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.entities.Ownership;
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
import java.sql.Timestamp;
import java.time.Clock;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Podhora, Branislav Smolicek
 */
public class OwnershipManagerImpl implements OwnershipManager {
    private final DataSource dataSource;
    private final Clock clock;
    private final OwnerManager ownerManager;
    private final RealEstateManager realEstateManager;

    private static final Logger log = LoggerFactory.getLogger(OwnershipManagerImpl.class);

    public OwnershipManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.clock = null;
        ownerManager = new OwnerManagerImpl(dataSource);
        realEstateManager = new RealEstateManagerImpl(dataSource);
    }

    public OwnershipManagerImpl(DataSource dataSource, Clock clock) {
        this.dataSource = dataSource;
        this.clock = clock;
        ownerManager = new OwnerManagerImpl(dataSource);
        realEstateManager = new RealEstateManagerImpl(dataSource);
    }

    @Override
    public void createOwnership(Ownership ownership) {
        validateOwnership(ownership);
        log.info("Creating ownership: " + ownership.toString());
        if (ownership.getId() != null) throw new IllegalEntityException("Ownership has already assigned id");
        if (ownership.getOwnershipRemoved() != null) throw new ValidationException("Ownership has already assigne timeRemoved");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO OWNERSHIP (OWNERSHIPCREATED, OWNERSHIPREMOVED, SHARENUMERATOR, SHAREDENOMINATOR, OWNER, REALESTATE) VALUES (?, ?, ?, ?, ?, ?)"
                     , Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(ownership.getOwnershipCreated().toLocalDateTime()));
            preparedStatement.setTimestamp(2, null);
            preparedStatement.setInt(3, ownership.getShareNumerator());
            preparedStatement.setInt(4, ownership.getShareDenominator());
            preparedStatement.setLong(5, ownership.getOwner().getId());
            preparedStatement.setLong(6, ownership.getRealEstate().getId());
            preparedStatement.executeUpdate();
            ownership.setId(DBUtils.getId(preparedStatement.getGeneratedKeys()));
        } catch (SQLException e) {
            log.error("Error occured while creating new " + ownership.toString() + " record in the database", e);
            throw new ServiceFailureException("Error occured while creating new " + ownership.toString() + " record in the database", e);
        }
        log.info("Created ownership: " + ownership.toString());
    }

    @Override
    public Ownership retrieveOwnershipById(Long id) {
        if (id == null) throw new IllegalEntityException("Id is null");
        log.info("Retrieving ownership with id: " + id);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OWNERSHIP WHERE id=?")) {
            preparedStatement.setLong(1, id);
            log.info("Retrieved ownership with id: " + id);
            return executeQueryForSingleOwnership(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occured while retrieving Ownership with id = " + id + " from the database", e);
            throw new ServiceFailureException("Error occured while retrieving Ownership with id = " + id + " from the database", e);
        }
    }

    @Override
    public List<Ownership> retrieveAllOwnerships() {
        log.info("Retrieving all ownerships");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OWNERSHIP")) {
            log.info("Retrieved all ownerships");
            return executeQueryForMultipleOwnerships(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occured while retrieving all Ownerships from the database", e);
            throw new ServiceFailureException("Error occured while retrieving all Ownerships from the database", e);
        }
    }

    @Override
    public List<Ownership> retrieveOwnershipsForOwner(Owner owner) {
        if (owner == null) {
            log.error("Owner is null");
            throw new IllegalArgumentException("Owner is null");
        }
        if (owner.getId() == null) {
            log.error("Owner's id is null");
            throw new IllegalEntityException("Owner's id is null");
        }
        log.info("Retrieving ownerships for owner: " + owner.toString());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OWNERSHIP WHERE OWNER=?")) {
            preparedStatement.setLong(1, owner.getId());
            log.info("Retrieved ownerships for owner");
            return executeQueryForMultipleOwnerships(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occured while retrieving Owner " + owner.toString() + " from the table Ownership", e);
            throw new ServiceFailureException("Error occured while retrieving Owner " + owner.toString() + " from the table Ownership", e);
        }
    }

    @Override
    public List<Ownership> retrieveOwnershipsForRealEstate(RealEstate realEstate) {
        if (realEstate == null) {
            log.error("Owner is null");
            throw new IllegalArgumentException("Owner is null");
        }
        if (realEstate.getId() == null) {
            log.error("Owner's id is null");
            throw new IllegalEntityException("Owner's id is null");
        }
        log.info("Retrieving ownerships for real estate: " + realEstate.toString());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OWNERSHIP WHERE REALESTATE=?")) {
            preparedStatement.setLong(1, realEstate.getId());
            log.info("Retrieved ownerships for real estate");
            return executeQueryForMultipleOwnerships(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occured while retrieving RealEstate " + realEstate.toString() + " from the table Ownership", e);
            throw new ServiceFailureException("Error occured while retrieving RealEstate " + realEstate.toString() + " from the table Ownership", e);
        }
    }

    @Override
    public void updateOwnership(Ownership ownership) {
        validateOwnership(ownership);
        if (ownership.getId() == null) {
            log.error("Ownership doesn't assigned id");
            throw new IllegalEntityException("Ownership doesn't assigned id");
        }
        log.info("Updating ownership: " + ownership.toString());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE OWNERSHIP SET OWNERSHIPCREATED=?, OWNERSHIPREMOVED=?, SHARENUMERATOR=?, SHAREDENOMINATOR=?, OWNER=?, REALESTATE=? WHERE ID=?")) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(ownership.getOwnershipCreated().toLocalDateTime()));
            if (ownership.getOwnershipRemoved() != null) preparedStatement.setTimestamp(2, Timestamp.valueOf(ownership.getOwnershipRemoved().toLocalDateTime()));
            else preparedStatement.setTimestamp(2, null);
            preparedStatement.setInt(3, ownership.getShareNumerator());
            preparedStatement.setInt(4, ownership.getShareDenominator());
            preparedStatement.setLong(5, ownership.getOwner().getId());
            preparedStatement.setLong(6, ownership.getRealEstate().getId());
            preparedStatement.setLong(7, ownership.getId());
            int count = preparedStatement.executeUpdate();
            if (count != 1) {
                log.error("Updated " + count + " instead of 1 Ownership");
                throw new IllegalEntityException("Updated " + count + " instead of 1 Ownership");
            }
        } catch (SQLException e) {
            log.error("Error occurred while updating " + ownership.toString() + " record in the database", e);
            throw new ServiceFailureException("Error occurred while updating " + ownership.toString() + " record in the database", e);
        }
        log.info("Updated ownership");
    }

    @Override
    public void deleteOwnership(Ownership ownership) {
        if (ownership == null) {
            log.error("Ownership is null");
            throw new IllegalArgumentException("Ownership is null");
        }
        if (ownership.getId() == null) {
            log.error("Ownership's id is null");
            throw new IllegalEntityException("Ownership's id is null");
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM OWNERSHIP WHERE ID=?")) {
            preparedStatement.setLong(1, ownership.getId());
            int count = preparedStatement.executeUpdate();
            if (count != 1) {
                log.error("Deleted " + count + " instead of 1 Ownership");
                throw new IllegalEntityException("Deleted " + count + " instead of 1 Ownership");
            }
        } catch (SQLException e) {
            log.error("Error occurred when deleting Ownership from the database", e);
            throw new ServiceFailureException("Error occurred when deleting Ownership from the database", e);
        }
        log.info("Deleted ownership");
    }

    private Ownership executeQueryForSingleOwnership(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return rowToOwnership(resultSet);
            } else {
                return null;
            }
        }
    }

    private List<Ownership> executeQueryForMultipleOwnerships(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Ownership> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowToOwnership(resultSet));
            }
            return result;
        }
    }

    private Ownership rowToOwnership(ResultSet resultSet) throws SQLException {
        Ownership result = new Ownership();
        result.setId(resultSet.getLong("ID"));
        result.setOwnershipCreated(resultSet.getTimestamp("OWNERSHIPCREATED").toLocalDateTime().atZone(ZoneId.systemDefault()));
        Timestamp ownershipRemoved = resultSet.getTimestamp("OWNERSHIPREMOVED");
        if (ownershipRemoved != null) result.setOwnershipRemoved(ownershipRemoved.toLocalDateTime().atZone(ZoneId.systemDefault()));
        result.setShareNumerator(resultSet.getInt("SHARENUMERATOR"));
        result.setShareDenominator(resultSet.getInt("SHAREDENOMINATOR"));
        result.setOwner(ownerManager.retrieveOwnerById(resultSet.getLong("OWNER")));
        result.setRealEstate(realEstateManager.retrieveRealEstateById(resultSet.getLong("REALESTATE")));
        return result;
    }

    private void validateOwnership(Ownership ownership) {
        if (ownership == null) {
            log.error("Ownership is null");
            throw new IllegalArgumentException("Ownership is null");
        }
        if (ownership.getOwnershipCreated() == null) {
            log.error("Ownership creation date is null");
            throw new ValidationException("Ownership creation date is null");
        }
        if (ownership.getShareNumerator() == null) {
            log.error("Ownership numerator is null");
            throw new ValidationException("Ownership numerator is null");
        }
        if (ownership.getShareNumerator() < 0) {
            log.error("Ownership share numerator is subzero");
            throw new ValidationException("Ownership share numerator is subzero");
        }
        if (ownership.getShareDenominator() == null) {
            log.error("Ownership share denominator is null");
            throw new ValidationException("Ownership share denominator is null");
        }
        if (ownership.getShareDenominator() <= 0) {
            log.error("Ownership share denominator is 0 or subzero");
            throw new ValidationException("Ownership share denominator is 0 or subzero");
        }
        if (ownership.getOwner() == null) {
            log.error("Ownership's owner is null");
            throw new ValidationException("Ownership's owner is null");
        }
        if (ownership.getRealEstate() == null) {
            log.error("Ownership's real estate is null");
            throw new ValidationException("Ownership's real estate is null");
        }
        if (ownership.getOwnershipRemoved() != null) {
            if (ownership.getOwnershipCreated().compareTo(ownership.getOwnershipRemoved()) == 1) {
                log.error("Ownership's date of removal is before date of creation");
                throw new ValidationException("Ownership's date of removal is before date of creation");
            }
        }
    }
}
