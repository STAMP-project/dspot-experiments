/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import Skip.OperatingSystem.Windows;
import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.Skip;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class QuotedTableNameSchemaUpdateTest extends BaseUnitTestCase {
    private File output;

    private StandardServiceRegistry ssr;

    @Test
    @TestForIssue(jiraKey = "HHH-10820")
    @Skip(condition = Windows.class, message = "On Windows, MySQL is case insensitive!")
    public void testSchemaUpdateWithQuotedTableName() throws Exception {
        final MetadataSources metadataSources = new MetadataSources(ssr);
        metadataSources.addAnnotatedClass(QuotedTableNameSchemaUpdateTest.QuotedTable.class);
        MetadataImplementor metadata = ((MetadataImplementor) (metadataSources.buildMetadata()));
        metadata.validate();
        new SchemaExport().setOutputFile(output.getAbsolutePath()).setFormat(false).create(EnumSet.of(DATABASE), metadata);
        new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setFormat(false).execute(EnumSet.of(DATABASE, SCRIPT), metadata);
        final List<String> sqlLines = Files.readAllLines(output.toPath(), Charset.defaultCharset());
        Assert.assertThat("The update should recognize the existing table", sqlLines.isEmpty(), Is.is(true));
        new SchemaExport().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setFormat(false).drop(EnumSet.of(DATABASE), metadata);
    }

    @Entity(name = "QuotedTable")
    @Table(name = "\"QuotedTable\"")
    public static class QuotedTable {
        @Id
        long id;
    }
}

