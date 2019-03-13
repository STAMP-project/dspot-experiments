/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.mock.web;


import PageContext.APPLICATION_SCOPE;
import PageContext.PAGE_SCOPE;
import PageContext.REQUEST_SCOPE;
import PageContext.SESSION_SCOPE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the {@code MockPageContext} class.
 *
 * @author Rick Evans
 */
public class MockPageContextTests {
    private final String key = "foo";

    private final String value = "bar";

    private final MockPageContext ctx = new MockPageContext();

    @Test
    public void setAttributeWithNoScopeUsesPageScope() throws Exception {
        ctx.setAttribute(key, value);
        Assert.assertEquals(value, ctx.getAttribute(key, PAGE_SCOPE));
        Assert.assertNull(ctx.getAttribute(key, APPLICATION_SCOPE));
        Assert.assertNull(ctx.getAttribute(key, REQUEST_SCOPE));
        Assert.assertNull(ctx.getAttribute(key, SESSION_SCOPE));
    }

    @Test
    public void removeAttributeWithNoScopeSpecifiedRemovesValueFromAllScopes() throws Exception {
        ctx.setAttribute(key, value, APPLICATION_SCOPE);
        ctx.removeAttribute(key);
        Assert.assertNull(ctx.getAttribute(key, PAGE_SCOPE));
        Assert.assertNull(ctx.getAttribute(key, APPLICATION_SCOPE));
        Assert.assertNull(ctx.getAttribute(key, REQUEST_SCOPE));
        Assert.assertNull(ctx.getAttribute(key, SESSION_SCOPE));
    }
}

