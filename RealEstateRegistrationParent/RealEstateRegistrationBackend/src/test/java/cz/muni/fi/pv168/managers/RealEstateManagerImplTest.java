package cz.muni.fi.pv168.managers;

import cz.muni.fi.pv168.entities.RealEstate;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.exceptions.ValidationException;
import cz.muni.fi.pv168.utils.DBUtils;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Martin Podhora
 */
public class RealEstateManagerImplTest {
    private RealEstateManagerImpl realEstateManager;
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
        realEstateManager = new RealEstateManagerImpl(dataSource);
    }

    @After
    public void tearDown() throws SQLException, IOException {
        DBUtils.executeSqlScript(dataSource, getClass().getClassLoader().getResource("dropTables.sql").openStream());
    }


    private RealEstateBuilder sampleBrnoEstateBuilder() {
        return new RealEstateBuilder()
                .cadastralArea("Brno")
                .parcelNumber("123a")
                .areaInMetersSquared(Double.valueOf(15));
    }

    private RealEstateBuilder sampleBratislavaEstateBuilder() {
        return new RealEstateBuilder()
                .cadastralArea("Bratislava")
                .parcelNumber("321b")
                .areaInMetersSquared(Double.valueOf(35.4));
    }

    @Test
    public void createOwnerPerson() {
        RealEstate estate = sampleBrnoEstateBuilder().build();
        realEstateManager.createRealEstate(estate);

        Long estateId = estate.getId();
        assertThat(estateId).isNotNull();

        assertThat(realEstateManager.retrieveRealEstateById(estateId))
                .isNotSameAs(estate)
                .isEqualToComparingFieldByField(estate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNullRealEstate() {
        realEstateManager.createRealEstate(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void createRealEstateWithExistingId() {
        RealEstate estate = sampleBratislavaEstateBuilder()
                .id(1L)
                .build();
        realEstateManager.createRealEstate(estate);
    }

    @Test(expected = ValidationException.class)
    public void createRealEstateWithNullCadastralArea() {
        RealEstate estate = sampleBrnoEstateBuilder()
                .cadastralArea(null)
                .build();
        realEstateManager.createRealEstate(estate);
    }

    @Test(expected = ValidationException.class)
    public void createRealEstateWithNullParcelNumber() {
        RealEstate estate = sampleBrnoEstateBuilder()
                .parcelNumber(null)
                .build();
        realEstateManager.createRealEstate(estate);
    }

    @Test(expected = ValidationException.class)
    public void createRealEstateWithSubzeroArea() {
        RealEstate estate = sampleBrnoEstateBuilder()
                .areaInMetersSquared(Double.valueOf(-8.5))
                .build();
        realEstateManager.createRealEstate(estate);
    }

    @Test(expected = ValidationException.class)
    public void createRealEstateWithNullArea() {
        RealEstate estate = sampleBrnoEstateBuilder()
                .areaInMetersSquared(null)
                .build();
        realEstateManager.createRealEstate(estate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieveRealEstate() {
        realEstateManager.retrieveRealEstateById(null);
    }

    @Test
    public void retrieveAllRealEstates() {
        assertThat(realEstateManager.retrieveAllRealEstates()).isEmpty();

        RealEstate brno = sampleBrnoEstateBuilder().build();
        RealEstate bratislava = sampleBratislavaEstateBuilder().build();

        realEstateManager.createRealEstate(brno);
        realEstateManager.createRealEstate(bratislava);

        assertThat(realEstateManager.retrieveAllRealEstates())
                .usingFieldByFieldElementComparator()
                .containsOnly(brno, bratislava);
    }

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }

    private void updateRealEstateTest(Operation<RealEstate> updateOperation) {
        RealEstate estateToUpdate = sampleBratislavaEstateBuilder().build();
        RealEstate anotherEstate = sampleBrnoEstateBuilder().build();

        realEstateManager.createRealEstate(estateToUpdate);
        realEstateManager.createRealEstate(anotherEstate);

        updateOperation.callOn(estateToUpdate);

        realEstateManager.updateRealEstate(estateToUpdate);

        assertThat(realEstateManager.retrieveRealEstateById(estateToUpdate.getId()))
                .isEqualToComparingFieldByField(estateToUpdate);
        assertThat(realEstateManager.retrieveRealEstateById(anotherEstate.getId()))
                .isEqualToComparingFieldByField(anotherEstate);
    }

    @Test
    public void updateCadastralArea() {
        updateRealEstateTest((estate) -> estate.setCadastralArea("Bratislava-Petržalka"));
    }

    @Test
    public void updateParcelNumber() {
        updateRealEstateTest((estate) -> estate.setParcelNumber("897c"));
    }

    @Test
    public void updateAreaInMetresSquared() {
        updateRealEstateTest((estate) -> estate.setAreaInMetersSquared(Double.valueOf(1000)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullRealEstate() {
        realEstateManager.updateRealEstate(null);
    }

    @Test(expected = ValidationException.class)
    public void updateNullCadastralArea() {
        updateRealEstateTest((estate) -> estate.setCadastralArea(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullParcelNumber() {
        updateRealEstateTest((estate) -> estate.setParcelNumber(null));
    }

    @Test(expected = ValidationException.class)
    public void updateNullAreaInMetresSquared() {
        updateRealEstateTest((estate) -> estate.setAreaInMetersSquared(null));
    }

    @Test(expected = ValidationException.class)
    public void updateSubzeroAreaInMetersSquared() {
        updateRealEstateTest((estate) -> estate.setAreaInMetersSquared(Double.valueOf(-8888)));
    }

    @Test(expected = IllegalEntityException.class)
    public void updateNonExistingRealEstate() {
        RealEstate estate = sampleBrnoEstateBuilder().id(1000L).build();
        realEstateManager.updateRealEstate(estate);
    }

    @Test
    public void deleteRealEstate() {
        RealEstate brno = sampleBrnoEstateBuilder().build();
        RealEstate bratislava = sampleBratislavaEstateBuilder().build();

        realEstateManager.createRealEstate(brno);
        realEstateManager.createRealEstate(bratislava);

        assertThat(realEstateManager.retrieveRealEstateById(brno.getId())).isNotNull();
        assertThat(realEstateManager.retrieveRealEstateById(bratislava.getId())).isNotNull();

        realEstateManager.deleteRealEstate(brno);

        assertThat(realEstateManager.retrieveRealEstateById(brno.getId())).isNull();
        assertThat(realEstateManager.retrieveRealEstateById(bratislava.getId())).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullRealEstate() {
        realEstateManager.deleteRealEstate(null);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteRealEstateWithNullId() {
        RealEstate estate = sampleBrnoEstateBuilder().id(null).build();
        realEstateManager.deleteRealEstate(estate);
    }

    @Test(expected = IllegalEntityException.class)
    public void deleteNonExistingRealEstate() {
        RealEstate estate = sampleBrnoEstateBuilder().id(1000L).build();
        realEstateManager.deleteRealEstate(estate);
    }

    private void testExpectedServiceFailureException(Operation<RealEstateManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);

        realEstateManager = new RealEstateManagerImpl(failingDataSource);

        assertThatThrownBy(() -> operation.callOn(realEstateManager))
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void createOwnerWithSqlExceptionThrown() throws SQLException {
        RealEstate estate = sampleBrnoEstateBuilder().build();
        testExpectedServiceFailureException((realEstateManager) -> realEstateManager.createRealEstate(estate));
    }

    @Test
    public void updateOwnerWithSqlExceptionThrown() throws SQLException {
        RealEstate estate = sampleBrnoEstateBuilder().build();
        realEstateManager.createRealEstate(estate);
        testExpectedServiceFailureException((realEstateManager) -> realEstateManager.updateRealEstate(estate));
    }

    @Test
    public void retrieveOwnerWithSqlExceptionThrown() throws SQLException {
        RealEstate estate = sampleBrnoEstateBuilder().build();
        realEstateManager.createRealEstate(estate);
        testExpectedServiceFailureException((realEstateManager) -> realEstateManager.retrieveRealEstateById(estate.getId()));
    }

    @Test
    public void retrieveAllOwnersWithSqlExceptionThrown() throws SQLException {
        RealEstate estate = sampleBrnoEstateBuilder().build();
        realEstateManager.createRealEstate(estate);
        testExpectedServiceFailureException((realEstateManager) -> realEstateManager.retrieveAllRealEstates());
    }

    @Test
    public void deleteOwnerWithSqlExceptionThrown() throws SQLException {
        RealEstate estate = sampleBrnoEstateBuilder().build();
        realEstateManager.createRealEstate(estate);
        testExpectedServiceFailureException((realEstateManager) -> realEstateManager.deleteRealEstate(estate));
    }
}
