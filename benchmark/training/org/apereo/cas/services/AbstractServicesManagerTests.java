package org.apereo.cas.services;


import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link AbstractServicesManagerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public abstract class AbstractServicesManagerTests {
    private static final String TEST = "test";

    protected final List<RegisteredService> listOfDefaultServices = new ArrayList<>();

    protected ServiceRegistry serviceRegistry;

    protected ServicesManager servicesManager;

    public AbstractServicesManagerTests() {
        val r = new RegexRegisteredService();
        r.setId(2500);
        r.setServiceId("serviceId");
        r.setName("serviceName");
        r.setEvaluationOrder(1000);
        listOfDefaultServices.add(r);
    }

    @Test
    public void verifySaveAndGet() {
        val r = new RegexRegisteredService();
        r.setId(1000);
        r.setName(AbstractServicesManagerTests.TEST);
        r.setServiceId(AbstractServicesManagerTests.TEST);
        this.servicesManager.save(r);
        Assertions.assertNotNull(this.servicesManager.findServiceBy(1000));
    }

    @Test
    public void verifyDelete() {
        val r = new RegexRegisteredService();
        r.setId(1000);
        r.setName(AbstractServicesManagerTests.TEST);
        r.setServiceId(AbstractServicesManagerTests.TEST);
        this.servicesManager.save(r);
        Assertions.assertNotNull(this.servicesManager.findServiceBy(r.getServiceId()));
        this.servicesManager.delete(r);
        Assertions.assertNull(this.servicesManager.findServiceBy(r.getId()));
    }
}

