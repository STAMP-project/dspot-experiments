/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.config;


import Conf.HIBERNATE;
import Conf.JDBC;
import Conf.ROOT;
import Conf.USERS;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.env.Env;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.u.U;
import org.rapidoid.util.MscOpts;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class ConfigurationTest extends IsolatedIntegrationTest {
    private static boolean TLS_ENABLED = MscOpts.isTLSEnabled();

    @Test
    public void testBasicConfig() {
        isTrue(Env.test());
        ROOT.set("abc", "123");
        ROOT.set("cool", true);
        eq(ROOT.entry("abc").or(0).longValue(), 123);
        isTrue(ROOT.is("cool"));
        eq(Env.mode(), EnvMode.TEST);
        isTrue(Env.test());
        isFalse(Env.production());
        isFalse(Env.dev());
        checkDefaults();
    }

    @Test
    public void testDefaultConfig() {
        isTrue(Env.test());
        checkDefaults();
    }

    @Test
    public void testProfiles() {
        Env.setArgs("port=12345", "profiles=mysql,p1,p2");
        eq(Env.profiles(), U.set("mysql", "p1", "p2", "test"));
        isTrue(Env.test());
        isFalse(Env.dev());
        isFalse(Env.production());
        checkDefaults();
    }

    @Test
    public void testDefaultProfiles() {
        eq(Env.profiles(), U.set("test", "default"));
    }

    @Test
    public void testPathChange() {
        Env.setArgs("config=myconfig");
        checkDefaults();
    }

    @Test
    public void testUsersConfigWithArgs() {
        String pswd = "m-i_h?1f~@121";
        Env.setArgs("users.admin.password=abc123", ("users.nick.password=" + pswd), "users.nick.roles=moderator");
        checkDefaults();
        eq(USERS.toMap().keySet(), U.set("admin", "nick"));
        eq(USERS.sub("admin").toMap(), U.map("roles", "administrator", "password", "abc123"));
        eq(USERS.sub("nick").toMap(), U.map("roles", "moderator", "password", pswd));
    }

    @Test
    public void testEnvironmentProperties() {
        Env.setArgs("config=myconfig");
        String osName = System.getProperty("os.name", "?");
        Config os = Conf.section("os");
        isFalse(os.isEmpty());
        eq(os.get("name"), osName);
        eq(os.entry("name").getOrNull(), osName);
        eq(os.entry("name").or(""), osName);
        isTrue(os.has("name"));
        isFalse(os.is("name"));
        checkDefaults();
    }

    @Test
    public void testArgOverride() {
        Env.setArgs("foo=bar123");
        ROOT.set("foo", "b");
        eq(ROOT.get("foo"), "bar123");
    }

    @Test
    public void testNestedSet() {
        ROOT.set("foo.bar", "b");
        ROOT.set("foo.baz", "z");
        eq(Conf.section("foo").toMap(), U.map("bar", "b", "baz", "z"));
    }

    @Test
    public void testMySqlProfile() {
        if (ConfigurationTest.TLS_ENABLED)
            return;

        Env.setArgs("jdbc.port=3333", "profiles=mysql");
        eq(Env.profiles(), U.set("mysql", "test"));
        verifyJson("jdbc-mysql-profile", JDBC.toMap());
        verifyJson("hibernate-mysql-profile", HIBERNATE.toMap());
        verifyJson("root", ROOT.toMap());
    }

    @Test
    public void testPostgresProfile() {
        if (ConfigurationTest.TLS_ENABLED)
            return;

        Env.setArgs("profiles=postgres");
        eq(Env.profiles(), U.set("postgres", "test"));
        verifyJson("jdbc-postgres-profile", JDBC.toMap());
        verifyJson("hibernate-postgres-profile", HIBERNATE.toMap());
        verifyJson("root", ROOT.toMap());
    }

    @Test
    public void testBuiltInConfig() {
        if (ConfigurationTest.TLS_ENABLED)
            return;

        verifyJson("root", ROOT.toMap());
    }
}

