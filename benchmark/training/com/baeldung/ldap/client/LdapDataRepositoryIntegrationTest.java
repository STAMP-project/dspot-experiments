package com.baeldung.ldap.client;


import com.baeldung.ldap.data.service.UserService;
import com.baeldung.ldap.javaconfig.TestConfig;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("testlive")
@ContextConfiguration(classes = { TestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class LdapDataRepositoryIntegrationTest {
    private static final String USER2 = "TEST02";

    private static final String USER3 = "TEST03";

    private static final String USER4 = "TEST04";

    private static final String USER2_PWD = "TEST02";

    private static final String USER3_PWD = "TEST03";

    private static final String USER4_PWD = "TEST04";

    private static final String SEARCH_STRING = "TEST*";

    @Autowired
    private UserService userService;

    @Test
    public void givenLdapClient_whenCorrectCredentials_thenSuccessfulLogin() {
        Boolean isValid = userService.authenticate(LdapDataRepositoryIntegrationTest.USER3, LdapDataRepositoryIntegrationTest.USER3_PWD);
        Assert.assertEquals(true, isValid);
    }

    @Test
    public void givenLdapClient_whenIncorrectCredentials_thenFailedLogin() {
        Boolean isValid = userService.authenticate(LdapDataRepositoryIntegrationTest.USER3, LdapDataRepositoryIntegrationTest.USER2_PWD);
        Assert.assertEquals(false, isValid);
    }

    @Test
    public void givenLdapClient_whenCorrectSearchFilter_thenEntriesReturned() {
        List<String> userList = userService.search(LdapDataRepositoryIntegrationTest.SEARCH_STRING);
        Assert.assertThat(userList, Matchers.containsInAnyOrder(LdapDataRepositoryIntegrationTest.USER2, LdapDataRepositoryIntegrationTest.USER3));
    }

    @Test
    public void givenLdapClientNotExists_whenDataProvided_thenNewUserCreated() {
        userService.create(LdapDataRepositoryIntegrationTest.USER4, LdapDataRepositoryIntegrationTest.USER4_PWD);
        userService.authenticate(LdapDataRepositoryIntegrationTest.USER4, LdapDataRepositoryIntegrationTest.USER4_PWD);
    }

    @Test
    public void givenLdapClientExists_whenDataProvided_thenExistingUserModified() {
        userService.modify(LdapDataRepositoryIntegrationTest.USER2, LdapDataRepositoryIntegrationTest.USER3_PWD);
        userService.authenticate(LdapDataRepositoryIntegrationTest.USER2, LdapDataRepositoryIntegrationTest.USER3_PWD);
    }
}

