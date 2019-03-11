package org.apereo.cas.support.oauth.web.views;


import OAuth20UserProfileViewRenderer.MODEL_ATTRIBUTE_ATTRIBUTES;
import OAuth20UserProfileViewRenderer.MODEL_ATTRIBUTE_ID;
import java.util.Map;
import lombok.val;
import org.apereo.cas.support.oauth.web.AbstractOAuth20Tests;
import org.apereo.cas.ticket.accesstoken.AccessToken;
import org.apereo.cas.util.CollectionUtils;
import org.hjson.JsonValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link OAuth20DefaultUserProfileViewRendererFlatTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
@TestPropertySource(properties = "cas.authn.oauth.userProfileViewType=FLAT")
public class OAuth20DefaultUserProfileViewRendererFlatTests extends AbstractOAuth20Tests {
    @Autowired
    @Qualifier("oauthUserProfileViewRenderer")
    private OAuth20UserProfileViewRenderer oauthUserProfileViewRenderer;

    @Test
    public void verifyNestedOption() {
        val map = CollectionUtils.wrap(MODEL_ATTRIBUTE_ID, "cas", MODEL_ATTRIBUTE_ATTRIBUTES, CollectionUtils.wrap("email", "cas@example.org", "name", "Test"), "something", CollectionUtils.wrapList("something"));
        val json = oauthUserProfileViewRenderer.render(((Map) (map)), Mockito.mock(AccessToken.class));
        val value = JsonValue.readJSON(json).asObject();
        Assertions.assertNotNull(value.get(MODEL_ATTRIBUTE_ID));
        Assertions.assertNotNull(value.get("email"));
        Assertions.assertNotNull(value.get("name"));
        Assertions.assertNull(value.get(MODEL_ATTRIBUTE_ATTRIBUTES));
    }
}

