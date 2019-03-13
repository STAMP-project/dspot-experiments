/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */


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
@TestForIssue(jiraKey = "METAGEN-40")
@WithClasses(DefaultPackageEntity.class)
public class DefaultPackageTest extends CompilationTest {
    @Test
    public void testMetaModelGeneratedForEntitiesInDefaultPackage() {
        TestUtil.assertMetamodelClassGeneratedFor(DefaultPackageEntity.class);
    }
}

