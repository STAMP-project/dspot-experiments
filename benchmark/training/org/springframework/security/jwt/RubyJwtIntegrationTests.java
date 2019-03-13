/**
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.jwt;


import org.jruby.embed.ScriptingContainer;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.security.jwt.crypto.sign.MacSigner;


/**
 * Tests for compatibility with Ruby's JWT Gem.
 *
 * Requires a local JRuby installation with jruby.home as a system property. Using RVM
 * sets this up automatically (e.g. "rvm use jruby-1.7.12"), or alternatively Maven can be
 * run with:
 *
 * <pre>
 * mvn -DargLine="-Djruby.home=${JRUBY_HOME}" test
 * </pre>
 *
 * See https://github.com/jruby/jruby/wiki/JavaIntegration for issues with gem loading
 *
 * You also need to: jruby -S gem install jwt jruby -S gem install jruby-openssl
 *
 * for the tests to work. If the environment isn't set up correctly or jruby.home isn't
 * set, they will be skipped.
 *
 * @author Luke Taylor
 */
public class RubyJwtIntegrationTests {
    public static final String TEST_CLAIMS = "{\"some\":\"payload\"}";

    MacSigner hmac = new MacSigner(utf8Encode("secret"));

    @Rule
    public final MethodRule jrubySetupOk = new RubyJwtIntegrationTests.JRubyJwtInstalled();

    @Test
    public void rubyCanDecodeHmacSignedToken() throws Exception {
        Jwt jwt = JwtHelper.encode(RubyJwtIntegrationTests.TEST_CLAIMS, hmac);
        ScriptingContainer container = new ScriptingContainer();
        container.put("@token", jwt.getEncoded());
        container.put("@claims", "");
        String script = "require \"jwt\"\n" + ("@claims = JWT.decode(@token, \"secret\", \"HS256\")[0].to_json\n" + "puts @claims");
        container.runScriptlet(script);
        Assert.assertEquals(RubyJwtIntegrationTests.TEST_CLAIMS, container.get("@claims"));
    }

    @Test
    public void canDecodeRubyHmacSignedToken() throws Exception {
        ScriptingContainer container = new ScriptingContainer();
        container.put("@token", "xxx");
        String script = "require \"jwt\"\n" + ("@token = JWT.encode({\"some\" => \"payload\"}, \"secret\", \"HS256\")\n" + "puts @token");
        container.runScriptlet(script);
        String token = ((String) (container.get("@token")));
        Jwt jwt = JwtHelper.decodeAndVerify(token, hmac);
        Assert.assertEquals(RubyJwtIntegrationTests.TEST_CLAIMS, jwt.getClaims());
        container.terminate();
    }

    public static class JRubyJwtInstalled implements MethodRule {
        private static boolean setupOkSet;

        private static boolean setupOk;

        public JRubyJwtInstalled() {
            if (!(RubyJwtIntegrationTests.JRubyJwtInstalled.setupOkSet)) {
                RubyJwtIntegrationTests.JRubyJwtInstalled.setupOk = false;
                RubyJwtIntegrationTests.JRubyJwtInstalled.setupOkSet = true;
                try {
                    String script = "require \"jwt\"\n" + ("require \"bouncy-castle-java\"\n" + "require \"openssl\"");
                    ScriptingContainer container = new ScriptingContainer();
                    container.runScriptlet(script);
                    RubyJwtIntegrationTests.JRubyJwtInstalled.setupOk = true;
                } catch (Exception e) {
                    System.out.println(("jruby.home not set or JWT gem not available. JWT ruby integration tests will be skipped" + (e.getMessage())));
                }
            }
        }

        @Override
        public Statement apply(final Statement base, FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    Assume.assumeTrue(RubyJwtIntegrationTests.JRubyJwtInstalled.setupOk);
                    base.evaluate();
                }
            };
        }
    }
}

