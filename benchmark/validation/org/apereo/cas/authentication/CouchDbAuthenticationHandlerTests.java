package org.apereo.cas.authentication;


import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.config.CasCoreAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationHandlersConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationMetadataConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPolicyConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationPrincipalConfiguration;
import org.apereo.cas.config.CasCoreAuthenticationSupportConfiguration;
import org.apereo.cas.config.CasCoreHttpConfiguration;
import org.apereo.cas.config.CasCoreServicesAuthenticationConfiguration;
import org.apereo.cas.config.CasCoreServicesConfiguration;
import org.apereo.cas.config.CasCoreTicketCatalogConfiguration;
import org.apereo.cas.config.CasCoreTicketsConfiguration;
import org.apereo.cas.config.CasCoreUtilConfiguration;
import org.apereo.cas.config.CasCoreWebConfiguration;
import org.apereo.cas.config.CasCouchDbCoreConfiguration;
import org.apereo.cas.config.CasPersonDirectoryConfiguration;
import org.apereo.cas.config.CouchDbAuthenticationConfiguration;
import org.apereo.cas.config.support.CasWebApplicationServiceFactoryConfiguration;
import org.apereo.cas.couchdb.core.CouchDbConnectorFactory;
import org.apereo.cas.couchdb.core.ProfileCouchDbRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.pac4j.couch.profile.service.CouchProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link CouchDbAuthenticationHandlerTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Getter
@SpringBootTest(classes = { CasCouchDbCoreConfiguration.class, CouchDbAuthenticationConfiguration.class, CasCoreAuthenticationConfiguration.class, CasCoreServicesAuthenticationConfiguration.class, CasCoreUtilConfiguration.class, CasCoreAuthenticationPrincipalConfiguration.class, CasCoreAuthenticationPolicyConfiguration.class, CasCoreAuthenticationMetadataConfiguration.class, CasCoreAuthenticationSupportConfiguration.class, CasCoreAuthenticationHandlersConfiguration.class, CasCoreHttpConfiguration.class, CasCoreTicketCatalogConfiguration.class, CasCoreTicketsConfiguration.class, CasCoreServicesConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, CasPersonDirectoryConfiguration.class, CasCoreWebConfiguration.class, CasWebApplicationServiceFactoryConfiguration.class, RefreshAutoConfiguration.class })
@TestPropertySource(properties = { "cas.authn.couchDb.dbName=authentication", "cas.authn.couchDb.attributes=loc,state", "cas.authn.couchDb.usernameAttribute=username", "cas.authn.couchDb.passwordAttribute=password", "cas.authn.couchDb.username=cas", "cas.authn.couchdb.password=password", "cas.authn.pac4j.typedIdUsed=false" })
@Tag("CouchDb")
public class CouchDbAuthenticationHandlerTests {
    @Autowired
    @Qualifier("authenticationCouchDbFactory")
    private CouchDbConnectorFactory couchDbFactory;

    @Autowired
    @Qualifier("authenticationCouchDbRepository")
    private ProfileCouchDbRepository couchDbRepository;

    @Autowired
    @Qualifier("couchDbAuthenticationHandler")
    private AuthenticationHandler authenticationHandler;

    @Autowired
    @Qualifier("couchDbPrincipalFactory")
    private PrincipalFactory principalFactory;

    @Autowired
    @Qualifier("couchDbAuthenticatorProfileService")
    private CouchProfileService profileService;

    @Test
    @SneakyThrows
    public void verifyAuthentication() {
        val result = this.authenticationHandler.authenticate(CoreAuthenticationTestUtils.getCredentialsWithDifferentUsernameAndPassword("u1", "p1"));
        Assertions.assertEquals("u1", result.getPrincipal().getId());
        Assertions.assertEquals("Chicago", result.getPrincipal().getAttributes().get("loc"));
        Assertions.assertEquals("Illinois", result.getPrincipal().getAttributes().get("state"));
    }
}

