/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.nativequery;


import java.util.List;
import javax.persistence.Query;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class EntityResultNativeQueryTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(new SimpleEntity("Hibernate"));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12776")
    public void testNativeQueryResultHandling() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Query query = entityManager.createNativeQuery("select * from SimpleEntity", .class);
            List results = query.getResultList();
            SimpleEntity result = ((SimpleEntity) (results.get(0)));
            assertThat(result.getStringField(), is("Hibernate"));
        });
    }
}

