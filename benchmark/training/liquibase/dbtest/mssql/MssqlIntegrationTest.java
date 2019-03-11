package liquibase.dbtest.mssql;


import CatalogAndSchema.DEFAULT;
import java.util.Calendar;
import java.util.Date;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.core.MSSQLDatabase;
import liquibase.datatype.DataTypeFactory;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.statement.DatabaseFunction;
import liquibase.structure.core.Column;
import liquibase.structure.core.Table;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class MssqlIntegrationTest extends AbstractMssqlIntegrationTest {
    public MssqlIntegrationTest() throws Exception {
        super("mssql", DatabaseFactory.getInstance().getDatabase("mssql"));
    }

    @Test
    public void defaultValuesTests() throws Exception {
        clearDatabase();
        Assume.assumeNotNull(this.getDatabase());
        Liquibase liquibase = createLiquibase("changelogs/mssql/issues/default.values.xml");
        liquibase.update(((String) (null)));
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(DEFAULT, this.getDatabase(), new liquibase.snapshot.SnapshotControl(getDatabase()));
        for (Table table : snapshot.get(Table.class)) {
            for (Column column : table.getColumns()) {
                if (column.getName().toLowerCase().endsWith("_default")) {
                    Object defaultValue = column.getDefaultValue();
                    Assert.assertNotNull(((("Null default value for " + (table.getName())) + ".") + (column.getName())), defaultValue);
                    if ((column.getName().toLowerCase().contains("date")) || (column.getName().toLowerCase().contains("time"))) {
                        if (defaultValue instanceof String) {
                            Assert.assertTrue(defaultValue.equals("2017-12-09 23:52:39.1234567 +01:00"));
                        } else
                            if (defaultValue instanceof DatabaseFunction) {
                                getValue().contains("type datetimeoffset");
                            } else {
                                Assert.assertTrue(((((("Unexpected default type " + (defaultValue.getClass().getName())) + " for ") + (table.getName())) + ".") + (column.getName())), (defaultValue instanceof Date));
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(((Date) (defaultValue)));
                                Assert.assertEquals(9, calendar.get(Calendar.DAY_OF_MONTH));
                                Assert.assertEquals(11, calendar.get(Calendar.MONTH));
                                Assert.assertEquals(2017, calendar.get(Calendar.YEAR));
                            }

                    } else
                        if (column.getName().toLowerCase().contains("char_")) {
                            Assert.assertTrue(((((("Unexpected default type " + (defaultValue.getClass().getName())) + " for ") + (table.getName())) + ".") + (column.getName())), (defaultValue instanceof String));
                        } else
                            if (column.getName().toLowerCase().contains("binary_")) {
                                Assert.assertTrue(((((("Unexpected default type " + (defaultValue.getClass().getName())) + " for ") + (table.getName())) + ".") + (column.getName())), (defaultValue instanceof DatabaseFunction));
                            } else {
                                Assert.assertTrue(((((("Unexpected default type " + (defaultValue.getClass().getName())) + " for ") + (table.getName())) + ".") + (column.getName())), (defaultValue instanceof Number));
                                Assert.assertEquals(1, ((Number) (defaultValue)).intValue());
                            }


                }
            }
        }
    }

    @Test
    public void dataTypesTest() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        clearDatabase();
        Liquibase liquibase = createLiquibase("changelogs/mssql/issues/data.types.xml");
        liquibase.update(((String) (null)));
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(DEFAULT, this.getDatabase(), new liquibase.snapshot.SnapshotControl(getDatabase()));
        for (Table table : snapshot.get(Table.class)) {
            if (getDatabase().isLiquibaseObject(table)) {
                continue;
            }
            for (Column column : table.getColumns()) {
                String expectedType = column.getName().split("_")[0];
                switch (expectedType.toUpperCase()) {
                    // See https://docs.microsoft.com/en-us/sql/t-sql/data-types/ntext-text-and-image-transact-sql
                    // Types text, ntext and image are deprecated and should be translated into
                    // varchar(max), nvarchar(max) and varbinary(max).
                    case "TEXT" :
                        expectedType = "varchar";
                        break;
                    case "NTEXT" :
                        expectedType = "nvarchar";
                        break;
                    case "IMAGE" :
                        expectedType = "varbinary";
                        break;
                    default :
                        // nothing to do
                }
                String foundTypeDefinition = DataTypeFactory.getInstance().from(column.getType(), new MSSQLDatabase()).toDatabaseDataType(getDatabase()).toString();
                // [varbinary] -> varbinary
                foundTypeDefinition = foundTypeDefinition.replaceFirst("^\\[(.*?)\\]", "$1");
                String foundType = foundTypeDefinition.replaceFirst("\\(.*", "").trim();
                Assert.assertEquals(((("Wrong data type for " + (table.getName())) + ".") + (column.getName())), expectedType.toLowerCase(), foundType.toLowerCase());
                if ("varbinary".equalsIgnoreCase(expectedType)) {
                    if (column.getName().endsWith("_MAX")) {
                        Assert.assertEquals("VARBINARY(MAX)", foundTypeDefinition.toUpperCase());
                    } else {
                        Assert.assertEquals("VARBINARY(1)", foundTypeDefinition.toUpperCase());
                    }
                }
            }
        }
    }

    /**
     * When snapshotting an MSSQL database, size information is included for
     * XML, SMALLMONEY, HIERARCHYID, DATETIME2, IMAGE, and DATETIMEOFFSET even when the default precisions (if
     * applicable at all) are used. Default sizes/precisions should not be transferred into resulting ChangeLogs/
     * snapshots.
     *
     * Reference: https://liquibase.jira.com/browse/CORE-1515
     */
    @Test
    public void dataTypeParamsTest() throws Exception {
        Assume.assumeNotNull(this.getDatabase());
        clearDatabase();
        Liquibase liquibase = createLiquibase("changelogs/mssql/issues/data.type.params.xml");
        liquibase.update(((String) (null)));
        DatabaseSnapshot snapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(DEFAULT, this.getDatabase(), new liquibase.snapshot.SnapshotControl(getDatabase()));
        for (Table table : snapshot.get(Table.class)) {
            if (getDatabase().isLiquibaseObject(table)) {
                continue;
            }
            for (Column column : table.getColumns()) {
                String expectedType = column.getName().split("_")[0];
                String foundTypeDefinition = DataTypeFactory.getInstance().from(column.getType(), new MSSQLDatabase()).toDatabaseDataType(getDatabase()).toString();
                Assert.assertFalse(((("Parameter found in " + (table.getName())) + ".") + (column.getName())), foundTypeDefinition.contains("("));
            }
        }
    }
}

