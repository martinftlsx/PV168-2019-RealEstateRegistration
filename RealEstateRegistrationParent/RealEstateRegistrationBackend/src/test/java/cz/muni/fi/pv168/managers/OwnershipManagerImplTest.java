package cz.muni.fi.pv168.managers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.util.List;
import javax.sql.DataSource;

import cz.muni.fi.pv168.entities.Owner;
import cz.muni.fi.pv168.entities.Ownership;
import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.exceptions.ValidationException;
import cz.muni.fi.pv168.utils.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Branislav Smolicek
 */
public class OwnershipManagerImplTest {

    private OwnershipManagerImpl manager;
    private OwnerManagerImpl ownerManager;
    private RealEstateManagerImpl realEstateManager;
    private DataSource ds;

    private final static ZonedDateTime NOW
            = LocalDateTime.of(2016, Month.FEBRUARY, 29, 17, 12).atZone(ZoneId.systemDefault());

    private final static ZonedDateTime BEFORE
            = LocalDateTime.of(2014, Month.MARCH, 15, 4, 26).atZone(ZoneId.systemDefault());

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    //--------------------------------------------------------------------------
    // Test initialization
    //--------------------------------------------------------------------------

    private static DataSource prepareDataSource() {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        ds.setDatabaseName("memory:realEstateRegistration-test");
        ds.setCreateDatabase("create");
        return ds;
    }

    @Before
    public void setUp() throws SQLException, IOException {
        ds = prepareDataSource();
        DBUtils.executeSqlScript(ds, getClass().getClassLoader().getResource("createTables.sql").openStream());
        manager = new OwnershipManagerImpl(ds, Clock.fixed(NOW.toInstant(), NOW.getZone()));
        ownerManager = new OwnerManagerImpl(ds);
        realEstateManager = new RealEstateManagerImpl(ds);
        prepareTestData();
    }

    @After
    public void tearDown() throws SQLException, IOException {
        DBUtils.executeSqlScript(ds, getClass().getClassLoader().getResource("dropTables.sql").openStream());
    }

    //--------------------------------------------------------------------------
    // Preparing test data
    //--------------------------------------------------------------------------

    private Owner owner1, owner2, owner3, ownerWithNullId, ownerNotInDB;
    private RealEstate realEst1, realEst2, realEst3, realEst4, realEst5, realEstateWithNullId, realEstateNotInDB;

    private void prepareTestData() {

        owner1 = new OwnerBuilder().idCardOrCorpNumber("A1B3C002").isCorp(true).name("Owner 1").build();
        owner2 = new OwnerBuilder().idCardOrCorpNumber("A2B3").isCorp(false).name("Owner 2").build();
        owner3 = new OwnerBuilder().idCardOrCorpNumber("A3B3").isCorp(false).name("Owner 3").build();

        realEst1 = new RealEstateBuilder().areaInMetersSquared(25.2).cadastralArea("Brno-stred").parcelNumber("5").build();
        realEst2 = new RealEstateBuilder().areaInMetersSquared(5.0).cadastralArea("Ostrava-2").parcelNumber("A31").build();
        realEst3 = new RealEstateBuilder().areaInMetersSquared(110.34).cadastralArea("Brno-juh").parcelNumber("Y81").build();
        realEst4 = new RealEstateBuilder().areaInMetersSquared(51.0).cadastralArea("Praha-4").parcelNumber("G5").build();
        realEst5 = new RealEstateBuilder().areaInMetersSquared(16.97).cadastralArea("Brno-zapad").parcelNumber("C2").build();

        ownerManager.createOwner(owner1);
        ownerManager.createOwner(owner2);
        ownerManager.createOwner(owner3);

        realEstateManager.createRealEstate(realEst1);
        realEstateManager.createRealEstate(realEst2);
        realEstateManager.createRealEstate(realEst3);
        realEstateManager.createRealEstate(realEst4);
        realEstateManager.createRealEstate(realEst5);

        ownerWithNullId = new OwnerBuilder().id(null).build();
        ownerNotInDB = new OwnerBuilder().id(owner3.getId() + 100).build();

        realEstateWithNullId = new RealEstateBuilder().areaInMetersSquared(0.0).id(null).build();
        realEstateNotInDB = new RealEstateBuilder().areaInMetersSquared(0.0).id(realEst5.getId() + 100).build();
    }


    private OwnershipBuilder sampleOwnershipBuilder() {
        return new OwnershipBuilder()
                .owner(owner2)
                .realEstate(realEst2)
                .ownershipCreated(NOW)
                .shareNumerator(1)
                .shareDenominator(3);
    }


    @Test
    public void createOwnership() {
        Ownership ownership = sampleOwnershipBuilder().realEstate(realEst1).owner(owner3).build();
        manager.createOwnership(ownership);

        Long ownershipId = ownership.getId();
        assertThat(ownershipId).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullOwnership() {
        manager.createOwnership(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void createOwnershipWithExistingId() {
        Ownership ownership = sampleOwnershipBuilder()
                .id(1L)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithNullOwner() {
        Ownership ownership = sampleOwnershipBuilder()
                .owner(null)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithNullRealEstate() {
        Ownership ownership = sampleOwnershipBuilder()
                .realEstate(null)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithNullShareNumerator() {
        Ownership ownership = sampleOwnershipBuilder()
                .shareNumerator(null)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithNegativeShareNumerator() {
        Ownership ownership = sampleOwnershipBuilder()
                .shareNumerator(-1)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithNullShareDenominator() {
        Ownership ownership = sampleOwnershipBuilder()
                .shareDenominator(null)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithZeroShareDenominator() {
        Ownership ownership = sampleOwnershipBuilder()
                .shareDenominator(0)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithNegativeShareDenominator() {
        Ownership ownership = sampleOwnershipBuilder()
                .shareDenominator(-3)
                .build();
        manager.createOwnership(ownership);
    }

    @Test(expected = ValidationException.class)
    public void createOwnershipWithTimeRemoved() {
        Ownership ownership = sampleOwnershipBuilder()
                .ownershipRemoved(NOW)
                .build();
        manager.createOwnership(ownership);
    }

    @Test
    public void retrieveAllOwnerships() {
        assertThat(manager.retrieveAllOwnerships()).isEmpty();

        Ownership ownership1 = sampleOwnershipBuilder().realEstate(realEst1).owner(owner3).build();
        Ownership ownership2 = sampleOwnershipBuilder().realEstate(realEst2).owner(owner2).build();

        manager.createOwnership(ownership1);
        manager.createOwnership(ownership2);
        List<Ownership> ownerships = manager.retrieveAllOwnerships();
        assertThat(ownerships).containsOnly(ownership1, ownership2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveOwnershipsForNullOwner() {
        manager.retrieveOwnershipsForOwner(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void retrieveOwnershipsForOwnerHavingNullId() {
        manager.retrieveOwnershipsForOwner(ownerWithNullId);
    }

    @Test
    public void retrieveOwnershipsForOwner() {
        assertThat(manager.retrieveOwnershipsForOwner(owner1)).isEmpty();
        assertThat(manager.retrieveOwnershipsForOwner(owner2)).isEmpty();
        assertThat(manager.retrieveOwnershipsForOwner(owner3)).isEmpty();

        manager.createOwnership(sampleOwnershipBuilder()
                .realEstate(realEst1)
                .owner(owner3)
                .build());

        assertThat(manager.retrieveOwnershipsForOwner(owner3).size() == 1);
        assertThat(manager.retrieveOwnershipsForOwner(owner3).get(0).getRealEstate().equals(realEst1)).isTrue();

        assertThat(manager.retrieveOwnershipsForOwner(owner1)).isEmpty();
        assertThat(manager.retrieveOwnershipsForOwner(owner2)).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveOwnershipsForNullRealEstate() {
        manager.retrieveOwnershipsForRealEstate(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void retrieveOwnershipsForRealEstateHavingNullId() {
        manager.retrieveOwnershipsForRealEstate(realEstateWithNullId);
    }

    @Test
    public void retrieveOwnershipsForRealEstate() {

        assertThat(manager.retrieveOwnershipsForRealEstate(realEst1)).isEmpty();
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst2)).isEmpty();
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst3)).isEmpty();
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst4)).isEmpty();
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst5)).isEmpty();

        manager.createOwnership(sampleOwnershipBuilder()
                .realEstate(realEst1)
                .owner(owner3)
                .build());

        assertThat(manager.retrieveOwnershipsForRealEstate(realEst1).size() == 1);
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst1).get(0).getOwner().equals(owner3)).isTrue();

        assertThat(manager.retrieveOwnershipsForRealEstate(realEst2)).isEmpty();
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst3)).isEmpty();
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst4)).isEmpty();
        assertThat(manager.retrieveOwnershipsForRealEstate(realEst5)).isEmpty();
    }

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }

    private void updateOwnershipTest(Operation<Ownership> updateOperation) {
        Ownership ownershipToUpdate = sampleOwnershipBuilder().realEstate(realEst1).owner(owner3).build();
        Ownership anotherOwnership = sampleOwnershipBuilder().realEstate(realEst2).owner(owner2).build();

        manager.createOwnership(ownershipToUpdate);
        manager.createOwnership(anotherOwnership);

        updateOperation.callOn(ownershipToUpdate);

        manager.updateOwnership(ownershipToUpdate);

        Ownership o1 = manager.retrieveOwnershipById(ownershipToUpdate.getId());
        Ownership o2 = manager.retrieveOwnershipById(anotherOwnership.getId());

        assertThat(manager.retrieveOwnershipById(ownershipToUpdate.getId()))
                .isEqualToComparingFieldByField(ownershipToUpdate);
        assertThat(manager.retrieveOwnershipById(anotherOwnership.getId()))
                .isEqualToComparingFieldByField(anotherOwnership);
    }

    @Test
    public void updateOwner() {
        updateOwnershipTest((ownership) -> ownership.setOwner(owner1));
    }

    @Test
    public void updateRealEstate() {
        updateOwnershipTest((ownership) -> ownership.setRealEstate(realEst1));
    }

    @Test
    public void updateOwnershipCreated() {
        updateOwnershipTest((ownership) -> ownership.setOwnershipCreated(ZonedDateTime.now()));
    }

    @Test
    public void updateOwnershipRemoved() {
        updateOwnershipTest((ownership) -> ownership.setOwnershipRemoved(NOW));
    }

    @Test(expected = ValidationException.class)
    public void updateOwnershipRemovedEarlierThanCreated() {
        updateOwnershipTest((ownership) -> {
            ownership.setOwnershipCreated(NOW);
            ownership.setOwnershipRemoved(BEFORE);
        });
    }

    @Test
    public void updateShareNumerator() {
        updateOwnershipTest((ownership) -> ownership.setShareNumerator(1));
    }

    @Test(expected = ValidationException.class)
    public void updateNegativeShareNumerator() {
        updateOwnershipTest((ownership) -> ownership.setShareDenominator(-1));
    }

    @Test
    public void updateShareDenominator() {
        updateOwnershipTest((ownership) -> ownership.setShareDenominator(4));
    }

    @Test(expected = ValidationException.class)
    public void updateZeroShareDenominator() {
        updateOwnershipTest((ownership) -> ownership.setShareDenominator(0));
    }

    @Test(expected = ValidationException.class)
    public void updateNegativeShareDenominator() {
        updateOwnershipTest((ownership) -> ownership.setShareDenominator(-2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullOwnership() {
        manager.updateOwnership(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void updateNullId() {
        updateOwnershipTest((ownership) -> ownership.setId(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullOwner() {
        updateOwnershipTest((ownership) -> ownership.setOwner(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullRealEstate() {
        updateOwnershipTest((ownership) -> ownership.setRealEstate(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullNumerator() {
        updateOwnershipTest((ownership) -> ownership.setShareNumerator(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullDenominator() {
        updateOwnershipTest((ownership) -> ownership.setShareDenominator(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullOwnershipCreated() {
        updateOwnershipTest((ownership) -> ownership.setOwnershipCreated(null));
    }

    @Test
    public void updateNullOwnershipRemoved() {
        updateOwnershipTest((ownership) -> ownership.setOwnershipRemoved(null));
    }

    @Test(expected = IllegalEntityException.class)
    public void updateNonExistingOwnership() {
        Ownership ownership = sampleOwnershipBuilder().id(1000L).build();
        manager.updateOwnership(ownership);
    }

    @Test
    public void deleteOwnership() {
        Ownership ownership1 = sampleOwnershipBuilder().build();
        Ownership ownership2 = sampleOwnershipBuilder().build();

        manager.createOwnership(ownership1);
        manager.createOwnership(ownership2);

        assertThat(manager.retrieveOwnershipById(ownership1.getId())).isNotNull();
        assertThat(manager.retrieveOwnershipById(ownership2.getId())).isNotNull();

        manager.deleteOwnership(ownership1);

        assertThat(manager.retrieveOwnershipById(ownership1.getId())).isNull();
        assertThat(manager.retrieveOwnershipById(ownership2.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullOwnership() {
        manager.deleteOwnership(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteOwnershipWithNullId() {
        Ownership ownership = sampleOwnershipBuilder().id(null).build();
        manager.deleteOwnership(ownership);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteNonExistingOwnership() {
        Ownership ownership = sampleOwnershipBuilder().id(1000L).build();
        manager.deleteOwnership(ownership);
    }

    private void testExpectedServiceFailureException(OwnershipManagerImplTest.Operation<OwnershipManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);

        manager = new OwnershipManagerImpl(failingDataSource, Clock.fixed(NOW.toInstant(), NOW.getZone()));

        assertThatThrownBy(() -> operation.callOn(manager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void createOwnerWithSqlExceptionThrown() throws SQLException {
        Ownership ownership = sampleOwnershipBuilder().realEstate(realEst2).owner(owner2).build();
        testExpectedServiceFailureException((manager) -> manager.createOwnership(ownership));
    }

    @Test
    public void updateOwnerWithSqlExceptionThrown() throws SQLException {
        Ownership ownership = sampleOwnershipBuilder().realEstate(realEst2).owner(owner2).build();
        manager.createOwnership(ownership);
        testExpectedServiceFailureException((manager) -> manager.updateOwnership(ownership));
    }

    @Test
    public void retrieveOwnerWithSqlExceptionThrown() throws SQLException {
        Ownership ownership = sampleOwnershipBuilder().realEstate(realEst2).owner(owner2).build();
        manager.createOwnership(ownership);
        testExpectedServiceFailureException((manager) -> manager.retrieveOwnershipById(ownership.getId()));
    }

    @Test
    public void retrieveAllOwnersWithSqlExceptionThrown() throws SQLException {
        Ownership ownership = sampleOwnershipBuilder().realEstate(realEst2).owner(owner2).build();
        manager.createOwnership(ownership);
        testExpectedServiceFailureException((manager) -> manager.retrieveAllOwnerships());
    }

    @Test
    public void deleteOwnerWithSqlExceptionThrown() throws SQLException {
        Ownership ownership = sampleOwnershipBuilder().realEstate(realEst2).owner(owner2).build();
        manager.createOwnership(ownership);
        testExpectedServiceFailureException((manager) -> manager.deleteOwnership(ownership));
    }
}

