/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.jaas;


import JAASLoginService.DEFAULT_ROLE_CLASS_NAME;
import java.security.Principal;
import java.util.Collections;
import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.server.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;


/**
 * JAASLoginServiceTest
 */
public class JAASLoginServiceTest {
    public static class TestConfiguration extends Configuration {
        AppConfigurationEntry _entry = new AppConfigurationEntry(TestLoginModule.class.getCanonicalName(), REQUIRED, Collections.emptyMap());

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            return new AppConfigurationEntry[]{ _entry };
        }
    }

    interface SomeRole {}

    public class TestRole implements Principal , JAASLoginServiceTest.SomeRole {
        String _name;

        public TestRole(String name) {
            _name = name;
        }

        public String getName() {
            return _name;
        }
    }

    public class AnotherTestRole extends JAASLoginServiceTest.TestRole {
        public AnotherTestRole(String name) {
            super(name);
        }
    }

    public class NotTestRole implements Principal {
        String _name;

        public NotTestRole(String n) {
            _name = n;
        }

        public String getName() {
            return _name;
        }
    }

    @Test
    public void testServletRequestCallback() throws Exception {
        // Test with the DefaultCallbackHandler
        JAASLoginService ls = new JAASLoginService("foo");
        ls.setCallbackHandlerClass("org.eclipse.jetty.jaas.callback.DefaultCallbackHandler");
        ls.setIdentityService(new DefaultIdentityService());
        ls.setConfiguration(new JAASLoginServiceTest.TestConfiguration());
        Request request = new Request(null, null);
        ls.login("aaardvaark", "aaa", request);
        // Test with the fallback CallbackHandler
        ls = new JAASLoginService("foo");
        ls.setIdentityService(new DefaultIdentityService());
        ls.setConfiguration(new JAASLoginServiceTest.TestConfiguration());
        ls.login("aaardvaark", "aaa", request);
    }

    @Test
    public void testLoginServiceRoles() throws Exception {
        JAASLoginService ls = new JAASLoginService("foo");
        // test that we always add in the DEFAULT ROLE CLASSNAME
        ls.setRoleClassNames(new String[]{ "arole", "brole" });
        String[] roles = ls.getRoleClassNames();
        Assertions.assertEquals(3, roles.length);
        Assertions.assertEquals(DEFAULT_ROLE_CLASS_NAME, roles[2]);
        ls.setRoleClassNames(new String[]{  });
        Assertions.assertEquals(1, ls.getRoleClassNames().length);
        Assertions.assertEquals(DEFAULT_ROLE_CLASS_NAME, ls.getRoleClassNames()[0]);
        ls.setRoleClassNames(null);
        Assertions.assertEquals(1, ls.getRoleClassNames().length);
        Assertions.assertEquals(DEFAULT_ROLE_CLASS_NAME, ls.getRoleClassNames()[0]);
        // test a custom role class where some of the roles are subclasses of it
        ls.setRoleClassNames(new String[]{ JAASLoginServiceTest.TestRole.class.getName() });
        Subject subject = new Subject();
        subject.getPrincipals().add(new JAASLoginServiceTest.NotTestRole("w"));
        subject.getPrincipals().add(new JAASLoginServiceTest.TestRole("x"));
        subject.getPrincipals().add(new JAASLoginServiceTest.TestRole("y"));
        subject.getPrincipals().add(new JAASLoginServiceTest.AnotherTestRole("z"));
        String[] groups = ls.getGroups(subject);
        Assertions.assertEquals(3, groups.length);
        for (String g : groups)
            Assertions.assertTrue((((g.equals("x")) || (g.equals("y"))) || (g.equals("z"))));

        // test a custom role class
        ls.setRoleClassNames(new String[]{ JAASLoginServiceTest.AnotherTestRole.class.getName() });
        Subject subject2 = new Subject();
        subject2.getPrincipals().add(new JAASLoginServiceTest.NotTestRole("w"));
        subject2.getPrincipals().add(new JAASLoginServiceTest.TestRole("x"));
        subject2.getPrincipals().add(new JAASLoginServiceTest.TestRole("y"));
        subject2.getPrincipals().add(new JAASLoginServiceTest.AnotherTestRole("z"));
        Assertions.assertEquals(1, ls.getGroups(subject2).length);
        Assertions.assertEquals("z", ls.getGroups(subject2)[0]);
        // test a custom role class that implements an interface
        ls.setRoleClassNames(new String[]{ JAASLoginServiceTest.SomeRole.class.getName() });
        Subject subject3 = new Subject();
        subject3.getPrincipals().add(new JAASLoginServiceTest.NotTestRole("w"));
        subject3.getPrincipals().add(new JAASLoginServiceTest.TestRole("x"));
        subject3.getPrincipals().add(new JAASLoginServiceTest.TestRole("y"));
        subject3.getPrincipals().add(new JAASLoginServiceTest.AnotherTestRole("z"));
        Assertions.assertEquals(3, ls.getGroups(subject3).length);
        for (String g : groups)
            Assertions.assertTrue((((g.equals("x")) || (g.equals("y"))) || (g.equals("z"))));

        // test a class that doesn't match
        ls.setRoleClassNames(new String[]{ JAASLoginServiceTest.NotTestRole.class.getName() });
        Subject subject4 = new Subject();
        subject4.getPrincipals().add(new JAASLoginServiceTest.TestRole("x"));
        subject4.getPrincipals().add(new JAASLoginServiceTest.TestRole("y"));
        subject4.getPrincipals().add(new JAASLoginServiceTest.AnotherTestRole("z"));
        Assertions.assertEquals(0, ls.getGroups(subject4).length);
    }
}

