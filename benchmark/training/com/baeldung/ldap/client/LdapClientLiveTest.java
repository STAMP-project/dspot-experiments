package com.baeldung.ldap.client;


import com.baeldung.ldap.javaconfig.TestConfig;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("testlive")
@ContextConfiguration(classes = { TestConfig.class }, loader = AnnotationConfigContextLoader.class)
public class LdapClientLiveTest {
    private static final String USER2 = "TEST02";

    private static final String USER3 = "TEST03";

    private static final String USER4 = "TEST04";

    private static final String USER2_PWD = "TEST02";

    private static final String USER3_PWD = "TEST03";

    private static final String USER4_PWD = "TEST04";

    private static final String SEARCH_STRING = "TEST*";

    @Autowired
    private LdapClient ldapClient;

    @Test
    public void givenLdapClient_whenCorrectCredentials_thenSuccessfulLogin() {
        ldapClient.authenticate(LdapClientLiveTest.USER3, LdapClientLiveTest.USER3_PWD);
    }

    @Test(expected = AuthenticationException.class)
    public void givenLdapClient_whenIncorrectCredentials_thenFailedLogin() {
        ldapClient.authenticate(LdapClientLiveTest.USER3, LdapClientLiveTest.USER2_PWD);
    }

    @Test
    public void givenLdapClient_whenCorrectSearchFilter_thenEntriesReturned() {
        List<String> users = ldapClient.search(LdapClientLiveTest.SEARCH_STRING);
        Assert.assertThat(users, Matchers.containsInAnyOrder(LdapClientLiveTest.USER2, LdapClientLiveTest.USER3));
    }

    @Test
    public void givenLdapClientNotExists_whenDataProvided_thenNewUserCreated() {
        ldapClient.create(LdapClientLiveTest.USER4, LdapClientLiveTest.USER4_PWD);
        ldapClient.authenticate(LdapClientLiveTest.USER4, LdapClientLiveTest.USER4_PWD);
    }

    @Test
    public void givenLdapClientExists_whenDataProvided_thenExistingUserModified() {
        ldapClient.modify(LdapClientLiveTest.USER2, LdapClientLiveTest.USER3_PWD);
        ldapClient.authenticate(LdapClientLiveTest.USER2, LdapClientLiveTest.USER3_PWD);
    }
}

