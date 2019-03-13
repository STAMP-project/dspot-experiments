package com.baeldung.persistence.audit;


import com.baeldung.persistence.model.Bar;
import com.baeldung.persistence.model.Foo;
import com.baeldung.persistence.service.IBarAuditableService;
import com.baeldung.persistence.service.IFooAuditableService;
import com.baeldung.spring.config.PersistenceTestConfig;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
public class EnversFooBarAuditIntegrationTest {
    private static Logger logger = LoggerFactory.getLogger(EnversFooBarAuditIntegrationTest.class);

    @Autowired
    @Qualifier("fooHibernateAuditableService")
    private IFooAuditableService fooService;

    @Autowired
    @Qualifier("barHibernateAuditableService")
    private IBarAuditableService barService;

    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    @Test
    public final void whenFooBarsModified_thenFooBarsAudited() {
        List<Bar> barRevisionList;
        List<Foo> fooRevisionList;
        // test Bar revisions
        barRevisionList = barService.getRevisions();
        Assert.assertNotNull(barRevisionList);
        Assert.assertEquals(4, barRevisionList.size());
        Assert.assertEquals("BAR", barRevisionList.get(0).getName());
        Assert.assertEquals("BAR", barRevisionList.get(1).getName());
        Assert.assertEquals("BAR1", barRevisionList.get(2).getName());
        Assert.assertEquals("BAR1", barRevisionList.get(3).getName());
        Assert.assertEquals(1, barRevisionList.get(0).getFooSet().size());
        Assert.assertEquals(2, barRevisionList.get(1).getFooSet().size());
        Assert.assertEquals(2, barRevisionList.get(2).getFooSet().size());
        Assert.assertEquals(3, barRevisionList.get(3).getFooSet().size());
        // test Foo revisions
        fooRevisionList = fooService.getRevisions();
        Assert.assertNotNull(fooRevisionList);
        Assert.assertEquals(3, fooRevisionList.size());
        Assert.assertEquals("FOO1", fooRevisionList.get(0).getName());
        Assert.assertEquals("FOO2", fooRevisionList.get(1).getName());
        Assert.assertEquals("FOO3", fooRevisionList.get(2).getName());
    }
}

