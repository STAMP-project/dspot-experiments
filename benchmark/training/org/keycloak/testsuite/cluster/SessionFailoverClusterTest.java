package org.keycloak.testsuite.cluster;


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author tkyjovsk
 */
public class SessionFailoverClusterTest extends AbstractFailoverClusterTest {
    @Test
    public void sessionFailover() {
        boolean expectSuccessfulFailover = (AbstractFailoverClusterTest.SESSION_CACHE_OWNERS) >= 2;
        log.info((((((("SESSION FAILOVER TEST: cluster size = " + (getClusterSize())) + ", session-cache owners = ") + (AbstractFailoverClusterTest.SESSION_CACHE_OWNERS)) + " --> Testsing for ") + (expectSuccessfulFailover ? "" : "UN")) + "SUCCESSFUL session failover."));
        Assert.assertEquals(2, getClusterSize());
        sessionFailover(expectSuccessfulFailover);
    }
}

