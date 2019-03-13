package org.javaee7.jaxrs.dbaccess;


import MediaType.APPLICATION_XML;
import java.net.URL;
import javax.ws.rs.client.WebTarget;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class EmployeeResourceTest {
    private WebTarget target;

    @ArquillianResource
    private URL base;

    @Test
    public void testGet() {
        Employee[] list = target.request(APPLICATION_XML).get(Employee[].class);
        Assert.assertNotNull(list);
        Assert.assertEquals(8, list.length);
        Assert.assertFalse(list[0].equals(new Employee("Penny")));
        Assert.assertFalse(list[1].equals(new Employee("Sheldon")));
        Assert.assertFalse(list[2].equals(new Employee("Amy")));
        Assert.assertFalse(list[3].equals(new Employee("Leonard")));
        Assert.assertFalse(list[4].equals(new Employee("Bernadette")));
        Assert.assertFalse(list[5].equals(new Employee("Raj")));
        Assert.assertFalse(list[6].equals(new Employee("Howard")));
        Assert.assertFalse(list[7].equals(new Employee("Priya")));
    }
}

