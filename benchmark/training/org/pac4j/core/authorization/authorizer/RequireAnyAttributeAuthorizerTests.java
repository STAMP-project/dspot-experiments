package org.pac4j.core.authorization.authorizer;


import com.google.common.collect.Lists;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.profile.CommonProfile;


/**
 * Tests {@link RequireAnyAttributeAuthorizer}.
 *
 * @author Misagh Moayyed
 * @since 1.9.2
 */
public final class RequireAnyAttributeAuthorizerTests {
    private final JEEContext context = new JEEContext(Mockito.mock(HttpServletRequest.class), Mockito.mock(HttpServletResponse.class));

    private List<CommonProfile> profiles;

    private CommonProfile profile;

    @Test
    public void testAttributeNotFound() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("");
        authorizer.setElements("name1");
        profile.addAttribute("name2", "anything-goes-here");
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testNoValueProvided() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("");
        authorizer.setElements("name1");
        profile.addAttribute("name1", "anything-goes-here");
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testPatternSingleValuedAttribute() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("^value.+");
        authorizer.setElements("name1");
        profile.addAttribute("name1", "valueAddedHere");
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testPatternFails() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("^v");
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2", "nothing"));
        Assert.assertFalse(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testMatchesPattern() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer("^v\\d");
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2", "nothing"));
        profile.addAttribute("name2", "v3");
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }

    @Test
    public void testMatchesEverythingByDefault() {
        final RequireAnyAttributeAuthorizer authorizer = new RequireAnyAttributeAuthorizer();
        authorizer.setElements("name1");
        profile.addAttribute("name1", Lists.newArrayList("v1", "v2"));
        profile.addAttribute("name2", "v3");
        Assert.assertTrue(authorizer.isAuthorized(context, profiles));
    }
}

