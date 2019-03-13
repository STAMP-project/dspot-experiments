package liquibase.statement;


import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import liquibase.change.ColumnConfig;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.PreparedStatementFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ResourceAccessor;
import liquibase.structure.core.Column;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class UpdateExecutablePreparedStatementTest {
    @Mock
    private ChangeSet changeSet;

    @Mock
    private ResourceAccessor resourceAccessor;

    @Mock
    private JdbcConnection connection;

    @Mock
    private PreparedStatement ps;

    private Database database;

    private PreparedStatementFactory preparedStatementFactory;

    @Test
    public void testExecuteWithParamPlaceholders() throws Exception {
        // given
        UpdateExecutablePreparedStatement statement = new UpdateExecutablePreparedStatement(database, null, null, "DATABASECHANGELOG", new ArrayList<ColumnConfig>(Arrays.asList(new ColumnConfig().setName("MD5SUM").setValue("7:e27bf9c0c2313160ef960a15d44ced47"))), changeSet, resourceAccessor).setWhereClause(((((((((database.escapeObjectName("ID", Column.class)) + " = ? ") + "AND ") + (database.escapeObjectName("AUTHOR", Column.class))) + " = ? ") + "AND ") + (database.escapeObjectName("FILENAME", Column.class))) + " = ?")).addWhereParameters("SYPA: AUTO_START t??p INT -> TEXT, vaartus 0 00 17 * * ?", "martin", "db/changelog.xml");
        // when
        statement.execute(preparedStatementFactory);
        // then
        Mockito.verify(connection).prepareStatement(("UPDATE DATABASECHANGELOG " + ((("SET MD5SUM = ? " + "WHERE ID = N'SYPA: AUTO_START t??p INT -> TEXT, vaartus 0 00 17 * * ?' ") + "AND AUTHOR = 'martin' ") + "AND FILENAME = 'db/changelog.xml'")));
        Mockito.verify(ps).setString(1, "7:e27bf9c0c2313160ef960a15d44ced47");
    }

    @Test
    public void testExecuteWithNameValuePlaceholderPairs() throws Exception {
        // given
        UpdateExecutablePreparedStatement statement = setWhereClause(":name = :value AND :name = :value AND :name = :value").addWhereColumnName("ID").addWhereColumnName("AUTHOR").addWhereColumnName("FILENAME").addWhereParameters("SYPA: AUTO_START t??p INT -> TEXT, vaartus 0 00 17 * * ?", "martin", "db/changelog.xml");
        // when
        statement.execute(preparedStatementFactory);
        // then
        Mockito.verify(connection).prepareStatement(("UPDATE DATABASECHANGELOG " + ((("SET MD5SUM = ? " + "WHERE ID = N'SYPA: AUTO_START t??p INT -> TEXT, vaartus 0 00 17 * * ?' ") + "AND AUTHOR = 'martin' ") + "AND FILENAME = 'db/changelog.xml'")));
        Mockito.verify(ps).setString(1, "7:e27bf9c0c2313160ef960a15d44ced47");
    }
}

