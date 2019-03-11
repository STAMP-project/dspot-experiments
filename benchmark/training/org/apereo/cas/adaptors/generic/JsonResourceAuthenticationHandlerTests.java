package org.apereo.cas.adaptors.generic;


import CasUserAccount.AccountStatus.DISABLED;
import CasUserAccount.AccountStatus.EXPIRED;
import CasUserAccount.AccountStatus.LOCKED;
import CasUserAccount.AccountStatus.MUST_CHANGE_PASSWORD;
import CasUserAccount.AccountStatus.OK;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.principal.DefaultPrincipalFactory;
import org.apereo.cas.authentication.support.password.PasswordPolicyConfiguration;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;


/**
 * This is {@link JsonResourceAuthenticationHandlerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class JsonResourceAuthenticationHandlerTests {
    private final JsonResourceAuthenticationHandler handler;

    @SneakyThrows
    public JsonResourceAuthenticationHandlerTests() {
        val accounts = new LinkedHashMap<String, CasUserAccount>();
        var acct = new CasUserAccount();
        acct.setPassword("Mellon");
        acct.setExpirationDate(LocalDate.now(ZoneOffset.UTC).plusWeeks(2));
        acct.setAttributes(CollectionUtils.wrap("firstName", "Apereo", "lastName", "CAS"));
        accounts.put("casexpiring", acct);
        acct = new CasUserAccount();
        acct.setPassword("Mellon");
        acct.setStatus(OK);
        acct.setAttributes(CollectionUtils.wrap("firstName", "Apereo", "lastName", "CAS"));
        accounts.put("casuser", acct);
        acct = new CasUserAccount();
        acct.setPassword("Mellon");
        acct.setStatus(DISABLED);
        acct.setAttributes(CollectionUtils.wrap("firstName", "Apereo", "lastName", "CAS"));
        accounts.put("casdisabled", acct);
        acct = new CasUserAccount();
        acct.setPassword("Mellon");
        acct.setStatus(MUST_CHANGE_PASSWORD);
        acct.setAttributes(CollectionUtils.wrap("firstName", "Apereo", "lastName", "CAS"));
        accounts.put("casmustchange", acct);
        acct = new CasUserAccount();
        acct.setPassword("Mellon");
        acct.setStatus(LOCKED);
        acct.setAttributes(CollectionUtils.wrap("firstName", "Apereo", "lastName", "CAS"));
        accounts.put("caslocked", acct);
        acct = new CasUserAccount();
        acct.setPassword("Mellon");
        acct.setStatus(EXPIRED);
        acct.setAttributes(CollectionUtils.wrap("firstName", "Apereo", "lastName", "CAS"));
        accounts.put("casexpired", acct);
        val resource = new FileSystemResource(File.createTempFile("account", ".json"));
        val mapper = Jackson2ObjectMapperBuilder.json().featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE).featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).build();
        mapper.findAndRegisterModules().setSerializationInclusion(NON_EMPTY).enableDefaultTyping(NON_FINAL, PROPERTY).writerWithDefaultPrettyPrinter().writeValue(resource.getFile(), accounts);
        this.handler = new JsonResourceAuthenticationHandler(null, Mockito.mock(ServicesManager.class), new DefaultPrincipalFactory(), null, resource);
        this.handler.setPasswordPolicyConfiguration(new PasswordPolicyConfiguration(15));
    }

    @Test
    @SneakyThrows
    public void verifyExpiringAccount() {
        val result = handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casexpiring", "Mellon"));
        Assertions.assertFalse(result.getWarnings().isEmpty());
    }

    @Test
    @SneakyThrows
    public void verifyOkAccount() {
        Assertions.assertNotNull(handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casuser", "Mellon")));
    }

    @Test
    public void verifyNotFoundAccount() {
        Assertions.assertThrows(AccountNotFoundException.class, () -> handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("nobody", "Mellon")));
    }

    @Test
    public void verifyExpiredAccount() {
        Assertions.assertThrows(AccountExpiredException.class, () -> handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casexpired", "Mellon")));
    }

    @Test
    public void verifyDisabledAccount() {
        Assertions.assertThrows(AccountDisabledException.class, () -> handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casdisabled", "Mellon")));
    }

    @Test
    public void verifyLockedAccount() {
        Assertions.assertThrows(AccountLockedException.class, () -> handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("caslocked", "Mellon")));
    }

    @Test
    public void verifyMustChangePswAccount() {
        Assertions.assertThrows(AccountPasswordMustChangeException.class, () -> handler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("casmustchange", "Mellon")));
    }
}

