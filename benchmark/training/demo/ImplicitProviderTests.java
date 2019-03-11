package demo;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import sparklr.common.AbstractImplicitProviderTests;


/**
 *
 *
 * @author Dave Syer
 */
public class ImplicitProviderTests extends AbstractImplicitProviderTests {
    @Test
    @OAuth2ContextConfiguration(ImplicitProviderTests.ResourceOwner.class)
    public void parallelGrants() throws Exception {
        getToken();
        Collection<Future<?>> futures = new HashSet<Future<?>>();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 100; i++) {
            futures.add(pool.submit(new Runnable() {
                @Override
                public void run() {
                    getToken();
                }
            }));
        }
        for (Future<?> future : futures) {
            future.get();
        }
    }

    protected static class ResourceOwner extends ResourceOwnerPasswordResourceDetails {
        public ResourceOwner(Object target) {
            setClientId("my-trusted-client");
            setScope(Arrays.asList("read"));
            setId(getClientId());
            setUsername("user");
            setPassword("password");
        }
    }
}

