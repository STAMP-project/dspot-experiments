/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.security;


import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.naming.CommunicationException;
import javax.naming.directory.SearchControls;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.apache.hadoop.security.TestLdapGroupsMappingBase.DummyLdapCtxFactory.setExpectedLdapUrl;


/**
 * Test failover functionality for switching to different
 * LDAP server URLs upon failures.
 */
public class TestLdapGroupsMappingWithFailover extends TestLdapGroupsMappingBase {
    private static final String TEST_USER_NAME = "some_user";

    /**
     * Test that when disabled, we will retry the configured number
     * of times using the same LDAP server.
     */
    @Test
    public void testDoesNotFailoverWhenDisabled() throws Exception {
        final int numAttempts = 3;
        Configuration conf = getBaseConf();
        conf.setStrings(LdapGroupsMapping.LDAP_URL_KEY, "ldap://test", "ldap://test1", "ldap://test2");
        setExpectedLdapUrl("ldap://test");
        conf.setInt(LdapGroupsMapping.LDAP_NUM_ATTEMPTS_KEY, numAttempts);
        conf.setInt(LdapGroupsMapping.LDAP_NUM_ATTEMPTS_BEFORE_FAILOVER_KEY, numAttempts);
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenThrow(new CommunicationException());
        LdapGroupsMapping groupsMapping = getGroupsMapping();
        groupsMapping.setConf(conf);
        List<String> groups = groupsMapping.getGroups(TestLdapGroupsMappingWithFailover.TEST_USER_NAME);
        Assert.assertTrue(groups.isEmpty());
        // Test that we made 3 attempts using the same server
        Mockito.verify(getContext(), Mockito.times(numAttempts)).search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class));
    }

    /**
     * Test that when configured, we will make the specified amount of
     * attempts using one ldap url before failing over to the next one.
     *
     * This also tests that we wrap back to the first server
     * if we've tried them all.
     */
    @Test
    public void testFailover() throws Exception {
        Queue<String> ldapUrls = new LinkedList<>();
        ldapUrls.add("ldap://test");
        ldapUrls.add("ldap://test1");
        ldapUrls.add("ldap://test2");
        final int numAttempts = 12;
        final int numAttemptsBeforeFailover = 2;
        Configuration conf = getBaseConf();
        conf.setStrings(LdapGroupsMapping.LDAP_URL_KEY, "ldap://test", "ldap://test1", "ldap://test2");
        conf.setInt(LdapGroupsMapping.LDAP_NUM_ATTEMPTS_KEY, numAttempts);
        conf.setInt(LdapGroupsMapping.LDAP_NUM_ATTEMPTS_BEFORE_FAILOVER_KEY, numAttemptsBeforeFailover);
        // Set the first expected url and add it back to the queue
        String nextLdapUrl = ldapUrls.remove();
        setExpectedLdapUrl(nextLdapUrl);
        ldapUrls.add(nextLdapUrl);
        // Number of attempts using a single ldap server url
        final AtomicInteger serverAttempts = new AtomicInteger(numAttemptsBeforeFailover);
        Mockito.when(getContext().search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if ((serverAttempts.get()) == 1) {
                    // Take the head of the queue and re-queue it to the back
                    String nextLdapUrl = ldapUrls.remove();
                    ldapUrls.add(nextLdapUrl);
                    setExpectedLdapUrl(nextLdapUrl);
                    serverAttempts.set(numAttemptsBeforeFailover);
                } else {
                    serverAttempts.decrementAndGet();
                }
                throw new CommunicationException();
            }
        });
        LdapGroupsMapping groupsMapping = getGroupsMapping();
        groupsMapping.setConf(conf);
        List<String> groups = groupsMapping.getGroups(TestLdapGroupsMappingWithFailover.TEST_USER_NAME);
        Assert.assertTrue(groups.isEmpty());
        // Test that we made 6 attempts overall
        Mockito.verify(getContext(), Mockito.times(numAttempts)).search(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Object[].class), ArgumentMatchers.any(SearchControls.class));
    }
}

