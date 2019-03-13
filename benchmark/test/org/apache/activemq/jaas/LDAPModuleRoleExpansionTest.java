/**
 * Copyright 2012 The Apache Software Foundation.
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
package org.apache.activemq.jaas;


import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.ldap.LdapServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP", port = 1024) })
@ApplyLdifFiles("test.ldif")
public class LDAPModuleRoleExpansionTest extends AbstractLdapTestUnit {
    public static LdapServer ldapServer;

    private static final String PRINCIPAL = "uid=admin,ou=system";

    private static final String CREDENTIALS = "secret";

    private final String loginConfigSysPropName = "java.security.auth.login.config";

    private String oldLoginConfig;

    @SuppressWarnings("unchecked")
    @Test
    public void testRunning() throws Exception {
        Hashtable env = new Hashtable();
        env.put(Context.PROVIDER_URL, "ldap://localhost:1024");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, LDAPModuleRoleExpansionTest.PRINCIPAL);
        env.put(Context.SECURITY_CREDENTIALS, LDAPModuleRoleExpansionTest.CREDENTIALS);
        DirContext ctx = new InitialDirContext(env);
        HashSet set = new HashSet();
        NamingEnumeration list = ctx.list("ou=system");
        while (list.hasMore()) {
            NameClassPair ncp = ((NameClassPair) (list.next()));
            set.add(ncp.getName());
        } 
        Assert.assertTrue(set.contains("uid=admin"));
        Assert.assertTrue(set.contains("ou=users"));
        Assert.assertTrue(set.contains("ou=groups"));
        Assert.assertTrue(set.contains("ou=configuration"));
        Assert.assertTrue(set.contains("prefNodeName=sysPrefRoot"));
    }

    @Test
    public void testRoleExpansion() throws LoginException {
        LoginContext context = new LoginContext("ExpandedLDAPLogin", new CallbackHandler() {
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                for (int i = 0; i < (callbacks.length); i++) {
                    if ((callbacks[i]) instanceof NameCallback) {
                        ((NameCallback) (callbacks[i])).setName("first");
                    } else
                        if ((callbacks[i]) instanceof PasswordCallback) {
                            ((PasswordCallback) (callbacks[i])).setPassword("secret".toCharArray());
                        } else {
                            throw new UnsupportedCallbackException(callbacks[i]);
                        }

                }
            }
        });
        context.login();
        Subject subject = context.getSubject();
        boolean isAdmin = false;
        boolean isUser = false;
        for (Principal principal : subject.getPrincipals()) {
            if (principal instanceof GroupPrincipal) {
                GroupPrincipal groupPrincipal = ((GroupPrincipal) (principal));
                if (groupPrincipal.getName().equalsIgnoreCase("admins"))
                    isAdmin = true;

                if (groupPrincipal.getName().equalsIgnoreCase("users"))
                    isUser = true;

            }
        }
        // Should be in users by virtue of being in admins
        Assert.assertTrue((isAdmin && isUser));
        context.logout();
    }
}

