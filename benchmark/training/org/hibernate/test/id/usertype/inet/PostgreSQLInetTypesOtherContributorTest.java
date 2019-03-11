/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.usertype.inet;


import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(PostgreSQL82Dialect.class)
public class PostgreSQLInetTypesOtherContributorTest extends PostgreSQLInetTypesOtherTest {
    @Test
    public void testTypeContribution() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Inet> inets = entityManager.createNativeQuery(("select e.ip " + ("from Event e " + "where e.id = :id"))).setParameter("id", 1L).getResultList();
            assertEquals(1, inets.size());
            assertEquals("192.168.0.123/24", inets.get(0).getAddress());
        });
    }

    public class InetTypeMetadataBuilderContributor implements MetadataBuilderContributor {
        @Override
        public void contribute(MetadataBuilder metadataBuilder) {
            metadataBuilder.applyBasicType(InetType.INSTANCE, "inet");
        }
    }
}

