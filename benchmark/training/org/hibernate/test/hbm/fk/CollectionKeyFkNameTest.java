/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hbm.fk;


import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CollectionKeyFkNameTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    @Test
    @TestForIssue(jiraKey = "HHH-10207")
    public void testExplicitFkNameOnCollectionKey() {
        verifyFkNameUsed("org/hibernate/test/hbm/fk/person_set.hbm.xml", "person_persongroup_fk");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10207")
    public void testExplicitFkNameOnManyToOne() {
        verifyFkNameUsed("org/hibernate/test/hbm/fk/person_set.hbm.xml", "person_persongroup_fk");
    }
}

