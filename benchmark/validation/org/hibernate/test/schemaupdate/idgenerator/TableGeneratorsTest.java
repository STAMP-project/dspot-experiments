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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.TableGenerators;
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
public class TableGeneratorsTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private File output;

    private MetadataImplementor metadata;

    private static final int INITIAL_VALUE = 5;

    private static final int EXPECTED_DB_INSERTED_VALUE = TableGeneratorsTest.INITIAL_VALUE;

    @Test
    public void testTableGeneratorIsGenerated() throws Exception {
        new SchemaExport().setOutputFile(output.getAbsolutePath()).create(EnumSet.of(SCRIPT, DATABASE), metadata);
        final List<String> commands = Files.readAllLines(output.toPath());
        final String expectedTestEntityTableCreationCommand = "CREATE TABLE TEST_ENTITY \\(ID .*, PRIMARY KEY \\(ID\\)\\)";
        Assert.assertTrue((("The command '" + expectedTestEntityTableCreationCommand) + "' has not been correctly generated"), isCommandGenerated(commands, expectedTestEntityTableCreationCommand));
        final String expectedIdTableGeneratorCreationCommand = "CREATE TABLE ID_TABLE_GENERATOR \\(PK .*, VALUE .*, PRIMARY KEY \\(PK\\)\\)";
        Assert.assertTrue((("The command '" + expectedIdTableGeneratorCreationCommand) + "' has not been correctly generated"), isCommandGenerated(commands, expectedIdTableGeneratorCreationCommand));
        final String expectedInsertIntoTableGeneratorCommand = ("INSERT INTO ID_TABLE_GENERATOR\\(PK, VALUE\\) VALUES \\(\'TEST_ENTITY_ID\'," + (TableGeneratorsTest.EXPECTED_DB_INSERTED_VALUE)) + "\\)";
        Assert.assertTrue((("The command '" + expectedInsertIntoTableGeneratorCommand) + "' has not been correctly generated"), isCommandGenerated(commands, expectedInsertIntoTableGeneratorCommand));
    }

    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    @TableGenerators({ @TableGenerator(name = "tableGenerator", table = "ID_TABLE_GENERATOR", pkColumnName = "PK", pkColumnValue = "TEST_ENTITY_ID", valueColumnName = "VALUE", allocationSize = 3, initialValue = TableGeneratorsTest.INITIAL_VALUE) })
    public static class TestEntity {
        Long id;

        @Id
        @GeneratedValue(strategy = GenerationType.TABLE, generator = "tableGenerator")
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}

