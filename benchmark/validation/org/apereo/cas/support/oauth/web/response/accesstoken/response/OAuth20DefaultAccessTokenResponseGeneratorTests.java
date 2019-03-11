package org.apereo.cas.support.oauth.web.response.accesstoken.response;


import OAuth20Constants.ACCESS_TOKEN;
import OAuth20Constants.EXPIRES_IN;
import OAuth20Constants.SCOPE;
import OAuth20Constants.TOKEN_TYPE;
import RegisteredServiceProperty.RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_ENCRYPTION_KEY;
import RegisteredServiceProperty.RegisteredServiceProperties.ACCESS_TOKEN_AS_JWT_SIGNING_KEY;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.LinkedHashSet;
import lombok.val;
import org.apereo.cas.services.DefaultRegisteredServiceProperty;
import org.apereo.cas.support.oauth.web.AbstractOAuth20Tests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * This is {@link OAuth20DefaultAccessTokenResponseGeneratorTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class OAuth20DefaultAccessTokenResponseGeneratorTests extends AbstractOAuth20Tests {
    @Test
    public void verifyAccessTokenAsDefault() throws Exception {
        val registeredService = getRegisteredService("example", "secret", new LinkedHashSet<>());
        registeredService.setJwtAccessToken(false);
        servicesManager.save(registeredService);
        val mv = getModelAndView(registeredService);
        Assertions.assertTrue(mv.getModel().containsKey(ACCESS_TOKEN));
        Assertions.assertTrue(mv.getModel().containsKey(EXPIRES_IN));
        Assertions.assertTrue(mv.getModel().containsKey(SCOPE));
        Assertions.assertTrue(mv.getModel().containsKey(TOKEN_TYPE));
        Assertions.assertThrows(ParseException.class, () -> {
            val at = mv.getModel().get(ACCESS_TOKEN).toString();
            JWTParser.parse(at);
        });
    }

    @Test
    public void verifyAccessTokenAsJwt() throws Exception {
        val registeredService = getRegisteredService("example", "secret", new LinkedHashSet<>());
        registeredService.setJwtAccessToken(true);
        servicesManager.save(registeredService);
        val mv = getModelAndView(registeredService);
        Assertions.assertTrue(mv.getModel().containsKey(ACCESS_TOKEN));
        val at = mv.getModel().get(ACCESS_TOKEN).toString();
        val jwt = JWTParser.parse(at);
        Assertions.assertNotNull(jwt);
    }

    @Test
    public void verifyAccessTokenAsJwtPerService() throws Exception {
        val registeredService = getRegisteredService("example", "secret", new LinkedHashSet<>());
        registeredService.setJwtAccessToken(true);
        val signingKey = new DefaultRegisteredServiceProperty();
        signingKey.addValue("pR3Vizkn5FSY5xCg84cIS4m-b6jomamZD68C8ash-TlNmgGPcoLgbgquxHPoi24tRmGpqHgM4mEykctcQzZ-Xg");
        registeredService.getProperties().put(ACCESS_TOKEN_AS_JWT_SIGNING_KEY.getPropertyName(), signingKey);
        val encKey = new DefaultRegisteredServiceProperty();
        encKey.addValue("0KVXaN-nlXafRUwgsr3H_l6hkufY7lzoTy7OVI5pN0E");
        registeredService.getProperties().put(ACCESS_TOKEN_AS_JWT_ENCRYPTION_KEY.getPropertyName(), encKey);
        servicesManager.save(registeredService);
        val mv = getModelAndView(registeredService);
        Assertions.assertTrue(mv.getModel().containsKey(ACCESS_TOKEN));
        val at = mv.getModel().get(ACCESS_TOKEN).toString();
        val jwt = JWTParser.parse(at);
        Assertions.assertNotNull(jwt);
    }
}

