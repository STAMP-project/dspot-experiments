package com.gitblit.tests;


import Keys.realm.ldap.username;
import ResultCode.SUCCESS;
import com.gitblit.ldap.LdapConnection;
import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.gitblit.tests.LdapBasedUnitTest.AuthMode.ANONYMOUS;
import static com.gitblit.tests.LdapBasedUnitTest.AuthMode.DS_MANAGER;


/* Test for the LdapConnection

@author Florian Zschocke
 */
@RunWith(Parameterized.class)
public class LdapConnectionTest extends LdapBasedUnitTest {
    @Test
    public void testEscapeLDAPFilterString() {
        // This test is independent from authentication mode, so run only once.
        Assume.assumeTrue(((authMode) == (ANONYMOUS)));
        // From: https://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java
        Assert.assertEquals("No special characters to escape", "Hi This is a test #??", LdapConnection.escapeLDAPSearchFilter("Hi This is a test #??"));
        Assert.assertEquals("LDAP Christams Tree", "Hi \\28This\\29 = is \\2a a \\5c test # \u00e7 \u00e0 \u00f4", LdapConnection.escapeLDAPSearchFilter("Hi (This) = is * a \\ test # \u00e7 \u00e0 \u00f4"));
        Assert.assertEquals("Injection", "\\2a\\29\\28userPassword=secret", LdapConnection.escapeLDAPSearchFilter("*)(userPassword=secret"));
    }

    @Test
    public void testConnect() {
        // This test is independent from authentication mode, so run only once.
        Assume.assumeTrue(((authMode) == (ANONYMOUS)));
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
        } finally {
            conn.close();
        }
    }

    @Test
    public void testBindAnonymous() {
        // This test tests for anonymous bind, so run only in authentication mode ANONYMOUS.
        Assume.assumeTrue(((authMode) == (ANONYMOUS)));
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
            BindResult br = conn.bind();
            Assert.assertNotNull(br);
            Assert.assertEquals(SUCCESS, br.getResultCode());
            Assert.assertEquals("", authMode.getBindTracker().getLastSuccessfulBindDN(br.getMessageID()));
        } finally {
            conn.close();
        }
    }

    @Test
    public void testBindAsAdmin() {
        // This test tests for anonymous bind, so run only in authentication mode DS_MANAGER.
        Assume.assumeTrue(((authMode) == (DS_MANAGER)));
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
            BindResult br = conn.bind();
            Assert.assertNotNull(br);
            Assert.assertEquals(SUCCESS, br.getResultCode());
            Assert.assertEquals(settings.getString(username, "UNSET"), authMode.getBindTracker().getLastSuccessfulBindDN(br.getMessageID()));
        } finally {
            conn.close();
        }
    }

    @Test
    public void testBindToBindpattern() {
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
            String bindPattern = "CN=${username},OU=Canada," + (LdapBasedUnitTest.ACCOUNT_BASE);
            BindResult br = conn.bind(bindPattern, "UserThree", "userThreePassword");
            Assert.assertNotNull(br);
            Assert.assertEquals(SUCCESS, br.getResultCode());
            Assert.assertEquals(("CN=UserThree,OU=Canada," + (LdapBasedUnitTest.ACCOUNT_BASE)), authMode.getBindTracker().getLastSuccessfulBindDN(br.getMessageID()));
            br = conn.bind(bindPattern, "UserFour", "userThreePassword");
            Assert.assertNull(br);
            br = conn.bind(bindPattern, "UserTwo", "userTwoPassword");
            Assert.assertNull(br);
        } finally {
            conn.close();
        }
    }

    @Test
    public void testRebindAsUser() {
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
            Assert.assertFalse(conn.rebindAsUser());
            BindResult br = conn.bind();
            Assert.assertNotNull(br);
            Assert.assertFalse(conn.rebindAsUser());
            String bindPattern = "CN=${username},OU=Canada," + (LdapBasedUnitTest.ACCOUNT_BASE);
            br = conn.bind(bindPattern, "UserThree", "userThreePassword");
            Assert.assertNotNull(br);
            Assert.assertFalse(conn.rebindAsUser());
            br = conn.bind();
            Assert.assertNotNull(br);
            Assert.assertTrue(conn.rebindAsUser());
            Assert.assertEquals(SUCCESS, br.getResultCode());
            Assert.assertEquals(("CN=UserThree,OU=Canada," + (LdapBasedUnitTest.ACCOUNT_BASE)), authMode.getBindTracker().getLastSuccessfulBindDN());
        } finally {
            conn.close();
        }
    }

    @Test
    public void testSearchRequest() throws LDAPException {
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
            BindResult br = conn.bind();
            Assert.assertNotNull(br);
            SearchRequest req;
            SearchResult result;
            SearchResultEntry entry;
            req = new SearchRequest(LdapBasedUnitTest.ACCOUNT_BASE, SearchScope.BASE, "(CN=UserOne)");
            result = conn.search(req);
            Assert.assertNotNull(result);
            Assert.assertEquals(0, result.getEntryCount());
            req = new SearchRequest(LdapBasedUnitTest.ACCOUNT_BASE, SearchScope.ONE, "(CN=UserTwo)");
            result = conn.search(req);
            Assert.assertNotNull(result);
            Assert.assertEquals(0, result.getEntryCount());
            req = new SearchRequest(LdapBasedUnitTest.ACCOUNT_BASE, SearchScope.SUB, "(CN=UserThree)");
            result = conn.search(req);
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getEntryCount());
            entry = result.getSearchEntries().get(0);
            Assert.assertEquals(("CN=UserThree,OU=Canada," + (LdapBasedUnitTest.ACCOUNT_BASE)), entry.getDN());
            req = new SearchRequest(LdapBasedUnitTest.ACCOUNT_BASE, SearchScope.SUBORDINATE_SUBTREE, "(CN=UserFour)");
            result = conn.search(req);
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getEntryCount());
            entry = result.getSearchEntries().get(0);
            Assert.assertEquals(("CN=UserFour,OU=Canada," + (LdapBasedUnitTest.ACCOUNT_BASE)), entry.getDN());
        } finally {
            conn.close();
        }
    }

    @Test
    public void testSearch() throws LDAPException {
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
            BindResult br = conn.bind();
            Assert.assertNotNull(br);
            SearchResult result;
            SearchResultEntry entry;
            result = conn.search(LdapBasedUnitTest.ACCOUNT_BASE, false, "(CN=UserOne)", null);
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getEntryCount());
            entry = result.getSearchEntries().get(0);
            Assert.assertEquals(("CN=UserOne,OU=US," + (LdapBasedUnitTest.ACCOUNT_BASE)), entry.getDN());
            result = conn.search(LdapBasedUnitTest.ACCOUNT_BASE, true, "(&(CN=UserOne)(surname=One))", null);
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getEntryCount());
            entry = result.getSearchEntries().get(0);
            Assert.assertEquals(("CN=UserOne,OU=US," + (LdapBasedUnitTest.ACCOUNT_BASE)), entry.getDN());
            result = conn.search(LdapBasedUnitTest.ACCOUNT_BASE, true, "(&(CN=UserOne)(surname=Two))", null);
            Assert.assertNotNull(result);
            Assert.assertEquals(0, result.getEntryCount());
            result = conn.search(LdapBasedUnitTest.ACCOUNT_BASE, true, "(surname=Two)", Arrays.asList("givenName", "surname"));
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getEntryCount());
            entry = result.getSearchEntries().get(0);
            Assert.assertEquals(("CN=UserTwo,OU=US," + (LdapBasedUnitTest.ACCOUNT_BASE)), entry.getDN());
            Assert.assertEquals(2, entry.getAttributes().size());
            Assert.assertEquals("User", entry.getAttributeValue("givenName"));
            Assert.assertEquals("Two", entry.getAttributeValue("surname"));
            result = conn.search(LdapBasedUnitTest.ACCOUNT_BASE, true, "(personalTitle=Mr*)", null);
            Assert.assertNotNull(result);
            Assert.assertEquals(3, result.getEntryCount());
            ArrayList<String> names = new ArrayList<>(3);
            names.add(result.getSearchEntries().get(0).getAttributeValue("surname"));
            names.add(result.getSearchEntries().get(1).getAttributeValue("surname"));
            names.add(result.getSearchEntries().get(2).getAttributeValue("surname"));
            Assert.assertTrue(names.contains("One"));
            Assert.assertTrue(names.contains("Two"));
            Assert.assertTrue(names.contains("Three"));
        } finally {
            conn.close();
        }
    }

    @Test
    public void testSearchUser() throws LDAPException {
        LdapConnection conn = new LdapConnection(settings);
        try {
            Assert.assertTrue(conn.connect());
            BindResult br = conn.bind();
            Assert.assertNotNull(br);
            SearchResult result;
            SearchResultEntry entry;
            result = conn.searchUser("UserOne");
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getEntryCount());
            entry = result.getSearchEntries().get(0);
            Assert.assertEquals(("CN=UserOne,OU=US," + (LdapBasedUnitTest.ACCOUNT_BASE)), entry.getDN());
            result = conn.searchUser("UserFour", Arrays.asList("givenName", "surname"));
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.getEntryCount());
            entry = result.getSearchEntries().get(0);
            Assert.assertEquals(("CN=UserFour,OU=Canada," + (LdapBasedUnitTest.ACCOUNT_BASE)), entry.getDN());
            Assert.assertEquals(2, entry.getAttributes().size());
            Assert.assertEquals("User", entry.getAttributeValue("givenName"));
            Assert.assertEquals("Four", entry.getAttributeValue("surname"));
        } finally {
            conn.close();
        }
    }
}

