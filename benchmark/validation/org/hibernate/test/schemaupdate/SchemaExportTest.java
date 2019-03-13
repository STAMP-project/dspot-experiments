/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import DialectChecks.SupportSchemaCreation;
import SchemaExport.Action.BOTH;
import SchemaExport.Action.CREATE;
import SchemaExport.Action.DROP;
import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hamcrest.core.Is;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class SchemaExportTest extends BaseUnitTestCase {
    protected ServiceRegistry serviceRegistry;

    protected MetadataImplementor metadata;

    @Test
    public void testCreateAndDropOnlyType() {
        final SchemaExport schemaExport = new SchemaExport();
        // create w/o dropping first; (OK because tables don't exist yet
        schemaExport.execute(EnumSet.of(DATABASE), CREATE, metadata);
        Assert.assertEquals(0, schemaExport.getExceptions().size());
        // create w/o dropping again; should cause an exception because the tables exist already
        schemaExport.execute(EnumSet.of(DATABASE), CREATE, metadata);
        Assert.assertEquals(1, schemaExport.getExceptions().size());
        // drop tables only
        schemaExport.execute(EnumSet.of(DATABASE), DROP, metadata);
        Assert.assertEquals(0, schemaExport.getExceptions().size());
    }

    @Test
    public void testBothType() {
        final SchemaExport schemaExport = new SchemaExport();
        // drop before create (nothing to drop yeT)
        schemaExport.execute(EnumSet.of(DATABASE), DROP, metadata);
        if (doesDialectSupportDropTableIfExist()) {
            Assert.assertEquals(0, schemaExport.getExceptions().size());
        } else {
            Assert.assertEquals(1, schemaExport.getExceptions().size());
        }
        // drop before create again (this time drops the tables before re-creating)
        schemaExport.execute(EnumSet.of(DATABASE), BOTH, metadata);
        int exceptionCount = schemaExport.getExceptions().size();
        if (doesDialectSupportDropTableIfExist()) {
            Assert.assertEquals(0, exceptionCount);
        }
        // drop tables
        schemaExport.execute(EnumSet.of(DATABASE), DROP, metadata);
        Assert.assertEquals(0, schemaExport.getExceptions().size());
    }

    @Test
    public void testGenerateDdlToFile() {
        final SchemaExport schemaExport = new SchemaExport();
        File outFile = new File("schema.ddl");
        schemaExport.setOutputFile(outFile.getPath());
        // do not script to console or export to database
        schemaExport.execute(EnumSet.of(SCRIPT), DROP, metadata);
        if ((doesDialectSupportDropTableIfExist()) && ((schemaExport.getExceptions().size()) > 0)) {
            Assert.assertEquals(2, schemaExport.getExceptions().size());
        }
        Assert.assertTrue(outFile.exists());
        // check file is not empty
        Assert.assertTrue(((outFile.length()) > 0));
        outFile.delete();
    }

    @Test
    public void testCreateAndDrop() {
        final SchemaExport schemaExport = new SchemaExport();
        // should drop before creating, but tables don't exist yet
        schemaExport.create(EnumSet.of(DATABASE), metadata);
        if (doesDialectSupportDropTableIfExist()) {
            Assert.assertEquals(0, schemaExport.getExceptions().size());
        } else {
            Assert.assertEquals(1, schemaExport.getExceptions().size());
        }
        // call create again; it should drop tables before re-creating
        schemaExport.create(EnumSet.of(DATABASE), metadata);
        Assert.assertEquals(0, schemaExport.getExceptions().size());
        // drop the tables
        schemaExport.drop(EnumSet.of(DATABASE), metadata);
        Assert.assertEquals(0, schemaExport.getExceptions().size());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10678")
    @RequiresDialectFeature(SupportSchemaCreation.class)
    public void testHibernateMappingSchemaPropertyIsNotIgnored() throws Exception {
        File output = File.createTempFile("update_script", ".sql");
        output.deleteOnExit();
        final MetadataImplementor metadata = ((MetadataImplementor) (addResource("org/hibernate/test/schemaupdate/mapping2.hbm.xml").buildMetadata()));
        metadata.validate();
        final SchemaExport schemaExport = new SchemaExport();
        schemaExport.setOutputFile(output.getAbsolutePath());
        schemaExport.execute(EnumSet.of(SCRIPT), CREATE, metadata);
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table schema1.version");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(fileContent, fileContentMatcher.find(), Is.is(true));
    }
}

