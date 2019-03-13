/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.idgenerator;


import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialect(H2Dialect.class)
public class SequenceGenerationTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private File output;

    private MetadataImplementor metadata;

    @Test
    public void testSequenceIsGenerated() throws Exception {
        new SchemaExport().setOutputFile(output.getAbsolutePath()).create(EnumSet.of(SCRIPT, DATABASE), metadata);
        List<String> commands = Files.readAllLines(output.toPath());
        Assert.assertThat(isCommandGenerated(commands, "create table test_entity \\(id .*, primary key \\(id\\)\\)"), Is.is(true));
        Assert.assertThat(isCommandGenerated(commands, "create sequence sequence_generator start with 5 increment by 3"), Is.is(true));
    }

    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    public static class TestEntity {
        Long id;

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQUENCEGENERATOR")
        @SequenceGenerator(name = "SEQUENCEGENERATOR", allocationSize = 3, initialValue = 5, sequenceName = "SEQUENCE_GENERATOR")
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}

