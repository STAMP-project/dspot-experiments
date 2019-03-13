package com.baeldung.persistence.audit;


import OPERATION.INSERT;
import OPERATION.UPDATE;
import com.baeldung.persistence.model.Bar;
import com.baeldung.persistence.service.IBarService;
import com.baeldung.spring.config.PersistenceTestConfig;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceTestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class JPABarAuditIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(JPABarAuditIntegrationTest.class);

    @Autowired
    @Qualifier("barJpaService")
    private IBarService barService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager em;

    @Test
    public final void whenBarsModified_thenBarsAudited() {
        // insert BAR1
        Bar bar1 = new Bar("BAR1");
        barService.create(bar1);
        // update BAR1
        bar1.setName("BAR1a");
        barService.update(bar1);
        // insert BAR2
        Bar bar2 = new Bar("BAR2");
        barService.create(bar2);
        // update BAR1
        bar1.setName("BAR1b");
        barService.update(bar1);
        // get BAR1 and BAR2 from the DB and check the audit values
        // detach instances from persistence context to make sure we fire db
        em.detach(bar1);
        em.detach(bar2);
        bar1 = barService.findOne(bar1.getId());
        bar2 = barService.findOne(bar2.getId());
        Assert.assertNotNull(bar1);
        Assert.assertNotNull(bar2);
        Assert.assertEquals(UPDATE, bar1.getOperation());
        Assert.assertEquals(INSERT, bar2.getOperation());
        Assert.assertTrue(((bar1.getTimestamp()) > (bar2.getTimestamp())));
        barService.deleteById(bar1.getId());
        barService.deleteById(bar2.getId());
    }
}

