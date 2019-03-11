/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.glassfish.jersey.server.wadl.config;


import com.sun.research.ws.wadl.Application;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import com.sun.research.ws.wadl.Request;
import com.sun.research.ws.wadl.Resource;
import com.sun.research.ws.wadl.Resources;
import com.sun.research.ws.wadl.Response;
import com.sun.research.ws.wadl.org.glassfish.jersey.server.model.Resource;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.server.TestInjectionManagerFactory;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.server.wadl.internal.ApplicationDescription;
import org.junit.Assert;
import org.junit.Test;


/**
 * TODO: DESCRIBE ME<br>
 * Created on: Aug 2, 2008<br>
 *
 * @author <a href="mailto:martin.grotzke@freiheit.com">Martin Grotzke</a>
 * @author Miroslav Fuksa
 */
public class WadlGeneratorConfigTest {
    @Test
    public void testBuildWadlGeneratorFromGenerators() {
        final Class<WadlGeneratorConfigTest.MyWadlGenerator> generator = WadlGeneratorConfigTest.MyWadlGenerator.class;
        final Class<WadlGeneratorConfigTest.MyWadlGenerator2> generator2 = WadlGeneratorConfigTest.MyWadlGenerator2.class;
        WadlGeneratorConfig config = generator(generator2).build();
        TestInjectionManagerFactory.BootstrapResult result = TestInjectionManagerFactory.createInjectionManager();
        WadlGenerator wadlGenerator = config.createWadlGenerator(result.injectionManager);
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator2.class, wadlGenerator.getClass());
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator.class, ((WadlGeneratorConfigTest.MyWadlGenerator2) (wadlGenerator)).getDelegate().getClass());
    }

    @Test
    public void testBuildWadlGeneratorFromDescriptions() {
        TestInjectionManagerFactory.BootstrapResult result = TestInjectionManagerFactory.createInjectionManager();
        final String propValue = "bar";
        WadlGeneratorConfig config = WadlGeneratorConfig.generator(WadlGeneratorConfigTest.MyWadlGenerator.class).prop("foo", propValue).build();
        WadlGenerator wadlGenerator = config.createWadlGenerator(result.injectionManager);
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator.class, wadlGenerator.getClass());
        Assert.assertEquals(((WadlGeneratorConfigTest.MyWadlGenerator) (wadlGenerator)).getFoo(), propValue);
        final String propValue2 = "baz";
        config = generator(WadlGeneratorConfigTest.MyWadlGenerator2.class).prop("bar", propValue2).build();
        wadlGenerator = config.createWadlGenerator(result.injectionManager);
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator2.class, wadlGenerator.getClass());
        final WadlGeneratorConfigTest.MyWadlGenerator2 wadlGenerator2 = ((WadlGeneratorConfigTest.MyWadlGenerator2) (wadlGenerator));
        Assert.assertEquals(wadlGenerator2.getBar(), propValue2);
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator.class, wadlGenerator2.getDelegate().getClass());
        Assert.assertEquals(((WadlGeneratorConfigTest.MyWadlGenerator) (wadlGenerator2.getDelegate())).getFoo(), propValue);
    }

    @Test
    public void testCustomWadlGeneratorConfig() {
        final String propValue = "someValue";
        final String propValue2 = "baz";
        class MyWadlGeneratorConfig extends WadlGeneratorConfig {
            @Override
            public List<WadlGeneratorDescription> configure() {
                return generator(WadlGeneratorConfigTest.MyWadlGenerator2.class).prop("bar", propValue2).descriptions();
            }
        }
        TestInjectionManagerFactory.BootstrapResult result = TestInjectionManagerFactory.createInjectionManager();
        WadlGeneratorConfig config = new MyWadlGeneratorConfig();
        WadlGenerator wadlGenerator = config.createWadlGenerator(result.injectionManager);
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator2.class, wadlGenerator.getClass());
        final WadlGeneratorConfigTest.MyWadlGenerator2 wadlGenerator2 = ((WadlGeneratorConfigTest.MyWadlGenerator2) (wadlGenerator));
        Assert.assertEquals(wadlGenerator2.getBar(), propValue2);
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator.class, wadlGenerator2.getDelegate().getClass());
        Assert.assertEquals(((WadlGeneratorConfigTest.MyWadlGenerator) (wadlGenerator2.getDelegate())).getFoo(), propValue);
    }

    public abstract static class BaseWadlGenerator implements WadlGenerator {
        public Application createApplication() {
            return null;
        }

        public Method createMethod(org.glassfish.jersey.server.model.Resource r, ResourceMethod m) {
            return null;
        }

        public Request createRequest(org.glassfish.jersey.server.model.Resource r, ResourceMethod m) {
            return null;
        }

        public Param createParam(org.glassfish.jersey.server.model.Resource r, ResourceMethod m, Parameter p) {
            return null;
        }

        public Representation createRequestRepresentation(org.glassfish.jersey.server.model.Resource r, ResourceMethod m, MediaType mediaType) {
            return null;
        }

        public Resource createResource(org.glassfish.jersey.server.model.Resource r, String path) {
            return null;
        }

        public Resources createResources() {
            return null;
        }

        public List<Response> createResponses(org.glassfish.jersey.server.model.Resource r, ResourceMethod m) {
            return null;
        }

        public String getRequiredJaxbContextPath() {
            return null;
        }

        public void init() {
        }

        public void setWadlGeneratorDelegate(WadlGenerator delegate) {
        }

        @Override
        public ExternalGrammarDefinition createExternalGrammar() {
            return new ExternalGrammarDefinition();
        }

        @Override
        public void attachTypes(ApplicationDescription egd) {
        }
    }

    public static class MyWadlGenerator extends WadlGeneratorConfigTest.BaseWadlGenerator {
        private String _foo;

        /**
         *
         *
         * @return the foo
         */
        public String getFoo() {
            return _foo;
        }

        /**
         *
         *
         * @param foo
         * 		the foo to set
         */
        public void setFoo(String foo) {
            _foo = foo;
        }
    }

    public static class MyWadlGenerator2 extends WadlGeneratorConfigTest.BaseWadlGenerator {
        private String _bar;

        private WadlGenerator _delegate;

        /**
         *
         *
         * @return the delegate
         */
        public WadlGenerator getDelegate() {
            return _delegate;
        }

        /**
         *
         *
         * @return the foo
         */
        public String getBar() {
            return _bar;
        }

        /**
         *
         *
         * @param foo
         * 		the foo to set
         */
        public void setBar(String foo) {
            _bar = foo;
        }

        public void setWadlGeneratorDelegate(WadlGenerator delegate) {
            _delegate = delegate;
        }
    }

    public static class Foo {
        String s;

        public Foo(String s) {
            this.s = s;
        }
    }

    public static class Bar {}

    public static class MyWadlGenerator3 extends WadlGeneratorConfigTest.BaseWadlGenerator {
        WadlGeneratorConfigTest.Foo foo;

        WadlGeneratorConfigTest.Bar bar;

        /**
         *
         *
         * @param foo
         * 		the foo to set
         */
        public void setFoo(WadlGeneratorConfigTest.Foo foo) {
            this.foo = foo;
        }

        public void setBar(WadlGeneratorConfigTest.Bar bar) {
            this.bar = bar;
        }
    }

    @Test
    public void testBuildWadlGeneratorFromDescriptionsWithTypes() {
        WadlGeneratorConfig config = WadlGeneratorConfig.generator(WadlGeneratorConfigTest.MyWadlGenerator3.class).prop("foo", "string").prop("bar", new WadlGeneratorConfigTest.Bar()).build();
        TestInjectionManagerFactory.BootstrapResult result = TestInjectionManagerFactory.createInjectionManager();
        WadlGenerator wadlGenerator = config.createWadlGenerator(result.injectionManager);
        Assert.assertEquals(WadlGeneratorConfigTest.MyWadlGenerator3.class, wadlGenerator.getClass());
        WadlGeneratorConfigTest.MyWadlGenerator3 g = ((WadlGeneratorConfigTest.MyWadlGenerator3) (wadlGenerator));
        Assert.assertNotNull(g.foo);
        Assert.assertEquals(g.foo.s, "string");
        Assert.assertNotNull(g.bar);
    }
}

