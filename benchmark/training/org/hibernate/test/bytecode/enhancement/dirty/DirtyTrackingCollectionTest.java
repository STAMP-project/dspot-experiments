/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.dirty;


import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-11293")
@RunWith(BytecodeEnhancerRunner.class)
public class DirtyTrackingCollectionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        TransactionUtil.doInJPA(this::sessionFactory, ( entityManager) -> {
            org.hibernate.test.bytecode.enhancement.dirty.StringsEntity entity = entityManager.find(.class, 1L);
            entity.someStrings.clear();
        });
        TransactionUtil.doInJPA(this::sessionFactory, ( entityManager) -> {
            org.hibernate.test.bytecode.enhancement.dirty.StringsEntity entity = entityManager.find(.class, 1L);
            assertEquals(0, entity.someStrings.size());
            entity.someStrings.add("d");
        });
        TransactionUtil.doInJPA(this::sessionFactory, ( entityManager) -> {
            org.hibernate.test.bytecode.enhancement.dirty.StringsEntity entity = entityManager.find(.class, 1L);
            assertEquals(1, entity.someStrings.size());
            entity.someStrings = new ArrayList<>();
        });
        TransactionUtil.doInJPA(this::sessionFactory, ( entityManager) -> {
            org.hibernate.test.bytecode.enhancement.dirty.StringsEntity entity = entityManager.find(.class, 1L);
            assertEquals(0, entity.someStrings.size());
        });
    }

    // --- //
    @Entity
    @Table(name = "STRINGS_ENTITY")
    private static class StringsEntity {
        @Id
        Long id;

        @ElementCollection
        List<String> someStrings;
    }
}

