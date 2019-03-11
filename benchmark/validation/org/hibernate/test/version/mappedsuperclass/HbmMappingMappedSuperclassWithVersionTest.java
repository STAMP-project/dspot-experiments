/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.version.mappedsuperclass;


import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11549")
public class HbmMappingMappedSuperclassWithVersionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testMetamodelContainsHbmVersion() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final TestEntity entity = new TestEntity();
            entity.setName("Chris");
            entityManager.persist(entity);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<TestEntity> query = builder.createQuery(.class);
            final Root<TestEntity> root = query.from(.class);
            assertThat(root.get("version"), is(notNullValue()));
        });
    }
}

