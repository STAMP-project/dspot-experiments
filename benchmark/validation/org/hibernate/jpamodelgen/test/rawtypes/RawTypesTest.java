/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.rawtypes;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class RawTypesTest extends CompilationTest {
    @Test
    @WithClasses({ DeskWithRawType.class, EmployeeWithRawType.class })
    public void testGenerics() {
        TestUtil.assertMetamodelClassGeneratedFor(DeskWithRawType.class);
        TestUtil.assertMetamodelClassGeneratedFor(EmployeeWithRawType.class);
    }
}

