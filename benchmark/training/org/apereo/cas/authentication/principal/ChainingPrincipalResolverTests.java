package org.apereo.cas.authentication.principal;


import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import lombok.val;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.handler.support.SimpleTestUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.resolvers.ChainingPrincipalResolver;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit test for {@link ChainingPrincipalResolver}.
 *
 * @author Marvin S. Addison
 * @since 4.0.0
 */
public class ChainingPrincipalResolverTests {
    private final PrincipalFactory principalFactory = PrincipalFactoryUtils.newPrincipalFactory();

    @Test
    public void examineSupports() {
        val credential = Mockito.mock(Credential.class);
        Mockito.when(credential.getId()).thenReturn("a");
        val resolver1 = Mockito.mock(PrincipalResolver.class);
        Mockito.when(resolver1.supports(ArgumentMatchers.eq(credential))).thenReturn(true);
        val resolver2 = Mockito.mock(PrincipalResolver.class);
        Mockito.when(resolver2.supports(ArgumentMatchers.eq(credential))).thenReturn(false);
        val resolver = new ChainingPrincipalResolver();
        resolver.setChain(Arrays.asList(resolver1, resolver2));
        Assertions.assertTrue(resolver.supports(credential));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void examineResolve() {
        val principalOut = principalFactory.createPrincipal("output");
        val credential = Mockito.mock(Credential.class);
        Mockito.when(credential.getId()).thenReturn("input");
        val resolver1 = Mockito.mock(PrincipalResolver.class);
        Mockito.when(resolver1.supports(ArgumentMatchers.eq(credential))).thenReturn(true);
        Mockito.when(resolver1.resolve(ArgumentMatchers.eq(credential), ArgumentMatchers.any(Optional.class), ArgumentMatchers.any(Optional.class))).thenReturn(principalOut);
        val resolver2 = Mockito.mock(PrincipalResolver.class);
        Mockito.when(resolver2.supports(ArgumentMatchers.any(Credential.class))).thenReturn(true);
        Mockito.when(resolver2.resolve(ArgumentMatchers.any(Credential.class), ArgumentMatchers.any(Optional.class), ArgumentMatchers.any(Optional.class))).thenReturn(principalFactory.createPrincipal("output", Collections.singletonMap("mail", "final@example.com")));
        val resolver = new ChainingPrincipalResolver();
        resolver.setChain(Arrays.asList(resolver1, resolver2));
        val principal = resolver.resolve(credential, Optional.of(principalOut), Optional.of(new SimpleTestUsernamePasswordAuthenticationHandler()));
        Assertions.assertEquals("output", principal.getId());
        val mail = CollectionUtils.firstElement(principal.getAttributes().get("mail"));
        Assertions.assertTrue(mail.isPresent());
        Assertions.assertEquals("final@example.com", mail.get());
    }
}

