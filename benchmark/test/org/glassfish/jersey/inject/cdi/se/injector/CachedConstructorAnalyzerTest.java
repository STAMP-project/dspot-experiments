/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.inject.cdi.se.injector;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import javax.enterprise.inject.InjectionException;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link CachedConstructorAnalyzer}.
 */
public class CachedConstructorAnalyzerTest {
    private static final Collection<Class<? extends Annotation>> ANNOTATIONS = Arrays.asList(Context.class, PathParam.class);

    @Test
    public void testDefaultConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.DefaultConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.DefaultConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(0, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testNoArgsConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.NoArgsConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.NoArgsConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(0, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testSingleAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.SingleAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.SingleAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(1, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testSingleMultiAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.SingleMultiAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.SingleMultiAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(2, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testLargestAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.LargestAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.LargestAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(3, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testContainsSmallerNonAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.ContainsSmallerNonAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.ContainsSmallerNonAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(2, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testContainsLargerNonAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.ContainsLargerNonAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.ContainsLargerNonAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(1, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testSameNonAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.SameNonAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.SameNonAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(1, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testBothAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.BothAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.BothAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Constructor<CachedConstructorAnalyzerTest.BothAnnotatedConstructor> constructor = analyzer.getConstructor();
        Assert.assertEquals(1, constructor.getParameterCount());
        Assert.assertEquals(Integer.class, constructor.getParameterTypes()[0]);
    }

    @Test
    public void testOneNonAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.OneNonAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.OneNonAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(1, analyzer.getConstructor().getParameterCount());
    }

    @Test
    public void testMultiAnnotatedConstructor() {
        CachedConstructorAnalyzer<CachedConstructorAnalyzerTest.MultiAnnotatedConstructor> analyzer = new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.MultiAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS);
        Assert.assertEquals(2, analyzer.getConstructor().getParameterCount());
    }

    @Test(expected = InjectionException.class)
    public void testUnknownAnnotatedConstructor() {
        new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.UnknownAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS).getConstructor();
    }

    @Test(expected = InjectionException.class)
    public void testSingleNonAnnotatedConstructor() {
        new CachedConstructorAnalyzer(CachedConstructorAnalyzerTest.SingleNonAnnotatedConstructor.class, CachedConstructorAnalyzerTest.ANNOTATIONS).getConstructor();
    }

    public static class DefaultConstructor {}

    public static class NoArgsConstructor {
        public NoArgsConstructor() {
        }
    }

    public static class SingleNonAnnotatedConstructor {
        public SingleNonAnnotatedConstructor(String str) {
        }
    }

    public static class SingleAnnotatedConstructor {
        public SingleAnnotatedConstructor(@Context
        String str) {
        }
    }

    public static class SingleMultiAnnotatedConstructor {
        public SingleMultiAnnotatedConstructor(@Context
        String str, @PathParam("name")
        String name) {
        }
    }

    public static class LargestAnnotatedConstructor {
        public LargestAnnotatedConstructor(@Context
        String str, @PathParam("name")
        String name, @Context
        String str2) {
        }

        public LargestAnnotatedConstructor(@Context
        String str) {
        }

        public LargestAnnotatedConstructor(@Context
        String str, @PathParam("name")
        String name) {
        }
    }

    public static class ContainsSmallerNonAnnotatedConstructor {
        public ContainsSmallerNonAnnotatedConstructor(String str) {
        }

        public ContainsSmallerNonAnnotatedConstructor(@Context
        String str, @PathParam("name")
        String name) {
        }
    }

    public static class ContainsLargerNonAnnotatedConstructor {
        public ContainsLargerNonAnnotatedConstructor(@Context
        String str) {
        }

        public ContainsLargerNonAnnotatedConstructor(String str, String name) {
        }
    }

    public static class SameNonAnnotatedConstructor {
        public SameNonAnnotatedConstructor(@Context
        String str) {
        }

        public SameNonAnnotatedConstructor(Integer name) {
        }
    }

    public static class BothAnnotatedConstructor {
        public BothAnnotatedConstructor(@Context
        String str) {
        }

        public BothAnnotatedConstructor(@Context
        Integer name) {
        }
    }

    public static class OneNonAnnotatedConstructor {
        public OneNonAnnotatedConstructor(@Context
        String str) {
        }

        public OneNonAnnotatedConstructor(@Context
        Integer name, String str) {
        }
    }

    public static class MultiAnnotatedConstructor {
        public MultiAnnotatedConstructor(@Context
        Integer name, @PathParam("str")
        @Context
        String str) {
        }
    }

    public static class UnknownAnnotatedConstructor {
        public UnknownAnnotatedConstructor(@Context
        Integer name, @MatrixParam("matrix")
        String str) {
        }
    }
}

