/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.boot.jaxb.hbm.internal;


import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Jean-Fran?ois Boeuf
 */
public class EntityModeConverterTest extends BaseUnitTestCase {
    @Test
    public void testMashallNullEntityMode() throws Exception {
        XmlBindingChecker.checkValidGeneration(generateXml(false));
    }

    @Test
    public void testMashallNotNullEntityMode() throws Exception {
        XmlBindingChecker.checkValidGeneration(generateXml(true));
    }
}

