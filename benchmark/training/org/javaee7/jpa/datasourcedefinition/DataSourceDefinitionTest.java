package org.javaee7.jpa.datasourcedefinition;


import javax.annotation.Resource;
import javax.sql.DataSource;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Alexis Hassler
 */
@RunWith(Arquillian.class)
public class DataSourceDefinitionTest {
    @Resource(lookup = "java:global/MyApp/MyDataSource")
    DataSource dataSource;

    @Test
    public void should_bean_be_injected() throws Exception {
        Assert.assertThat(dataSource, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(dataSource.getConnection(), CoreMatchers.is(CoreMatchers.notNullValue()));
    }
}

