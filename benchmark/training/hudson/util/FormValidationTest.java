/**
 * The MIT License
 *
 * Copyright (c) 2010, Seiji Sogabe
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
package hudson.util;


import FormValidation.Kind.ERROR;
import FormValidation.Kind.OK;
import FormValidation.Kind.WARNING;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author sogabe
 */
public class FormValidationTest {
    @Test
    public void testValidateRequired_OK() {
        FormValidation actual = FormValidation.validateRequired("Name");
        Assert.assertEquals(FormValidation.ok(), actual);
    }

    @Test
    public void testValidateRequired_Null() {
        FormValidation actual = FormValidation.validateRequired(null);
        Assert.assertNotNull(actual);
        Assert.assertEquals(ERROR, actual.kind);
    }

    @Test
    public void testValidateRequired_Empty() {
        FormValidation actual = FormValidation.validateRequired("  ");
        Assert.assertNotNull(actual);
        Assert.assertEquals(ERROR, actual.kind);
    }

    // @Issue("JENKINS-7438")
    @Test
    public void testMessage() {
        Assert.assertEquals("test msg", FormValidation.errorWithMarkup("test msg").getMessage());
    }

    @Test
    public void aggregateZeroValidations() {
        Assert.assertEquals(FormValidation.ok(), aggregate());
    }

    @Test
    public void aggregateSingleValidations() {
        FormValidation ok = FormValidation.ok();
        FormValidation warning = FormValidation.warning("");
        FormValidation error = FormValidation.error("");
        Assert.assertEquals(ok, aggregate(ok));
        Assert.assertEquals(warning, aggregate(warning));
        Assert.assertEquals(error, aggregate(error));
    }

    @Test
    public void aggregateSeveralValidations() {
        FormValidation ok = FormValidation.ok("ok_message");
        FormValidation warning = FormValidation.warning("warning_message");
        FormValidation error = FormValidation.error("error_message");
        final FormValidation ok_ok = aggregate(ok, ok);
        Assert.assertEquals(OK, ok_ok.kind);
        Assert.assertTrue(ok_ok.renderHtml().contains(ok.getMessage()));
        final FormValidation ok_warning = aggregate(ok, warning);
        Assert.assertEquals(WARNING, ok_warning.kind);
        Assert.assertTrue(ok_warning.renderHtml().contains(ok.getMessage()));
        Assert.assertTrue(ok_warning.renderHtml().contains(warning.getMessage()));
        final FormValidation ok_error = aggregate(ok, error);
        Assert.assertEquals(ERROR, ok_error.kind);
        Assert.assertTrue(ok_error.renderHtml().contains(ok.getMessage()));
        Assert.assertTrue(ok_error.renderHtml().contains(error.getMessage()));
        final FormValidation warning_error = aggregate(warning, error);
        Assert.assertEquals(ERROR, warning_error.kind);
        Assert.assertTrue(warning_error.renderHtml().contains(error.getMessage()));
        Assert.assertTrue(warning_error.renderHtml().contains(warning.getMessage()));
    }

    @Test
    public void formValidationException() {
        FormValidation fv = FormValidation.error(new Exception("<html"), "Message<html");
        Assert.assertThat(fv.renderHtml(), CoreMatchers.not(CoreMatchers.containsString("<html")));
    }
}

