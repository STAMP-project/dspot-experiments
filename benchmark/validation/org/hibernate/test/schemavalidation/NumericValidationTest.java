/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemavalidation;


import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
 * @author Jonathan Bregler
 */
@TestForIssue(jiraKey = "HHH-12203")
@RunWith(Parameterized.class)
public class NumericValidationTest implements ExecutionOptions {
    @Parameterized.Parameter
    public String jdbcMetadataExtractorStrategy;

    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    public void testValidation() {
        doValidation();
    }

    @Entity(name = "TestEntity")
    public static class TestEntity {
        @Id
        public Integer id;

        @Column(name = "numberValue")
        BigDecimal number;
    }
}

