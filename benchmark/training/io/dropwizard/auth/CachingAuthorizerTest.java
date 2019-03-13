package io.dropwizard.auth;


import com.codahale.metrics.MetricRegistry;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import io.dropwizard.util.Sets;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class CachingAuthorizerTest {
    @SuppressWarnings("unchecked")
    private final Authorizer<Principal> underlying = Mockito.mock(Authorizer.class);

    private final CachingAuthorizer<Principal> cached = new CachingAuthorizer(new MetricRegistry(), underlying, CaffeineSpec.parse("maximumSize=1"));

    private final Principal principal = new PrincipalImpl("principal");

    private final Principal principal2 = new PrincipalImpl("principal2");

    private final String role = "popular_kids";

    @Test
    public void cachesTheFirstReturnedPrincipal() throws Exception {
        assertThat(cached.authorize(principal, role)).isTrue();
        assertThat(cached.authorize(principal, role)).isTrue();
        Mockito.verify(underlying, Mockito.times(1)).authorize(principal, role);
    }

    @Test
    public void respectsTheCacheConfiguration() throws Exception {
        cached.authorize(principal, role);
        // We need to make sure that background cache invalidation is done before other requests
        cached.cache.cleanUp();
        cached.authorize(principal2, role);
        cached.cache.cleanUp();
        cached.authorize(principal, role);
        final InOrder inOrder = Mockito.inOrder(underlying);
        inOrder.verify(underlying, Mockito.times(1)).authorize(principal, role);
        inOrder.verify(underlying, Mockito.times(1)).authorize(principal2, role);
        inOrder.verify(underlying, Mockito.times(1)).authorize(principal, role);
    }

    @Test
    public void invalidatesPrincipalAndRole() throws Exception {
        cached.authorize(principal, role);
        cached.invalidate(principal, role);
        cached.authorize(principal, role);
        Mockito.verify(underlying, Mockito.times(2)).authorize(principal, role);
    }

    @Test
    public void invalidatesSinglePrincipal() throws Exception {
        cached.authorize(principal, role);
        cached.invalidate(principal);
        cached.authorize(principal, role);
        Mockito.verify(underlying, Mockito.times(2)).authorize(principal, role);
    }

    @Test
    public void invalidatesSetsofPrincipals() throws Exception {
        cached.authorize(principal, role);
        cached.authorize(principal2, role);
        cached.invalidateAll(Sets.of(principal, principal2));
        cached.authorize(principal, role);
        cached.authorize(principal2, role);
        Mockito.verify(underlying, Mockito.times(2)).authorize(principal, role);
        Mockito.verify(underlying, Mockito.times(2)).authorize(principal2, role);
    }

    @Test
    public void invalidatesPrincipalsMatchingGivenPredicate() throws Exception {
        cached.authorize(principal, role);
        cached.invalidateAll(principal::equals);
        cached.authorize(principal, role);
        Mockito.verify(underlying, Mockito.times(2)).authorize(principal, role);
    }

    @Test
    public void invalidatesAllPrincipals() throws Exception {
        cached.authorize(principal, role);
        cached.authorize(principal2, role);
        cached.invalidateAll();
        cached.authorize(principal, role);
        cached.authorize(principal2, role);
        Mockito.verify(underlying, Mockito.times(2)).authorize(principal, role);
        Mockito.verify(underlying, Mockito.times(2)).authorize(principal2, role);
    }

    @Test
    public void calculatesTheSizeOfTheCache() throws Exception {
        assertThat(cached.size()).isEqualTo(0);
        cached.authorize(principal, role);
        assertThat(cached.size()).isEqualTo(1);
        cached.invalidateAll();
        assertThat(cached.size()).isEqualTo(0);
    }

    @Test
    public void calculatesCacheStats() throws Exception {
        assertThat(cached.stats().loadCount()).isEqualTo(0);
        cached.authorize(principal, role);
        assertThat(cached.stats().loadCount()).isEqualTo(1);
        assertThat(cached.size()).isEqualTo(1);
    }

    @Test
    public void shouldPropagateRuntimeException() throws AuthenticationException {
        final RuntimeException e = new NullPointerException();
        Mockito.when(underlying.authorize(principal, role)).thenThrow(e);
        assertThatNullPointerException().isThrownBy(() -> cached.authorize(principal, role)).isSameAs(e);
    }
}

