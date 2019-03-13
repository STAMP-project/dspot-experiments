/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.web.util;


import PageContext.APPLICATION_SCOPE;
import PageContext.PAGE_SCOPE;
import PageContext.REQUEST_SCOPE;
import PageContext.SESSION_SCOPE;
import TagUtils.SCOPE_APPLICATION;
import TagUtils.SCOPE_PAGE;
import TagUtils.SCOPE_REQUEST;
import TagUtils.SCOPE_SESSION;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the {@link TagUtils} class.
 *
 * @author Alef Arendsen
 * @author Rick Evans
 */
public class TagUtilsTests {
    @Test
    public void getScopeSunnyDay() {
        Assert.assertEquals("page", SCOPE_PAGE);
        Assert.assertEquals("application", SCOPE_APPLICATION);
        Assert.assertEquals("session", SCOPE_SESSION);
        Assert.assertEquals("request", SCOPE_REQUEST);
        Assert.assertEquals(PAGE_SCOPE, TagUtils.getScope("page"));
        Assert.assertEquals(REQUEST_SCOPE, TagUtils.getScope("request"));
        Assert.assertEquals(SESSION_SCOPE, TagUtils.getScope("session"));
        Assert.assertEquals(APPLICATION_SCOPE, TagUtils.getScope("application"));
        // non-existent scope
        Assert.assertEquals(("TagUtils.getScope(..) with a non-existent scope argument must " + "just return the default scope (PageContext.PAGE_SCOPE)."), PAGE_SCOPE, TagUtils.getScope("bla"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getScopeWithNullScopeArgument() {
        TagUtils.getScope(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasAncestorOfTypeWhereAncestorTagIsNotATagType() throws Exception {
        Assert.assertFalse(TagUtils.hasAncestorOfType(new TagSupport(), String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasAncestorOfTypeWithNullTagArgument() throws Exception {
        Assert.assertFalse(TagUtils.hasAncestorOfType(null, TagSupport.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void hasAncestorOfTypeWithNullAncestorTagClassArgument() throws Exception {
        Assert.assertFalse(TagUtils.hasAncestorOfType(new TagSupport(), null));
    }

    @Test
    public void hasAncestorOfTypeTrueScenario() throws Exception {
        Tag a = new TagUtilsTests.TagA();
        Tag b = new TagUtilsTests.TagB();
        Tag c = new TagUtilsTests.TagC();
        a.setParent(b);
        b.setParent(c);
        Assert.assertTrue(TagUtils.hasAncestorOfType(a, TagUtilsTests.TagC.class));
    }

    @Test
    public void hasAncestorOfTypeFalseScenario() throws Exception {
        Tag a = new TagUtilsTests.TagA();
        Tag b = new TagUtilsTests.TagB();
        Tag anotherB = new TagUtilsTests.TagB();
        a.setParent(b);
        b.setParent(anotherB);
        Assert.assertFalse(TagUtils.hasAncestorOfType(a, TagUtilsTests.TagC.class));
    }

    @Test
    public void hasAncestorOfTypeWhenTagHasNoParent() throws Exception {
        Assert.assertFalse(TagUtils.hasAncestorOfType(new TagUtilsTests.TagA(), TagUtilsTests.TagC.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertHasAncestorOfTypeWithNullTagName() throws Exception {
        TagUtils.assertHasAncestorOfType(new TagUtilsTests.TagA(), TagUtilsTests.TagC.class, null, "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertHasAncestorOfTypeWithNullAncestorTagName() throws Exception {
        TagUtils.assertHasAncestorOfType(new TagUtilsTests.TagA(), TagUtilsTests.TagC.class, "a", null);
    }

    @Test(expected = IllegalStateException.class)
    public void assertHasAncestorOfTypeThrowsExceptionOnFail() throws Exception {
        Tag a = new TagUtilsTests.TagA();
        Tag b = new TagUtilsTests.TagB();
        Tag anotherB = new TagUtilsTests.TagB();
        a.setParent(b);
        b.setParent(anotherB);
        TagUtils.assertHasAncestorOfType(a, TagUtilsTests.TagC.class, "a", "c");
    }

    @Test
    public void testAssertHasAncestorOfTypeDoesNotThrowExceptionOnPass() throws Exception {
        Tag a = new TagUtilsTests.TagA();
        Tag b = new TagUtilsTests.TagB();
        Tag c = new TagUtilsTests.TagC();
        a.setParent(b);
        b.setParent(c);
        TagUtils.assertHasAncestorOfType(a, TagUtilsTests.TagC.class, "a", "c");
    }

    @SuppressWarnings("serial")
    private static final class TagA extends TagSupport {}

    @SuppressWarnings("serial")
    private static final class TagB extends TagSupport {}

    @SuppressWarnings("serial")
    private static final class TagC extends TagSupport {}
}

