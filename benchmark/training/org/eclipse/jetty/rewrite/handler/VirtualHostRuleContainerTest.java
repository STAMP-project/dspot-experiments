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
package org.eclipse.jetty.rewrite.handler;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class VirtualHostRuleContainerTest extends AbstractRuleTestCase {
    private RewriteHandler _handler;

    private RewritePatternRule _rule;

    private RewritePatternRule _fooRule;

    private VirtualHostRuleContainer _fooContainerRule;

    @Test
    public void testArbitraryHost() throws Exception {
        _request.setAuthority("cheese.com", 0);
        _handler.setRules(new Rule[]{ _rule, _fooContainerRule });
        handleRequest();
        Assertions.assertEquals("/rule/bar", _request.getRequestURI(), "{_rule, _fooContainerRule, Host: cheese.com}: applied _rule");
    }

    @Test
    public void testVirtualHost() throws Exception {
        _request.setAuthority("foo.com", 0);
        _handler.setRules(new Rule[]{ _fooContainerRule });
        handleRequest();
        Assertions.assertEquals("/cheese/fooRule", _request.getRequestURI(), "{_fooContainerRule, Host: foo.com}: applied _fooRule");
    }

    @Test
    public void testCascadingRules() throws Exception {
        _request.setAuthority("foo.com", 0);
        _request.setURIPathQuery("/cheese/bar");
        _rule.setTerminating(false);
        _fooRule.setTerminating(false);
        _fooContainerRule.setTerminating(false);
        _handler.setRules(new Rule[]{ _rule, _fooContainerRule });
        handleRequest();
        Assertions.assertEquals("/rule/bar", _request.getRequestURI(), "{_rule, _fooContainerRule}: applied _rule, didn't match _fooRule");
        _request.setURIPathQuery("/cheese/bar");
        _handler.setRules(new Rule[]{ _fooContainerRule, _rule });
        handleRequest();
        Assertions.assertEquals("/rule/fooRule", _request.getRequestURI(), "{_fooContainerRule, _rule}: applied _fooRule, _rule");
        _request.setURIPathQuery("/cheese/bar");
        _fooRule.setTerminating(true);
        handleRequest();
        Assertions.assertEquals("/rule/fooRule", _request.getRequestURI(), "{_fooContainerRule, _rule}: (_fooRule is terminating); applied _fooRule, _rule");
        _request.setURIPathQuery("/cheese/bar");
        _fooRule.setTerminating(false);
        _fooContainerRule.setTerminating(true);
        handleRequest();
        Assertions.assertEquals("/cheese/fooRule", _request.getRequestURI(), "{_fooContainerRule, _rule}: (_fooContainerRule is terminating); applied _fooRule, terminated before _rule");
    }

    @Test
    public void testCaseInsensitiveHostname() throws Exception {
        _request.setAuthority("Foo.com", 0);
        _fooContainerRule.setVirtualHosts(new String[]{ "foo.com" });
        _handler.setRules(new Rule[]{ _fooContainerRule });
        handleRequest();
        Assertions.assertEquals("/cheese/fooRule", _request.getRequestURI(), "Foo.com and foo.com are equivalent");
    }

    @Test
    public void testEmptyVirtualHost() throws Exception {
        _request.setAuthority("cheese.com", 0);
        _handler.setRules(new Rule[]{ _fooContainerRule });
        _fooContainerRule.setVirtualHosts(null);
        handleRequest();
        Assertions.assertEquals("/cheese/fooRule", _request.getRequestURI(), "{_fooContainerRule: virtual hosts array is null, Host: cheese.com}: apply _fooRule");
        _request.setURIPathQuery("/cheese/bar");
        _request.setURIPathQuery("/cheese/bar");
        _fooContainerRule.setVirtualHosts(new String[]{  });
        handleRequest();
        Assertions.assertEquals("/cheese/fooRule", _request.getRequestURI(), "{_fooContainerRule: virtual hosts array is empty, Host: cheese.com}: apply _fooRule");
        _request.setURIPathQuery("/cheese/bar");
        _request.setURIPathQuery("/cheese/bar");
        _fooContainerRule.setVirtualHosts(new String[]{ null });
        handleRequest();
        Assertions.assertEquals("/cheese/fooRule", _request.getRequestURI(), "{_fooContainerRule: virtual host is null, Host: cheese.com}: apply _fooRule");
    }

    @Test
    public void testMultipleVirtualHosts() throws Exception {
        _request.setAuthority("foo.com", 0);
        _handler.setRules(new Rule[]{ _fooContainerRule });
        _fooContainerRule.setVirtualHosts(new String[]{ "cheese.com" });
        handleRequest();
        Assertions.assertEquals("/cheese/bar", _request.getRequestURI(), "{_fooContainerRule: vhosts[cheese.com], Host: foo.com}: no effect");
        _request.setURIPathQuery("/cheese/bar");
        _fooContainerRule.addVirtualHost("foo.com");
        handleRequest();
        Assertions.assertEquals("/cheese/fooRule", _request.getRequestURI(), "{_fooContainerRule: vhosts[cheese.com, foo.com], Host: foo.com}: apply _fooRule");
    }

    @Test
    public void testWildcardVirtualHosts() throws Exception {
        checkWildcardHost(true, null, new String[]{ "foo.com", ".foo.com", "vhost.foo.com" });
        checkWildcardHost(true, new String[]{ null }, new String[]{ "foo.com", ".foo.com", "vhost.foo.com" });
        checkWildcardHost(true, new String[]{ "foo.com", "*.foo.com" }, new String[]{ "foo.com", ".foo.com", "vhost.foo.com" });
        checkWildcardHost(false, new String[]{ "foo.com", "*.foo.com" }, new String[]{ "badfoo.com", ".badfoo.com", "vhost.badfoo.com" });
        checkWildcardHost(false, new String[]{ "*." }, new String[]{ "anything.anything" });
        checkWildcardHost(true, new String[]{ "*.foo.com" }, new String[]{ "vhost.foo.com", ".foo.com" });
        checkWildcardHost(false, new String[]{ "*.foo.com" }, new String[]{ "vhost.www.foo.com", "foo.com", "www.vhost.foo.com" });
        checkWildcardHost(true, new String[]{ "*.sub.foo.com" }, new String[]{ "vhost.sub.foo.com", ".sub.foo.com" });
        checkWildcardHost(false, new String[]{ "*.sub.foo.com" }, new String[]{ ".foo.com", "sub.foo.com", "vhost.foo.com" });
        checkWildcardHost(false, new String[]{ "foo.*.com", "foo.com.*" }, new String[]{ "foo.vhost.com", "foo.com.vhost", "foo.com" });
    }
}

