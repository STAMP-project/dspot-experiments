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
package org.springframework.web.method.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;


/**
 * Test fixture with {@link HandlerMethodArgumentResolverComposite}.
 *
 * @author Rossen Stoyanchev
 */
public class HandlerMethodArgumentResolverCompositeTests {
    private HandlerMethodArgumentResolverComposite resolverComposite;

    private MethodParameter paramInt;

    private MethodParameter paramStr;

    @Test
    public void supportsParameter() {
        this.resolverComposite.addResolver(new StubArgumentResolver(Integer.class));
        Assert.assertTrue(this.resolverComposite.supportsParameter(paramInt));
        Assert.assertFalse(this.resolverComposite.supportsParameter(paramStr));
    }

    @Test
    public void resolveArgument() throws Exception {
        this.resolverComposite.addResolver(new StubArgumentResolver(55));
        Object resolvedValue = this.resolverComposite.resolveArgument(paramInt, null, null, null);
        Assert.assertEquals(55, resolvedValue);
    }

    @Test
    public void checkArgumentResolverOrder() throws Exception {
        this.resolverComposite.addResolver(new StubArgumentResolver(1));
        this.resolverComposite.addResolver(new StubArgumentResolver(2));
        Object resolvedValue = this.resolverComposite.resolveArgument(paramInt, null, null, null);
        Assert.assertEquals("Didn't use the first registered resolver", 1, resolvedValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSuitableArgumentResolver() throws Exception {
        this.resolverComposite.resolveArgument(paramStr, null, null, null);
    }
}

