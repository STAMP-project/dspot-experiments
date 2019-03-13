package org.pac4j.core.authorization.authorizer;


import HTTP_METHOD.DELETE;
import HTTP_METHOD.GET;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.profile.UserProfile;

import static HTTP_METHOD.GET;
import static HTTP_METHOD.POST;
import static HTTP_METHOD.PUT;


/**
 * Tests {@link CheckHttpMethodAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class CheckHttpMethodAuthorizerTests {
    private List<UserProfile> profiles;

    @Test
    public void testGoodHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(GET, POST);
        Assert.assertTrue(authorizer.isAuthorized(MockWebContext.create().setRequestMethod(GET.name()), profiles));
    }

    @Test
    public void testBadHttpMethod() {
        final CheckHttpMethodAuthorizer authorizer = new CheckHttpMethodAuthorizer(PUT);
        Assert.assertFalse(authorizer.isAuthorized(MockWebContext.create().setRequestMethod(DELETE.name()), profiles));
    }
}

