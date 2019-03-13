package org.javaee7.jpa.nativesql.resultset.mapping;


import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * In this sample we're going to query a simple +JPA Entity+, using the +JPA EntityManager Native Query+, perform
 * a select operation and map the query result using +@SqlResultSetMapping+.
 *
 * include::Employee[]
 *
 * The select operation is very simple. We just need to call the API method +createNativeQuery+ on the +EntityManager+
 * and use the mapping defined on +Employee+ by the +@SqlResultSetMapping+ annotation.
 *
 * include::EmployeeBean#get[]
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class JpaNativeSqlResultSetMappingTest {
    @Inject
    private EmployeeBean employeeBean;

    /**
     * In the test, we're just going to invoke the only available operation in the +EmployeeBean+ and assert a few
     * details to confirm that the native query was successfully executed.
     */
    @Test
    public void testJpaNativeSqlResultSetMapping() {
        List<Employee> employees = employeeBean.get();
        Assert.assertFalse(employees.isEmpty());
        Assert.assertEquals(8, employees.size());
    }
}

