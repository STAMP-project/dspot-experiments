package org.hibernate.test.c3p0;


import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(Oracle8iDialect.class)
@TestForIssue(jiraKey = "HHH-10256")
public class OracleSQLCallableStatementProxyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testStoredProcedureOutParameter() {
        doInHibernate(this::sessionFactory, ( session) -> {
            List<Object[]> persons = session.createNamedQuery("getPerson").setParameter(1, 1L).getResultList();
            assertEquals(1, persons.size());
        });
    }

    @NamedNativeQuery(name = "getPerson", query = "{ ? = call fn_person( ? ) }", callable = true, resultSetMapping = "person")
    @SqlResultSetMappings({ @SqlResultSetMapping(name = "person", entities = { @EntityResult(entityClass = OracleSQLCallableStatementProxyTest.Person.class, fields = { @FieldResult(name = "id", column = "p.id"), @FieldResult(name = "name", column = "p.name"), @FieldResult(name = "nickName", column = "p.nickName") }) }) })
    @Entity(name = "Person")
    @Table(name = "person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        private String nickName;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }
}

