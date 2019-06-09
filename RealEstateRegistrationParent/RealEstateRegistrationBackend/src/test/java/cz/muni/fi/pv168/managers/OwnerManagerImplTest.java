package cz.muni.fi.pv168.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.exceptions.ValidationException;
import cz.muni.fi.pv168.utils.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Martin Podhora
 */
public class OwnerManagerImplTest {
    private OwnerManagerImpl ownerManager;
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:realEstateRegistration-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws SQLException, IOException {
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource, getClass().getClassLoader().getResource("createTables.sql").openStream());
        ownerManager = new OwnerManagerImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException, IOException {
        DBUtils.executeSqlScript(dataSource, getClass().getClassLoader().getResource("dropTables.sql").openStream());
    }


    private OwnerBuilder sampleJamesOwnerBuilder() {
        return new OwnerBuilder()
                .name("James Johnson")
                .idCardOrCorpNumber("JJ12345678")
                .isCorp(false);
    }

    private OwnerBuilder sampleCraigOwnerBuilder() {
        return new OwnerBuilder()
                .name("Craig Simpson")
                .idCardOrCorpNumber("CS12345678")
                .isCorp(false);
    }

    private OwnerBuilder sampleMaryOwnerBuilder() {
        return new OwnerBuilder()
                .name("Mary United Corp.")
                .idCardOrCorpNumber("MUC0123456789")
                .isCorp(true);
    }

    @Test
    public void createOwnerPerson() {
        Owner owner = sampleJamesOwnerBuilder().build();
        ownerManager.createOwner(owner);

        Long ownerId = owner.getId();
        assertThat(ownerId).isNotNull();

        assertThat(ownerManager.retrieveOwnerById(ownerId))
                .isNotSameAs(owner)
                .isEqualToComparingFieldByField(owner);
    }

    @Test
    public void createOwnerCorporation() {
        Owner owner = sampleMaryOwnerBuilder().build();
        ownerManager.createOwner(owner);

        Long ownerId = owner.getId();
        assertThat(ownerId).isNotNull();

        assertThat(ownerManager.retrieveOwnerById(ownerId))
                .isNotSameAs(owner)
                .isEqualToComparingFieldByField(owner);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullOwner() {
        ownerManager.createOwner(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void createOwnerWithExistingId() {
        Owner owner = sampleJamesOwnerBuilder()
                .id(1L)
                .build();
        ownerManager.createOwner(owner);
    }

    @Test(expected = ValidationException.class)
    public void createOwnerWithNullName() {
        Owner owner = sampleJamesOwnerBuilder()
                .name(null)
                .build();
        ownerManager.createOwner(owner);
    }

    @Test(expected = ValidationException.class)
    public void createOwnerWithNullIdCardOrCorpNumber() {
        Owner owner = sampleJamesOwnerBuilder()
                .idCardOrCorpNumber(null)
                .build();
        ownerManager.createOwner(owner);
    }

    @Test(expected = ValidationException.class)
    public void createOwnerWithNullIsCorp() {
        Owner owner = sampleJamesOwnerBuilder()
                .isCorp(null)
                .build();
        ownerManager.createOwner(owner);
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveOwner() {
        ownerManager.retrieveOwnerById(null);
    }

    @Test
    public void retrieveAllOwners() {
        assertThat(ownerManager.retrieveAllOwners()).isEmpty();

        Owner james = sampleJamesOwnerBuilder().build();
        Owner mary = sampleMaryOwnerBuilder().build();

        ownerManager.createOwner(james);
        ownerManager.createOwner(mary);

        assertThat(ownerManager.retrieveAllOwners())
                .usingFieldByFieldElementComparator()
                .containsOnly(james, mary);
    }

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }

    private void updateOwnerTest(Operation<Owner> updateOperation) {
        Owner ownerToUpdate = sampleJamesOwnerBuilder().build();
        Owner anotherOwner = sampleCraigOwnerBuilder().build();

        ownerManager.createOwner(ownerToUpdate);
        ownerManager.createOwner(anotherOwner);

        updateOperation.callOn(ownerToUpdate);

        ownerManager.updateOwner(ownerToUpdate);

        assertThat(ownerManager.retrieveOwnerById(ownerToUpdate.getId()))
                .isEqualToComparingFieldByField(ownerToUpdate);
        assertThat(ownerManager.retrieveOwnerById(anotherOwner.getId()))
                .isEqualToComparingFieldByField(anotherOwner);
    }

    @Test
    public void updateName() {
        updateOwnerTest((owner) -> owner.setName("William Street"));
    }

    @Test
    public void updateIdCardOrCorpNumber() {
        updateOwnerTest((owner) -> owner.setIdCardOrCorpNumber("JJ00000000"));
    }

    @Test
    public void updateIsCorp() {
        updateOwnerTest((owner) -> owner.setCorp(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullOwner() {
        ownerManager.updateOwner(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void updateNullId() {
        updateOwnerTest((owner) -> owner.setId(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullName() {
        updateOwnerTest((owner) -> owner.setName(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullIdCardOrCorpNumber() {
        updateOwnerTest((owner) -> owner.setIdCardOrCorpNumber(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullIsCorp() {
        updateOwnerTest((owner) -> owner.setCorp(null));
    }

    @Test(expected = IllegalEntityException.class)
    public void updateNonExistingOwner() {
        Owner owner = sampleCraigOwnerBuilder().id(1000L).build();
        ownerManager.updateOwner(owner);
    }

    @Test
    public void deleteOwner() {
        Owner james = sampleJamesOwnerBuilder().build();
        Owner mary = sampleMaryOwnerBuilder().build();

        ownerManager.createOwner(james);
        ownerManager.createOwner(mary);

        assertThat(ownerManager.retrieveOwnerById(james.getId())).isNotNull();
        assertThat(ownerManager.retrieveOwnerById(mary.getId())).isNotNull();

        ownerManager.deleteOwner(james);

        assertThat(ownerManager.retrieveOwnerById(james.getId())).isNull();
        assertThat(ownerManager.retrieveOwnerById(mary.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullOwner() {
        ownerManager.deleteOwner(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteOwnerWithNullId() {
        Owner owner = sampleCraigOwnerBuilder().build();
        ownerManager.deleteOwner(owner);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteNonExistingOwner() {
        Owner owner = sampleCraigOwnerBuilder().id(1000L).build();
        ownerManager.deleteOwner(owner);
    }

    private void testExpectedServiceFailureException(Operation<OwnerManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);

        ownerManager = new OwnerManagerImpl(failingDataSource);

        assertThatThrownBy(() -> operation.callOn(ownerManager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void createOwnerWithSqlExceptionThrown() throws SQLException {
        Owner owner = sampleMaryOwnerBuilder().build();
        testExpectedServiceFailureException((ownerManager) -> ownerManager.createOwner(owner));
    }

    @Test
    public void updateOwnerWithSqlExceptionThrown() throws SQLException {
        Owner owner = sampleMaryOwnerBuilder().build();
        ownerManager.createOwner(owner);
        testExpectedServiceFailureException((ownerManager) -> ownerManager.updateOwner(owner));
    }

    @Test
    public void retrieveOwnerWithSqlExceptionThrown() throws SQLException {
        Owner owner = sampleMaryOwnerBuilder().build();
        ownerManager.createOwner(owner);
        testExpectedServiceFailureException((ownerManager) -> ownerManager.retrieveOwnerById(owner.getId()));
    }

    @Test
    public void retrieveAllOwnersWithSqlExceptionThrown() throws SQLException {
        Owner owner = sampleMaryOwnerBuilder().build();
        ownerManager.createOwner(owner);
        testExpectedServiceFailureException((ownerManager) -> ownerManager.retrieveAllOwners());
    }

    @Test
    public void deleteOwnerWithSqlExceptionThrown() throws SQLException {
        Owner owner = sampleMaryOwnerBuilder().build();
        ownerManager.createOwner(owner);
        testExpectedServiceFailureException((ownerManager) -> ownerManager.deleteOwner(owner));
    }
}
