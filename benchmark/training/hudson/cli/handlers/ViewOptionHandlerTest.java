/**
 * The MIT License
 *
 * Copyright (c) 2013 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.cli.handlers;


import View.READ;
import hudson.model.View;
import hudson.model.ViewGroup;
import jenkins.model.Jenkins;
import org.acegisecurity.AccessDeniedException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.args4j.spi.Setter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@PrepareForTest(Jenkins.class)
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*" })
public class ViewOptionHandlerTest {
    @Mock
    private Setter<View> setter;

    private ViewOptionHandler handler;

    // Hierarchy of views used as a shared fixture:
    // $JENKINS_URL/view/outer/view/nested/view/inner/
    @Mock
    private View inner;

    @Mock
    private ViewOptionHandlerTest.CompositeView nested;

    @Mock
    private ViewOptionHandlerTest.CompositeView outer;

    @Mock
    private Jenkins jenkins;

    @Test
    public void resolveTopLevelView() throws Exception {
        parse("outer");
        Mockito.verify(setter).addValue(outer);
    }

    @Test
    public void resolveNestedView() throws Exception {
        parse("outer/nested");
        Mockito.verify(setter).addValue(nested);
    }

    @Test
    public void resolveOuterView() throws Exception {
        parse("outer/nested/inner");
        Mockito.verify(setter).addValue(inner);
    }

    @Test
    public void ignoreLeadingAndTrailingSlashes() throws Exception {
        parse("/outer/nested/inner/");
        Mockito.verify(setter).addValue(inner);
    }

    @Test
    public void reportNonexistentTopLevelView() throws Exception {
        Assert.assertEquals("No view named missing_view inside view Jenkins", parseFailedWith(IllegalArgumentException.class, "missing_view"));
        Mockito.verifyZeroInteractions(setter);
    }

    @Test
    public void reportNonexistentNestedView() throws Exception {
        Assert.assertEquals("No view named missing_view inside view outer", parseFailedWith(IllegalArgumentException.class, "outer/missing_view"));
        Mockito.verifyZeroInteractions(setter);
    }

    @Test
    public void reportNonexistentInnerView() throws Exception {
        Assert.assertEquals("No view named missing_view inside view nested", parseFailedWith(IllegalArgumentException.class, "outer/nested/missing_view"));
        Mockito.verifyZeroInteractions(setter);
    }

    @Test
    public void reportTraversingViewThatIsNotAViewGroup() throws Exception {
        Assert.assertEquals("inner view can not contain views", parseFailedWith(IllegalStateException.class, "outer/nested/inner/missing"));
        Mockito.verifyZeroInteractions(setter);
    }

    @Test
    public void reportEmptyViewNameRequestAsNull() throws Exception {
        Assert.assertEquals(handler.getView(""), null);
        Mockito.verifyZeroInteractions(setter);
    }

    @Test
    public void reportViewSpaceNameRequestAsIAE() throws Exception {
        try {
            Assert.assertEquals(handler.getView(" "), null);
            Assert.fail("No exception thrown. Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("No view named   inside view Jenkins", e.getMessage());
            Mockito.verifyZeroInteractions(setter);
        }
    }

    @Test
    public void reportNullViewAsNPE() throws Exception {
        try {
            handler.getView(null);
            Assert.fail("No exception thrown. Expected NullPointerException");
        } catch (NullPointerException e) {
            Mockito.verifyZeroInteractions(setter);
        }
    }

    @Test
    public void refuseToReadOuterView() throws Exception {
        denyAccessOn(outer);
        Assert.assertEquals("Access denied for: outer", parseFailedWith(AccessDeniedException.class, "outer/nested/inner"));
        Mockito.verify(outer).checkPermission(READ);
        Mockito.verifyZeroInteractions(nested);
        Mockito.verifyZeroInteractions(inner);
        Mockito.verifyZeroInteractions(setter);
    }

    @Test
    public void refuseToReadNestedView() throws Exception {
        denyAccessOn(nested);
        Assert.assertEquals("Access denied for: nested", parseFailedWith(AccessDeniedException.class, "outer/nested/inner"));
        Mockito.verify(nested).checkPermission(READ);
        Mockito.verifyZeroInteractions(inner);
        Mockito.verifyZeroInteractions(setter);
    }

    @Test
    public void refuseToReadInnerView() throws Exception {
        denyAccessOn(inner);
        Assert.assertEquals("Access denied for: inner", parseFailedWith(AccessDeniedException.class, "outer/nested/inner"));
        Mockito.verify(inner).checkPermission(READ);
        Mockito.verifyZeroInteractions(setter);
    }

    private abstract static class CompositeView extends View implements ViewGroup {
        protected CompositeView(String name) {
            super(name);
        }
    }
}

