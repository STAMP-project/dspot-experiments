package org.javaee7.jpasamples.schema.gen.scripts.external;


import javax.inject.Inject;
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
public class SchemaGenScriptsExternalTest {
    @Inject
    private EmployeeBean employeeBean;

    @Test
    public void testSchemaGenScriptExternal() throws Exception {
        Assert.assertFalse(employeeBean.get().isEmpty());
    }
}

