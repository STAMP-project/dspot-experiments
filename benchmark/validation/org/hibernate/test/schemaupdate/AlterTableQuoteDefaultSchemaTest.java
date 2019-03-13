/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS;
import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Guillaume Smet
 */
@TestForIssue(jiraKey = "HHH-12939")
@RequiresDialect({ H2Dialect.class, PostgreSQL82Dialect.class, SQLServer2012Dialect.class })
public class AlterTableQuoteDefaultSchemaTest extends AbstractAlterTableQuoteSchemaTest {
    @Test
    public void testDefaultSchema() throws IOException {
        File output = File.createTempFile("update_script", ".sql");
        output.deleteOnExit();
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(GLOBALLY_QUOTED_IDENTIFIERS, Boolean.TRUE.toString()).build();
        try {
            final MetadataSources metadataSources = new MetadataSources(ssr) {
                @Override
                public MetadataBuilder getMetadataBuilder() {
                    MetadataBuilder metadataBuilder = super.getMetadataBuilder();
                    metadataBuilder.applyImplicitSchemaName("default-schema");
                    return metadataBuilder;
                }
            };
            metadataSources.addAnnotatedClass(AlterTableQuoteDefaultSchemaTest.MyEntity.class);
            final MetadataImplementor metadata = ((MetadataImplementor) (metadataSources.buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(";").setFormat(true).execute(EnumSet.of(DATABASE, SCRIPT), metadata);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
        try {
            String fileContent = new String(Files.readAllBytes(output.toPath()));
            Pattern fileContentPattern = Pattern.compile(("create table " + (regexpQuote("default-schema", "my_entity"))));
            Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
            Assert.assertThat(fileContentMatcher.find(), Is.is(true));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
        ssr = new StandardServiceRegistryBuilder().applySetting(GLOBALLY_QUOTED_IDENTIFIERS, Boolean.TRUE.toString()).build();
        try {
            final MetadataSources metadataSources = new MetadataSources(ssr) {
                @Override
                public MetadataBuilder getMetadataBuilder() {
                    MetadataBuilder metadataBuilder = super.getMetadataBuilder();
                    metadataBuilder.applyImplicitSchemaName("default-schema");
                    return metadataBuilder;
                }
            };
            metadataSources.addAnnotatedClass(AlterTableQuoteDefaultSchemaTest.MyEntityUpdated.class);
            final MetadataImplementor metadata = ((MetadataImplementor) (metadataSources.buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(";").setFormat(true).execute(EnumSet.of(DATABASE, SCRIPT), metadata);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
        try {
            String fileContent = new String(Files.readAllBytes(output.toPath()));
            Pattern fileContentPattern = Pattern.compile(("alter table.* " + (regexpQuote("default-schema", "my_entity"))));
            Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
            Assert.assertThat(fileContentMatcher.find(), Is.is(true));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Entity(name = "MyEntity")
    @Table(name = "my_entity")
    public static class MyEntity {
        @Id
        public Integer id;
    }

    @Entity(name = "MyEntity")
    @Table(name = "my_entity")
    public static class MyEntityUpdated {
        @Id
        public Integer id;

        private String title;
    }
}

