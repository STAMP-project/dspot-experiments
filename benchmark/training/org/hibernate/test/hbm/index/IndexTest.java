/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hbm.index;


import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-10208")
public class IndexTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    // @FailureExpected( jiraKey = "HHH-10208" )
    @Test
    public void testOneToMany() throws Exception {
        verifyIndexCreated("org/hibernate/test/hbm/index/person_manytoone.hbm.xml", "person_persongroup_index");
    }

    // @FailureExpected( jiraKey = "HHH-10208" )
    @Test
    public void testProperty() throws Exception {
        verifyIndexCreated("org/hibernate/test/hbm/index/person_property.hbm.xml", "person_name_index");
    }

    @Test
    public void testPropertyColumn() throws Exception {
        verifyIndexCreated("org/hibernate/test/hbm/index/person_propertycolumn.hbm.xml", "person_name_index");
    }
}

