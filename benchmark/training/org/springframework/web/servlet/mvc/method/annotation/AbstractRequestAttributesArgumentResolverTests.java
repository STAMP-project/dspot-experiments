/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.servlet.mvc.method.annotation;


import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;


/**
 * Base class for {@code @RequestAttribute} and {@code @SessionAttribute} method
 * method argument resolution tests.
 *
 * @author Rossen Stoyanchev
 * @since 4.3
 */
public abstract class AbstractRequestAttributesArgumentResolverTests {
    private ServletWebRequest webRequest;

    private HandlerMethodArgumentResolver resolver;

    private Method handleMethod;

    @Test
    public void supportsParameter() throws Exception {
        Assert.assertTrue(this.resolver.supportsParameter(new MethodParameter(this.handleMethod, 0)));
        Assert.assertFalse(this.resolver.supportsParameter(new MethodParameter(this.handleMethod, (-1))));
    }

    @Test
    public void resolve() throws Exception {
        MethodParameter param = initMethodParameter(0);
        try {
            testResolveArgument(param);
            Assert.fail("Should be required by default");
        } catch (ServletRequestBindingException ex) {
            Assert.assertTrue(ex.getMessage().startsWith("Missing "));
        }
        AbstractRequestAttributesArgumentResolverTests.Foo foo = new AbstractRequestAttributesArgumentResolverTests.Foo();
        this.webRequest.setAttribute("foo", foo, getScope());
        Assert.assertSame(foo, testResolveArgument(param));
    }

    @Test
    public void resolveWithName() throws Exception {
        MethodParameter param = initMethodParameter(1);
        AbstractRequestAttributesArgumentResolverTests.Foo foo = new AbstractRequestAttributesArgumentResolverTests.Foo();
        this.webRequest.setAttribute("specialFoo", foo, getScope());
        Assert.assertSame(foo, testResolveArgument(param));
    }

    @Test
    public void resolveNotRequired() throws Exception {
        MethodParameter param = initMethodParameter(2);
        Assert.assertNull(testResolveArgument(param));
        AbstractRequestAttributesArgumentResolverTests.Foo foo = new AbstractRequestAttributesArgumentResolverTests.Foo();
        this.webRequest.setAttribute("foo", foo, getScope());
        Assert.assertSame(foo, testResolveArgument(param));
    }

    @Test
    public void resolveOptional() throws Exception {
        WebDataBinder dataBinder = new WebRequestDataBinder(null);
        dataBinder.setConversionService(new DefaultConversionService());
        WebDataBinderFactory factory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(factory.createBinder(this.webRequest, null, "foo")).willReturn(dataBinder);
        MethodParameter param = initMethodParameter(3);
        Object actual = testResolveArgument(param, factory);
        Assert.assertNotNull(actual);
        Assert.assertEquals(Optional.class, actual.getClass());
        Assert.assertFalse(((Optional<?>) (actual)).isPresent());
        AbstractRequestAttributesArgumentResolverTests.Foo foo = new AbstractRequestAttributesArgumentResolverTests.Foo();
        this.webRequest.setAttribute("foo", foo, getScope());
        actual = testResolveArgument(param, factory);
        Assert.assertNotNull(actual);
        Assert.assertEquals(Optional.class, actual.getClass());
        Assert.assertTrue(((Optional<?>) (actual)).isPresent());
        Assert.assertSame(foo, ((Optional<?>) (actual)).get());
    }

    private static class Foo {}
}

