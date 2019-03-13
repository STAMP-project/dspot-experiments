/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.arraytype;


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
public class ArrayTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-2")
    @WithClasses(Image.class)
    public void testPrimitiveArray() {
        TestUtil.assertAttributeTypeInMetaModelFor(Image.class, "data", byte[].class, "Wrong type for field.");
    }

    @Test
    @TestForIssue(jiraKey = "METAGEN-2")
    @WithClasses(TemperatureSamples.class)
    public void testIntegerArray() {
        TestUtil.assertAttributeTypeInMetaModelFor(TemperatureSamples.class, "samples", Integer[].class, "Wrong type for field.");
    }
}

