package org.springframework.security.oauth2.provider.client;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;


public class JdbcClientDetailsServiceTests {
    private JdbcClientDetailsService service;

    private JdbcTemplate jdbcTemplate;

    private EmbeddedDatabase db;

    private static final String SELECT_SQL = "select client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity from oauth_client_details where client_id=?";

    private static final String INSERT_SQL = "insert into oauth_client_details (client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, autoapprove) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String CUSTOM_INSERT_SQL = "insert into ClientDetails (appId, appSecret, resourceIds, scope, grantTypes, redirectUrl, authorities) values (?, ?, ?, ?, ?, ?, ?)";

    @Test(expected = NoSuchClientException.class)
    public void testLoadingClientForNonExistingClientId() {
        service.loadClientByClientId("nonExistingClientId");
    }

    @Test
    public void testLoadingClientIdWithNoDetails() {
        jdbcTemplate.update(JdbcClientDetailsServiceTests.INSERT_SQL, "clientIdWithNoDetails", null, null, null, null, null, null, null, null, null);
        ClientDetails clientDetails = service.loadClientByClientId("clientIdWithNoDetails");
        Assert.assertEquals("clientIdWithNoDetails", clientDetails.getClientId());
        Assert.assertFalse(clientDetails.isSecretRequired());
        Assert.assertNull(clientDetails.getClientSecret());
        Assert.assertFalse(clientDetails.isScoped());
        Assert.assertEquals(0, clientDetails.getScope().size());
        Assert.assertEquals(2, clientDetails.getAuthorizedGrantTypes().size());
        Assert.assertNull(clientDetails.getRegisteredRedirectUri());
        Assert.assertEquals(0, clientDetails.getAuthorities().size());
        Assert.assertEquals(null, clientDetails.getAccessTokenValiditySeconds());
        Assert.assertEquals(null, clientDetails.getAccessTokenValiditySeconds());
    }

    @Test
    public void testLoadingClientIdWithAdditionalInformation() {
        jdbcTemplate.update(JdbcClientDetailsServiceTests.INSERT_SQL, "clientIdWithAddInfo", null, null, null, null, null, null, null, null, null);
        jdbcTemplate.update("update oauth_client_details set additional_information=? where client_id=?", "{\"foo\":\"bar\"}", "clientIdWithAddInfo");
        ClientDetails clientDetails = service.loadClientByClientId("clientIdWithAddInfo");
        Assert.assertEquals("clientIdWithAddInfo", clientDetails.getClientId());
        Assert.assertEquals(Collections.singletonMap("foo", "bar"), clientDetails.getAdditionalInformation());
    }

    @Test
    public void testLoadingClientIdWithSingleDetails() {
        jdbcTemplate.update(JdbcClientDetailsServiceTests.INSERT_SQL, "clientIdWithSingleDetails", "mySecret", "myResource", "myScope", "myAuthorizedGrantType", "myRedirectUri", "myAuthority", 100, 200, "true");
        ClientDetails clientDetails = service.loadClientByClientId("clientIdWithSingleDetails");
        Assert.assertEquals("clientIdWithSingleDetails", clientDetails.getClientId());
        Assert.assertTrue(clientDetails.isSecretRequired());
        Assert.assertEquals("mySecret", clientDetails.getClientSecret());
        Assert.assertTrue(clientDetails.isScoped());
        Assert.assertEquals(1, clientDetails.getScope().size());
        Assert.assertEquals("myScope", clientDetails.getScope().iterator().next());
        Assert.assertEquals(1, clientDetails.getResourceIds().size());
        Assert.assertEquals("myResource", clientDetails.getResourceIds().iterator().next());
        Assert.assertEquals(1, clientDetails.getAuthorizedGrantTypes().size());
        Assert.assertEquals("myAuthorizedGrantType", clientDetails.getAuthorizedGrantTypes().iterator().next());
        Assert.assertEquals("myRedirectUri", clientDetails.getRegisteredRedirectUri().iterator().next());
        Assert.assertEquals(1, clientDetails.getAuthorities().size());
        Assert.assertEquals("myAuthority", clientDetails.getAuthorities().iterator().next().getAuthority());
        Assert.assertEquals(new Integer(100), clientDetails.getAccessTokenValiditySeconds());
        Assert.assertEquals(new Integer(200), clientDetails.getRefreshTokenValiditySeconds());
    }

    @Test
    public void testLoadingClientIdWithSingleDetailsInCustomTable() {
        jdbcTemplate.update(JdbcClientDetailsServiceTests.CUSTOM_INSERT_SQL, "clientIdWithSingleDetails", "mySecret", "myResource", "myScope", "myAuthorizedGrantType", "myRedirectUri", "myAuthority");
        JdbcClientDetailsService customService = new JdbcClientDetailsService(db);
        customService.setSelectClientDetailsSql(("select appId, appSecret, resourceIds, scope, " + "grantTypes, redirectUrl, authorities, access_token_validity, refresh_token_validity, additionalInformation, autoApproveScopes from ClientDetails where appId = ?"));
        ClientDetails clientDetails = customService.loadClientByClientId("clientIdWithSingleDetails");
        Assert.assertEquals("clientIdWithSingleDetails", clientDetails.getClientId());
        Assert.assertTrue(clientDetails.isSecretRequired());
        Assert.assertEquals("mySecret", clientDetails.getClientSecret());
        Assert.assertTrue(clientDetails.isScoped());
        Assert.assertEquals(1, clientDetails.getScope().size());
        Assert.assertEquals("myScope", clientDetails.getScope().iterator().next());
        Assert.assertEquals(1, clientDetails.getResourceIds().size());
        Assert.assertEquals("myResource", clientDetails.getResourceIds().iterator().next());
        Assert.assertEquals(1, clientDetails.getAuthorizedGrantTypes().size());
        Assert.assertEquals("myAuthorizedGrantType", clientDetails.getAuthorizedGrantTypes().iterator().next());
        Assert.assertEquals("myRedirectUri", clientDetails.getRegisteredRedirectUri().iterator().next());
        Assert.assertEquals(1, clientDetails.getAuthorities().size());
        Assert.assertEquals("myAuthority", clientDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    public void testLoadingClientIdWithMultipleDetails() {
        jdbcTemplate.update(JdbcClientDetailsServiceTests.INSERT_SQL, "clientIdWithMultipleDetails", "mySecret", "myResource1,myResource2", "myScope1,myScope2", "myAuthorizedGrantType1,myAuthorizedGrantType2", "myRedirectUri1,myRedirectUri2", "myAuthority1,myAuthority2", 100, 200, "read,write");
        ClientDetails clientDetails = service.loadClientByClientId("clientIdWithMultipleDetails");
        Assert.assertEquals("clientIdWithMultipleDetails", clientDetails.getClientId());
        Assert.assertTrue(clientDetails.isSecretRequired());
        Assert.assertEquals("mySecret", clientDetails.getClientSecret());
        Assert.assertTrue(clientDetails.isScoped());
        Assert.assertEquals(2, clientDetails.getResourceIds().size());
        Iterator<String> resourceIds = clientDetails.getResourceIds().iterator();
        Assert.assertEquals("myResource1", resourceIds.next());
        Assert.assertEquals("myResource2", resourceIds.next());
        Assert.assertEquals(2, clientDetails.getScope().size());
        Iterator<String> scope = clientDetails.getScope().iterator();
        Assert.assertEquals("myScope1", scope.next());
        Assert.assertEquals("myScope2", scope.next());
        Assert.assertEquals(2, clientDetails.getAuthorizedGrantTypes().size());
        Iterator<String> grantTypes = clientDetails.getAuthorizedGrantTypes().iterator();
        Assert.assertEquals("myAuthorizedGrantType1", grantTypes.next());
        Assert.assertEquals("myAuthorizedGrantType2", grantTypes.next());
        Assert.assertEquals(2, clientDetails.getRegisteredRedirectUri().size());
        Iterator<String> redirectUris = clientDetails.getRegisteredRedirectUri().iterator();
        Assert.assertEquals("myRedirectUri1", redirectUris.next());
        Assert.assertEquals("myRedirectUri2", redirectUris.next());
        Assert.assertEquals(2, clientDetails.getAuthorities().size());
        Iterator<GrantedAuthority> authorities = clientDetails.getAuthorities().iterator();
        Assert.assertEquals("myAuthority1", authorities.next().getAuthority());
        Assert.assertEquals("myAuthority2", authorities.next().getAuthority());
        Assert.assertEquals(new Integer(100), clientDetails.getAccessTokenValiditySeconds());
        Assert.assertEquals(new Integer(200), clientDetails.getRefreshTokenValiditySeconds());
        Assert.assertTrue(clientDetails.isAutoApprove("read"));
    }

    @Test
    public void testAddClientWithNoDetails() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("addedClientIdWithNoDetails");
        service.addClientDetails(clientDetails);
        Map<String, Object> map = jdbcTemplate.queryForMap(JdbcClientDetailsServiceTests.SELECT_SQL, "addedClientIdWithNoDetails");
        Assert.assertEquals("addedClientIdWithNoDetails", map.get("client_id"));
        Assert.assertTrue(map.containsKey("client_secret"));
        Assert.assertEquals(null, map.get("client_secret"));
    }

    @Test(expected = ClientAlreadyExistsException.class)
    public void testInsertDuplicateClient() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("duplicateClientIdWithNoDetails");
        service.addClientDetails(clientDetails);
        service.addClientDetails(clientDetails);
    }

    @Test
    public void testUpdateClientSecret() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("newClientIdWithNoDetails");
        service.setPasswordEncoder(new PasswordEncoder() {
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return true;
            }

            public String encode(CharSequence rawPassword) {
                return "BAR";
            }
        });
        service.addClientDetails(clientDetails);
        service.updateClientSecret(clientDetails.getClientId(), "foo");
        Map<String, Object> map = jdbcTemplate.queryForMap(JdbcClientDetailsServiceTests.SELECT_SQL, "newClientIdWithNoDetails");
        Assert.assertEquals("newClientIdWithNoDetails", map.get("client_id"));
        Assert.assertTrue(map.containsKey("client_secret"));
        Assert.assertEquals("BAR", map.get("client_secret"));
    }

    @Test
    public void testUpdateClientRedirectURI() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("newClientIdWithNoDetails");
        service.addClientDetails(clientDetails);
        String[] redirectURI = new String[]{ "http://localhost:8080", "http://localhost:9090" };
        clientDetails.setRegisteredRedirectUri(new HashSet<String>(Arrays.asList(redirectURI)));
        service.updateClientDetails(clientDetails);
        Map<String, Object> map = jdbcTemplate.queryForMap(JdbcClientDetailsServiceTests.SELECT_SQL, "newClientIdWithNoDetails");
        Assert.assertEquals("newClientIdWithNoDetails", map.get("client_id"));
        Assert.assertTrue(map.containsKey("web_server_redirect_uri"));
        Assert.assertEquals("http://localhost:8080,http://localhost:9090", map.get("web_server_redirect_uri"));
    }

    @Test(expected = NoSuchClientException.class)
    public void testUpdateNonExistentClient() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("nosuchClientIdWithNoDetails");
        service.updateClientDetails(clientDetails);
    }

    @Test
    public void testRemoveClient() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("deletedClientIdWithNoDetails");
        service.addClientDetails(clientDetails);
        service.removeClientDetails(clientDetails.getClientId());
        int count = jdbcTemplate.queryForObject("select count(*) from oauth_client_details where client_id=?", Integer.class, "deletedClientIdWithNoDetails");
        Assert.assertEquals(0, count);
    }

    @Test(expected = NoSuchClientException.class)
    public void testRemoveNonExistentClient() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("nosuchClientIdWithNoDetails");
        service.removeClientDetails(clientDetails.getClientId());
    }

    @Test
    public void testFindClients() {
        BaseClientDetails clientDetails = new BaseClientDetails();
        clientDetails.setClientId("aclient");
        service.addClientDetails(clientDetails);
        int count = service.listClientDetails().size();
        Assert.assertEquals(1, count);
    }
}

