package org.whispersystems.textsecuregcm.tests.controllers;


import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import junit.framework.TestCase;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.mappers.RateLimitExceededExceptionMapper;
import org.whispersystems.textsecuregcm.storage.Account;
import org.whispersystems.textsecuregcm.storage.AccountsManager;
import org.whispersystems.textsecuregcm.storage.PublicAccount;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.tests.util.JsonHelpers;
import org.whispersystems.textsecuregcm.util.SystemMapper;


public class TransparentDataControllerTest {
    private final AccountsManager accountsManager = Mockito.mock(AccountsManager.class);

    private final Map<String, String> indexMap = new HashMap<>();

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).addProvider(new RateLimitExceededExceptionMapper()).setMapper(SystemMapper.getMapper()).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addResource(new org.whispersystems.textsecuregcm.controllers.TransparentDataController(accountsManager, indexMap)).build();

    @Test
    public void testAccountOne() throws IOException {
        Response response = resources.getJerseyTest().target(String.format("/v1/transparency/account/%s", "1")).request().get();
        TestCase.assertEquals(200, response.getStatus());
        Account result = response.readEntity(PublicAccount.class);
        TestCase.assertTrue(result.getPin().isPresent());
        TestCase.assertEquals("******", result.getPin().get());
        TestCase.assertNull(result.getNumber());
        TestCase.assertEquals("OneProfileName", result.getProfileName());
        MatcherAssert.assertThat("Account serialization works", JsonHelpers.asJson(result), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/transparent_account.json"))));
        Mockito.verify(accountsManager, Mockito.times(1)).get(ArgumentMatchers.eq("+14151231111"));
        Mockito.verifyNoMoreInteractions(accountsManager);
    }

    @Test
    public void testAccountTwo() throws IOException {
        Response response = resources.getJerseyTest().target(String.format("/v1/transparency/account/%s", "2")).request().get();
        TestCase.assertEquals(200, response.getStatus());
        Account result = response.readEntity(PublicAccount.class);
        TestCase.assertTrue(result.getPin().isPresent());
        TestCase.assertEquals("******", result.getPin().get());
        TestCase.assertNull(result.getNumber());
        TestCase.assertEquals("TwoProfileName", result.getProfileName());
        MatcherAssert.assertThat("Account serialization works 2", JsonHelpers.asJson(result), CoreMatchers.is(CoreMatchers.equalTo(JsonHelpers.jsonFixture("fixtures/transparent_account2.json"))));
        Mockito.verify(accountsManager, Mockito.times(1)).get(ArgumentMatchers.eq("+14151232222"));
    }

    @Test
    public void testAccountMissing() {
        Response response = resources.getJerseyTest().target(String.format("/v1/transparency/account/%s", "3")).request().get();
        TestCase.assertEquals(404, response.getStatus());
        Mockito.verifyNoMoreInteractions(accountsManager);
    }
}

