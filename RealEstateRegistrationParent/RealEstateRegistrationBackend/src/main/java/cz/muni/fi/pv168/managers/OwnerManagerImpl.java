package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.Owner;
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
 * @author Martin Podhora
 */
public class OwnerManagerImpl implements OwnerManager {
    private final DataSource dataSource;

    private final static Logger log = LoggerFactory.getLogger(OwnerManagerImpl.class);

    public OwnerManagerImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createOwner(Owner owner) {
        validateOwner(owner);
        log.info("Creating owner: " + owner.toString());
        if (owner.getId() != null) throw new IllegalEntityException("Owner has already assigned id");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO OWNER (NAME, IDCARDORCORPNUMBER, ISCORP) VALUES (?, ?, ?)"
                    , Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, owner.getName());
            preparedStatement.setString(2, owner.getIdCardOrCorpNumber());
            preparedStatement.setBoolean(3, owner.getCorp());
            preparedStatement.executeUpdate();
            owner.setId(DBUtils.getId(preparedStatement.getGeneratedKeys()));
        } catch (SQLException e) {
            log.error("Error occurred while creating new "+ owner.toString() +"record in the database", e);
            throw new ServiceFailureException("Error occurred while creating new "+ owner.toString() +"record in the database", e);
        }
        log.info("Created owner: " + owner.toString());
    }

    @Override
    public Owner retrieveOwnerById(Long id) {
        log.info("Retrieving owner with id: " + id);
        if (id == null) throw new IllegalArgumentException("Owner's id is null");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OWNER WHERE ID = ?")) {
            preparedStatement.setLong(1, id);
            log.info("Owner with id: " + id + " retrieved");
            return executeQueryForSingleOwner(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving owner with id: " + id, e);
            throw new ServiceFailureException("Error occurred while retrieving Owner with id = " + id + " from the database", e);
        }
    }

    @Override
    public List<Owner> retrieveOwnersByIdCardOrCorpNumber(String idCardOrCorpNumber) {
        log.info("Retrieving owner with id: " + idCardOrCorpNumber);
        if (idCardOrCorpNumber == null) throw new IllegalArgumentException("Owner's id is null");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OWNER WHERE IDCARDORCORPNUMBER = ?")) {
            preparedStatement.setString(1, idCardOrCorpNumber);
            log.info("Owner with id: " + idCardOrCorpNumber + " retrieved");
            return executeQueryForMultipleOwners(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving owner with id: " + idCardOrCorpNumber, e);
            throw new ServiceFailureException("Error occurred while retrieving Owner with id = " + idCardOrCorpNumber + " from the database", e);
        }    }

    @Override
    public List<Owner> retrieveAllOwners() {
        log.info("Retrieving all owners");
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM OWNER")) {
            log.info("Retrieved all owners");
            return executeQueryForMultipleOwners(preparedStatement);
        } catch (SQLException e) {
            log.error("Error occurred while retrieving all owners", e);
            throw new ServiceFailureException("Error occurred while retrieving all Owners from the database", e);
        }
    }

    @Override
    public void updateOwner(Owner owner) {
        validateOwner(owner);
        log.info("Updating owner: " + owner.toString());
        if (owner.getId() == null) throw new IllegalEntityException("Owner does not have assigned id");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE OWNER SET NAME=?, IDCARDORCORPNUMBER=?, ISCORP=? WHERE ID=?")) {
            preparedStatement.setString(1, owner.getName());
            preparedStatement.setString(2, owner.getIdCardOrCorpNumber());
            preparedStatement.setBoolean(3, owner.getCorp());
            preparedStatement.setLong(4, owner.getId());
            int count = preparedStatement.executeUpdate();
            if (count != 1) throw new IllegalEntityException("Updated " + count + " instead of 1 Owner");
        } catch (SQLException e) {
            log.error("Error occurred while updating new "+ owner.toString() +"record in the database", e);
            throw new ServiceFailureException("Error occurred while updating Owner in the database", e);
        }
        log.info("Updated owner: " + owner.toString());
    }

    @Override
    public void deleteOwner(Owner owner) {
        if (owner == null) throw new IllegalArgumentException("Owner is null");
        if (owner.getId() == null) throw new IllegalEntityException("Owner's id is null");
        log.info("Deleting owner: " + owner.toString());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM OWNER WHERE ID=?")) {
            preparedStatement.setLong(1, owner.getId());
            int count = preparedStatement.executeUpdate();
            if (count != 1) throw new IllegalEntityException("Deleted " + count + " instead of 1 Owner");
        } catch (SQLException e) {
            log.error("Error occurred while deleting Owner from the database", e);
            throw new ServiceFailureException("Error occurred while deleting Owner from the database", e);
        }
        log.info("Deleted owner: " + owner.toString());
    }

    private Owner executeQueryForSingleOwner(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return rowToOwner(resultSet);
            } else {
                return null;
            }
        }
    }

    private List<Owner> executeQueryForMultipleOwners(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Owner> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(rowToOwner(resultSet));
            }
            return result;
        }
    }

    private Owner rowToOwner(ResultSet resultSet) throws SQLException {
        Owner result = new Owner();
        result.setId(resultSet.getLong("ID"));
        result.setName(resultSet.getString("NAME"));
        result.setIdCardOrCorpNumber(resultSet.getString("IDCARDORCORPNUMBER"));
        result.setCorp(resultSet.getBoolean("ISCORP"));
        return result;
    }

    private void validateOwner(Owner owner) {
        if (owner == null) {
            log.error("Owner is null");
            throw new IllegalArgumentException("Owner is null");
        }
        if (owner.getName() == null) {
            log.error("Owner's name is null");
            throw new ValidationException("Owner's name is null");
        }
        if (owner.getName().isEmpty()) {
            log.error("Owner's name is empty");
            throw new ValidationException("Owner's name is empty");
        }
        if (owner.getCorp() == null) {
            log.error("Owner's corp is null");
            throw new ValidationException("Owner's corp is null");
        }
        if (owner.getIdCardOrCorpNumber() == null) {
            log.error("Owner's idCardOrCorpNumber is null");
            throw new ValidationException("Owner's idCardOrCorpNumber is null");
        }
        if (owner.getIdCardOrCorpNumber().isEmpty()) {
            log.error("Owner's idCardOrCorpNumber is empty");
            throw new ValidationException("Owner's idCardOrCorpNumber is empty");
        }
    }
}
