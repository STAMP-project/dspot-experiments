/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.tests;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.teavm.diagnostics.Problem;
import org.teavm.model.MethodReference;


public class JSOTest {
    @Test
    public void reportsAboutWrongParameterOfJSBody() {
        Problem foundProblem = build("callJSBodyWithWrongParameter").stream().filter(( problem) -> {
            return (problem.getLocation().getMethod().getName().equals("callJSBodyWithWrongParameter")) && (problem.getText().equals(("Method {{m0}} is not a proper native JavaScript method " + "declaration: its 1th parameter has wrong type")));
        }).findAny().orElse(null);
        Assert.assertNotNull(foundProblem);
        Object[] params = foundProblem.getParams();
        Assert.assertThat(params[0], CoreMatchers.is(new MethodReference(JSOTest.class, "jsBodyWithWrongParameter", Object.class, void.class)));
    }

    @Test
    public void reportsAboutWrongNonStaticJSBody() {
        Problem foundProblem = build("callWrongNonStaticJSBody").stream().filter(( problem) -> {
            return (problem.getLocation().getMethod().getName().equals("callWrongNonStaticJSBody")) && (problem.getText().equals(("Method {{m0}} is not a proper native JavaScript method " + "declaration. It is non-static and declared on a non-overlay class {{c1}}")));
        }).findAny().orElse(null);
        Assert.assertNotNull(foundProblem);
        Object[] params = foundProblem.getParams();
        Assert.assertThat(params[0], CoreMatchers.is(new MethodReference(JSOTest.class, "wrongNonStaticJSBody", void.class)));
        Assert.assertThat(params[1], CoreMatchers.is(JSOTest.class.getName()));
    }

    @Test
    public void reportsAboutJSBodyWithWrongReturningType() {
        Problem foundProblem = build("callJSBodyWithWrongReturningType").stream().filter(( problem) -> {
            return (problem.getLocation().getMethod().getName().equals("callJSBodyWithWrongReturningType")) && (problem.getText().equals(("Method {{m0}} is not a proper native JavaScript method " + "declaration, since it returns wrong type")));
        }).findAny().orElse(null);
        Assert.assertNotNull(foundProblem);
        Object[] params = foundProblem.getParams();
        Assert.assertThat(params[0], CoreMatchers.is(new MethodReference(JSOTest.class, "jsBodyWithWrongReturningType", String.class, Object.class)));
    }
}

