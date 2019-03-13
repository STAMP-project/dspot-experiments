/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.supresswarnings;


import org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor;
import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.hibernate.jpamodelgen.test.util.WithProcessorOption;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class SuppressWarningsAnnotationGeneratedTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-50")
    @WithClasses(TestEntity.class)
    @WithProcessorOption(key = JPAMetaModelEntityProcessor.ADD_SUPPRESS_WARNINGS_ANNOTATION, value = "true")
    public void testSuppressedWarningsAnnotationGenerated() {
        TestUtil.assertMetamodelClassGeneratedFor(TestEntity.class);
        // need to check the source because @SuppressWarnings is not a runtime annotation
        String metaModelSource = TestUtil.getMetaModelSourceAsString(TestEntity.class);
        Assert.assertTrue("@SuppressWarnings should be added to the metamodel.", metaModelSource.contains("@SuppressWarnings({ \"deprecation\", \"rawtypes\" })"));
    }
}

