package org.apereo.cas.services;


import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author battags
 * @since 3.0.0
 */
public class DefaultServicesManagerByEnvironmentTests extends AbstractServicesManagerTests {
    @Test
    public void verifyServiceByEnvironment() {
        val r = new RegexRegisteredService();
        r.setId(2000);
        r.setName(getClass().getSimpleName());
        r.setServiceId(getClass().getSimpleName());
        r.setEnvironments(CollectionUtils.wrapHashSet("dev1"));
        this.servicesManager.save(r);
        Assertions.assertNull(this.servicesManager.findServiceBy(getClass().getSimpleName()));
        Assertions.assertNull(this.servicesManager.findServiceBy(2000));
        r.setEnvironments(CollectionUtils.wrapHashSet("prod1"));
        this.servicesManager.save(r);
        Assertions.assertNotNull(this.servicesManager.findServiceBy(2000));
    }
}

