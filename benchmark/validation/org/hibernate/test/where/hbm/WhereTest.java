/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.where.hbm;


import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Max Rydahl Andersen
 */
public class WhereTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testHql() {
        inTransaction(( s) -> {
            File parent = s.createQuery("from File f where f.id = 4", .class).uniqueResult();
            assertNull(parent);
        });
    }

    @Test
    public void testHqlWithFetch() {
        inTransaction(( s) -> {
            final List<File> files = s.createQuery("from File f left join fetch f.children where f.parent is null", .class).list();
            final HashSet<File> filesSet = new HashSet<>(files);
            assertEquals(1, filesSet.size());
            File parent = files.get(0);
            assertEquals(parent.getChildren().size(), 1);
        });
    }

    @Test
    public void testCriteria() {
        inTransaction(( s) -> {
            File parent = ((File) (s.createCriteria(.class).setFetchMode("children", FetchMode.JOIN).add(Restrictions.isNull("parent")).uniqueResult()));
            assertEquals(parent.getChildren().size(), 1);
        });
    }

    @Test
    public void testNativeQuery() {
        inTransaction(( s) -> {
            final NativeQuery query = s.createNativeQuery("select {f.*}, {c.*} from T_FILE f left join T_FILE c on f.id = c.parent where f.parent is null").addEntity("f", .class);
            query.addFetch("c", "f", "children");
            File parent = ((File) (((Object[]) (query.list().get(0)))[0]));
            // @Where should not be applied
            assertEquals(parent.getChildren().size(), 2);
        });
    }
}

