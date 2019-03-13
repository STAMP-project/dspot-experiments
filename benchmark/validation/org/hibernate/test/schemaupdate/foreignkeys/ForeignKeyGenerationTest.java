/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.foreignkeys;


import java.io.File;
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
public class ForeignKeyGenerationTest extends BaseUnitTestCase {
    private File output;

    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    @Test
    @TestForIssue(jiraKey = "HHH-9591")
    public void oneToOneTest() throws Exception {
        createSchema(new Class[]{ User.class, UserSetting.class, Group.class });
        /* The generated SQL for the foreign keys should be:
        alter table USERS add constraint FK_TO_USER_SETTING foreign key (USER_SETTING_ID) references USER_SETTING
        alter table USER_SETTING add constraint FK_TO_USER foreign key (USERS_ID) references USERS
         */
        checkAlterTableStatement(new ForeignKeyGenerationTest.AlterTableStatement(ssr, "USERS", "FK_TO_USER_SETTING", "USER_SETTING_ID", "USER_SETTING"));
        checkAlterTableStatement(new ForeignKeyGenerationTest.AlterTableStatement(ssr, "USER_SETTING", "FK_TO_USER", "USER_ID", "USERS"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10396")
    public void oneToManyTest() throws Exception {
        createSchema(new Class[]{ User.class, UserSetting.class, Group.class });
        /* The generated SQL for the foreign keys should be:
        alter table GROUP add constraint FK_USER_GROUP foreign key (USER_ID) references USERS
         */
        checkAlterTableStatement(new ForeignKeyGenerationTest.AlterTableStatement(ssr, "GROUP", "FK_USER_GROUP", "USER_ID", "USERS"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10385")
    public void oneToManyWithJoinTableTest() throws Exception {
        createSchema(new Class[]{ Person.class, Phone.class });
        /* The generated SQL for the foreign keys should be:
        alter table PERSON_PHONE add constraint PERSON_ID_FK foreign key (PERSON_ID) references PERSON
        alter table PERSON_PHONE add constraint PHONE_ID_FK foreign key (PHONE_ID) references PHONE
         */
        checkAlterTableStatement(new ForeignKeyGenerationTest.AlterTableStatement(ssr, "PERSON_PHONE", "PERSON_ID_FK", "PERSON_ID", "PERSON"));
        checkAlterTableStatement(new ForeignKeyGenerationTest.AlterTableStatement(ssr, "PERSON_PHONE", "PHONE_ID_FK", "PHONE_ID", "PHONE"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10386")
    public void manyToManyTest() throws Exception {
        createSchema(new Class[]{ Project.class, Employee.class });
        /* The generated SQL for the foreign keys should be:
        alter table EMPLOYEE_PROJECT add constraint FK_EMPLOYEE foreign key (EMPLOYEE_ID) references EMPLOYEE
        alter table EMPLOYEE_PROJECT add constraint FK_PROJECT foreign key (PROJECT_ID) references PROJECT
         */
        checkAlterTableStatement(new ForeignKeyGenerationTest.AlterTableStatement(ssr, "EMPLOYEE_PROJECT", "FK_EMPLOYEE", "EMPLOYEE_ID", "EMPLOYEE"));
        checkAlterTableStatement(new ForeignKeyGenerationTest.AlterTableStatement(ssr, "EMPLOYEE_PROJECT", "FK_PROJECT", "PROJECT_ID", "PROJECT"));
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
}

