/**
 * -
 * #%L
 * rapidoid-web
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
import org.rapidoid.setup.On;
import org.rapidoid.util.Msc;


@Authors("Nikolche Mihajlovski")
@Since("5.2.5")
public class HttpCookiesTest extends HttpTestCommons {
    private static final int THREADS = Msc.normalOrHeavy(5, 100);

    private static final int ROUNDS = Msc.normalOrHeavy(10, 1000);

    @Test
    public void testHttpCookies() {
        On.req(((ReqRespHandler) (( req, resp) -> {
            resp.cookie(req.uri(), ("" + (req.cookies().size())));
            return resp.json(req.cookies());
        })));
        multiThreaded(HttpCookiesTest.THREADS, HttpCookiesTest.ROUNDS, this::checkCookies);
    }

    @Test
    public void testNoCookies() {
        On.req(((ReqRespHandler) (( req, resp) -> {
            isTrue(req.cookies().isEmpty());
            return req.cookies().size();
        })));
        multiThreaded(HttpCookiesTest.THREADS, HttpCookiesTest.ROUNDS, () -> {
            checkNoCookies(true);
            checkNoCookies(false);
        });
    }
}

