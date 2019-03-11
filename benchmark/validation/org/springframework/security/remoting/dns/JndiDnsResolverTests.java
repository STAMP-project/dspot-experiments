/**
 * Copyright 2009-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.remoting.dns;


import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Mike Wiesner
 * @since 3.0
 */
public class JndiDnsResolverTests {
    private JndiDnsResolver dnsResolver;

    private InitialContextFactory contextFactory;

    private DirContext context;

    @Test
    public void testResolveIpAddress() throws Exception {
        Attributes records = new BasicAttributes("A", "63.246.7.80");
        Mockito.when(context.getAttributes("www.springsource.com", new String[]{ "A" })).thenReturn(records);
        String ipAddress = dnsResolver.resolveIpAddress("www.springsource.com");
        assertThat(ipAddress).isEqualTo("63.246.7.80");
    }

    @Test(expected = DnsEntryNotFoundException.class)
    public void testResolveIpAddressNotExisting() throws Exception {
        Mockito.when(context.getAttributes(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String[].class))).thenThrow(new NameNotFoundException("not found"));
        dnsResolver.resolveIpAddress("notexisting.ansdansdugiuzgguzgioansdiandwq.foo");
    }

    @Test
    public void testResolveServiceEntry() throws Exception {
        BasicAttributes records = createSrvRecords();
        Mockito.when(context.getAttributes("_ldap._tcp.springsource.com", new String[]{ "SRV" })).thenReturn(records);
        String hostname = dnsResolver.resolveServiceEntry("ldap", "springsource.com");
        assertThat(hostname).isEqualTo("kdc.springsource.com");
    }

    @Test(expected = DnsEntryNotFoundException.class)
    public void testResolveServiceEntryNotExisting() throws Exception {
        Mockito.when(context.getAttributes(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String[].class))).thenThrow(new NameNotFoundException("not found"));
        dnsResolver.resolveServiceEntry("wrong", "secpod.de");
    }

    @Test
    public void testResolveServiceIpAddress() throws Exception {
        BasicAttributes srvRecords = createSrvRecords();
        BasicAttributes aRecords = new BasicAttributes("A", "63.246.7.80");
        Mockito.when(context.getAttributes("_ldap._tcp.springsource.com", new String[]{ "SRV" })).thenReturn(srvRecords);
        Mockito.when(context.getAttributes("kdc.springsource.com", new String[]{ "A" })).thenReturn(aRecords);
        String ipAddress = dnsResolver.resolveServiceIpAddress("ldap", "springsource.com");
        assertThat(ipAddress).isEqualTo("63.246.7.80");
    }

    @Test(expected = DnsLookupException.class)
    public void testUnknowError() throws Exception {
        Mockito.when(context.getAttributes(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String[].class))).thenThrow(new NamingException("error"));
        dnsResolver.resolveIpAddress("");
    }
}

