package com.ctrip.framework.apollo.openapi.service;


import Env.DEV;
import com.ctrip.framework.apollo.openapi.entity.Consumer;
import com.ctrip.framework.apollo.openapi.entity.ConsumerRole;
import com.ctrip.framework.apollo.openapi.entity.ConsumerToken;
import com.ctrip.framework.apollo.openapi.repository.ConsumerRepository;
import com.ctrip.framework.apollo.openapi.repository.ConsumerRoleRepository;
import com.ctrip.framework.apollo.openapi.repository.ConsumerTokenRepository;
import com.ctrip.framework.apollo.portal.AbstractUnitTest;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.entity.bo.UserInfo;
import com.ctrip.framework.apollo.portal.entity.po.Role;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.spi.UserInfoHolder;
import com.ctrip.framework.apollo.portal.spi.UserService;
import com.ctrip.framework.apollo.portal.util.RoleUtils;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;


public class ConsumerServiceTest extends AbstractUnitTest {
    @Mock
    private ConsumerTokenRepository consumerTokenRepository;

    @Mock
    private ConsumerRepository consumerRepository;

    @Mock
    private PortalConfig portalConfig;

    @Mock
    private UserService userService;

    @Mock
    private UserInfoHolder userInfoHolder;

    @Mock
    private ConsumerRoleRepository consumerRoleRepository;

    @Mock
    private RolePermissionService rolePermissionService;

    @Spy
    @InjectMocks
    private ConsumerService consumerService;

    private String someTokenSalt = "someTokenSalt";

    private String testAppId = "testAppId";

    private String testConsumerName = "testConsumerName";

    private String testOwner = "testOwner";

    @Test
    public void testGetConsumerId() throws Exception {
        String someToken = "someToken";
        long someConsumerId = 1;
        ConsumerToken someConsumerToken = new ConsumerToken();
        someConsumerToken.setConsumerId(someConsumerId);
        Mockito.when(consumerTokenRepository.findTopByTokenAndExpiresAfter(ArgumentMatchers.eq(someToken), ArgumentMatchers.any(Date.class))).thenReturn(someConsumerToken);
        Assert.assertEquals(someConsumerId, consumerService.getConsumerIdByToken(someToken).longValue());
    }

    @Test
    public void testGetConsumerIdWithNullToken() throws Exception {
        Long consumerId = consumerService.getConsumerIdByToken(null);
        Assert.assertNull(consumerId);
        Mockito.verify(consumerTokenRepository, Mockito.never()).findTopByTokenAndExpiresAfter(ArgumentMatchers.anyString(), ArgumentMatchers.any(Date.class));
    }

    @Test
    public void testGetConsumerByConsumerId() throws Exception {
        long someConsumerId = 1;
        Consumer someConsumer = Mockito.mock(Consumer.class);
        Mockito.when(consumerRepository.findById(someConsumerId)).thenReturn(Optional.of(someConsumer));
        Assert.assertEquals(someConsumer, consumerService.getConsumerByConsumerId(someConsumerId));
        Mockito.verify(consumerRepository, Mockito.times(1)).findById(someConsumerId);
    }

    @Test
    public void testCreateConsumerToken() throws Exception {
        ConsumerToken someConsumerToken = Mockito.mock(ConsumerToken.class);
        ConsumerToken savedConsumerToken = Mockito.mock(ConsumerToken.class);
        Mockito.when(consumerTokenRepository.save(someConsumerToken)).thenReturn(savedConsumerToken);
        Assert.assertEquals(savedConsumerToken, consumerService.createConsumerToken(someConsumerToken));
    }

    @Test
    public void testGenerateConsumerToken() throws Exception {
        String someConsumerAppId = "100003171";
        Date generationTime = new GregorianCalendar(2016, Calendar.AUGUST, 9, 12, 10, 50).getTime();
        String tokenSalt = "apollo";
        Assert.assertEquals("d0da35292dd5079eeb73cc3a5f7c0759afabd806", consumerService.generateToken(someConsumerAppId, generationTime, tokenSalt));
    }

    @Test
    public void testGenerateAndEnrichConsumerToken() throws Exception {
        String someConsumerAppId = "someAppId";
        long someConsumerId = 1;
        String someToken = "someToken";
        Date generationTime = new Date();
        Consumer consumer = Mockito.mock(Consumer.class);
        Mockito.when(consumerRepository.findById(someConsumerId)).thenReturn(Optional.of(consumer));
        Mockito.when(consumer.getAppId()).thenReturn(someConsumerAppId);
        Mockito.when(consumerService.generateToken(someConsumerAppId, generationTime, someTokenSalt)).thenReturn(someToken);
        ConsumerToken consumerToken = new ConsumerToken();
        consumerToken.setConsumerId(someConsumerId);
        consumerToken.setDataChangeCreatedTime(generationTime);
        consumerService.generateAndEnrichToken(consumer, consumerToken);
        Assert.assertEquals(someToken, consumerToken.getToken());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateAndEnrichConsumerTokenWithConsumerNotFound() throws Exception {
        long someConsumerIdNotExist = 1;
        ConsumerToken consumerToken = new ConsumerToken();
        consumerToken.setConsumerId(someConsumerIdNotExist);
        consumerService.generateAndEnrichToken(null, consumerToken);
    }

    @Test
    public void testCreateConsumer() {
        Consumer consumer = createConsumer(testConsumerName, testAppId, testOwner);
        UserInfo owner = createUser(testOwner);
        Mockito.when(consumerRepository.findByAppId(testAppId)).thenReturn(null);
        Mockito.when(userService.findByUserId(testOwner)).thenReturn(owner);
        Mockito.when(userInfoHolder.getUser()).thenReturn(owner);
        consumerService.createConsumer(consumer);
        Mockito.verify(consumerRepository).save(consumer);
    }

    @Test
    public void testAssignNamespaceRoleToConsumer() {
        Long consumerId = 1L;
        String token = "token";
        Mockito.doReturn(consumerId).when(consumerService).getConsumerIdByToken(token);
        String testNamespace = "namespace";
        String modifyRoleName = RoleUtils.buildModifyNamespaceRoleName(testAppId, testNamespace);
        String releaseRoleName = RoleUtils.buildReleaseNamespaceRoleName(testAppId, testNamespace);
        String envModifyRoleName = RoleUtils.buildModifyNamespaceRoleName(testAppId, testNamespace, DEV.toString());
        String envReleaseRoleName = RoleUtils.buildReleaseNamespaceRoleName(testAppId, testNamespace, DEV.toString());
        long modifyRoleId = 1;
        long releaseRoleId = 2;
        long envModifyRoleId = 3;
        long envReleaseRoleId = 4;
        Role modifyRole = createRole(modifyRoleId, modifyRoleName);
        Role releaseRole = createRole(releaseRoleId, releaseRoleName);
        Role envModifyRole = createRole(envModifyRoleId, modifyRoleName);
        Role envReleaseRole = createRole(envReleaseRoleId, releaseRoleName);
        Mockito.when(rolePermissionService.findRoleByRoleName(modifyRoleName)).thenReturn(modifyRole);
        Mockito.when(rolePermissionService.findRoleByRoleName(releaseRoleName)).thenReturn(releaseRole);
        Mockito.when(rolePermissionService.findRoleByRoleName(envModifyRoleName)).thenReturn(envModifyRole);
        Mockito.when(rolePermissionService.findRoleByRoleName(envReleaseRoleName)).thenReturn(envReleaseRole);
        Mockito.when(consumerRoleRepository.findByConsumerIdAndRoleId(consumerId, modifyRoleId)).thenReturn(null);
        UserInfo owner = createUser(testOwner);
        Mockito.when(userInfoHolder.getUser()).thenReturn(owner);
        ConsumerRole namespaceModifyConsumerRole = createConsumerRole(consumerId, modifyRoleId);
        ConsumerRole namespaceEnvModifyConsumerRole = createConsumerRole(consumerId, envModifyRoleId);
        ConsumerRole namespaceReleaseConsumerRole = createConsumerRole(consumerId, releaseRoleId);
        ConsumerRole namespaceEnvReleaseConsumerRole = createConsumerRole(consumerId, envReleaseRoleId);
        Mockito.doReturn(namespaceModifyConsumerRole).when(consumerService).createConsumerRole(consumerId, modifyRoleId, testOwner);
        Mockito.doReturn(namespaceEnvModifyConsumerRole).when(consumerService).createConsumerRole(consumerId, envModifyRoleId, testOwner);
        Mockito.doReturn(namespaceReleaseConsumerRole).when(consumerService).createConsumerRole(consumerId, releaseRoleId, testOwner);
        Mockito.doReturn(namespaceEnvReleaseConsumerRole).when(consumerService).createConsumerRole(consumerId, envReleaseRoleId, testOwner);
        consumerService.assignNamespaceRoleToConsumer(token, testAppId, testNamespace);
        consumerService.assignNamespaceRoleToConsumer(token, testAppId, testNamespace, DEV.toString());
        Mockito.verify(consumerRoleRepository).save(namespaceModifyConsumerRole);
        Mockito.verify(consumerRoleRepository).save(namespaceEnvModifyConsumerRole);
        Mockito.verify(consumerRoleRepository).save(namespaceReleaseConsumerRole);
        Mockito.verify(consumerRoleRepository).save(namespaceEnvReleaseConsumerRole);
    }
}

