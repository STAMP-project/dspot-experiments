/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.persistenceunit;


import AvailableSettings.USE_SECOND_LEVEL_CACHE;
import SharedCacheMode.ENABLE_SELECTIVE;
import java.util.Collections;
import java.util.Map;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.cfg.Environment;
import org.hibernate.jpa.AvailableSettings.LOADED_CLASSES;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class TwoPersistenceUnits2LCDisabledEnabled {
    @Test
    @TestForIssue(jiraKey = "HHH-11516")
    public void testDisabledEnabled() {
        final Map<Object, Object> config = Environment.getProperties();
        config.put(LOADED_CLASSES, Collections.singletonList(TwoPersistenceUnits2LCDisabledEnabled.AnEntity.class));
        config.put("javax.persistence.sharedCache.mode", ENABLE_SELECTIVE);
        config.put(USE_SECOND_LEVEL_CACHE, "false");
        testIt(config);
        config.put(USE_SECOND_LEVEL_CACHE, "true");
        testIt(config);
    }

    @Cacheable
    @Entity(name = "AnEntity")
    public static class AnEntity {
        @Id
        private Long id;
    }
}

