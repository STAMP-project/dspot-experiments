/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metagen.mappedsuperclass.idclass;


import AvailableSettings.LOADED_CLASSES;
import ProductAttribute_.key;
import ProductAttribute_.owner;
import ProductAttribute_.value;
import java.util.Arrays;
import javax.persistence.EntityManagerFactory;
import org.hibernate.jpa.test.TestingEntityManagerFactoryGenerator;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alexis Bataille
 * @author Steve Ebersole
 */
public class MappedSuperclassWithEntityWithIdClassTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-5024")
    public void testStaticMetamodel() {
        EntityManagerFactory emf = TestingEntityManagerFactoryGenerator.generateEntityManagerFactory(LOADED_CLASSES, Arrays.asList(ProductAttribute.class));
        try {
            Assert.assertNotNull("'ProductAttribute_.value' should not be null)", value);
            Assert.assertNotNull("'ProductAttribute_.owner' should not be null)", owner);
            Assert.assertNotNull("'ProductAttribute_.key' should not be null)", key);
            Assert.assertNotNull("'AbstractAttribute_.value' should not be null)", AbstractAttribute_.value);
        } finally {
            emf.close();
        }
    }
}

