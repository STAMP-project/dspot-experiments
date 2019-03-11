package io.dropwizard.migrations;


import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;


@NotThreadSafe
public class DbCommandTest extends AbstractMigrationTest {
    private final DbCommand<TestMigrationConfiguration> dbCommand = new DbCommand("db", new TestMigrationDatabaseConfiguration(), TestMigrationConfiguration.class, "migrations.xml");

    @Test
    public void testRunSubCommand() throws Exception {
        final String databaseUrl = AbstractMigrationTest.getDatabaseUrl();
        final TestMigrationConfiguration conf = AbstractMigrationTest.createConfiguration(databaseUrl);
        dbCommand.run(null, new Namespace(Collections.singletonMap("subcommand", "migrate")), conf);
        try (Handle handle = new DBI(databaseUrl, "sa", "").open()) {
            assertThat(handle.createQuery("select count(*) from persons").mapTo(Integer.class).first()).isEqualTo(1);
        }
    }

    @Test
    public void testPrintHelp() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AbstractMigrationTest.createSubparser(dbCommand).printHelp(new PrintWriter(new OutputStreamWriter(baos, AbstractMigrationTest.UTF_8), true));
        assertThat(baos.toString(AbstractMigrationTest.UTF_8)).isEqualTo(String.format(("usage: db db [-h]%n" + ((((((((("          {calculate-checksum,clear-checksums,drop-all,dump,fast-forward,generate-docs,locks,migrate,prepare-rollback,rollback,status,tag,test}%n" + "          ...%n") + "%n") + "Run database migration tasks%n") + "%n") + "positional arguments:%n") + "  {calculate-checksum,clear-checksums,drop-all,dump,fast-forward,generate-docs,locks,migrate,prepare-rollback,rollback,status,tag,test}%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n"))));
    }
}

