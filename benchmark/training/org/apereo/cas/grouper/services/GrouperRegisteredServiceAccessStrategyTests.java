package org.apereo.cas.grouper.services;


import java.util.HashMap;
import java.util.HashSet;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.services.replication.NoOpRegisteredServiceReplicationStrategy;
import org.apereo.cas.services.resource.DefaultRegisteredServiceResourceNamingStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;


/**
 * The {@link GrouperRegisteredServiceAccessStrategyTests} provides
 * test cases for {@link GrouperRegisteredServiceAccessStrategy}.
 *
 * @author Misagh Moayyed
 * @since 4.2
 */
@Slf4j
public class GrouperRegisteredServiceAccessStrategyTests {
    private static final ClassPathResource RESOURCE = new ClassPathResource("services");

    @Test
    public void checkAccessStrategyJson() throws Exception {
        val attributes = new HashMap<String, java.util.Set<String>>();
        val v1 = new HashSet<String>();
        v1.add("admin");
        attributes.put("memberOf", v1);
        val service = RegisteredServiceTestUtils.getRegisteredService("test");
        val grouper = new GrouperRegisteredServiceAccessStrategy();
        grouper.setRequiredAttributes(attributes);
        service.setAccessStrategy(grouper);
        val dao = new org.apereo.cas.services.JsonServiceRegistry(GrouperRegisteredServiceAccessStrategyTests.RESOURCE, false, Mockito.mock(ApplicationEventPublisher.class), new NoOpRegisteredServiceReplicationStrategy(), new DefaultRegisteredServiceResourceNamingStrategy());
        val saved = dao.save(service);
        Assertions.assertEquals(service, saved);
        Assertions.assertFalse(dao.load().isEmpty());
    }
}

