package se.citerus.dddsample.infrastructure.persistence.hibernate;


import java.util.List;
import javax.sql.DataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;


@RunWith(SpringRunner.class)
@ContextConfiguration({ "/context-infrastructure-persistence.xml" })
@Transactional
public class LocationRepositoryTest {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateTransactionManager transactionManager;

    @Test
    public void testFind() {
        final UnLocode melbourne = new UnLocode("AUMEL");
        Location location = locationRepository.find(melbourne);
        assertThat(location).isNotNull();
        assertThat(location.unLocode()).isEqualTo(melbourne);
        assertThat(locationRepository.find(new UnLocode("NOLOC"))).isNull();
    }

    @Test
    public void testFindAll() {
        List<Location> allLocations = locationRepository.findAll();
        assertThat(allLocations).isNotNull();
        assertThat(allLocations).hasSize(7);
    }
}

