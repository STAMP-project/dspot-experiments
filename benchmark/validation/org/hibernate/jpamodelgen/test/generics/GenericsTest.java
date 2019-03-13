/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.generics;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class GenericsTest extends CompilationTest {
    @Test
    @WithClasses({ Parent.class, Child.class })
    public void testGenerics() {
        TestUtil.assertMetamodelClassGeneratedFor(Parent.class);
        TestUtil.assertMetamodelClassGeneratedFor(Child.class);
    }
}

