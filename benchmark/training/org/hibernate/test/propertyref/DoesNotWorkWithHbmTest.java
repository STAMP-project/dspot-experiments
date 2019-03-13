/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.propertyref;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialect(H2Dialect.class)
public class DoesNotWorkWithHbmTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testIt() {
        DoesNotWorkPk pk = new DoesNotWorkPk();
        pk.setId1("ZZZ");
        pk.setId2("00");
        // {
        // Session session = openSession();
        // session.beginTransaction();
        // DoesNotWork entity = new DoesNotWork( pk );
        // entity.setGlobalNotes( Arrays.asList( "My first note!" ) );
        // session.save( entity );
        // session.getTransaction().commit();
        // session.close();
        // }
        {
            Session session = openSession();
            session.beginTransaction();
            DoesNotWork entity = ((DoesNotWork) (session.get(DoesNotWork.class, pk)));
            Assert.assertNotNull(entity);
            List<String> notes = entity.getGlobalNotes();
            Assert.assertNotNull(notes);
            Assert.assertEquals(2, notes.size());
            for (String s : notes) {
                System.out.println(s);
            }
            session.delete(entity);
            session.getTransaction().commit();
            session.close();
        }
    }
}

