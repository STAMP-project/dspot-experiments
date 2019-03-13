/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id: ExtendsTest.java 10977 2006-12-12 23:28:04Z steve.ebersole@jboss.com $
 */
package org.hibernate.test.extendshbm;


import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class ExtendsTest extends BaseUnitTestCase {
    private StandardServiceRegistryImpl serviceRegistry;

    @Test
    public void testAllInOne() {
        Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/allinone.hbm.xml")).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Customer"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Person"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Employee"));
    }

    @Test
    public void testOutOfOrder() {
        Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/Employee.hbm.xml")).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Customer"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Person"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Employee"));
    }

    @Test
    public void testNwaitingForSuper() {
        Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/Person.hbm.xml")).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Customer"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Person"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Employee"));
    }

    @Test
    public void testMissingSuper() {
        try {
            Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/Employee.hbm.xml")).buildMetadata();
            Assert.fail("Should not be able to build sessionFactory without a Person");
        } catch (HibernateException e) {
        }
    }

    @Test
    public void testAllSeparateInOne() {
        Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/allseparateinone.hbm.xml")).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Customer"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Person"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Employee"));
    }

    @Test
    public void testJoinedSubclassAndEntityNamesOnly() {
        Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/entitynames.hbm.xml")).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding("EntityHasName"));
        Assert.assertNotNull(metadata.getEntityBinding("EntityCompany"));
    }

    @Test
    public void testEntityNamesWithPackage() {
        Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/packageentitynames.hbm.xml")).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding("EntityHasName"));
        Assert.assertNotNull(metadata.getEntityBinding("EntityCompany"));
    }

    @Test
    public void testUnionSubclass() {
        Metadata metadata = addResource(((getBaseForMappings()) + "extendshbm/unionsubclass.hbm.xml")).buildMetadata();
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Person"));
        Assert.assertNotNull(metadata.getEntityBinding("org.hibernate.test.extendshbm.Customer"));
    }
}

