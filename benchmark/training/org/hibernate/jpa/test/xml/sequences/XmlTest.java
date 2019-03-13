/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.xml.sequences;


import DialectChecks.SupportsSequences;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
@RequiresDialectFeature(SupportsSequences.class)
public class XmlTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testXmlMappingCorrectness() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.close();
    }
}

