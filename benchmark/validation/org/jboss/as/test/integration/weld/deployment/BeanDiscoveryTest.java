package org.jboss.as.test.integration.weld.deployment;


import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
public class BeanDiscoveryTest {
    @Inject
    private VerifyingExtension extension;

    @Inject
    private BeanManager manager;

    @Test
    public void testNoBeanArchiveModeNone() {
        assertNotDiscoveredAndNotAvailable(Foxtrot.class);
    }
}

