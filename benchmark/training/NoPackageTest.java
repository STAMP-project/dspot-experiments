/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */


import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * Test using an entity which is in no package.
 * We had problems with ByteBuddy in the past.
 */
@TestForIssue(jiraKey = "HHH-13112")
public class NoPackageTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNoException() {
        inTransaction(( session) -> {
            AnnotationMappedNoPackageEntity box = new AnnotationMappedNoPackageEntity();
            box.setId(42);
            box.setName("This feels dirty");
            session.persist(box);
        });
        inTransaction(( session) -> {
            Query<AnnotationMappedNoPackageEntity> query = session.createQuery((("select e from " + (.class.getSimpleName())) + " e"), .class);
            AnnotationMappedNoPackageEntity box = query.getSingleResult();
            assertEquals(((Integer) (42)), box.getId());
        });
    }
}

