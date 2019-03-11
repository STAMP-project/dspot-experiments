/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.cascade.multicircle.nonjpa.identity;


import DialectChecks.SupportsIdentityColumns;
import org.hibernate.Session;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * This test uses a complicated model that requires Hibernate to delay
 * inserts until non-nullable transient entity dependencies are resolved.
 *
 * All IDs are generated from identity columns.
 *
 * Hibernate cascade types are used (org.hibernate.annotations.CascadeType)..
 *
 * This test uses the following model:
 *
 * <code>
 *     ------------------------------ N G
 *     |
 *     |                                1
 *     |                                |
 *     |                                |
 *     |                                N
 *     |
 *     |         E N--------------0,1 * F
 *     |
 *     |         1                      N
 *     |         |                      |
 *     |         |                      |
 *     1         N                      |
 *     *                                |
 *     B * N---1 D * 1------------------
 *     *
 *     N         N
 *     |         |
 *     |         |
 *     1         |
 *               |
 *     C * 1-----
 * </code>
 *
 * In the diagram, all associations are bidirectional;
 * assocations marked with '*' cascade persist, save, merge operations to the
 * associated entities (e.g., B cascades persist to D, but D does not cascade
 * persist to B);
 *
 * b, c, d, e, f, and g are all transient unsaved that are associated with each other.
 *
 * When saving b, the entities are added to the ActionQueue in the following order:
 * c, d (depends on e), f (depends on d, g), e, b, g.
 *
 * Entities are inserted in the following order:
 * c, e, d, b, g, f.
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class MultiCircleNonJpaCascadeIdentityTest extends BaseCoreFunctionalTestCase {
    private EntityB b;

    private EntityC c;

    private EntityD d;

    private EntityE e;

    private EntityF f;

    private EntityG g;

    @Test
    public void testPersist() {
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(b);
        s.getTransaction().commit();
        s.close();
        check();
    }

    @Test
    public void testSave() {
        Session s = openSession();
        s.getTransaction().begin();
        s.save(b);
        s.getTransaction().commit();
        s.close();
        check();
    }

    @Test
    public void testSaveOrUpdate() {
        Session s = openSession();
        s.getTransaction().begin();
        s.saveOrUpdate(b);
        s.getTransaction().commit();
        s.close();
        check();
    }

    @Test
    public void testMerge() {
        Session s = openSession();
        s.getTransaction().begin();
        b = ((EntityB) (s.merge(b)));
        c = b.getC();
        d = b.getD();
        e = d.getE();
        f = e.getF();
        g = f.getG();
        s.getTransaction().commit();
        s.close();
        check();
    }
}

