package liquibase.datatype;


import GlobalConfiguration.CONVERT_DATA_TYPES;
import liquibase.configuration.GlobalConfiguration;
import liquibase.configuration.LiquibaseConfiguration;
import liquibase.database.core.MSSQLDatabase;
import liquibase.datatype.core.ClobType;
import org.junit.Assert;
import org.junit.Test;


public class ClobTypeTest {
    @Test
    public void mssqlTextToVarcharTest() {
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).getProperty(CONVERT_DATA_TYPES).setValue(Boolean.TRUE);
        ClobType ct = new ClobType();
        ct.finishInitialization("Text");
        DatabaseDataType dbType = ct.toDatabaseDataType(new MSSQLDatabase());
        Assert.assertEquals("varchar (max)", dbType.getType());
    }

    @Test
    public void mssqlEscapedTextToVarcharTest() {
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).getProperty(CONVERT_DATA_TYPES).setValue(Boolean.TRUE);
        ClobType ct = new ClobType();
        ct.finishInitialization("[Text]");
        DatabaseDataType dbType = ct.toDatabaseDataType(new MSSQLDatabase());
        Assert.assertEquals("varchar (max)", dbType.getType());
    }

    @Test
    public void mssqlTextToVarcharNoConvertTest() {
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).getProperty(CONVERT_DATA_TYPES).setValue(Boolean.FALSE);
        ClobType ct = new ClobType();
        ct.finishInitialization("Text");
        DatabaseDataType dbType = ct.toDatabaseDataType(new MSSQLDatabase());
        Assert.assertEquals("varchar (max)", dbType.getType());
    }

    @Test
    public void mssqlNTextToNVarcharNoConvertTest() {
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).getProperty(CONVERT_DATA_TYPES).setValue(Boolean.FALSE);
        ClobType ct = new ClobType();
        ct.finishInitialization("NText");
        DatabaseDataType dbType = ct.toDatabaseDataType(new MSSQLDatabase());
        Assert.assertEquals("nvarchar (max)", dbType.getType());
    }

    @Test
    public void mssqlEscapedTextToVarcharNoConvertTest() {
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).getProperty(CONVERT_DATA_TYPES).setValue(Boolean.FALSE);
        ClobType ct = new ClobType();
        ct.finishInitialization("[Text]");
        DatabaseDataType dbType = ct.toDatabaseDataType(new MSSQLDatabase());
        Assert.assertEquals("varchar (max)", dbType.getType());
    }

    @Test
    public void mssqlEscapedNTextToNVarcharNoConvertTest() {
        LiquibaseConfiguration.getInstance().getConfiguration(GlobalConfiguration.class).getProperty(CONVERT_DATA_TYPES).setValue(Boolean.FALSE);
        ClobType ct = new ClobType();
        ct.finishInitialization("[NText]");
        DatabaseDataType dbType = ct.toDatabaseDataType(new MSSQLDatabase());
        Assert.assertEquals("nvarchar (max)", dbType.getType());
    }
}

