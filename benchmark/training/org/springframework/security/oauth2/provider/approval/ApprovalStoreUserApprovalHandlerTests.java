package org.springframework.security.oauth2.provider.approval;


import Approval.ApprovalStatus;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.InMemoryClientDetailsService;


public class ApprovalStoreUserApprovalHandlerTests {
    private ApprovalStoreUserApprovalHandler handler = new ApprovalStoreUserApprovalHandler();

    private InMemoryApprovalStore store = new InMemoryApprovalStore();

    private InMemoryClientDetailsService clientDetailsService = new InMemoryClientDetailsService();

    private Authentication userAuthentication;

    @Test
    public void testApprovalLongExpiry() throws Exception {
        handler.setApprovalExpiryInSeconds((((365 * 24) * 60) * 60));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        authorizationRequest.setApprovalParameters(Collections.singletonMap("scope.read", "approved"));
        AuthorizationRequest result = handler.updateAfterApproval(authorizationRequest, userAuthentication);
        Assert.assertTrue(handler.isApproved(result, userAuthentication));
    }

    @Test
    public void testExplicitlyApprovedScopes() {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        authorizationRequest.setApprovalParameters(Collections.singletonMap("scope.read", "approved"));
        AuthorizationRequest result = handler.updateAfterApproval(authorizationRequest, userAuthentication);
        Assert.assertTrue(handler.isApproved(result, userAuthentication));
        Assert.assertEquals(1, store.getApprovals("user", "client").size());
        Assert.assertEquals(1, result.getScope().size());
        Assert.assertTrue(result.isApproved());
    }

    @Test
    public void testImplicitlyDeniedScope() {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read", "write"));
        authorizationRequest.setApprovalParameters(Collections.singletonMap("scope.read", "approved"));
        AuthorizationRequest result = handler.updateAfterApproval(authorizationRequest, userAuthentication);
        Assert.assertTrue(handler.isApproved(result, userAuthentication));
        Collection<Approval> approvals = store.getApprovals("user", "client");
        Assert.assertEquals(2, approvals.size());
        approvals.contains(new Approval("user", "client", "read", new Date(), ApprovalStatus.APPROVED));
        approvals.contains(new Approval("user", "client", "write", new Date(), ApprovalStatus.DENIED));
        Assert.assertEquals(1, result.getScope().size());
    }

    @Test
    public void testExplicitlyPreapprovedScopes() {
        store.addApprovals(Arrays.asList(new Approval("user", "client", "read", new Date(((System.currentTimeMillis()) + 10000)), ApprovalStatus.APPROVED)));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        AuthorizationRequest result = handler.checkForPreApproval(authorizationRequest, userAuthentication);
        Assert.assertTrue(result.isApproved());
    }

    @Test
    public void testExplicitlyUnapprovedScopes() {
        store.addApprovals(Arrays.asList(new Approval("user", "client", "read", new Date(((System.currentTimeMillis()) + 10000)), ApprovalStatus.DENIED)));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        AuthorizationRequest result = handler.checkForPreApproval(authorizationRequest, userAuthentication);
        Assert.assertFalse(result.isApproved());
    }

    @Test
    public void testAutoapprovedScopes() {
        handler.setClientDetailsService(clientDetailsService);
        BaseClientDetails client = new BaseClientDetails("client", null, "read", "authorization_code", null);
        client.setAutoApproveScopes(new HashSet<String>(Arrays.asList("read")));
        clientDetailsService.setClientDetailsStore(Collections.singletonMap("client", client));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        AuthorizationRequest result = handler.checkForPreApproval(authorizationRequest, userAuthentication);
        Assert.assertTrue(result.isApproved());
    }

    @Test
    public void testAutoapprovedWildcardScopes() {
        handler.setClientDetailsService(clientDetailsService);
        BaseClientDetails client = new BaseClientDetails("client", null, "read", "authorization_code", null);
        client.setAutoApproveScopes(new HashSet<String>(Arrays.asList(".*")));
        clientDetailsService.setClientDetailsStore(Collections.singletonMap("client", client));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        AuthorizationRequest result = handler.checkForPreApproval(authorizationRequest, userAuthentication);
        Assert.assertTrue(result.isApproved());
    }

    @Test
    public void testApprovalsAddedForAutoapprovedScopes() {
        handler.setClientDetailsService(clientDetailsService);
        BaseClientDetails client = new BaseClientDetails("client", null, "read", "authorization_code", null);
        client.setAutoApproveScopes(new HashSet<String>(Arrays.asList("read")));
        clientDetailsService.setClientDetailsStore(Collections.singletonMap("client", client));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        AuthorizationRequest result = handler.checkForPreApproval(authorizationRequest, userAuthentication);
        Collection<Approval> approvals = store.getApprovals(userAuthentication.getName(), "client");
        Assert.assertEquals(1, approvals.size());
        Approval approval = approvals.iterator().next();
        Assert.assertEquals("read", approval.getScope());
    }

    @Test
    public void testAutoapprovedAllScopes() {
        handler.setClientDetailsService(clientDetailsService);
        BaseClientDetails client = new BaseClientDetails("client", null, "read", "authorization_code", null);
        client.setAutoApproveScopes(new HashSet<String>(Arrays.asList("true")));
        clientDetailsService.setClientDetailsStore(Collections.singletonMap("client", client));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        AuthorizationRequest result = handler.checkForPreApproval(authorizationRequest, userAuthentication);
        Assert.assertTrue(result.isApproved());
    }

    @Test
    public void testExpiredPreapprovedScopes() {
        store.addApprovals(Arrays.asList(new Approval("user", "client", "read", new Date(((System.currentTimeMillis()) - 10000)), ApprovalStatus.APPROVED)));
        AuthorizationRequest authorizationRequest = new AuthorizationRequest("client", Arrays.asList("read"));
        AuthorizationRequest result = handler.checkForPreApproval(authorizationRequest, userAuthentication);
        Assert.assertFalse(result.isApproved());
    }
}

