package se.jsquad.repository;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import se.jsquad.SystemProperty;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.Persistence;
import javax.transaction.TransactionScoped;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(WeldJunit5Extension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.SAME_THREAD)
class SystemPropertyRepositoryWeldTest {
    @Inject
    private SystemPropertyRepository systemPropertyRepository;

    @WeldSetup
    WeldInitiator weld = WeldInitiator.from(SystemPropertyRepository.class).activate(TransactionScoped.class)
                .setPersistenceContextFactory(getEntityManager()).build();

    static Function<InjectionPoint, Object> getEntityManager() {
        Properties properties = new Properties();
        properties.setProperty(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence.xml");

        return injectionPoint -> Persistence.createEntityManagerFactory("openBankPU", properties)
                .createEntityManager();
    }

    @Test
    @Order(2)
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
    @Order(1)
    public void testSecondaryCacheLevelIsEmpty() throws NoSuchFieldException, IllegalAccessException {
        // Given
        SystemProperty systemProperty = new SystemProperty();

        Field field = SystemProperty.class.getDeclaredField("id");
        field.setAccessible(true);

        // Set value
        field.set(systemProperty, Long.valueOf(1000));

        // Then
        assertEquals(false,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
    }

    @Test
    @Order(3)
    public void testSecondaryCacheLevelIsCached() throws NoSuchFieldException, IllegalAccessException {
        // Given
        SystemProperty systemProperty = new SystemProperty();

        Field field = SystemProperty.class.getDeclaredField("id");
        field.setAccessible(true);

        // Set value
        field.set(systemProperty, Long.valueOf(1000));

        // Then
        assertEquals(true,
                systemPropertyRepository.getEntityManager().getEntityManagerFactory().getCache().contains(
                        SystemProperty.class, systemProperty.getId()));
    }

    @Test
    @Order(4)
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
}
