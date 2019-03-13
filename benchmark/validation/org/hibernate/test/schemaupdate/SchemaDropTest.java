/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.schema.spi.ExceptionHandler;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10605")
@RequiresDialect(HSQLDialect.class)
public class SchemaDropTest extends BaseUnitTestCase implements ExceptionHandler , ExecutionOptions {
    protected ServiceRegistry serviceRegistry;

    protected MetadataImplementor metadata;

    @Test
    public void testDropSequence() {
        getSchemaDropper().doDrop(metadata, this, getSourceDescriptor(), getTargetDescriptor());
    }

    @Entity(name = "MyEntity")
    public static class MyEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        Long id;
    }
}

