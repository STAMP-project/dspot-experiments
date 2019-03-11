/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import DialectChecks.SupportsNoColumnInsert;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.junit.Test;


/**
 * Some simple test queries using the classic translator explicitly
 * to ensure that code is not broken in changes for the new translator.
 * <p/>
 * Only really checking translation and syntax, not results.
 *
 * @author Steve Ebersole
 */
@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class ClassicTranslatorTest extends QueryTranslatorTestCase {
    @Test
    public void testQueries() {
        Session session = openSession();
        session.beginTransaction();
        session.createQuery("from Animal").list();
        session.createQuery("select a from Animal as a").list();
        session.createQuery("select a.mother from Animal as a").list();
        session.createQuery("select m from Animal as a inner join a.mother as m").list();
        session.createQuery("select a from Animal as a inner join fetch a.mother").list();
        session.createQuery("from Animal as a where a.description = ?").setString(0, "jj").list();
        session.createQuery("from Animal as a where a.description = :desc").setString("desc", "jr").list();
        session.getTransaction().commit();
        session.close();
    }
}

