package io.dropwizard.migrations;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import net.jcip.annotations.NotThreadSafe;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


@NotThreadSafe
public class DbDumpCommandTest extends AbstractMigrationTest {
    private static final List<String> ATTRIBUTE_NAMES = Arrays.asList("columns", "foreign-keys", "indexes", "primary-keys", "sequences", "tables", "unique-constraints", "views");

    private static DocumentBuilder xmlParser;

    private final DbDumpCommand<TestMigrationConfiguration> dumpCommand = new DbDumpCommand(new TestMigrationDatabaseConfiguration(), TestMigrationConfiguration.class, "migrations.xml");

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private TestMigrationConfiguration existedDbConf;

    @Test
    public void testDumpSchema() throws Exception {
        dumpCommand.run(null, new Namespace(DbDumpCommandTest.ATTRIBUTE_NAMES.stream().collect(Collectors.toMap(( a) -> a, ( b) -> true))), existedDbConf);
        final Element changeSet = DbDumpCommandTest.getFirstElement(DbDumpCommandTest.toXmlDocument(baos).getDocumentElement(), "changeSet");
        DbDumpCommandTest.assertCreateTable(changeSet);
    }

    @Test
    public void testDumpSchemaAndData() throws Exception {
        dumpCommand.run(null, new Namespace(Stream.concat(DbDumpCommandTest.ATTRIBUTE_NAMES.stream(), Stream.of("data")).collect(Collectors.toMap(( a) -> a, ( b) -> true))), existedDbConf);
        final NodeList changeSets = DbDumpCommandTest.toXmlDocument(baos).getDocumentElement().getElementsByTagName("changeSet");
        DbDumpCommandTest.assertCreateTable(((Element) (changeSets.item(0))));
        DbDumpCommandTest.assertInsertData(((Element) (changeSets.item(1))));
    }

    @Test
    public void testDumpOnlyData() throws Exception {
        dumpCommand.run(null, new Namespace(Collections.singletonMap("data", true)), existedDbConf);
        final Element changeSet = DbDumpCommandTest.getFirstElement(DbDumpCommandTest.toXmlDocument(baos).getDocumentElement(), "changeSet");
        DbDumpCommandTest.assertInsertData(changeSet);
    }

    @Test
    public void testWriteToFile() throws Exception {
        final File file = File.createTempFile("migration", ".xml");
        dumpCommand.run(null, new Namespace(Collections.singletonMap("output", file.getAbsolutePath())), existedDbConf);
        // Check that file is exist, and has some XML content (no reason to make a full-blown XML assertion)
        assertThat(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8)).startsWith("<?xml version=\"1.1\" encoding=\"UTF-8\" standalone=\"no\"?>");
    }

    @Test
    public void testHelpPage() throws Exception {
        AbstractMigrationTest.createSubparser(dumpCommand).printHelp(new PrintWriter(new OutputStreamWriter(baos, AbstractMigrationTest.UTF_8), true));
        assertThat(baos.toString(AbstractMigrationTest.UTF_8)).isEqualTo(String.format(("usage: db dump [-h] [--migrations MIGRATIONS-FILE] [--catalog CATALOG]%n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("          [--schema SCHEMA] [-o OUTPUT] [--tables] [--ignore-tables]%n" + "          [--columns] [--ignore-columns] [--views] [--ignore-views]%n") + "          [--primary-keys] [--ignore-primary-keys] [--unique-constraints]%n") + "          [--ignore-unique-constraints] [--indexes] [--ignore-indexes]%n") + "          [--foreign-keys] [--ignore-foreign-keys] [--sequences]%n") + "          [--ignore-sequences] [--data] [--ignore-data] [file]%n") + "%n") + "Generate a dump of the existing database state.%n") + "%n") + "positional arguments:%n") + "  file                   application configuration file%n") + "%n") + "named arguments:%n") + "  -h, --help             show this help message and exit%n") + "  --migrations MIGRATIONS-FILE%n") + "                         the file containing  the  Liquibase migrations for%n") + "                         the application%n") + "  --catalog CATALOG      Specify  the   database   catalog   (use  database%n") + "                         default if omitted)%n") + "  --schema SCHEMA        Specify the database schema  (use database default%n") + "                         if omitted)%n") + "  -o OUTPUT, --output OUTPUT%n") + "                         Write output to <file> instead of stdout%n") + "%n") + "Tables:%n") + "  --tables               Check for added or removed tables (default)%n") + "  --ignore-tables        Ignore tables%n") + "%n") + "Columns:%n") + "  --columns              Check for  added,  removed,  or  modified  columns%n") + "                         (default)%n") + "  --ignore-columns       Ignore columns%n") + "%n") + "Views:%n") + "  --views                Check  for  added,  removed,   or  modified  views%n") + "                         (default)%n") + "  --ignore-views         Ignore views%n") + "%n") + "Primary Keys:%n") + "  --primary-keys         Check for changed primary keys (default)%n") + "  --ignore-primary-keys  Ignore primary keys%n") + "%n") + "Unique Constraints:%n") + "  --unique-constraints   Check for changed unique constraints (default)%n") + "  --ignore-unique-constraints%n") + "                         Ignore unique constraints%n") + "%n") + "Indexes:%n") + "  --indexes              Check for changed indexes (default)%n") + "  --ignore-indexes       Ignore indexes%n") + "%n") + "Foreign Keys:%n") + "  --foreign-keys         Check for changed foreign keys (default)%n") + "  --ignore-foreign-keys  Ignore foreign keys%n") + "%n") + "Sequences:%n") + "  --sequences            Check for changed sequences (default)%n") + "  --ignore-sequences     Ignore sequences%n") + "%n") + "Data:%n") + "  --data                 Check for changed data%n") + "  --ignore-data          Ignore data (default)%n"))));
    }
}

