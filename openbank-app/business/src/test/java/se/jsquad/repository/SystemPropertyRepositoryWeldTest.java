package se.jsquad.repository;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.jsquad.SystemProperty;
import se.jsquad.producer.LoggerProducer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SystemPropertyRepositoryWeldTest {
    private static SystemPropertyRepository systemPropertyRepository;
    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;

    private static Weld weld;
    private static WeldContainer weldContainer;

    @BeforeEach
    void initSystemPropertyRepositoryForEachUnitTest() throws IllegalAccessException, NoSuchFieldException {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        entityManagerFactory = Persistence.createEntityManagerFactory("openBankPU", properties);
        entityManager = entityManagerFactory.createEntityManager();

        weld = new Weld();
        weld.disableDiscovery();

        weldContainer = weld.beanClasses(SystemPropertyRepository.class)
                .addBeanClass(LoggerProducer.class).addBeanClass(EntityManagerProducer.class).initialize();
        systemPropertyRepository = weldContainer.select(SystemPropertyRepository.class).get();

        Field field = EntityManagerProducer.class.getDeclaredField("entityManager");
        field.setAccessible(true);

        // Set value
        field.set(systemPropertyRepository, entityManager);
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
        assertEquals(1000, systemProperty.getId());
        assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
    }

    @Test
    public void testSecondaryCacheLevelAfterClearAndRefresh() throws NoSuchFieldException,
            IllegalAccessException {
        // Given
        SystemProperty systemProperty = new SystemProperty();

        Field field = SystemProperty.class.getDeclaredField("id");
        field.setAccessible(true);

        // Set value
        field.set(systemProperty, Long.valueOf(1000));

        // When
        systemPropertyRepository.clearSecondaryLevelCache();

        // Then
        assertEquals(false,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
        // When
        systemPropertyRepository.findAllUniqueSystemProperties();


        // Then
        assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
    }

    @Test
    public void testSecondaryCacheLevelAfterClearAndRefreshSecondaryLevelCache() throws NoSuchFieldException,
            IllegalAccessException {
        // Given
        SystemProperty systemProperty = new SystemProperty();

        Field field = SystemProperty.class.getDeclaredField("id");
        field.setAccessible(true);

        // Set value
        field.set(systemProperty, Long.valueOf(1000));

        // When
        systemPropertyRepository.clearSecondaryLevelCache();

        // Then
        assertEquals(false,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
        // When
        systemPropertyRepository.refreshSecondaryLevelCache();


        // Then
        assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
    }
}
