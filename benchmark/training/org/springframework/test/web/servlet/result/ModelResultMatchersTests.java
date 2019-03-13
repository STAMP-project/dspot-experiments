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
package org.springframework.test.web.servlet.result;


import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;


/**
 * Unit tests for
 * {@link org.springframework.test.web.servlet.result.ModelResultMatchers}.
 *
 * @author Craig Walls
 */
public class ModelResultMatchersTests {
    private ModelResultMatchers matchers;

    private MvcResult mvcResult;

    private MvcResult mvcResultWithError;

    @Test
    public void attributeExists() throws Exception {
        this.matchers.attributeExists("good").match(this.mvcResult);
    }

    @Test(expected = AssertionError.class)
    public void attributeExists_doesNotExist() throws Exception {
        this.matchers.attributeExists("bad").match(this.mvcResult);
    }

    @Test
    public void attributeDoesNotExist() throws Exception {
        this.matchers.attributeDoesNotExist("bad").match(this.mvcResult);
    }

    @Test(expected = AssertionError.class)
    public void attributeDoesNotExist_doesExist() throws Exception {
        this.matchers.attributeDoesNotExist("good").match(this.mvcResultWithError);
    }

    @Test
    public void attribute_equal() throws Exception {
        this.matchers.attribute("good", is("good")).match(this.mvcResult);
    }

    @Test(expected = AssertionError.class)
    public void attribute_notEqual() throws Exception {
        this.matchers.attribute("good", is("bad")).match(this.mvcResult);
    }

    @Test
    public void hasNoErrors() throws Exception {
        this.matchers.hasNoErrors().match(this.mvcResult);
    }

    @Test(expected = AssertionError.class)
    public void hasNoErrors_withErrors() throws Exception {
        this.matchers.hasNoErrors().match(this.mvcResultWithError);
    }

    @Test
    public void attributeHasErrors() throws Exception {
        this.matchers.attributeHasErrors("date").match(this.mvcResultWithError);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasErrors_withoutErrors() throws Exception {
        this.matchers.attributeHasErrors("good").match(this.mvcResultWithError);
    }

    @Test
    public void attributeHasNoErrors() throws Exception {
        this.matchers.attributeHasNoErrors("good").match(this.mvcResult);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasNoErrors_withoutAttribute() throws Exception {
        this.matchers.attributeHasNoErrors("missing").match(this.mvcResultWithError);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasNoErrors_withErrors() throws Exception {
        this.matchers.attributeHasNoErrors("date").match(this.mvcResultWithError);
    }

    @Test
    public void attributeHasFieldErrors() throws Exception {
        this.matchers.attributeHasFieldErrors("date", "time").match(this.mvcResultWithError);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasFieldErrors_withoutAttribute() throws Exception {
        this.matchers.attributeHasFieldErrors("missing", "bad").match(this.mvcResult);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasFieldErrors_withoutErrorsForAttribute() throws Exception {
        this.matchers.attributeHasFieldErrors("date", "time").match(this.mvcResult);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasFieldErrors_withoutErrorsForField() throws Exception {
        this.matchers.attributeHasFieldErrors("date", "good", "time").match(this.mvcResultWithError);
    }

    @Test
    public void attributeHasFieldErrorCode() throws Exception {
        this.matchers.attributeHasFieldErrorCode("date", "time", "error").match(this.mvcResultWithError);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasFieldErrorCode_withoutErrorOnField() throws Exception {
        this.matchers.attributeHasFieldErrorCode("date", "time", "incorrectError").match(this.mvcResultWithError);
    }

    @Test
    public void attributeHasFieldErrorCode_startsWith() throws Exception {
        this.matchers.attributeHasFieldErrorCode("date", "time", startsWith("err")).match(this.mvcResultWithError);
    }

    @Test(expected = AssertionError.class)
    public void attributeHasFieldErrorCode_startsWith_withoutErrorOnField() throws Exception {
        this.matchers.attributeHasFieldErrorCode("date", "time", startsWith("inc")).match(this.mvcResultWithError);
    }
}

