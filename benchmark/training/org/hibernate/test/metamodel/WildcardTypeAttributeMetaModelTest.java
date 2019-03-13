/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.metamodel;


import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.test.metamodel.wildcardmodel.AbstractOwner;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class WildcardTypeAttributeMetaModelTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9403")
    public void testWildcardGenericAttributeCanBeResolved() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            EntityType<AbstractOwner> entity = entityManager.getMetamodel().entity(.class);
            assertNotNull(entity);
        });
    }
}

