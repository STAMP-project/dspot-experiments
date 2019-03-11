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
package org.rapidoid.http;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.setup.My;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.2.0")
public class HttpWrappersTest extends IsolatedIntegrationTest {
    @Test
    public void testWrappers() {
        HttpWrapper hey = HttpWrappersTest.wrapper("hey");
        My.wrappers(hey);
        org.rapidoid.setup.App.defaults().wrappers(HttpWrappersTest.wrapper("on-def"));
        On.get("/def").plain("D");
        org.rapidoid.setup.App.defaults().wrappers(((HttpWrapper[]) (null)));// reset the default wrappers

        HttpWrapper[] wrappers = org.rapidoid.setup.App.custom().wrappers();
        eq(U.array(hey), wrappers);
        On.get("/").wrappers(HttpWrappersTest.wrapper("index")).plain("home");
        On.post("/x").wrappers(HttpWrappersTest.wrapper("x"), HttpWrappersTest.wrapper("x2")).json("X");
        On.get("/y").html("YYY");
        org.rapidoid.setup.App.custom().wrappers(HttpWrappersTest.wrapper("on"));
        onlyGet("/");
        onlyPost("/x");
        onlyGet("/y");
        onlyGet("/def");
    }

    @Test
    public void testDefaultWrappers() {
        My.wrappers(HttpWrappersTest.wrapper("def"));
        On.post("/z").plain("Zzz");
        onlyPost("/z");
    }

    @Test
    public void shouldTransformRespResult() {
        My.wrappers(HttpWrappersTest.wrapper("wrap1"), HttpWrappersTest.wrapper("wrap2"));
        On.get("/x").plain("X");
        On.get("/req").serve(( req) -> {
            req.response().plain("FOO");
            return req;
        });
        On.get("/resp").serve((Resp resp) -> resp.plain("BAR"));
        On.get("/json").json(() -> 123);
        On.get("/html").html(( req) -> "<p>hello</p>");
        On.get("/null").json(( req) -> null);
        onlyGet("/x");
        onlyGet("/req");
        onlyGet("/resp");
        onlyGet("/json");
        onlyGet("/html");
        onlyGet("/null");
    }

    @Test
    public void shouldThrowErrorsWithNonCatchingWrappers() {
        My.wrappers(HttpWrappersTest.wrapper("wrap1"), HttpWrappersTest.wrapper("wrap2"));
        On.get("/err").plain(() -> {
            throw U.rte("Intentional error!");
        });
        onlyGet("/err");
    }

    @Test
    public void shouldTransformErrorsWithCatchingWrappers() {
        My.wrappers(HttpWrappersTest.wrapper("wrap1"), HttpWrappersTest.catchingWrapper("wrap2"));
        On.get("/err").plain(() -> {
            throw U.rte("Intentional error!");
        });
        onlyGet("/err");
    }
}

