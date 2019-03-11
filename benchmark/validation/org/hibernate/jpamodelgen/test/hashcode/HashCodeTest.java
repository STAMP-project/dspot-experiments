/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.hashcode;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class HashCodeTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-76")
    @WithClasses(HashEntity.class)
    public void testHashCodeDoesNotCreateSingularAttribute() {
        TestUtil.assertMetamodelClassGeneratedFor(HashEntity.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(HashEntity.class, "id");
        TestUtil.assertAbsenceOfFieldInMetamodelFor(HashEntity.class, "hashCode", "hashCode is not a persistent property");
    }
}

