/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cache;


import AvailableSettings.JPA_SHARED_CACHE_MODE;
import Environment.CACHE_REGION_FACTORY;
import SharedCacheMode.ENABLE_SELECTIVE;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.cache.CachingRegionFactory;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
@TestForIssue(jiraKey = "HHH-11143")
public class NonRootEntityWithCacheAnnotationTest {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, EntityBinder.class.getName()));

    @Test
    public void testCacheOnNonRootEntity() {
        Map settings = new HashMap();
        settings.put(CACHE_REGION_FACTORY, CachingRegionFactory.class.getName());
        settings.put(JPA_SHARED_CACHE_MODE, ENABLE_SELECTIVE);
        ServiceRegistryImplementor serviceRegistry = ((ServiceRegistryImplementor) (new StandardServiceRegistryBuilder().applySettings(settings).build()));
        Triggerable triggerable = logInspection.watchForLogMessages("HHH000482");
        Metadata metadata = addAnnotatedClass(NonRootEntityWithCacheAnnotationTest.AEntity.class).buildMetadata();
        Assert.assertTrue(triggerable.wasTriggered());
        Assert.assertFalse(metadata.getEntityBinding(NonRootEntityWithCacheAnnotationTest.AEntity.class.getName()).isCached());
        serviceRegistry.destroy();
    }

    @Entity
    @Inheritance
    public static class ABase {
        @Id
        private Long id;
    }

    @Entity
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    public static class AEntity extends NonRootEntityWithCacheAnnotationTest.ABase {
        private String name;
    }
}

