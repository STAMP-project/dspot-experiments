/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.emops;


import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class FlushTest extends BaseEntityManagerFunctionalTestCase {
    private static Set<String> names = FlushTest.namesSet();

    @Test
    @TestForIssue(jiraKey = "EJBTHREE-722")
    public void testFlushOnDetached() throws Exception {
        EntityManager manager = getOrCreateEntityManager();
        manager.getTransaction().begin();
        Pet p1 = createCat("Toonses", 15.0, 9, manager);
        manager.flush();
        manager.clear();
        Pet p2 = createCat("Sox", 10.0, 5, manager);
        manager.flush();
        manager.clear();
        Pet p3 = createDog("Winnie", 70.0, 5, manager);
        manager.flush();
        manager.clear();
        Pet p4 = createDog("Junior", 11.0, 1, manager);
        manager.flush();
        manager.clear();
        Decorate d1 = createDecorate("Test", p1, manager);
        manager.flush();
        manager.clear();
        Decorate d2 = createDecorate("Test2", p2, manager);
        manager.flush();
        manager.clear();
        List l = findByWeight(14.0, manager);
        manager.flush();
        manager.clear();
        for (Object o : l) {
            Assert.assertTrue(FlushTest.names.contains(((Pet) (o)).getName()));
        }
        Collection<Decorate> founds = getDecorate(manager);
        manager.flush();
        manager.clear();
        for (Decorate value : founds) {
            Assert.assertTrue(FlushTest.names.contains(value.getPet().getName()));
        }
        manager.getTransaction().rollback();
        manager.close();
    }
}

