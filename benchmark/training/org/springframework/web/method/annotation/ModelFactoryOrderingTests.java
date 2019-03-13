/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.method.annotation;


import ReflectionUtils.MethodFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Unit tests verifying {@code @ModelAttribute} method inter-dependencies.
 *
 * @author Rossen Stoyanchev
 */
public class ModelFactoryOrderingTests {
    private static final Log logger = LogFactory.getLog(ModelFactoryOrderingTests.class);

    private NativeWebRequest webRequest;

    private ModelAndViewContainer mavContainer;

    private SessionAttributeStore sessionAttributeStore;

    @Test
    public void straightLineDependency() throws Exception {
        runTest(new ModelFactoryOrderingTests.StraightLineDependencyController());
        assertInvokedBefore("getA", "getB1", "getB2", "getC1", "getC2", "getC3", "getC4");
        assertInvokedBefore("getB1", "getB2", "getC1", "getC2", "getC3", "getC4");
        assertInvokedBefore("getB2", "getC1", "getC2", "getC3", "getC4");
        assertInvokedBefore("getC1", "getC2", "getC3", "getC4");
        assertInvokedBefore("getC2", "getC3", "getC4");
        assertInvokedBefore("getC3", "getC4");
    }

    @Test
    public void treeDependency() throws Exception {
        runTest(new ModelFactoryOrderingTests.TreeDependencyController());
        assertInvokedBefore("getA", "getB1", "getB2", "getC1", "getC2", "getC3", "getC4");
        assertInvokedBefore("getB1", "getC1", "getC2");
        assertInvokedBefore("getB2", "getC3", "getC4");
    }

    @Test
    public void InvertedTreeDependency() throws Exception {
        runTest(new ModelFactoryOrderingTests.InvertedTreeDependencyController());
        assertInvokedBefore("getC1", "getA", "getB1");
        assertInvokedBefore("getC2", "getA", "getB1");
        assertInvokedBefore("getC3", "getA", "getB2");
        assertInvokedBefore("getC4", "getA", "getB2");
        assertInvokedBefore("getB1", "getA");
        assertInvokedBefore("getB2", "getA");
    }

    @Test
    public void unresolvedDependency() throws Exception {
        runTest(new ModelFactoryOrderingTests.UnresolvedDependencyController());
        assertInvokedBefore("getA", "getC1", "getC2", "getC3", "getC4");
        // No other order guarantees for methods with unresolvable dependencies (and methods that depend on them),
        // Required dependencies will be created via default constructor.
    }

    private static class AbstractController {
        @RequestMapping
        public void handle() {
        }

        @SuppressWarnings("unchecked")
        <T> T updateAndReturn(Model model, String methodName, T returnValue) throws IOException {
            ((List<String>) (model.asMap().get("methods"))).add(methodName);
            return returnValue;
        }
    }

    private static class StraightLineDependencyController extends ModelFactoryOrderingTests.AbstractController {
        @ModelAttribute
        public ModelFactoryOrderingTests.A getA(Model model) throws IOException {
            return updateAndReturn(model, "getA", new ModelFactoryOrderingTests.A());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.B1 getB1(@ModelAttribute
        ModelFactoryOrderingTests.A a, Model model) throws IOException {
            return updateAndReturn(model, "getB1", new ModelFactoryOrderingTests.B1());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.B2 getB2(@ModelAttribute
        ModelFactoryOrderingTests.B1 b1, Model model) throws IOException {
            return updateAndReturn(model, "getB2", new ModelFactoryOrderingTests.B2());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C1 getC1(@ModelAttribute
        ModelFactoryOrderingTests.B2 b2, Model model) throws IOException {
            return updateAndReturn(model, "getC1", new ModelFactoryOrderingTests.C1());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C2 getC2(@ModelAttribute
        ModelFactoryOrderingTests.C1 c1, Model model) throws IOException {
            return updateAndReturn(model, "getC2", new ModelFactoryOrderingTests.C2());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C3 getC3(@ModelAttribute
        ModelFactoryOrderingTests.C2 c2, Model model) throws IOException {
            return updateAndReturn(model, "getC3", new ModelFactoryOrderingTests.C3());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C4 getC4(@ModelAttribute
        ModelFactoryOrderingTests.C3 c3, Model model) throws IOException {
            return updateAndReturn(model, "getC4", new ModelFactoryOrderingTests.C4());
        }
    }

    private static class TreeDependencyController extends ModelFactoryOrderingTests.AbstractController {
        @ModelAttribute
        public ModelFactoryOrderingTests.A getA(Model model) throws IOException {
            return updateAndReturn(model, "getA", new ModelFactoryOrderingTests.A());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.B1 getB1(@ModelAttribute
        ModelFactoryOrderingTests.A a, Model model) throws IOException {
            return updateAndReturn(model, "getB1", new ModelFactoryOrderingTests.B1());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.B2 getB2(@ModelAttribute
        ModelFactoryOrderingTests.A a, Model model) throws IOException {
            return updateAndReturn(model, "getB2", new ModelFactoryOrderingTests.B2());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C1 getC1(@ModelAttribute
        ModelFactoryOrderingTests.B1 b1, Model model) throws IOException {
            return updateAndReturn(model, "getC1", new ModelFactoryOrderingTests.C1());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C2 getC2(@ModelAttribute
        ModelFactoryOrderingTests.B1 b1, Model model) throws IOException {
            return updateAndReturn(model, "getC2", new ModelFactoryOrderingTests.C2());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C3 getC3(@ModelAttribute
        ModelFactoryOrderingTests.B2 b2, Model model) throws IOException {
            return updateAndReturn(model, "getC3", new ModelFactoryOrderingTests.C3());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C4 getC4(@ModelAttribute
        ModelFactoryOrderingTests.B2 b2, Model model) throws IOException {
            return updateAndReturn(model, "getC4", new ModelFactoryOrderingTests.C4());
        }
    }

    private static class InvertedTreeDependencyController extends ModelFactoryOrderingTests.AbstractController {
        @ModelAttribute
        public ModelFactoryOrderingTests.C1 getC1(Model model) throws IOException {
            return updateAndReturn(model, "getC1", new ModelFactoryOrderingTests.C1());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C2 getC2(Model model) throws IOException {
            return updateAndReturn(model, "getC2", new ModelFactoryOrderingTests.C2());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C3 getC3(Model model) throws IOException {
            return updateAndReturn(model, "getC3", new ModelFactoryOrderingTests.C3());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C4 getC4(Model model) throws IOException {
            return updateAndReturn(model, "getC4", new ModelFactoryOrderingTests.C4());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.B1 getB1(@ModelAttribute
        ModelFactoryOrderingTests.C1 c1, @ModelAttribute
        ModelFactoryOrderingTests.C2 c2, Model model) throws IOException {
            return updateAndReturn(model, "getB1", new ModelFactoryOrderingTests.B1());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.B2 getB2(@ModelAttribute
        ModelFactoryOrderingTests.C3 c3, @ModelAttribute
        ModelFactoryOrderingTests.C4 c4, Model model) throws IOException {
            return updateAndReturn(model, "getB2", new ModelFactoryOrderingTests.B2());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.A getA(@ModelAttribute
        ModelFactoryOrderingTests.B1 b1, @ModelAttribute
        ModelFactoryOrderingTests.B2 b2, Model model) throws IOException {
            return updateAndReturn(model, "getA", new ModelFactoryOrderingTests.A());
        }
    }

    private static class UnresolvedDependencyController extends ModelFactoryOrderingTests.AbstractController {
        @ModelAttribute
        public ModelFactoryOrderingTests.A getA(Model model) throws IOException {
            return updateAndReturn(model, "getA", new ModelFactoryOrderingTests.A());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C1 getC1(@ModelAttribute
        ModelFactoryOrderingTests.B1 b1, Model model) throws IOException {
            return updateAndReturn(model, "getC1", new ModelFactoryOrderingTests.C1());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C2 getC2(@ModelAttribute
        ModelFactoryOrderingTests.B1 b1, Model model) throws IOException {
            return updateAndReturn(model, "getC2", new ModelFactoryOrderingTests.C2());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C3 getC3(@ModelAttribute
        ModelFactoryOrderingTests.B2 b2, Model model) throws IOException {
            return updateAndReturn(model, "getC3", new ModelFactoryOrderingTests.C3());
        }

        @ModelAttribute
        public ModelFactoryOrderingTests.C4 getC4(@ModelAttribute
        ModelFactoryOrderingTests.B2 b2, Model model) throws IOException {
            return updateAndReturn(model, "getC4", new ModelFactoryOrderingTests.C4());
        }
    }

    private static class A {}

    private static class B1 {}

    private static class B2 {}

    private static class C1 {}

    private static class C2 {}

    private static class C3 {}

    private static class C4 {}

    private static final MethodFilter METHOD_FILTER = new ReflectionUtils.MethodFilter() {
        @Override
        public boolean matches(Method method) {
            return ((AnnotationUtils.findAnnotation(method, RequestMapping.class)) == null) && ((AnnotationUtils.findAnnotation(method, ModelAttribute.class)) != null);
        }
    };
}

