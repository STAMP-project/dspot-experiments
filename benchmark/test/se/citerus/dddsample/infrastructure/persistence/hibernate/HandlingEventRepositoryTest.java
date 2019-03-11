package se.citerus.dddsample.infrastructure.persistence.hibernate;


import HandlingEvent.Type;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;


@RunWith(SpringRunner.class)
@ContextConfiguration({ "/context-infrastructure-persistence.xml" })
@Transactional
public class HandlingEventRepositoryTest {
    @Autowired
    HandlingEventRepository handlingEventRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateTransactionManager transactionManager;

    private JdbcTemplate jdbcTemplate;

    @Test
    public void testSave() {
        Location location = locationRepository.find(new UnLocode("SESTO"));
        Cargo cargo = cargoRepository.find(new TrackingId("XYZ"));
        Date completionTime = new Date(10);
        Date registrationTime = new Date(20);
        HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, Type.CLAIM, location);
        handlingEventRepository.store(event);
        flush();
        Map<String, Object> result = jdbcTemplate.queryForMap("select * from HandlingEvent where id = ?", getLongId(event));
        assertThat(result.get("CARGO_ID")).isEqualTo(1L);
        Date completionDate = new Date(((Timestamp) (result.get("COMPLETIONTIME"))).getTime());// equals call is not symmetric between java.sql.Timestamp and java.util.Date, so we should convert Timestamp Date

        assertThat(completionDate).isEqualTo(new Date(10));
        Date registrationDate = new Date(((Timestamp) (result.get("REGISTRATIONTIME"))).getTime());// equals call is not symmetric between java.sql.Timestamp and java.util.Date, so we should convert Timestamp Date

        assertThat(registrationDate).isEqualTo(new Date(20));
        assertThat(result.get("TYPE")).isEqualTo("CLAIM");
        // TODO: the rest of the columns
    }

    @Test
    public void testFindEventsForCargo() {
        TrackingId trackingId = new TrackingId("XYZ");
        List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
        assertThat(handlingEvents).hasSize(12);
    }
}

