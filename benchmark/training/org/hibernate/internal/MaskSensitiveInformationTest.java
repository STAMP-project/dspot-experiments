/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.internal;


import AvailableSettings.JPA_JDBC_PASSWORD;
import AvailableSettings.JPA_JDBC_USER;
import AvailableSettings.PASS;
import AvailableSettings.USER;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test that sensitive information is correctly masked.
 *
 * @author Bruno P. Kinoshita
 */
public class MaskSensitiveInformationTest extends BaseEntityManagerFunctionalTestCase {
    private EntityManagerFactory entityManagerFactory;

    private static final String EXPECTED_MASKED_VALUE = "****";

    @Test
    public void testMaskOutSensitiveInformation() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        Map<String, Object> properties = sessionFactory.getProperties();
        Assert.assertEquals(MaskSensitiveInformationTest.EXPECTED_MASKED_VALUE, properties.get(USER));
        Assert.assertEquals(MaskSensitiveInformationTest.EXPECTED_MASKED_VALUE, properties.get(PASS));
        Assert.assertEquals(MaskSensitiveInformationTest.EXPECTED_MASKED_VALUE, properties.get(JPA_JDBC_USER));
        Assert.assertEquals(MaskSensitiveInformationTest.EXPECTED_MASKED_VALUE, properties.get(JPA_JDBC_PASSWORD));
    }
}

