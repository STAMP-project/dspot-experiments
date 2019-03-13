/**
 * -
 * #%L
 * rapidoid-http-server
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
import org.rapidoid.buffer.Buf;
import org.rapidoid.buffer.BufGroup;
import org.rapidoid.net.impl.RapidoidHelper;
import org.rapidoid.test.TestCommons;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class HttpParserTest extends TestCommons {
    private static final String CRLF = "\r\n";

    private static String REQ1 = HttpParserTest.req("GET /foo/bar?a=5&b&n=%20 HTTP/1.1|Host:www.test.com|Set-Cookie: aaa=2||", HttpParserTest.CRLF);

    private static String REQ2 = HttpParserTest.req(("POST /something/else/here?x=abc%20de HTTP/STRANGE|Host:a.b.c.org|:ign|ored:|My-Header: same|My-Header: again|" + (HttpParserTest.body("a"))), HttpParserTest.CRLF);

    private static String REQ3 = HttpParserTest.req(("PUT /books HTTP/1.0|CoNNectioN: keep-alive | AAAAA: c = 2 |" + (HttpParserTest.body("ab"))), HttpParserTest.CRLF);

    private static String REQ4 = HttpParserTest.req(("DELETE /?a&bb=c&d MY-PROTOCOL|" + (HttpParserTest.body("abc"))), HttpParserTest.CRLF);

    private static String REQ5 = HttpParserTest.req(("ABCD ///??? HTTP/1.1|" + (HttpParserTest.body("abcd"))), HttpParserTest.CRLF);

    private static String REQ6 = HttpParserTest.req("GET /?x A||", HttpParserTest.CRLF);

    private static final String CONTENT_LENGTH = "CoNtEnT-LenGth";

    @Test
    public void shouldParseRequest1() {
        RapidoidHelper req = parse(HttpParserTest.REQ1);
        BufGroup bufs = new BufGroup(4);
        Buf reqbuf = bufs.from(HttpParserTest.REQ1, "r2");
        eq(HttpParserTest.REQ1, req.verb, "GET");
        eq(HttpParserTest.REQ1, req.path, "/foo/bar");
        eqs(HttpParserTest.REQ1, req.params, "a", "5", "b", "", "n", "%20");
        eq(req.params.toMap(reqbuf, true, true, false), U.map("a", "5", "b", "", "n", " "));
        eq(HttpParserTest.REQ1, req.protocol, "HTTP/1.1");
        eqs(HttpParserTest.REQ1, req.headersKV, "Host", "www.test.com", "Set-Cookie", "aaa=2");
        isNone(req.body);
    }

    @Test
    public void shouldParseRequest2() {
        RapidoidHelper req = parse(HttpParserTest.REQ2);
        eq(HttpParserTest.REQ2, req.verb, "POST");
        eq(HttpParserTest.REQ2, req.path, "/something/else/here");
        eqs(HttpParserTest.REQ2, req.params, "x", "abc%20de");
        eq(HttpParserTest.REQ2, req.protocol, "HTTP/STRANGE");
        eqs(HttpParserTest.REQ2, req.headersKV, "Host", "a.b.c.org", "", "ign", "ored", "", "My-Header", "same", "My-Header", "again", HttpParserTest.CONTENT_LENGTH, "5");
        eq(HttpParserTest.REQ2, req.query, "x=abc%20de");
        eq(HttpParserTest.REQ2, req.body, "BODYa");
    }

    @Test
    public void shouldParseRequest3() {
        RapidoidHelper req = parse(HttpParserTest.REQ3);
        eq(HttpParserTest.REQ3, req.verb, "PUT");
        eq(HttpParserTest.REQ3, req.path, "/books");
        eqs(HttpParserTest.REQ3, req.params);
        eq(HttpParserTest.REQ3, req.protocol, "HTTP/1.0");
        eqs(HttpParserTest.REQ3, req.headersKV, "CoNNectioN", "keep-alive", "AAAAA", "c = 2", HttpParserTest.CONTENT_LENGTH, "6");
        eq(HttpParserTest.REQ3, req.body, "BODYab");
    }

    @Test
    public void shouldParseRequest4() {
        RapidoidHelper req = parse(HttpParserTest.REQ4);
        eq(HttpParserTest.REQ4, req.verb, "DELETE");
        eq(HttpParserTest.REQ4, req.path, "/");
        eqs(HttpParserTest.REQ4, req.params, "a", "", "bb", "c", "d", "");
        eq(HttpParserTest.REQ4, req.protocol, "MY-PROTOCOL");
        eqs(HttpParserTest.REQ4, req.headersKV, HttpParserTest.CONTENT_LENGTH, "7");
        eq(HttpParserTest.REQ4, req.body, "BODYabc");
    }

    @Test
    public void shouldParseRequest5() {
        RapidoidHelper req = parse(HttpParserTest.REQ5);
        eq(HttpParserTest.REQ5, req.verb, "ABCD");
        eq(HttpParserTest.REQ5, req.path, "///");
        eqs(HttpParserTest.REQ5, req.params, "??", "");
        eq(req.params.toMap(HttpParserTest.REQ5), U.map("??", ""));
        eq(HttpParserTest.REQ5, req.protocol, "HTTP/1.1");
        eqs(HttpParserTest.REQ5, req.headersKV, HttpParserTest.CONTENT_LENGTH, "8");
        eq(HttpParserTest.REQ5, req.body, "BODYabcd");
    }

    @Test
    public void shouldParseRequest6() {
        RapidoidHelper req = parse(HttpParserTest.REQ6);
        eq(HttpParserTest.REQ6, req.verb, "GET");
        eq(HttpParserTest.REQ6, req.path, "/");
        eqs(HttpParserTest.REQ6, req.params, "x", "");
        eq(HttpParserTest.REQ6, req.protocol, "A");
        eqs(HttpParserTest.REQ6, req.headersKV);
        isNone(req.body);
    }
}

