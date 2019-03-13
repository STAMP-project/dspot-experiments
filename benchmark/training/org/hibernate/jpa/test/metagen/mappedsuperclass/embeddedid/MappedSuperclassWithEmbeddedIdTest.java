/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metagen.mappedsuperclass.embeddedid;


import AvailableSettings.LOADED_CLASSES;
import ProductId_.code;
import Product_.description;
import Product_.id;
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
public class MappedSuperclassWithEmbeddedIdTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-5024")
    public void testStaticMetamodel() {
        EntityManagerFactory emf = TestingEntityManagerFactoryGenerator.generateEntityManagerFactory(LOADED_CLASSES, Arrays.asList(Product.class));
        try {
            Assert.assertNotNull("'Product_.description' should not be null)", description);
            Assert.assertNotNull("'Product_.id' should not be null)", id);
            Assert.assertNotNull("'AbstractProduct_.id' should not be null)", AbstractProduct_.id);
            Assert.assertNotNull("'ProductId_.id' should not be null)", ProductId_.id);
            Assert.assertNotNull("'ProductId_.code' should not be null)", code);
        } finally {
            emf.close();
        }
    }
}

