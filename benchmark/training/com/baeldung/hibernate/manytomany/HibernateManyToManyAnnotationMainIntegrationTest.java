package com.baeldung.hibernate.manytomany;


import com.baeldung.hibernate.manytomany.model.Employee;
import com.baeldung.hibernate.manytomany.model.Project;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Configured in: manytomany.cfg.xml
 */
public class HibernateManyToManyAnnotationMainIntegrationTest {
    private static SessionFactory sessionFactory;

    private Session session;

    @Test
    public void givenData_whenInsert_thenCreatesMtoMrelationship() {
        String[] employeeData = new String[]{ "Peter Oven", "Allan Norman" };
        String[] projectData = new String[]{ "IT Project", "Networking Project" };
        Set<Project> projects = new HashSet<Project>();
        for (String proj : projectData) {
            projects.add(new Project(proj));
        }
        for (String emp : employeeData) {
            Employee employee = new Employee(emp.split(" ")[0], emp.split(" ")[1]);
            TestCase.assertEquals(0, employee.getProjects().size());
            employee.setProjects(projects);
            session.persist(employee);
            Assert.assertNotNull(employee);
        }
    }

    @Test
    public void givenSession_whenRead_thenReturnsMtoMdata() {
        @SuppressWarnings("unchecked")
        List<Employee> employeeList = session.createQuery("FROM Employee").list();
        Assert.assertNotNull(employeeList);
        for (Employee employee : employeeList) {
            Assert.assertNotNull(employee.getProjects());
        }
    }
}

