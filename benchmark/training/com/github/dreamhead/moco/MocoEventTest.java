package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.github.dreamhead.moco.internal.SessionContext;
import com.github.dreamhead.moco.util.Idles;
import com.google.common.collect.ImmutableMultimap;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class MocoEventTest extends AbstractMocoHttpTest {
    @Test
    public void should_fire_event_on_complete() throws Exception {
        MocoEventAction action = Mockito.mock(MocoEventAction.class);
        server.response("foo").on(Moco.complete(action));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.root()), CoreMatchers.is("foo"));
            }
        });
        Mockito.verify(action).execute(ArgumentMatchers.any(Request.class));
    }

    @Test
    public void should_not_fire_event_on_specific_request() throws Exception {
        MocoEventAction action = Mockito.mock(MocoEventAction.class);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(action));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(action).execute(ArgumentMatchers.any(Request.class));
    }

    @Test
    public void should_not_fire_event_on_other_request() throws Exception {
        MocoEventAction action = Mockito.mock(MocoEventAction.class);
        server.request(Moco.by(Moco.uri("/noevent"))).response("noevent");
        server.request(Moco.by(Moco.uri("/event"))).response("foo").on(Moco.complete(action));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/noevent")), CoreMatchers.is("noevent"));
            }
        });
        Mockito.verify(action, Mockito.never()).execute(ArgumentMatchers.any(Request.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.by(Moco.uri("/target"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.get(RemoteTestUtils.remoteUrl("/target"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete_with_resource() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.by(Moco.uri("/target"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.get(Moco.text(RemoteTestUtils.remoteUrl("/target")))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete_with_template_fetching_var_from_request() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.by(Moco.uri("/target"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.get(Moco.template("${base}/${req.headers['foo']}", "base", RemoteTestUtils.root()))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.remoteUrl("/event"), ImmutableMultimap.of("foo", "target")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete_with_path_resource() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.by(Moco.uri("/target"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.get(Moco.pathResource("template.url"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_get_request_to_target_on_complete_with_template() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.by(Moco.uri("/target"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.get(Moco.template("${var}", "var", RemoteTestUtils.remoteUrl("/target")))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_string() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("content"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(RemoteTestUtils.remoteUrl("/target"), "content")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_string_and_resource_url() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("content"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(Moco.text(RemoteTestUtils.remoteUrl("/target")), "content")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_path_resource_url() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.by(Moco.uri("/target"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(Moco.pathResource("template.url"), "content")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_path_resource_content() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.by(Moco.uri("/target"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(RemoteTestUtils.remoteUrl("/target"), Moco.pathResource("foo.request"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("content"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(RemoteTestUtils.remoteUrl("/target"), Moco.text("content"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_template_content() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("content"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(RemoteTestUtils.remoteUrl("/target"), Moco.template("${req.headers['foo']}"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.remoteUrl("/event"), ImmutableMultimap.of("foo", "content")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_resource_url() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("content"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(Moco.text(RemoteTestUtils.remoteUrl("/target")), Moco.text("content"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_asyc() throws Exception {
        final ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("content"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.async(Moco.post(RemoteTestUtils.remoteUrl("/target"), Moco.text("content")))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
                Mockito.verify(handler, Mockito.never()).writeToResponse(ArgumentMatchers.any(SessionContext.class));
                Idles.idle(2, TimeUnit.SECONDS);
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_async_after_awhile() throws Exception {
        final ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by("content"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.async(Moco.post(RemoteTestUtils.remoteUrl("/target"), Moco.text("content")), Moco.latency(1, TimeUnit.SECONDS))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
                Mockito.verify(handler, Mockito.never()).writeToResponse(ArgumentMatchers.any(SessionContext.class));
                Idles.idle(2, TimeUnit.SECONDS);
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_fire_event_for_context_configuration() throws Exception {
        MocoEventAction action = Mockito.mock(MocoEventAction.class);
        Mockito.when(action.apply(ArgumentMatchers.any(MocoConfig.class))).thenReturn(action);
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.context("/context"));
        server.get(Moco.by(Moco.uri("/foo"))).response("foo").on(Moco.complete(action));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/context/foo")), CoreMatchers.is("foo"));
            }
        });
        Mockito.verify(action).execute(ArgumentMatchers.any(Request.class));
    }

    @Test
    public void should_send_post_request_with_file_root_configuration() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        Mockito.when(handler.apply(ArgumentMatchers.any(MocoConfig.class))).thenReturn(handler);
        server = Moco.httpServer(RemoteTestUtils.port(), Moco.fileRoot("src/test/resources"));
        server.request(Moco.by(Moco.uri("/target")), Moco.by(Moco.file("foo.request"))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(RemoteTestUtils.remoteUrl("/target"), Moco.file("foo.request"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }

    @Test
    public void should_send_post_request_to_target_on_complete_with_gbk() throws Exception {
        ResponseHandler handler = Mockito.mock(ResponseHandler.class);
        final Charset gbk = Charset.forName("GBK");
        server.request(Moco.and(Moco.by(Moco.uri("/target")), Moco.by(Moco.pathResource("gbk.json", gbk)))).response(handler);
        server.request(Moco.by(Moco.uri("/event"))).response("event").on(Moco.complete(Moco.post(Moco.text(RemoteTestUtils.remoteUrl("/target")), Moco.pathResource("gbk.json", gbk))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/event")), CoreMatchers.is("event"));
            }
        });
        Mockito.verify(handler).writeToResponse(ArgumentMatchers.any(SessionContext.class));
    }
}

