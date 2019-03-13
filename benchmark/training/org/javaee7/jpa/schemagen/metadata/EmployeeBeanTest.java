package org.javaee7.jpa.schemagen.metadata;


import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class EmployeeBeanTest {
    @Inject
    EmployeeBean bean;

    @Test
    public void testGet() throws Exception {
        Assert.assertNotNull(bean);
        List<Employee> list = bean.get();
        Assert.assertNotNull(list);
        Assert.assertEquals(8, list.size());
        Assert.assertFalse(list.contains(new Employee("Penny")));
        Assert.assertFalse(list.contains(new Employee("Sheldon")));
        Assert.assertFalse(list.contains(new Employee("Amy")));
        Assert.assertFalse(list.contains(new Employee("Leonard")));
        Assert.assertFalse(list.contains(new Employee("Bernadette")));
        Assert.assertFalse(list.contains(new Employee("Raj")));
        Assert.assertFalse(list.contains(new Employee("Howard")));
        Assert.assertFalse(list.contains(new Employee("Priya")));
    }
}

