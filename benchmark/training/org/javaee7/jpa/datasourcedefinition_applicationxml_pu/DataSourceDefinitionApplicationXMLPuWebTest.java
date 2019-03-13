package org.javaee7.jpa.datasourcedefinition_applicationxml_pu;


import java.util.List;
import javax.inject.Inject;
import org.javaee7.jpa.datasourcedefinition_applicationxml_pu.entity.TestEntity;
import org.javaee7.jpa.datasourcedefinition_applicationxml_pu.service.TestService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that a data source defined via the data-source element in an EAR's application.xml can be used by JPA.
 * <p>
 * In this test the persistence unit is defined inside a web module (.war)
 *
 * <p>
 * The actual JPA code being run is not specifically relevant; any kind of JPA operation that
 * uses the data source is okay here.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class DataSourceDefinitionApplicationXMLPuWebTest {
    @Inject
    private TestService testService;

    @Test
    public void insertAndQueryEntity() throws Exception {
        testService.saveNewEntity();
        List<TestEntity> testEntities = testService.getAllEntities();
        Assert.assertTrue(((testEntities.size()) == 1));
        Assert.assertTrue(testEntities.get(0).getValue().equals("mytest"));
    }
}

