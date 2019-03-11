package com.baeldung.services;


import com.baeldung.config.PersistenceConfiguration;
import com.baeldung.domain.Bar;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { PersistenceConfiguration.class }, loader = AnnotationConfigContextLoader.class)
public class SpringDataJPABarAuditIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(SpringDataJPABarAuditIntegrationTest.class);

    @Autowired
    @Qualifier("barSpringDataJpaService")
    private IBarService barService;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private EntityManager em;

    @Test
    @WithMockUser(username = "tutorialuser")
    public final void whenBarsModified_thenBarsAudited() {
        Bar bar = new Bar("BAR1");
        barService.create(bar);
        Assert.assertEquals(bar.getCreatedDate(), bar.getModifiedDate());
        Assert.assertEquals("tutorialuser", bar.getCreatedBy(), bar.getModifiedBy());
        bar.setName("BAR2");
        bar = barService.update(bar);
        Assert.assertTrue(((bar.getCreatedDate()) < (bar.getModifiedDate())));
        Assert.assertEquals("tutorialuser", bar.getCreatedBy(), bar.getModifiedBy());
    }
}

