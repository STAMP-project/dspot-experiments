/**
 * -
 * #%L
 * rapidoid-integration-tests
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
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
 * #L%
 */
package org.rapidoid.validation;


import javax.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.rapidoid.http.IsolatedIntegrationTest;
import org.rapidoid.jpa.JPA;
import org.rapidoid.setup.App;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.1.0")
public class HttpBeanValidationTest extends IsolatedIntegrationTest {
    @Test
    public void testValidation() {
        App.path("org.rapidoid.validation");
        App.bootstrap(new String[0]);
        JPA.bootstrap(App.path());
        onlyGet("/echo?num=123");
        onlyGet("/echo");
        onlyGet("/validating?num=123");
        onlyGet("/validating");
        onlyPost("/save?num=123");
        onlyPost("/save");
    }

    @Test
    public void testCustomValidation() {
        On.get("/invalid1").html((@Valid
        Bar bar) -> "ok");
        On.get("/invalid2").json((@Valid
        Bar bar) -> "ok");
        App.custom().validator(( req, bean) -> {
            throw U.rte("Invalid!");
        });
        onlyGet("/invalid1?err");
        onlyGet("/invalid2?err");
        App.custom().validator(null);
        My.validator(( req, bean) -> {
            throw new ValidationException("Validation failed!");
        });
        onlyGet("/invalid1?val");
        onlyGet("/invalid2?val");
        My.validator(null);
        App.custom().validator(( req, bean) -> {
            throw new InvalidData("Invalid data!");
        });
        onlyGet("/invalid1?inv");
        onlyGet("/invalid2?inv");
    }
}

