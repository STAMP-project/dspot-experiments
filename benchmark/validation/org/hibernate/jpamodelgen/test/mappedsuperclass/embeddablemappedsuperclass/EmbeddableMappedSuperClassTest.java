/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.mappedsuperclass.embeddablemappedsuperclass;


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
public class EmbeddableMappedSuperClassTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-36")
    @WithClasses(EmbeddableAndMappedSuperClass.class)
    public void testMetaModelsGenerated() {
        TestUtil.assertMetamodelClassGeneratedFor(EmbeddableAndMappedSuperClass.class);
    }
}

