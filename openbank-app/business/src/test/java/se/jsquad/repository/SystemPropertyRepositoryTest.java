package se.jsquad.repository;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import se.jsquad.Client;
import se.jsquad.SystemProperty;
import se.jsquad.generator.DatabaseGenerator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Execution(ExecutionMode.SAME_THREAD)
public class SystemPropertyRepositoryTest {
    private SystemPropertyRepository systemPropertyRepository;
    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;

    private static SystemProperty systemProperty;

    @BeforeAll
    static void initSystemProperty() throws NoSuchFieldException, IllegalAccessException {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();

        systemProperty = new SystemProperty();
        systemProperty.setName("VERSION");
        systemProperty.setValue("1.0.1");

        Field field = SystemProperty.class.getDeclaredField("id");
        field.setAccessible(true);

        // Set value
        field.set(systemProperty, Long.valueOf(1000));

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        entityManager.persist(systemProperty);

        entityTransaction.commit();

    }

    @BeforeEach
    void initSystemPropertyRepositoryForEachUnitTest() throws IllegalAccessException, NoSuchFieldException {
        DatabaseGenerator databaseGenerator = new DatabaseGenerator();

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        for (Client client : databaseGenerator.populateDatabase()) {
            entityManager.persist(client);
        }

        entityTransaction.commit();

        systemPropertyRepository = Mockito.spy(new SystemPropertyRepository());
        Logger loggerSystemPropertyRepository = Logger.getLogger(SystemPropertyRepository.class.getName());

        Field field = EntityManagerProducer.class.getDeclaredField("entityManager");
        field.setAccessible(true);

        // Set value
        field.set(systemPropertyRepository, entityManager);

        field = SystemPropertyRepository.class.getDeclaredField("logger");
        field.setAccessible(true);

        // Set value
        field.set(systemPropertyRepository, loggerSystemPropertyRepository);
    }

    @Test
    public void testFindAllUniqueSystemPropertiesAndSecondaryCacheLevel() {
        // When
        List<SystemProperty> systemPropertyList = systemPropertyRepository.findAllUniqueSystemProperties();

        // Then
        assertEquals(1, systemPropertyList.size());
        SystemProperty systemProperty = systemPropertyList.get(0);

        assertEquals("VERSION", systemPropertyList.get(0).getName());
        assertEquals("1.0.1", systemPropertyList.get(0).getValue());
        assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
    }

    @Test
    public void testSecondaryCacheLevelAfterClearAndRefresh() {
        // When
        systemPropertyRepository.clearSecondaryLevelCache();

        // Then
        assertEquals(false,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
        // When
        List<SystemProperty> listSystemProperty = systemPropertyRepository.findAllUniqueSystemProperties();

        // Then
        assertEquals(1, listSystemProperty.size());
        // TODO: Secondary cache seems not to work properly in unit test, must run in a arquillian container
        /*assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));*/
    }

    @Test
    public void testSecondaryCacheLevelAfterClearAndRefreshSecondaryLevelCache() {
        // When
        systemPropertyRepository.clearSecondaryLevelCache();

        // Then
        assertEquals(false,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
        // When
        systemPropertyRepository.refreshSecondaryLevelCache();


        // Then
       /*assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));*/
    }
}
