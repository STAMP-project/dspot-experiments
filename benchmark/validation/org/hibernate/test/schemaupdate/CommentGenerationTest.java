/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import Environment.DIALECT;
import Environment.HBM2DDL_AUTO;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10635")
public class CommentGenerationTest {
    @Test
    public void testSchemaUpdateScriptGeneration() throws Exception {
        final String resource = "org/hibernate/test/schemaupdate/CommentGeneration.hbm.xml";
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, "none").applySetting(DIALECT, CommentGenerationTest.SupportCommentDialect.class.getName()).build();
        try {
            File output = File.createTempFile("update_script", ".sql");
            output.deleteOnExit();
            final MetadataImplementor metadata = ((MetadataImplementor) (addResource(resource).buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(";").setFormat(true).execute(EnumSet.of(SCRIPT), metadata);
            String fileContent = new String(Files.readAllBytes(output.toPath()));
            Assert.assertThat(fileContent.contains("comment on column version.description "), Is.is(true));
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    public static class SupportCommentDialect extends Dialect {
        @Override
        public boolean supportsCommentOn() {
            return true;
        }
    }
}

