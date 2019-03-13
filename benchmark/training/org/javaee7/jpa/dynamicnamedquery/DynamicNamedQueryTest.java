package org.javaee7.jpa.dynamicnamedquery;


import java.io.IOException;
import javax.inject.Inject;
import org.javaee7.jpa.dynamicnamedquery.entity.TestEntity;
import org.javaee7.jpa.dynamicnamedquery.service.TestService;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.SAXException;


/**
 * This tests that queries which have been dynamically (programmatically) added as named queries
 * can be executed correctly.
 *
 * @author Arjan Tijms
 */
@RunWith(Arquillian.class)
public class DynamicNamedQueryTest {
    @Inject
    private TestService testService;

    @Test
    public void testDynamicNamedCriteriaQueries() throws IOException, SAXException {
        // Nothing inserted yet, data base should not contain any entities
        // (this tests that a simple query without parameters works as named query created by Criteria)
        Assert.assertTrue(((testService.getAll().size()) == 0));
        // Insert one entity
        TestEntity testEntity = new TestEntity();
        testEntity.setValue("myValue");
        testService.save(testEntity);
        // The total amount of entities should be 1
        Assert.assertTrue(((testService.getAll().size()) == 1));
        // The entity with "myValue" should be found
        // (this tests that a query with a parameter works as named query created by Criteria)
        Assert.assertTrue(((testService.getByValue("myValue").size()) == 1));
    }
}

