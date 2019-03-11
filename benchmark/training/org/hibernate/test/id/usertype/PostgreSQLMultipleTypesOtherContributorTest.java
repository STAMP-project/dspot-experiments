/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.usertype;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.TypeDef;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.test.id.usertype.inet.Inet;
import org.hibernate.test.id.usertype.inet.InetType;
import org.hibernate.test.id.usertype.json.Json;
import org.hibernate.test.id.usertype.json.JsonType;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.testing.util.ExceptionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(PostgreSQL82Dialect.class)
public class PostgreSQLMultipleTypesOtherContributorTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testMultipleTypeContributions() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            try {
                List<Inet> inets = entityManager.createNativeQuery(("select e.ip " + ("from Event e " + "where e.id = :id"))).setParameter("id", 1L).getResultList();
                assertEquals(1, inets.size());
                assertEquals("192.168.0.123/24", inets.get(0).getAddress());
            } catch ( e) {
                Throwable rootException = ExceptionUtil.rootCause(e);
                assertEquals("There are multiple Hibernate types: [inet, json] registered for the [1111] JDBC type code", rootException.getMessage());
            }
        });
    }

    @Test
    public void testMultipleTypeContributionsExplicitBinding() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Inet> inets = entityManager.createNativeQuery(("select e.ip " + ("from Event e " + "where e.id = :id"))).setParameter("id", 1L).unwrap(.class).addScalar("ip", InetType.INSTANCE).getResultList();
            assertEquals(1, inets.size());
            assertEquals("192.168.0.123/24", inets.get(0).getAddress());
        });
    }

    @Entity(name = "Event")
    @Table(name = "event")
    @TypeDef(name = "ipv4", typeClass = InetType.class, defaultForType = Inet.class)
    @TypeDef(name = "json", typeClass = JsonType.class, defaultForType = Json.class)
    public class Event {
        @Id
        private Long id;

        @Column(name = "ip", columnDefinition = "inet")
        private Inet ip;

        @Column(name = "properties", columnDefinition = "jsonb")
        private Json properties;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Inet getIp() {
            return ip;
        }

        public void setIp(String address) {
            this.ip = new Inet(address);
        }

        public Json getProperties() {
            return properties;
        }

        public void setProperties(Json properties) {
            this.properties = properties;
        }
    }
}

