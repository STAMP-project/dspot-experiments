package org.javaee7.jpa.datasourcedefinition_webxml_pu;


import java.util.List;
import javax.inject.Inject;
import org.javaee7.jpa.datasourcedefinition_webxml_pu.entity.TestEntity;
import org.javaee7.jpa.datasourcedefinition_webxml_pu.service.TestService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This tests that a data source defined in web.xml can be used by JPA.
 * <p>
 * The actual JPA code being run is not specifically relevant; any kind of JPA operation that
 * uses the data source is okay here.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class DataSourceDefinitionWebxmlPuTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

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

