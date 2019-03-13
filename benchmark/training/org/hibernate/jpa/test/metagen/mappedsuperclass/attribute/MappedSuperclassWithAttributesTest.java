/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metagen.mappedsuperclass.attribute;


import AvailableSettings.LOADED_CLASSES;
import Product_.id;
import Product_.name;
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
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-5024")
public class MappedSuperclassWithAttributesTest extends BaseUnitTestCase {
    @Test
    public void testStaticMetamodel() {
        EntityManagerFactory emf = TestingEntityManagerFactoryGenerator.generateEntityManagerFactory(LOADED_CLASSES, Arrays.asList(Product.class));
        try {
            Assert.assertNotNull("'Product_.id' should not be null)", id);
            Assert.assertNotNull("'Product_.name' should not be null)", name);
            Assert.assertNotNull("'AbstractNameable_.name' should not be null)", AbstractNameable_.name);
        } finally {
            emf.close();
        }
    }
}

