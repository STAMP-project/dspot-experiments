package org.apereo.cas.web.support;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.authentication.AuthenticationManager;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasThrottlingConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;


/**
 * Base class for submission throttle tests.
 *
 * @author Marvin S. Addison
 * @since 3.0.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class, CasCoreUtilConfiguration.class, CasCoreAuditConfiguration.class, AopAutoConfiguration.class, CasThrottlingConfiguration.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@TestPropertySource(properties = { "spring.aop.proxy-target-class=true", "cas.authn.throttle.failure.rangeSeconds=1", "cas.authn.throttle.failure.threshold=2" })
@EnableScheduling
@Slf4j
public abstract class BaseThrottledSubmissionHandlerInterceptorAdapterTests {
    protected static final String IP_ADDRESS = "1.2.3.4";

    @Autowired
    @Qualifier("casAuthenticationManager")
    protected AuthenticationManager authenticationManager;

    @Test
    @SneakyThrows
    public void verifyThrottle() {
        // Ensure that repeated logins BELOW threshold rate are allowed
        failLoop(3, 1000, HttpStatus.SC_UNAUTHORIZED);
        // Ensure that repeated logins ABOVE threshold rate are throttled
        failLoop(3, 200, HttpStatus.SC_LOCKED);
        // Ensure that slowing down relieves throttle
        getThrottle().decrement();
        Thread.sleep(1000);
        failLoop(3, 1000, HttpStatus.SC_UNAUTHORIZED);
    }
}

