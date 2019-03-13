/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.inheritance.tableperclass;


import TargetType.SCRIPT;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.List;
import org.hamcrest.core.Is;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class SchemaCreationTest {
    private File output;

    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    private Dialect dialect;

    @Test
    @TestForIssue(jiraKey = "HHH-10553")
    public void testUniqueConstraintIsCorrectlyGenerated() throws Exception {
        final MetadataSources metadataSources = new MetadataSources(ssr);
        metadataSources.addAnnotatedClass(Element.class);
        metadataSources.addAnnotatedClass(Category.class);
        metadata = ((MetadataImplementor) (metadataSources.buildMetadata()));
        metadata.validate();
        final SchemaExport schemaExport = new SchemaExport().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setFormat(false);
        schemaExport.create(EnumSet.of(SCRIPT), metadata);
        final List<String> sqlLines = Files.readAllLines(output.toPath(), Charset.defaultCharset());
        boolean isUniqueConstraintCreated = false;
        for (String statement : sqlLines) {
            Assert.assertThat("Should not try to create the unique constraint for the non existing table element", statement.toLowerCase().matches(dialect.getAlterTableString("element")), Is.is(false));
            if ((dialect) instanceof DB2Dialect) {
                if ((statement.toLowerCase().startsWith("create unique index")) && (statement.toLowerCase().contains("category (code)"))) {
                    isUniqueConstraintCreated = true;
                }
            } else
                if ((dialect) instanceof PostgreSQL81Dialect) {
                    if ((statement.toLowerCase().startsWith("alter table if exists category add constraint")) && (statement.toLowerCase().contains("unique (code)"))) {
                        isUniqueConstraintCreated = true;
                    }
                } else {
                    if ((statement.toLowerCase().startsWith("alter table category add constraint")) && (statement.toLowerCase().contains("unique (code)"))) {
                        isUniqueConstraintCreated = true;
                    }
                }

        }
        Assert.assertThat("Unique constraint for table category is not created", isUniqueConstraintCreated, Is.is(true));
    }
}

