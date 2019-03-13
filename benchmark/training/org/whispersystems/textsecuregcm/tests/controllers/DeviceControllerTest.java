/**
 * Copyright (C) 2014 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.tests.controllers;


import MediaType.APPLICATION_JSON_TYPE;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.testing.junit.ResourceTestRule;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Path;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.whispersystems.textsecuregcm.controllers.DeviceController;
import org.whispersystems.textsecuregcm.entities.AccountAttributes;
import org.whispersystems.textsecuregcm.entities.DeviceResponse;
import org.whispersystems.textsecuregcm.limits.RateLimiter;
import org.whispersystems.textsecuregcm.limits.RateLimiters;
import org.whispersystems.textsecuregcm.mappers.DeviceLimitExceededExceptionMapper;
import org.whispersystems.textsecuregcm.sqs.DirectoryQueue;
import org.whispersystems.textsecuregcm.tests.util.AuthHelper;
import org.whispersystems.textsecuregcm.util.VerificationCode;


public class DeviceControllerTest {
    @Path("/v1/devices")
    static class DumbVerificationDeviceController extends DeviceController {
        public DumbVerificationDeviceController(PendingDevicesManager pendingDevices, AccountsManager accounts, MessagesManager messages, DirectoryQueue cdsSender, RateLimiters rateLimiters, Map<String, Integer> deviceConfiguration) {
            super(pendingDevices, accounts, messages, cdsSender, rateLimiters, deviceConfiguration);
        }

        @Override
        protected VerificationCode generateVerificationCode() {
            return new VerificationCode(5678901);
        }
    }

    private PendingDevicesManager pendingDevicesManager = Mockito.mock(PendingDevicesManager.class);

    private AccountsManager accountsManager = Mockito.mock(AccountsManager.class);

    private MessagesManager messagesManager = Mockito.mock(MessagesManager.class);

    private DirectoryQueue directoryQueue = Mockito.mock(DirectoryQueue.class);

    private RateLimiters rateLimiters = Mockito.mock(RateLimiters.class);

    private RateLimiter rateLimiter = Mockito.mock(RateLimiter.class);

    private Account account = Mockito.mock(Account.class);

    private Account maxedAccount = Mockito.mock(Account.class);

    private Device masterDevice = Mockito.mock(Device.class);

    private Map<String, Integer> deviceConfiguration = new HashMap<String, Integer>() {
        {
        }
    };

    @Rule
    public final ResourceTestRule resources = ResourceTestRule.builder().addProvider(AuthHelper.getAuthFilter()).addProvider(new AuthValueFactoryProvider.Binder<>(.class)).setTestContainerFactory(new GrizzlyWebTestContainerFactory()).addProvider(new DeviceLimitExceededExceptionMapper()).addResource(new DeviceControllerTest.DumbVerificationDeviceController(pendingDevicesManager, accountsManager, messagesManager, directoryQueue, rateLimiters, deviceConfiguration)).build();

    @Test
    public void validDeviceRegisterTest() throws Exception {
        VerificationCode deviceCode = resources.getJerseyTest().target("/v1/devices/provisioning/code").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(VerificationCode.class);
        assertThat(deviceCode).isEqualTo(new VerificationCode(5678901));
        DeviceResponse response = resources.getJerseyTest().target("/v1/devices/5678901").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, "password1")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 1234, null), APPLICATION_JSON_TYPE), DeviceResponse.class);
        assertThat(response.getDeviceId()).isEqualTo(42L);
        Mockito.verify(pendingDevicesManager).remove(AuthHelper.VALID_NUMBER);
        Mockito.verify(messagesManager).clear(ArgumentMatchers.eq(AuthHelper.VALID_NUMBER), ArgumentMatchers.eq(42L));
    }

    @Test
    public void invalidDeviceRegisterTest() throws Exception {
        VerificationCode deviceCode = resources.getJerseyTest().target("/v1/devices/provisioning/code").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, AuthHelper.VALID_PASSWORD)).get(VerificationCode.class);
        assertThat(deviceCode).isEqualTo(new VerificationCode(5678901));
        Response response = resources.getJerseyTest().target("/v1/devices/5678902").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, "password1")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 1234, null), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(403);
        Mockito.verifyNoMoreInteractions(messagesManager);
    }

    @Test
    public void oldDeviceRegisterTest() throws Exception {
        Response response = resources.getJerseyTest().target("/v1/devices/1112223").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER_TWO, AuthHelper.VALID_PASSWORD_TWO)).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 1234, null), APPLICATION_JSON_TYPE));
        assertThat(response.getStatus()).isEqualTo(403);
        Mockito.verifyNoMoreInteractions(messagesManager);
    }

    @Test
    public void maxDevicesTest() throws Exception {
        Response response = resources.getJerseyTest().target("/v1/devices/provisioning/code").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER_TWO, AuthHelper.VALID_PASSWORD_TWO)).get();
        Assert.assertEquals(411, response.getStatus());
        Mockito.verifyNoMoreInteractions(messagesManager);
    }

    @Test
    public void longNameTest() throws Exception {
        Response response = resources.getJerseyTest().target("/v1/devices/5678901").request().header("Authorization", AuthHelper.getAuthHeader(AuthHelper.VALID_NUMBER, "password1")).put(Entity.entity(new AccountAttributes("keykeykeykey", false, 1234, "this is a really long name that is longer than 80 characters it's so long that it's even longer than 204 characters. that's a lot of characters. we're talking lots and lots and lots of characters. 12345678", true, true, null), APPLICATION_JSON_TYPE));
        Assert.assertEquals(response.getStatus(), 422);
        Mockito.verifyNoMoreInteractions(messagesManager);
    }
}

