/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metagen.mappedsuperclass.embedded;


import AvailableSettings.LOADED_CLASSES;
import Company_.address;
import Company_.id;
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
public class MappedSuperclassWithEmbeddedTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-5024")
    public void testStaticMetamodel() {
        EntityManagerFactory emf = TestingEntityManagerFactoryGenerator.generateEntityManagerFactory(LOADED_CLASSES, Arrays.asList(Company.class));
        try {
            Assert.assertNotNull("'Company_.id' should not be null)", id);
            Assert.assertNotNull("'Company_.address' should not be null)", address);
            Assert.assertNotNull("'AbstractAddressable_.address' should not be null)", AbstractAddressable_.address);
        } finally {
            emf.close();
        }
    }
}

