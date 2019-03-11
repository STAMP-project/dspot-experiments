/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemavalidation;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Type;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-9693")
@RunWith(Parameterized.class)
public class LongVarcharValidationTest implements ExecutionOptions {
    @Parameterized.Parameter
    public String jdbcMetadataExtractorStrategy;

    private StandardServiceRegistry ssr;

    @Test
    public void testValidation() {
        MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(LongVarcharValidationTest.Translation.class).buildMetadata()));
        metadata.validate();
        // create the schema
        createSchema(metadata);
        try {
            doValidation(metadata);
        } finally {
            dropSchema(metadata);
        }
    }

    @Entity(name = "Translation")
    public static class Translation {
        @Id
        public Integer id;

        @Type(type = "text")
        String text;
    }
}

