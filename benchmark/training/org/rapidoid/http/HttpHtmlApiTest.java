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


import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.io.Res;
import org.rapidoid.job.Jobs;
import org.rapidoid.setup.On;
import org.rapidoid.u.U;
import org.rapidoid.util.Bufs;


@Authors("Nikolche Mihajlovski")
@Since("5.0.11")
public class HttpHtmlApiTest extends IsolatedIntegrationTest {
    @Test
    public void test01() {
        On.get("/test01").html(((ReqRespHandler) (( req, resp) -> ((req.getClass().getSimpleName()) + ":") + (resp.getClass().getSimpleName()))));
        onlyGet("/test01");
    }

    @Test
    public void test02() {
        On.get("/test02").html(((ReqRespHandler) (( req, resp) -> resp.plain("RESULT 2"))));
        onlyGet("/test02");
    }

    @Test
    public void test03() {
        On.get("/test03").html(((ReqRespHandler) (( req, resp) -> resp.binary("RESULT 3"))));
        onlyGet("/test03");
    }

    @Test
    public void test04() {
        On.get("/test04").html(((ReqRespHandler) (( req, resp) -> resp.html("RESULT 4"))));
        onlyGet("/test04");
    }

    @Test
    public void test05() {
        On.get("/test05").html(((ReqRespHandler) (( req, resp) -> resp.json("RESULT 5"))));
        onlyGet("/test05");
    }

    @Test
    public void test06() {
        On.get("/test06").html(((ReqRespHandler) (( req, resp) -> {
            resp.contentType(MediaType.APPLICATION_X_FONT);
            return "RESULT 6";
        })));
        onlyGet("/test06");
    }

    @Test
    public void test07() {
        On.get("/test07").html(((ReqRespHandler) (( req, resp) -> new Date(1234567890))));
        onlyGet("/test07");
    }

    @Test
    public void test08() {
        On.get("/test08").html(((ReqRespHandler) (( req, resp) -> U.map("x", 123.456, "f", false, "msg", "RESULT 8"))));
        onlyGet("/test08");
    }

    @Test
    public void test09() {
        On.get("/test09").html(((ReqRespHandler) (( req, resp) -> U.list(1, "asd", true, U.set("1", "2")))));
        onlyGet("/test09");
    }

    @Test
    public void test10() {
        On.get("/test10").html(((ReqRespHandler) (( req, resp) -> null)));
        onlyGet("/test10");
    }

    @Test
    public void test11() {
        On.get("/test11").html(((ReqRespHandler) (( req, resp) -> false)));
        onlyGet("/test11");
    }

    @Test
    public void test12() {
        On.get("/test12").html(((ReqRespHandler) (( req, resp) -> true)));
        onlyGet("/test12");
    }

    @Test
    public void test13() {
        On.get("/test13").html(((ReqRespHandler) (( req, resp) -> req)));
        onlyGet("/test13");
    }

    @Test
    public void test14() {
        On.get("/test14").html(((ReqRespHandler) (( req, resp) -> resp)));
        onlyGet("/test14");
    }

    @Test
    public void test15() {
        On.get("/test15").html(((ReqRespHandler) (( req, resp) -> "some bytes".getBytes())));
        onlyGet("/test15");
    }

    @Test
    public void test16() {
        On.get("/test16").html(((ReqRespHandler) (( req, resp) -> Bufs.buf("some buffer"))));
        onlyGet("/test16");
    }

    @Test
    public void test17() {
        On.get("/test17").html(((ReqRespHandler) (( req, resp) -> new File("non-existing-file"))));
        onlyGet("/test17");
    }

    @Test
    public void test18() {
        On.get("/test18").html(((ReqRespHandler) (( req, resp) -> Res.from("non-existing-res", "abc"))));
        onlyGet("/test18");
    }

    @Test
    public void test19() {
        On.get("/test19").html(((ReqRespHandler) (( req, resp) -> new BigDecimal("123456789.0123456789"))));
        onlyGet("/test19");
    }

    @Test
    public void test20() {
        On.get("/test20").html(((ReqRespHandler) (( req, resp) -> new BigInteger("12345678901234567890"))));
        onlyGet("/test20");
    }

    @Test
    public void test21() {
        On.get("/test21").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 21"))));
        onlyGet("/test21");
    }

    @Test
    public void test22() {
        On.get("/test22").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 22").code(301))));
        onlyGet("/test22");
    }

    @Test
    public void test23() {
        On.get("/test23").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 23").code(404))));
        onlyGet("/test23");
    }

    @Test
    public void test24() {
        On.get("/test24").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 24").code(500))));
        onlyGet("/test24");
    }

    @Test
    public void test25() {
        On.get("/test25").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 25").binary("B25"))));
        onlyGet("/test25");
    }

    @Test
    public void test26() {
        On.get("/test26").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 26").contentType(MediaType.APPLICATION_MSWORD))));
        onlyGet("/test26");
    }

    @Test
    public void test27() {
        On.get("/test27").html(((ReqRespHandler) (( req, resp) -> {
            resp.cookies().put("cookie1", "abc");
            resp.cookies().put("cookie2", "xyz");
            return resp.result("RESULT 27");
        })));
        onlyGet("/test27");
    }

    @Test
    public void test28() {
        On.get("/test28").html(((ReqRespHandler) (( req, resp) -> {
            resp.headers().put("hdr1", "HDRX");
            resp.headers().put("hdr2", "hdry");
            return resp.result("RESULT 28");
        })));
        onlyGet("/test28");
    }

    @Test
    public void test29() {
        On.get("/test29").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 29").redirect("/abc"))));
        onlyGet("/test29");
    }

    @Test
    public void test30() {
        On.get("/test30").html(((ReqRespHandler) (( req, resp) -> resp.result("RESULT 30").redirect("/xyzz").code(302))));
        onlyGet("/test30");
    }

    @Test
    public void test31() {
        On.get("/test31").html(((ReqHandler) (( req) -> new File("test1.txt"))));
        onlyGet("/test31");
    }

    @Test
    public void test32() {
        On.get("/test32").html(((ReqRespHandler) (( req, resp) -> Res.from("test2.txt"))));
        onlyGet("/test32");
    }

    @Test
    public void test33() {
        On.route("get", "/test33").html((Req req) -> "req");
        onlyGet("/test33");
    }

    @Test
    public void test34() {
        On.route("GET", "/test34").html((Req req,Resp resp) -> "req+resp");
        onlyGet("/test34");
    }

    @Test
    public void test35() {
        On.get("/future").html((Req req,Resp resp) -> Jobs.after(1).seconds(() -> {
            resp.result("finished!").done();
        }));
        onlyGet("/future");
    }
}

