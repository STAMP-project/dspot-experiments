/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-9865")
public class ImplicitCompositeKeyJoinTest {
    private static final Logger LOGGER = Logger.getLogger(ImplicitCompositeKeyJoinTest.class);

    @Test
    public void testSchemaCreationSQLCommandIsGeneratedWithTheCorrectColumnSizeValues() throws Exception {
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final org.hibernate.boot.Metadata metadata = addAnnotatedClass(ImplicitCompositeKeyJoinTest.Employee.class).buildMetadata();
            boolean createTableEmployeeFound = false;
            final List<String> commands = new org.hibernate.tool.schema.internal.SchemaCreatorImpl(ssr).generateCreationCommands(metadata, false);
            for (String command : commands) {
                ImplicitCompositeKeyJoinTest.LOGGER.info(command);
                if (command.toLowerCase().matches("^create( (column|row))? table employee.+")) {
                    final String[] columnsDefinition = getColumnsDefinition(command);
                    for (int i = 0; i < (columnsDefinition.length); i++) {
                        checkColumnSize(columnsDefinition[i]);
                    }
                    createTableEmployeeFound = true;
                }
            }
            Assert.assertTrue("Expected create table command for Employee entity not found", createTableEmployeeFound);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity
    @Table(name = "Employee")
    public class Employee {
        @EmbeddedId
        @ForeignKey(name = "none")
        private ImplicitCompositeKeyJoinTest.EmployeeId id;

        @ManyToOne(optional = true)
        @ForeignKey(name = "none")
        private ImplicitCompositeKeyJoinTest.Employee manager;
    }

    @Embeddable
    public class EmployeeId implements Serializable {
        @Column(length = 15)
        public String age;

        @Column(length = 20)
        private String name;

        private String birthday;
    }
}

