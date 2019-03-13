/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.metamodel;


import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


public class EmbeddableMetaModelTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11111")
    public void testEmbeddableCanBeResolvedWhenUsedAsInterface() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            assertNotNull(entityManager.getMetamodel().embeddable(.class));
            assertEquals(.class, ProductEntity_.description.getElementType().getJavaType());
            assertNotNull(LocalizedValue_.value);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12124")
    public void testEmbeddableEquality() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            assertTrue(entityManager.getMetamodel().getEmbeddables().contains(Company_.address.getType()));
            assertTrue(entityManager.getMetamodel().getEmbeddables().contains(Person_.address.getType()));
        });
    }
}

