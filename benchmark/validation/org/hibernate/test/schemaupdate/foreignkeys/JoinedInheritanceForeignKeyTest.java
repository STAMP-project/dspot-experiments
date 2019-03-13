/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.foreignkeys;


import java.io.File;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrimaryKeyJoinColumn;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10352")
public class JoinedInheritanceForeignKeyTest extends BaseUnitTestCase {
    private File output;

    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    public void testForeignKeyHasCorrectName() throws Exception {
        createSchema(new Class[]{ JoinedInheritanceForeignKeyTest.Role.class, JoinedInheritanceForeignKeyTest.User.class, JoinedInheritanceForeignKeyTest.Person.class });
        checkAlterTableStatement(new JoinedInheritanceForeignKeyTest.AlterTableStatement(ssr, "User", "FK_PERSON_ROLE", "USER_ID", "PersonRole"));
    }

    private static class AlterTableStatement {
        final StandardServiceRegistry ssr;

        final String tableName;

        final String fkConstraintName;

        final String fkColumnName;

        final String referenceTableName;

        public AlterTableStatement(StandardServiceRegistry ssr, String tableName, String fkConstraintName, String fkColumnName, String referenceTableName) {
            this.ssr = ssr;
            this.tableName = tableName;
            this.fkConstraintName = fkConstraintName;
            this.fkColumnName = fkColumnName;
            this.referenceTableName = referenceTableName;
        }

        public String toSQL() {
            return ((((((ssr.getService(JdbcEnvironment.class).getDialect().getAlterTableString(tableName)) + " add constraint ") + (fkConstraintName)) + " foreign key (") + (fkColumnName)) + ") references ") + (referenceTableName);
        }
    }

    @Entity(name = "PersonRole")
    @Inheritance(strategy = InheritanceType.JOINED)
    @DiscriminatorColumn(name = "PERSON_ROLE_TYPE", discriminatorType = DiscriminatorType.INTEGER)
    public static class Role {
        @Id
        @GeneratedValue
        protected Long id;
    }

    @Entity(name = "User")
    @DiscriminatorValue("8")
    @PrimaryKeyJoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_PERSON_ROLE"))
    public static class User extends JoinedInheritanceForeignKeyTest.Role {}

    @Entity(name = "Person")
    @DiscriminatorValue("8")
    @PrimaryKeyJoinColumn(name = "USER_ID")
    public static class Person extends JoinedInheritanceForeignKeyTest.Role {}
}

