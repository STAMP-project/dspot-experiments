package org.apereo.cas.services;


import com.hazelcast.core.HazelcastInstance;
import lombok.val;
import org.apereo.cas.StringBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link RegisteredServiceHazelcastDistributedCacheManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class RegisteredServiceHazelcastDistributedCacheManagerTests {
    private HazelcastInstance hz;

    private RegisteredServiceHazelcastDistributedCacheManager mgr;

    @Test
    public void verifyAction() {
        val registeredService = RegisteredServiceTestUtils.getRegisteredService();
        var obj = mgr.get(registeredService);
        Assertions.assertNull(obj);
        Assertions.assertFalse(mgr.contains(registeredService));
        val cache = new org.apereo.cas.DistributedCacheObject<RegisteredService>(registeredService);
        mgr.set(registeredService, cache);
        Assertions.assertFalse(mgr.getAll().isEmpty());
        obj = mgr.get(registeredService);
        Assertions.assertNotNull(obj);
        val c = mgr.findAll(( obj1) -> obj1.getValue().equals(registeredService));
        Assertions.assertFalse(c.isEmpty());
        mgr.remove(registeredService, cache);
        Assertions.assertTrue(mgr.getAll().isEmpty());
    }

    @Test
    public void verifyPublisher() {
        val registeredService = RegisteredServiceTestUtils.getRegisteredService();
        val publisher = new org.apereo.cas.services.publisher.CasRegisteredServiceHazelcastStreamPublisher(mgr, new StringBean("123456"));
        publisher.publish(registeredService, new org.apereo.cas.support.events.service.CasRegisteredServiceDeletedEvent(this, registeredService));
        publisher.publish(registeredService, new org.apereo.cas.support.events.service.CasRegisteredServiceSavedEvent(this, registeredService));
        publisher.publish(registeredService, new org.apereo.cas.support.events.service.CasRegisteredServiceLoadedEvent(this, registeredService));
        Assertions.assertFalse(mgr.getAll().isEmpty());
    }
}

