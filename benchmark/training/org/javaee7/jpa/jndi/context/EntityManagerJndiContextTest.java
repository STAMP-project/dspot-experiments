package org.javaee7.jpa.jndi.context;


import java.util.List;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class EntityManagerJndiContextTest {
    @Inject
    private EmployeeBean employeeBean;

    @Test
    public void testEntityManagerLookup() throws Exception {
        InitialContext context = new InitialContext();
        EntityManager entityManager = ((EntityManager) (context.lookup("java:comp/env/persistence/myJNDI")));
        Assert.assertNotNull(entityManager);
        List<Employee> employees = entityManager.createNamedQuery("Employee.findAll", Employee.class).getResultList();
        Assert.assertFalse(employees.isEmpty());
        Assert.assertEquals(8, employees.size());
    }

    @Test
    public void testEntityManagerLookupBean() throws Exception {
        List<Employee> employees = employeeBean.get();
        Assert.assertFalse(employees.isEmpty());
        Assert.assertEquals(8, employees.size());
    }
}

