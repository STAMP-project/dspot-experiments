package io.dropwizard.migrations;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;


@NotThreadSafe
public class DbMigrateDifferentFileCommandTest extends AbstractMigrationTest {
    private DbMigrateCommand<TestMigrationConfiguration> migrateCommand = new DbMigrateCommand(TestMigrationConfiguration::getDataSource, TestMigrationConfiguration.class, "migrations-ddl.xml");

    private TestMigrationConfiguration conf;

    private String databaseUrl;

    @Test
    public void testRun() throws Exception {
        migrateCommand.run(null, new Namespace(Collections.emptyMap()), conf);
        try (Handle handle = new DBI(databaseUrl, "sa", "").open()) {
            final List<Map<String, Object>> rows = handle.select("select * from persons");
            assertThat(rows).hasSize(0);
        }
    }
}

