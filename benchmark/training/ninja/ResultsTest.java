/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;


import Result.APPLICATION_JSON;
import Result.APPLICATION_XML;
import Result.LOCATION;
import Result.SC_200_OK;
import Result.SC_204_NO_CONTENT;
import Result.SC_300_MULTIPLE_CHOICES;
import Result.SC_303_SEE_OTHER;
import Result.SC_307_TEMPORARY_REDIRECT;
import Result.SC_400_BAD_REQUEST;
import Result.SC_403_FORBIDDEN;
import Result.SC_404_NOT_FOUND;
import Result.SC_500_INTERNAL_SERVER_ERROR;
import Result.SC_501_NOT_IMPLEMENTED;
import Result.TEXT_HTML;
import ninja.utils.NoHttpBody;
import org.junit.Assert;
import org.junit.Test;


public class ResultsTest {
    @Test
    public void testResultsStatus() {
        Result result = Results.status(200);
        Assert.assertEquals(200, result.getStatusCode());
    }

    @Test
    public void testResultsOk() {
        Result result = Results.ok();
        Assert.assertEquals(200, result.getStatusCode());
    }

    @Test
    public void testResultsNotFound() {
        Result result = Results.notFound();
        Assert.assertEquals(SC_404_NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testResultsForbidden() {
        Result result = Results.forbidden();
        Assert.assertEquals(SC_403_FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testResultsBadRequest() {
        Result result = Results.badRequest();
        Assert.assertEquals(SC_400_BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void testResultsNoContent() {
        Result result = Results.noContent();
        Assert.assertEquals(SC_204_NO_CONTENT, result.getStatusCode());
        Assert.assertTrue(((result.getRenderable()) instanceof NoHttpBody));
    }

    @Test
    public void testResultsInternalServerError() {
        Result result = Results.internalServerError();
        Assert.assertEquals(SC_500_INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testResultsRedirect() {
        Result result = Results.redirect("http://example.com");
        Assert.assertEquals(SC_303_SEE_OTHER, result.getStatusCode());
        Assert.assertEquals("http://example.com", result.getHeaders().get(LOCATION));
        Assert.assertTrue(((result.getRenderable()) instanceof NoHttpBody));
    }

    @Test
    public void testResultsRedirectTemporary() {
        Result result = Results.redirectTemporary("http://example.com");
        Assert.assertEquals(SC_307_TEMPORARY_REDIRECT, result.getStatusCode());
        Assert.assertEquals("http://example.com", result.getHeaders().get(LOCATION));
        Assert.assertTrue(((result.getRenderable()) instanceof NoHttpBody));
    }

    @Test
    public void testResultsContentType() {
        Result result = Results.contentType("text/my-cool-content-type");
        Assert.assertEquals(SC_200_OK, result.getStatusCode());
        Assert.assertEquals("text/my-cool-content-type", result.getContentType());
    }

    @Test
    public void testResultsHtml() {
        Result result = Results.html();
        Assert.assertEquals(SC_200_OK, result.getStatusCode());
        Assert.assertEquals(TEXT_HTML, result.getContentType());
    }

    @Test
    public void testResultsHtmlWithStatusCode() {
        Result result = Results.html().status(SC_300_MULTIPLE_CHOICES);
        Assert.assertEquals(SC_300_MULTIPLE_CHOICES, result.getStatusCode());
        Assert.assertEquals(TEXT_HTML, result.getContentType());
    }

    @Test
    public void testResultsJson() {
        Result result = Results.json();
        Assert.assertEquals(SC_200_OK, result.getStatusCode());
        Assert.assertEquals(APPLICATION_JSON, result.getContentType());
    }

    @Test
    public void testResultsJsonWithObjectToRender() {
        ResultsTest.TestObject testObject = new ResultsTest.TestObject();
        Result result = Results.json().render(testObject);
        Assert.assertEquals(SC_200_OK, result.getStatusCode());
        Assert.assertEquals(APPLICATION_JSON, result.getContentType());
        Assert.assertEquals(testObject, result.getRenderable());
    }

    @Test
    public void testResultsXml() {
        Result result = Results.xml();
        Assert.assertEquals(SC_200_OK, result.getStatusCode());
        Assert.assertEquals(APPLICATION_XML, result.getContentType());
    }

    @Test
    public void testResultsTODO() {
        Result result = Results.TODO();
        Assert.assertEquals(SC_501_NOT_IMPLEMENTED, result.getStatusCode());
        Assert.assertEquals(APPLICATION_JSON, result.getContentType());
    }

    @Test
    public void testResultsAsync() {
        Result result = Results.async();
        Assert.assertTrue((result instanceof AsyncResult));
    }

    /**
     * Simple helper to test if objects get copied to result.
     */
    public class TestObject {}
}

