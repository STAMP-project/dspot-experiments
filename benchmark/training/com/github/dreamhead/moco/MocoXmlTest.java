package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import org.apache.http.client.HttpResponseException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoXmlTest extends AbstractMocoHttpTest {
    @Test
    public void should_return_content_based_on_xpath() throws Exception {
        server.request(Moco.eq(Moco.xpath("/request/parameters/id/text()"), "1")).response("foo");
        server.request(Moco.eq(Moco.xpath("/request/parameters/id/text()"), "2")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postFile(RemoteTestUtils.root(), "foo.xml"), CoreMatchers.is("foo"));
                Assert.assertThat(helper.postFile(RemoteTestUtils.root(), "bar.xml"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_mismatch_xpath() throws Exception {
        server.request(Moco.eq(Moco.xpath("/request/parameters/id/text()"), "3")).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postFile(RemoteTestUtils.root(), "foo.xml");
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_not_return_anything_for_unknown_xpath() throws Exception {
        server.request(Moco.eq(Moco.xpath("/response/parameters/id/text()"), "3")).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.postFile(RemoteTestUtils.root(), "foo.xml");
            }
        });
    }

    @Test
    public void should_return_content_based_on_xpath_with_many_elements() throws Exception {
        server.request(Moco.eq(Moco.xpath("/request/parameters/id/text()"), "2")).response("bar");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postFile(RemoteTestUtils.root(), "foobar.xml"), CoreMatchers.is("bar"));
            }
        });
    }

    @Test
    public void should_match_exact_xml() throws Exception {
        server.request(Moco.xml(Moco.file("src/test/resources/foo.xml"))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postFile(RemoteTestUtils.root(), "foo.xml"), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_match_xml() throws Exception {
        server.request(Moco.xml("<request><parameters><id>1</id></parameters></request>")).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postFile(RemoteTestUtils.root(), "foo.xml"), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_match_xml_with_resource() throws Exception {
        server.request(Moco.xml(Moco.text("<request><parameters><id>1</id></parameters></request>"))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postFile(RemoteTestUtils.root(), "foo.xml"), CoreMatchers.is("foo"));
            }
        });
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_content() throws Exception {
        server.request(Moco.xml("<request><parameters><id>1</id></parameters></request>")).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                helper.postContent(RemoteTestUtils.root(), "blah");
            }
        });
    }

    @Test
    public void should_return_content_based_on_xpath_existing() throws Exception {
        server.request(Moco.exist(Moco.xpath("/request/parameters/id/text()"))).response("foo");
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postFile(RemoteTestUtils.root(), "foo.xml"), CoreMatchers.is("foo"));
            }
        });
    }
}

