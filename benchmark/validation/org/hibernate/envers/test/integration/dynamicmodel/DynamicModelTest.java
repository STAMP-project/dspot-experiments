/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.dynamicmodel;


import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Felix Feisst (feisst dot felix at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-8769")
public class DynamicModelTest extends BaseEnversJPAFunctionalTestCase {
    /**
     * Tests that an EntityManager can be created when using a dynamic model mapping.
     */
    @Test
    public void testDynamicModelMapping() {
        EntityManager entityManager = getOrCreateEntityManager();
        Assert.assertNotNull("Expected an entity manager to be returned", entityManager);
    }
}

