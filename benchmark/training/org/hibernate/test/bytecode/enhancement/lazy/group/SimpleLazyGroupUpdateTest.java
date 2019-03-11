/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.bytecode.enhancement.lazy.group;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.LazyGroup;
import org.hibernate.bytecode.enhance.spi.UnloadedClass;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.bytecode.enhancement.CustomEnhancementContext;
import org.hibernate.testing.bytecode.enhancement.EnhancerTestContext;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-11155, HHH-11506")
@RunWith(BytecodeEnhancerRunner.class)
@CustomEnhancementContext({ EnhancerTestContext.class, SimpleLazyGroupUpdateTest.NoDirtyCheckingContext.class })
public class SimpleLazyGroupUpdateTest extends BaseCoreFunctionalTestCase {
    public static final String REALLY_BIG_STRING = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.group.TestEntity entity = s.load(.class, 1L);
            assertLoaded(entity, "name");
            assertNotLoaded(entity, "lifeStory");
            assertNotLoaded(entity, "reallyBigString");
            entity.lifeStory = "blah blah blah";
            assertLoaded(entity, "name");
            assertLoaded(entity, "lifeStory");
            assertNotLoaded(entity, "reallyBigString");
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.group.TestEntity entity = s.load(.class, 1L);
            assertLoaded(entity, "name");
            assertNotLoaded(entity, "lifeStory");
            assertNotLoaded(entity, "reallyBigString");
            assertEquals("blah blah blah", entity.lifeStory);
            assertEquals(REALLY_BIG_STRING, entity.reallyBigString);
        });
    }

    // --- //
    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    private static class TestEntity {
        @Id
        Long id;

        String name;

        @Basic(fetch = FetchType.LAZY)
        @LazyGroup("grp1")
        String lifeStory;

        @Basic(fetch = FetchType.LAZY)
        @LazyGroup("grp2")
        String reallyBigString;

        TestEntity() {
        }

        TestEntity(Long id, String name, String lifeStory, String reallyBigString) {
            this.id = id;
            this.name = name;
            this.lifeStory = lifeStory;
            this.reallyBigString = reallyBigString;
        }
    }

    // --- //
    public static class NoDirtyCheckingContext extends EnhancerTestContext {
        @Override
        public boolean doDirtyCheckingInline(UnloadedClass classDescriptor) {
            return false;
        }
    }
}

