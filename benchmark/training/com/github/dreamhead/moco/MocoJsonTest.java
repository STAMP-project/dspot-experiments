package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.support.JsonSupport;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MocoJsonTest extends AbstractMocoHttpTest {
    @Test
    public void should_return_content_based_on_jsonpath() throws Exception {
        server.request(Moco.eq(Moco.jsonPath("$.book.price"), "1")).response("jsonpath match success");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("jsonpath match success"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_mismatch_jsonpath() throws Exception {
        server.request(Moco.eq(Moco.jsonPath("$.book.price"), "1")).response("jsonpath match success");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(RemoteTestUtils.root(), "{\"book\":{\"price\":\"2\"}}");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_path_found() throws Exception {
        server.request(Moco.eq(Moco.jsonPath("anything"), "1")).response("jsonpath match success");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(RemoteTestUtils.root(), "{}");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_if_no_json_found() throws Exception {
        server.request(Moco.eq(Moco.jsonPath("$.book.price"), "1")).response("jsonpath match success");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(RemoteTestUtils.root(), "{}");
            }
        });
    }

    @Test
    public void should_match_exact_json() throws Exception {
        final String jsonText = Jsons.toJson(ImmutableMap.of("foo", "bar"));
        server.request(Moco.by(Moco.json(jsonText))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), jsonText), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_match_exact_json_with_resource() throws Exception {
        final String jsonContent = "{\"foo\":\"bar\"}";
        server.request(Moco.by(Moco.json(jsonContent))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), jsonContent), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_match_same_structure_json() throws Exception {
        final String jsonText = Jsons.toJson(ImmutableMap.of("foo", "bar"));
        server.request(Moco.by(Moco.json(jsonText))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), jsonText), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_match_POJO_json_resource() throws Exception {
        MocoJsonTest.PlainA pojo = new MocoJsonTest.PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.request(Moco.by(Moco.json(pojo))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "{\n\t\"code\":1,\n\t\"message\":\"message\"\n}"), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_match_POJO_json() throws Exception {
        MocoJsonTest.PlainA pojo = new MocoJsonTest.PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.request(Moco.by(Moco.json(pojo))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "{\n\t\"code\":1,\n\t\"message\":\"message\"\n}"), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_return_content_based_on_jsonpath_existing() throws Exception {
        server.request(Moco.exist(Moco.jsonPath("$.book.price"))).response("jsonpath match success");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.root(), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("jsonpath match success"));
            }
        });
    }

    @Test
    public void should_return_json_for_POJO() throws Exception {
        final MocoJsonTest.PlainA pojo = new MocoJsonTest.PlainA();
        pojo.code = 1;
        pojo.message = "message";
        server.response(Moco.json(pojo));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                JsonSupport.assertEquals(pojo, helper.getResponse(RemoteTestUtils.root()));
            }
        });
    }

    @Test
    public void should_return_json_for_POJO_with_CJK() throws Exception {
        final MocoJsonTest.PlainA pojo = new MocoJsonTest.PlainA();
        pojo.code = 1;
        pojo.message = "??";
        server.response(Moco.json(pojo));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String content = helper.get(RemoteTestUtils.remoteUrl(RemoteTestUtils.root()));
                JsonSupport.assertEquals(pojo, content);
            }
        });
    }

    @Test
    public void should_match_request_with_gbk_resource() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.log());
        server.request(Moco.by(Moco.json(Moco.pathResource("gbk.json", Charset.forName("GBK"))))).response("response");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String result = helper.postBytes(RemoteTestUtils.root(), "{\"message\": \"\u8bf7\u6c42\"}".getBytes());
                Assert.assertThat(result, CoreMatchers.is("response"));
            }
        });
    }

    @Test
    public void should_match_gbk_request() throws Exception {
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.log());
        final Charset gbk = Charset.forName("GBK");
        server.request(Moco.by(Moco.json(Moco.pathResource("gbk.json", gbk)))).response("response");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                URL resource = Resources.getResource("gbk.json");
                byte[] bytes = ByteStreams.toByteArray(resource.openStream());
                String result = helper.postBytes(RemoteTestUtils.root(), bytes, gbk);
                Assert.assertThat(result, CoreMatchers.is("response"));
            }
        });
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_json() throws Exception {
        MocoJsonTest.PlainA pojo = new MocoJsonTest.PlainA();
        pojo.code = 1;
        pojo.message = "message";
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.json(pojo)))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(RemoteTestUtils.remoteUrl("/target"), Moco.json(pojo))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    private static class PlainA {
        public int code;

        public String message;
    }
}

