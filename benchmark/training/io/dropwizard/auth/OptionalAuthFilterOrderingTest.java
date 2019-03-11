package io.dropwizard.auth;


import Response.Status.FORBIDDEN;
import io.dropwizard.auth.principal.NullPrincipal;
import java.io.IOException;
import java.security.Principal;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;


public class OptionalAuthFilterOrderingTest extends JerseyTest {
    public static class BasicAuthResourceConfigWithAuthorizationFilter extends AbstractAuthResourceConfig {
        public BasicAuthResourceConfigWithAuthorizationFilter() {
            register(AuthResource.class);
            register(OptionalAuthFilterOrderingTest.DummyAuthorizationFilter.class);
        }

        @Override
        protected Class<? extends Principal> getPrincipalClass() {
            return Principal.class;
        }

        @Override
        protected ContainerRequestFilter getAuthFilter() {
            return new OptionalAuthFilterOrderingTest.DummyAuthenticationFilter();
        }

        @Override
        protected AbstractBinder getAuthBinder() {
            return new AuthValueFactoryProvider.Binder<>(getPrincipalClass());
        }

        @Override
        protected DynamicFeature getAuthDynamicFeature(ContainerRequestFilter authFilter) {
            return new AuthDynamicFeature(authFilter);
        }
    }

    @Test
    public void authenticationFilterShouldExecuteInAuthenticationPhaseForImplicitPermitall() {
        assertThat(target("/test/implicit-permitall").request().get(String.class)).isEqualTo("authorization ok");
    }

    @Test
    public void authenticationFilterShouldExecuteInAuthenticationPhaseForOptionalPrincipal() {
        assertThat(target("/test/optional").request().get(String.class)).isEqualTo("authorization ok");
    }

    @Priority(Priorities.AUTHENTICATION)
    private static class DummyAuthenticationFilter extends AuthFilter<Object, Principal> {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            requestContext.setSecurityContext(new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return new NullPrincipal();
                }

                @Override
                public boolean isUserInRole(String s) {
                    return false;
                }

                @Override
                public boolean isSecure() {
                    return true;
                }

                @Override
                public String getAuthenticationScheme() {
                    return "doesn't matter";
                }
            });
        }
    }

    @Priority(Priorities.AUTHORIZATION)
    private static class DummyAuthorizationFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext request) throws IOException {
            if ((request.getSecurityContext().getUserPrincipal()) != null) {
                request.abortWith(Response.ok("authorization ok").build());
            } else {
                request.abortWith(Response.status(FORBIDDEN).build());
            }
        }
    }
}

