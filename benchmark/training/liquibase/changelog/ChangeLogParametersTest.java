package liquibase.changelog;


import liquibase.ContextExpression;
import liquibase.Contexts;
import liquibase.Labels;
import liquibase.database.core.H2Database;
import org.junit.Assert;
import org.junit.Test;


public class ChangeLogParametersTest {
    @Test
    public void setParameterValue_doubleSet() {
        ChangeLogParameters changeLogParameters = new ChangeLogParameters();
        changeLogParameters.set("doubleSet", "originalValue");
        changeLogParameters.set("doubleSet", "newValue");
        Assert.assertEquals("re-setting a param should not overwrite the value (like how ant works)", "originalValue", changeLogParameters.getValue("doubleSet", null));
    }

    @Test
    public void getParameterValue_systemProperty() {
        ChangeLogParameters changeLogParameters = new ChangeLogParameters();
        Assert.assertEquals(System.getProperty("user.name"), changeLogParameters.getValue("user.name", null));
    }

    @Test
    public void setParameterValue_doubleSetButSecondWrongDatabase() {
        ChangeLogParameters changeLogParameters = new ChangeLogParameters(new H2Database());
        changeLogParameters.set("doubleSet", "originalValue", new ContextExpression(), new Labels(), "baddb", true, null);
        changeLogParameters.set("doubleSet", "newValue");
        Assert.assertEquals("newValue", changeLogParameters.getValue("doubleSet", null));
    }

    @Test
    public void setParameterValue_multiDatabase() {
        ChangeLogParameters changeLogParameters = new ChangeLogParameters(new H2Database());
        changeLogParameters.set("doubleSet", "originalValue", new ContextExpression(), new Labels(), "baddb, h2", true, null);
        Assert.assertEquals("originalValue", changeLogParameters.getValue("doubleSet", null));
    }

    @Test
    public void setParameterValue_rightDBWrongContext() {
        ChangeLogParameters changeLogParameters = new ChangeLogParameters(new H2Database());
        changeLogParameters.setContexts(new Contexts("junit"));
        changeLogParameters.set("doubleSet", "originalValue", "anotherContext", "anotherLabel", "baddb, h2", true, null);
        Assert.assertNull(changeLogParameters.getValue("doubleSet", null));
    }

    @Test
    public void setParameterValue_rightDBRightContext() {
        ChangeLogParameters changeLogParameters = new ChangeLogParameters(new H2Database());
        changeLogParameters.setContexts(new Contexts("junit"));
        changeLogParameters.set("doubleSet", "originalValue", "junit", "junitLabel", "baddb, h2", true, null);
        Assert.assertEquals("originalValue", changeLogParameters.getValue("doubleSet", null));
    }

    /**
     * root.xml
     *  -a.xml
     *  -b.xml
     *
     *  in a and b we define same prop with key 'aKey'. Expected when b is processed then bValue is taken no matter of Object instances
     */
    @Test
    public void getParameterValue_SamePropertyNonGlobalIn2InnerFiles() {
        DatabaseChangeLog inner1 = new DatabaseChangeLog();
        inner1.setPhysicalFilePath("a");
        DatabaseChangeLog inner2 = new DatabaseChangeLog();
        inner2.setPhysicalFilePath("b");
        ChangeLogParameters changeLogParameters = new ChangeLogParameters(new H2Database());
        changeLogParameters.set("aKey", "aValue", "junit", "junitLabel", "baddb, h2", false, inner1);
        changeLogParameters.set("aKey", "bValue", "junit", "junitLabel", "baddb, h2", false, inner2);
        DatabaseChangeLog inner2SamePath = new DatabaseChangeLog();
        inner2SamePath.setPhysicalFilePath("b");
        Object aKey = changeLogParameters.getValue("aKey", inner2SamePath);
        Assert.assertEquals("bValue", aKey);
    }
}

