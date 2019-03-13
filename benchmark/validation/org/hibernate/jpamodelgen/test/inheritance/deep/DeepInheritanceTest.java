/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.inheritance.deep;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 * Tests a deep class hierarchy mixed with inheritance and a root class that
 * does not declare an id
 *
 * @author Igor Vaynberg
 */
public class DeepInheritanceTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-69")
    @WithClasses({ JetPlane.class, PersistenceBase.class, Plane.class })
    public void testDeepInheritance() throws Exception {
        TestUtil.assertMetamodelClassGeneratedFor(Plane.class);
        TestUtil.assertMetamodelClassGeneratedFor(JetPlane.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(JetPlane.class, "jets");
        TestUtil.assertAttributeTypeInMetaModelFor(JetPlane.class, "jets", Integer.class, "jets should be defined in JetPlane_");
    }
}

