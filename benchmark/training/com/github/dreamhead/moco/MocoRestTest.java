package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.github.dreamhead.moco.util.Jsons;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class MocoRestTest extends BaseMocoHttpTest<RestServer> {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void should_get_resource_by_id() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoRestTest.Plain response1 = getResource("/targets/1");
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                MocoRestTest.Plain response2 = getResource("/targets/2");
                Assert.assertThat(response2.code, CoreMatchers.is(2));
                Assert.assertThat(response2.message, CoreMatchers.is("world"));
            }
        });
    }

    @Test
    public void should_get_all_resources() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.get().response(Moco.json(ImmutableList.of(resource1, resource2))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String uri = "/targets";
                List<MocoRestTest.Plain> plains = getResources(uri);
                Assert.assertThat(plains.size(), CoreMatchers.is(2));
            }
        });
    }

    @Test
    public void should_get_all_resources_by_default() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                List<MocoRestTest.Plain> plains = getResources("/targets");
                Assert.assertThat(plains.size(), CoreMatchers.is(2));
            }
        });
    }

    @Test
    public void should_reply_404_for_unknown_resource() throws Exception {
        server.resource("targets", MocoRest.get("2").response(Moco.with(Moco.text("hello"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.remoteUrl("/targets/1"));
                Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(404));
            }
        });
    }

    @Test
    public void should_request_server_by_moco_config() throws Exception {
        RestServer server = MocoRest.restServer(12306, Moco.context("/rest"), Moco.response(Moco.header("foo", "bar")));
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoRestTest.Plain response1 = getResource("/rest/targets/1");
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                MocoRestTest.Plain response2 = getResource("/rest/targets/2");
                Assert.assertThat(response2.code, CoreMatchers.is(2));
                Assert.assertThat(response2.message, CoreMatchers.is("world"));
                HttpResponse response = helper.getResponse(RemoteTestUtils.remoteUrl("/rest/targets/1"));
                Assert.assertThat(response.getHeaders("foo")[0].getValue(), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_log_request_and_response() throws Exception {
        RestServer server = MocoRest.restServer(RemoteTestUtils.port(), Moco.log());
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "0XCAFE";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "0XBABE";
        server.resource("targets", MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2)));
        File file = folder.newFile();
        System.setOut(new PrintStream(new FileOutputStream(file)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(RemoteTestUtils.remoteUrl("/targets"));
            }
        });
        String actual = Files.toString(file, Charset.defaultCharset());
        Assert.assertThat(actual, CoreMatchers.containsString("0XCAFE"));
        Assert.assertThat(actual, CoreMatchers.containsString("0XBABE"));
    }

    @Test
    public void should_get_resource_by_id_with_request_config() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.get("1").request(Moco.eq(Moco.header(HttpHeaders.CONTENT_TYPE), "application/json")).response(Moco.json(resource1)), MocoRest.get("2").request(Moco.eq(Moco.header(HttpHeaders.CONTENT_TYPE), "application/json")).response(Moco.json(resource2)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponseWithHeader(RemoteTestUtils.remoteUrl("/targets/1"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
                MocoRestTest.Plain response1 = asPlain(response);
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                HttpResponse otherResponse = helper.getResponseWithHeader(RemoteTestUtils.remoteUrl("/targets/2"), ImmutableMultimap.of(HttpHeaders.CONTENT_TYPE, "application/json"));
                MocoRestTest.Plain response2 = asPlain(otherResponse);
                Assert.assertThat(response2.code, CoreMatchers.is(2));
                Assert.assertThat(response2.message, CoreMatchers.is("world"));
                HttpResponse notFoundResponse = helper.getResponse(RemoteTestUtils.remoteUrl("/targets/1"));
                Assert.assertThat(notFoundResponse.getStatusLine().getStatusCode(), CoreMatchers.is(404));
            }
        });
    }

    @Test
    public void should_query_with_condition() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.get().request(Moco.eq(Moco.query("foo"), "bar")).response(Moco.json(ImmutableList.of(resource1, resource2))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                List<MocoRestTest.Plain> plains = Jsons.toObjects(helper.get(RemoteTestUtils.remoteUrl("/targets?foo=bar")), MocoRestTest.Plain.class);
                Assert.assertThat(plains.size(), CoreMatchers.is(2));
                HttpResponse response = helper.getResponse(RemoteTestUtils.remoteUrl("/targets"));
                Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(404));
            }
        });
    }

    @Test
    public void should_get_resource_by_any_id() throws Exception {
        MocoRestTest.Plain resource = new MocoRestTest.Plain();
        resource.code = 1;
        resource.message = "hello";
        server.resource("targets", MocoRest.get(MocoRest.anyId()).response(Moco.json(resource)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoRestTest.Plain response1 = getResource("/targets/1");
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                MocoRestTest.Plain response2 = getResource("/targets/2");
                Assert.assertThat(response2.code, CoreMatchers.is(1));
                Assert.assertThat(response2.message, CoreMatchers.is("hello"));
            }
        });
    }

    @Test
    public void should_post() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        server.resource("targets", MocoRest.post().response(Moco.status(201), Moco.header("Location", "/targets/123")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(RemoteTestUtils.remoteUrl("/targets"), Jsons.toJson(resource1));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(201));
                Assert.assertThat(httpResponse.getFirstHeader("Location").getValue(), CoreMatchers.is("/targets/123"));
            }
        });
    }

    @Test
    public void should_post_with_header() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource = new MocoRestTest.Plain();
        resource.code = 1;
        resource.message = "hello";
        server.resource("targets", MocoRest.post().request(Moco.eq(Moco.header(HttpHeaders.CONTENT_TYPE), "application/json")).response(Moco.status(201), Moco.header("Location", "/targets/123")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(RemoteTestUtils.remoteUrl("/targets"), Jsons.toJson(resource), "application/json");
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(201));
                Assert.assertThat(httpResponse.getFirstHeader("Location").getValue(), CoreMatchers.is("/targets/123"));
                HttpResponse badRequest = helper.postForResponse(RemoteTestUtils.remoteUrl("/targets"), Jsons.toJson(resource));
                Assert.assertThat(badRequest.getStatusLine().getStatusCode(), CoreMatchers.is(400));
            }
        });
    }

    @Test
    public void should_return_404_for_post_with_id_by_default() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        server.resource("targets", MocoRest.post().response(Moco.status(201), Moco.header("Location", "/targets/123")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.postForResponse(RemoteTestUtils.remoteUrl("/targets/1"), Jsons.toJson(resource1));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(404));
            }
        });
    }

    @Test
    public void should_put() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        server.resource("targets", MocoRest.put("1").response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(RemoteTestUtils.remoteUrl("/targets/1"), Jsons.toJson(resource1));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_not_put_with_unknown_id() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        server.resource("targets", MocoRest.put("1").response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(RemoteTestUtils.remoteUrl("/targets/2"), Jsons.toJson(resource1));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(404));
            }
        });
    }

    @Test
    public void should_put_with_response_handler() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        server.resource("targets", MocoRest.put("1").response(Moco.status(409)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponse(RemoteTestUtils.remoteUrl("/targets/1"), Jsons.toJson(resource1));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(409));
            }
        });
    }

    @Test
    public void should_put_with_matcher() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        server.resource("targets", MocoRest.put("1").request(Moco.eq(Moco.header(HttpHeaders.IF_MATCH), "moco")).response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.putForResponseWithHeaders(RemoteTestUtils.remoteUrl("/targets/1"), Jsons.toJson(resource1), ImmutableMultimap.of(HttpHeaders.IF_MATCH, "moco"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_put_with_any_id() throws Exception {
        RestServer server = MocoRest.restServer(12306);
        final MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        server.resource("targets", MocoRest.put(MocoRest.anyId()).response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse1 = helper.putForResponse(RemoteTestUtils.remoteUrl("/targets/1"), Jsons.toJson(resource1));
                Assert.assertThat(httpResponse1.getStatusLine().getStatusCode(), CoreMatchers.is(200));
                HttpResponse httpResponse2 = helper.putForResponse(RemoteTestUtils.remoteUrl("/targets/2"), Jsons.toJson(resource1));
                Assert.assertThat(httpResponse2.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_not_delete_with_unknown_id() throws Exception {
        server.resource("targets", MocoRest.delete("1").response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponse(RemoteTestUtils.remoteUrl("/targets/2"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(404));
            }
        });
    }

    @Test
    public void should_delete() throws Exception {
        server.resource("targets", MocoRest.delete("1").response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponse(RemoteTestUtils.remoteUrl("/targets/1"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_delete_with_matcher() throws Exception {
        server.resource("targets", MocoRest.delete("1").request(Moco.eq(Moco.header(HttpHeaders.IF_MATCH), "moco")).response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponseWithHeaders(RemoteTestUtils.remoteUrl("/targets/1"), ImmutableMultimap.of(HttpHeaders.IF_MATCH, "moco"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_delete_with_response() throws Exception {
        server.resource("targets", MocoRest.delete("1").response(Moco.status(409)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.deleteForResponse(RemoteTestUtils.remoteUrl("/targets/1"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(409));
            }
        });
    }

    @Test
    public void should_delete_with_any_id() throws Exception {
        server.resource("targets", MocoRest.delete(MocoRest.anyId()).response(Moco.status(200)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse1 = helper.deleteForResponse(RemoteTestUtils.remoteUrl("/targets/1"));
                Assert.assertThat(httpResponse1.getStatusLine().getStatusCode(), CoreMatchers.is(200));
                HttpResponse httpResponse2 = helper.deleteForResponse(RemoteTestUtils.remoteUrl("/targets/2"));
                Assert.assertThat(httpResponse2.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_head_with_all() throws Exception {
        server.resource("targets", MocoRest.head().response(Moco.header("ETag", "Moco")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(RemoteTestUtils.remoteUrl("/targets"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
                Assert.assertThat(httpResponse.getHeaders("ETag")[0].getValue(), CoreMatchers.is("Moco"));
            }
        });
    }

    @Test
    public void should_head() throws Exception {
        server.resource("targets", MocoRest.head("1").response(Moco.header("ETag", "Moco")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(RemoteTestUtils.remoteUrl("/targets/1"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_not_head_with_unknown_id() throws Exception {
        server.resource("targets", MocoRest.head("1").response(Moco.header("ETag", "Moco")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(RemoteTestUtils.remoteUrl("/targets/2"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(404));
            }
        });
    }

    @Test
    public void should_head_with_matcher() throws Exception {
        server.resource("targets", MocoRest.head("1").request(Moco.eq(Moco.query("name"), "foo")).response(Moco.header("ETag", "Moco")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse = helper.headForResponse(RemoteTestUtils.remoteUrl("/targets/1?name=foo"));
                Assert.assertThat(httpResponse.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_head_with_any_id() throws Exception {
        server.resource("targets", MocoRest.head(MocoRest.anyId()).response(Moco.header("ETag", "Moco")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse httpResponse1 = helper.headForResponse(RemoteTestUtils.remoteUrl("/targets/1"));
                Assert.assertThat(httpResponse1.getStatusLine().getStatusCode(), CoreMatchers.is(200));
                HttpResponse httpResponse2 = helper.headForResponse(RemoteTestUtils.remoteUrl("/targets/2"));
                Assert.assertThat(httpResponse2.getStatusLine().getStatusCode(), CoreMatchers.is(200));
            }
        });
    }

    @Test
    public void should_patch() throws Exception {
        server.resource("targets", MocoRest.patch("1").response(Moco.with(Moco.text("patch result"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.patchForResponse(RemoteTestUtils.remoteUrl("/targets/1"), "result"), CoreMatchers.is("patch result"));
            }
        });
    }

    @Test
    public void should_patch_with_any_id() throws Exception {
        server.resource("targets", MocoRest.patch(MocoRest.anyId()).response(Moco.with(Moco.text("patch result"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.patchForResponse(RemoteTestUtils.remoteUrl("/targets/1"), "result"), CoreMatchers.is("patch result"));
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_get_id_with_slash() throws Exception {
        MocoRest.get("1/1").response(Moco.status(200));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_get_id_with_space() {
        MocoRest.get("1 1").response(Moco.status(200));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_resource_name_with_slash() {
        server.resource("hello/world", MocoRest.get().response("hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_resource_name_with_space() {
        server.resource("hello world", MocoRest.get().response("hello"));
    }

    @Test
    public void should_get_sub_resource() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.id("1").name("sub").settings(MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoRestTest.Plain response1 = getResource("/targets/1/sub/1");
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                MocoRestTest.Plain response2 = getResource("/targets/1/sub/2");
                Assert.assertThat(response2.code, CoreMatchers.is(2));
                Assert.assertThat(response2.message, CoreMatchers.is("world"));
            }
        });
    }

    @Test
    public void should_get_sub_resource_with_any_id() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.id(MocoRest.anyId()).name("sub").settings(MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoRestTest.Plain response1 = getResource("/targets/1/sub/1");
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                MocoRestTest.Plain response2 = getResource("/targets/2/sub/2");
                Assert.assertThat(response2.code, CoreMatchers.is(2));
                Assert.assertThat(response2.message, CoreMatchers.is("world"));
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_invalid_sub_resource_name() {
        MocoRest.id(MocoRest.anyId()).name("hello world");
    }

    @Test
    public void should_work_with_other_http_configuration() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        server.resource("targets", MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2)));
        server.response("hello");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoRestTest.Plain response1 = getResource("/targets/1");
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                MocoRestTest.Plain response2 = getResource("/targets/2");
                Assert.assertThat(response2.code, CoreMatchers.is(2));
                Assert.assertThat(response2.message, CoreMatchers.is("world"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/hello")), CoreMatchers.is("hello"));
            }
        });
    }

    @Test
    public void should_verify_expected_request_and_log_at_same_time() throws Exception {
        MocoRestTest.Plain resource1 = new MocoRestTest.Plain();
        resource1.code = 1;
        resource1.message = "hello";
        MocoRestTest.Plain resource2 = new MocoRestTest.Plain();
        resource2.code = 2;
        resource2.message = "world";
        final RequestHit hit = MocoRequestHit.requestHit();
        final RestServer server = MocoRest.restServer(RemoteTestUtils.port(), hit, Moco.log());
        server.resource("targets", MocoRest.get("1").response(Moco.json(resource1)), MocoRest.get("2").response(Moco.json(resource2)));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                MocoRestTest.Plain response1 = getResource("/targets/1");
                Assert.assertThat(response1.code, CoreMatchers.is(1));
                Assert.assertThat(response1.message, CoreMatchers.is("hello"));
                MocoRestTest.Plain response2 = getResource("/targets/2");
                Assert.assertThat(response2.code, CoreMatchers.is(2));
                Assert.assertThat(response2.message, CoreMatchers.is("world"));
            }
        });
        hit.verify(Moco.by(Moco.uri("/targets/1")), MocoRequestHit.times(1));
        hit.verify(Moco.by(Moco.uri("/targets/2")), MocoRequestHit.times(1));
    }

    private static class Plain {
        public int code;

        public String message;
    }
}

