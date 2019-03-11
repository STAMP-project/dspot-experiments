package com.baeldung.test;


import AuditedInterceptor.calledAfter;
import AuditedInterceptor.calledBefore;
import com.baeldung.service.SuperService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Assert;
import org.junit.Test;


public class InterceptorIntegrationTest {
    Weld weld;

    WeldContainer container;

    @Test
    public void givenTheService_whenMethodAndInterceptorExecuted_thenOK() {
        SuperService superService = container.select(SuperService.class).get();
        String code = "123456";
        superService.deliverService(code);
        Assert.assertTrue(calledBefore);
        Assert.assertTrue(calledAfter);
    }
}

