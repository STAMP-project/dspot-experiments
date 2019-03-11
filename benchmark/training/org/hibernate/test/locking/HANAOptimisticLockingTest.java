/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.locking;


import LockModeType.OPTIMISTIC;
import LockModeType.OPTIMISTIC_FORCE_INCREMENT;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import org.hibernate.dialect.HANAColumnStoreDialect;
import org.hibernate.dialect.HANARowStoreDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.RequiresDialects;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11656")
@RequiresDialects({ @RequiresDialect(HANAColumnStoreDialect.class), @RequiresDialect(HANARowStoreDialect.class) })
public class HANAOptimisticLockingTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOptimisticLock() throws Exception {
        testWithSpecifiedLockMode(OPTIMISTIC);
    }

    @Test
    public void testOptimisticLockForceIncrement() throws Exception {
        testWithSpecifiedLockMode(OPTIMISTIC_FORCE_INCREMENT);
    }

    @Entity(name = "SomeEntity")
    public static class SomeEntity {
        @Id
        @GeneratedValue
        private Integer id;

        @Version
        private Integer version;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }
    }
}

