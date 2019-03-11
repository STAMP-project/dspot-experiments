/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hbm.collectionpk;


import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-10206")
public class CollectionPkTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    @Test
    public void testSet() {
        verifyPkNameUsed("org/hibernate/test/hbm/collectionpk/person_set.hbm.xml", "primary key (group, name)");
    }

    @Test
    public void testMap() {
        verifyPkNameUsed("org/hibernate/test/hbm/collectionpk/person_map.hbm.xml", "primary key (group, locale)");
    }
}

