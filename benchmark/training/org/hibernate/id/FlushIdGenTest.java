/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id;


import DialectChecks.SupportsIdentityColumns;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-8611")
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class FlushIdGenTest extends BaseCoreFunctionalTestCase {
    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testPersistBeforeTransaction() {
        Session session = openSession();
        RootEntity ent1_0 = new RootEntity();
        RootEntity ent1_1 = new RootEntity();
        session.persist(ent1_0);
        session.persist(ent1_1);
        Transaction tx = session.beginTransaction();
        tx.commit();// flush

    }
}

