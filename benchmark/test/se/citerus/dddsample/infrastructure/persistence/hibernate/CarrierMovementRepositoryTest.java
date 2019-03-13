package se.citerus.dddsample.infrastructure.persistence.hibernate;


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
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;


@RunWith(SpringRunner.class)
@ContextConfiguration({ "/context-infrastructure-persistence.xml" })
@Transactional
public class CarrierMovementRepositoryTest {
    @Autowired
    VoyageRepository voyageRepository;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateTransactionManager transactionManager;

    private JdbcTemplate jdbcTemplate;

    @Test
    public void testFind() {
        Voyage voyage = voyageRepository.find(new VoyageNumber("0101"));
        assertThat(voyage).isNotNull();
        assertThat(voyage.voyageNumber().idString()).isEqualTo("0101");
        /* TODO adapt
        assertThat(carrierMovement.departureLocation()).isEqualTo(STOCKHOLM);
        assertThat(carrierMovement.arrivalLocation()).isEqualTo(HELSINKI);
        assertThat(carrierMovement.departureTime()).isEqualTo(DateTestUtil.toDate("2007-09-23", "02:00"));
        assertThat(carrierMovement.arrivalTime()).isEqualTo(DateTestUtil.toDate("2007-09-23", "03:00"));
         */
    }
}

