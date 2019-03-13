/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(value = SupportsIdentityColumns.class, jiraKey = "HHH-9271")
@SkipForDialect(value = Oracle12cDialect.class, comment = "Oracle and identity column: java.sql.Connection#prepareStatement(String sql, int columnIndexes[]) does not work with quoted table names and/or quoted columnIndexes")
public class QuotedIdentifierTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testDirectIdPropertyAccess() {
        QuotedIdentifierTest.QuotedIdentifier quotedIdentifier = new QuotedIdentifierTest.QuotedIdentifier();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            quotedIdentifier.timestamp = System.currentTimeMillis();
            quotedIdentifier.from = "HHH-9271";
            session.persist(quotedIdentifier);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.id.QuotedIdentifier result = session.get(.class, quotedIdentifier.index);
            assertNotNull(result);
        });
    }

    @Entity(name = "QuotedIdentifier")
    @Table(name = "`table`")
    public static class QuotedIdentifier {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "`index`")
        private int index;

        @Column(name = "`timestamp`")
        private long timestamp;

        @Column(name = "`from`")
        private String from;
    }
}

