package liquibase.sqlgenerator.core;


import liquibase.database.core.SybaseDatabase;
import liquibase.sql.Sql;
import liquibase.statement.core.GetViewDefinitionStatement;
import org.junit.Assert;
import org.junit.Test;


public class GetViewDefinitionGeneratorSybaseTest {
    @Test
    public void testGenerateSqlForDefaultSchema() {
        GetViewDefinitionGeneratorSybase generator = new GetViewDefinitionGeneratorSybase();
        GetViewDefinitionStatement statement = new GetViewDefinitionStatement(null, null, "view_name");
        Sql[] sql = generator.generateSql(statement, new SybaseDatabase(), null);
        Assert.assertEquals(1, sql.length);
        Assert.assertEquals("select text from syscomments where id = object_id('dbo.view_name') order by colid", sql[0].toSql());
    }

    @Test
    public void testGenerateSqlForNamedSchema() {
        GetViewDefinitionGeneratorSybase generator = new GetViewDefinitionGeneratorSybase();
        GetViewDefinitionStatement statement = new GetViewDefinitionStatement(null, "owner", "view_name");
        Sql[] sql = generator.generateSql(statement, new SybaseDatabase(), null);
        Assert.assertEquals(1, sql.length);
        Assert.assertEquals("select text from syscomments where id = object_id('OWNER.view_name') order by colid", sql[0].toSql());
    }
}

