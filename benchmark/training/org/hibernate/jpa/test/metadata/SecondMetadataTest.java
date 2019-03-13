/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metadata;


import javax.persistence.EntityManagerFactory;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class SecondMetadataTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testBaseOfService() throws Exception {
        EntityManagerFactory emf = entityManagerFactory();
        Assert.assertNotNull(emf.getMetamodel());
        Assert.assertNotNull(emf.getMetamodel().entity(DeskWithRawType.class));
        Assert.assertNotNull(emf.getMetamodel().entity(EmployeeWithRawType.class));
        Assert.assertNotNull(emf.getMetamodel().entity(SimpleMedicalHistory.class));
        Assert.assertNotNull(emf.getMetamodel().entity(SimplePerson.class));
    }
}

