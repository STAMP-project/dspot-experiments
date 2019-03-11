package liquibase.configuration;


import junit.framework.TestCase;
import liquibase.exception.UnexpectedLiquibaseException;
import org.junit.Assert;
import org.junit.Test;


public class ContextTest {
    private AbstractConfigurationContainer exampleConfiguration;

    @Test
    public void getValue() {
        TestCase.assertNull(exampleConfiguration.getContainer().getValue("propertyBooleanNoDefault", Boolean.class));
        Assert.assertEquals(Boolean.TRUE, exampleConfiguration.getContainer().getValue("propertyBooleanDefaultTrue", Boolean.class));
        Assert.assertEquals(Boolean.FALSE, exampleConfiguration.getContainer().getValue("propertyBooleanDefaultFalse", Boolean.class));
    }

    @Test(expected = UnexpectedLiquibaseException.class)
    public void setValue_wrongType() {
        exampleConfiguration.getContainer().setValue("propertyBooleanDefaultFalse", 124);
    }

    @Test
    public void getValue_defaultFromSystemProperties() {
        System.setProperty("liquibase.example.propertyBooleanNoDefault", "true");
        System.setProperty("liquibase.example.propertyBooleanDefaultFalse", "true");
        System.setProperty("liquibase.example.property.default.true", "false");
        ContextTest.ExampleContext exampleContext = new ContextTest.ExampleContext();
        exampleContext.init(new SystemPropertyProvider());
        TestCase.assertEquals(Boolean.TRUE, getContainer().getValue("propertyBooleanNoDefault", Boolean.class));
        TestCase.assertEquals(Boolean.TRUE, getContainer().getValue("propertyBooleanDefaultFalse", Boolean.class));
        TestCase.assertEquals(Boolean.FALSE, getContainer().getValue("propertyBooleanDefaultTrue", Boolean.class));
    }

    private static class ExampleContext extends AbstractConfigurationContainer {
        private ExampleContext() {
            super("liquibase.example");
            getContainer().addProperty("propertyBooleanNoDefault", Boolean.class).setDescription("An example boolean property with no default");
            getContainer().addProperty("propertyBooleanDefaultTrue", Boolean.class).setDefaultValue(true).addAlias("property.default.true");
            getContainer().addProperty("propertyBooleanDefaultFalse", Boolean.class).setDefaultValue(false);
        }
    }
}

