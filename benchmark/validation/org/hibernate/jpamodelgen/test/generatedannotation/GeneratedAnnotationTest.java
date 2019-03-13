/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.generatedannotation;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class GeneratedAnnotationTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-79")
    @WithClasses(TestEntity.class)
    public void testGeneratedAnnotationNotGenerated() {
        TestUtil.assertMetamodelClassGeneratedFor(TestEntity.class);
        // need to check the source because @Generated is not a runtime annotation
        String metaModelSource = TestUtil.getMetaModelSourceAsString(TestEntity.class);
        String generatedString = "@Generated(value = \"org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor\")";
        Assert.assertTrue("@Generated should be added to the metamodel.", metaModelSource.contains(generatedString));
    }
}

