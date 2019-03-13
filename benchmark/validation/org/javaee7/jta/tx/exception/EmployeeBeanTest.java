package org.javaee7.jta.tx.exception;


import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * This test is RED with WildFly 8.0.0.Beta1 because it does not have a standard default DataSource.
 *
 * @author Alexis Hassler
 */
@RunWith(Arquillian.class)
public class EmployeeBeanTest {
    @Inject
    EmployeeBean bean;

    @Test
    public void should_have_7_employees() {
        Assert.assertEquals(7, bean.getEmployees().size());
    }

    @Test
    public void should_have_1_more_employee_after_checked_exception() {
        try {
            bean.addAndThrowChecked();
        } catch (Exception ex) {
        }
        Assert.assertEquals(8, bean.getEmployees().size());
    }

    @Test
    public void should_not_have_1_more_employee_after_runtime_exception() {
        try {
            bean.addAndThrowRuntime();
        } catch (Exception ex) {
        }
        Assert.assertEquals(7, bean.getEmployees().size());
    }
}

