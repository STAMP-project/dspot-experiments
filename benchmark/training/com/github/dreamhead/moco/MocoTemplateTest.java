package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.RemoteTestUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoTemplateTest extends AbstractMocoHttpTest {
    @Test
    public void should_generate_response_with_http_method() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.method}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("GET"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "foo"), CoreMatchers.is("POST"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_version() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.version}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithVersion(RemoteTestUtils.remoteUrl("/template"), HttpVersion.HTTP_1_0), CoreMatchers.is("HTTP/1.0"));
            }
        });
    }

    @Test
    public void should_generate_response_with_content() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.content}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "foo"), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_header() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.headers['foo']}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getWithHeader(RemoteTestUtils.remoteUrl("/template"), ImmutableMultimap.of("foo", "bar")), CoreMatchers.is("bar"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_unknown_header() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.headers['foo']}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(RemoteTestUtils.remoteUrl("/template"));
            }
        });
    }

    @Test
    public void should_generate_response_with_http_query() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.uri} ${req.queries['foo']}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = helper.get(RemoteTestUtils.remoteUrl("/template?foo=bar"));
                Assert.assertThat(response, CoreMatchers.is("/template bar"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template(Moco.file("src/test/resources/foo.template")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("GET"));
            }
        });
    }

    @Test
    public void should_generate_response_version() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.version(Moco.template("${req.version}")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                ProtocolVersion version = helper.execute(Request.Get(RemoteTestUtils.remoteUrl("/template")).version(HttpVersion.HTTP_1_0)).getProtocolVersion();
                Assert.assertThat(version.toString(), CoreMatchers.is("HTTP/1.0"));
            }
        });
    }

    @Test
    public void should_generate_response_header() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.header("foo", Moco.template("${req.method}")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Header header = helper.execute(Request.Get(RemoteTestUtils.remoteUrl("/template")).version(HttpVersion.HTTP_1_0)).getFirstHeader("foo");
                Assert.assertThat(header.getValue(), CoreMatchers.is("GET"));
            }
        });
    }

    @Test
    public void should_generate_response_with_uri() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.uri}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                String response = helper.get(RemoteTestUtils.remoteUrl("/template"));
                Assert.assertThat(response, CoreMatchers.is("/template"));
            }
        });
    }

    @Test
    public void should_generate_response_with_form() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.forms['name']}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Request request = Request.Post(RemoteTestUtils.remoteUrl("/template")).bodyForm(new BasicNameValuePair("name", "dreamhead"));
                Assert.assertThat(helper.executeAsString(request), CoreMatchers.is("dreamhead"));
            }
        });
    }

    @Test
    public void should_generate_response_with_cookie() throws Exception {
        server.request(Moco.and(Moco.by(Moco.uri("/cookie")), Moco.eq(Moco.cookie("templateLoggedIn"), "true"))).response(Moco.template("${req.cookies['templateLoggedIn']}"));
        server.request(Moco.by(Moco.uri("/cookie"))).response(Moco.cookie("templateLoggedIn", "true"), Moco.status(302));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.getForStatus(RemoteTestUtils.remoteUrl("/cookie")), CoreMatchers.is(302));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/cookie")), CoreMatchers.is("true"));
            }
        });
    }

    @Test
    public void should_generate_response_with_variable() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${var}", "var", "TEMPLATE"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("TEMPLATE"));
            }
        });
    }

    @Test
    public void should_generate_response_with_two_variables() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${foo} ${bar}", "foo", "ANOTHER", "bar", "TEMPLATE"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("ANOTHER TEMPLATE"));
            }
        });
    }

    @Test
    public void should_generate_response_with_variable_map() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${foo} ${bar}", ImmutableMap.of("foo", Moco.var("ANOTHER"), "bar", Moco.var("TEMPLATE"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("ANOTHER TEMPLATE"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file_with_variable() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template(Moco.file("src/test/resources/var.template"), "var", "TEMPLATE"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("TEMPLATE"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file_with_two_variables() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template(Moco.file("src/test/resources/two_vars.template"), "foo", "ANOTHER", "bar", "TEMPLATE"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("ANOTHER TEMPLATE"));
            }
        });
    }

    // @Test
    // public void should_generate_response_from_file_with_variable_map() throws Exception {
    // server.request(by(uri("/template"))).response(template(file("src/test/resources/var.template"), of("var", "TEMPLATE")));
    // 
    // running(server, new Runnable() {
    // @Override
    // public void run() throws Exception {
    // assertThat(helper.get(remoteUrl("/template")), is("TEMPLATE"));
    // }
    // });
    // }
    @Test
    public void should_generate_response_with_two_variables_by_request() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${foo} ${bar}", "foo", Moco.jsonPath("$.book.price"), "bar", Moco.jsonPath("$.book.price")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"2\"}}"), CoreMatchers.is("2 2"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("1 1"));
            }
        });
    }

    @Test
    public void should_generate_response_with_variable_by_request() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${foo}", "foo", Moco.jsonPath("$.book.price")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"2\"}}"), CoreMatchers.is("2"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("1"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file_with_variable_by_request() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template(Moco.file("src/test/resources/var.template"), "var", Moco.jsonPath("$.book.price")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"2\"}}"), CoreMatchers.is("2"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("1"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file_with_two_variables_by_request() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template(Moco.file("src/test/resources/two_vars.template"), "foo", Moco.jsonPath("$.book.price"), "bar", Moco.jsonPath("$.book.price")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"2\"}}"), CoreMatchers.is("2 2"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("1 1"));
            }
        });
    }

    @Test
    public void should_generate_response_with_two_variables_by_request_and_one_variable_is_plain_text() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${foo} ${bar}", "foo", Moco.jsonPath("$.book.price"), "bar", Moco.var("bar")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"2\"}}"), CoreMatchers.is("2 bar"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("1 bar"));
            }
        });
    }

    @Test
    public void should_generate_response_from_file_with_variable_map() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template(Moco.file("src/test/resources/var.template"), ImmutableMap.of("var", Moco.jsonPath("$.book.price"))));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"2\"}}"), CoreMatchers.is("2"));
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\"book\":{\"price\":\"1\"}}"), CoreMatchers.is("1"));
            }
        });
    }

    @Test
    public void should_generate_response_with_many_extracted_variables() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("<#list seq as item>${item}</#list>", "seq", Moco.xpath("/request/parameters/id/text()")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.postFile(RemoteTestUtils.remoteUrl("/template"), "foobar.xml"), CoreMatchers.is("12"));
            }
        });
    }

    @Test
    public void should_return_file_with_template() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.file(Moco.template("src/test/resources/${var}", "var", "foo.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_file_with_template_and_charset() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.file(Moco.template("src/test/resources/${var}", "var", "gbk.response"), Charset.forName("GBK")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.getAsBytes(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is(Files.toByteArray(new File("src/test/resources/gbk.response"))));
            }
        });
    }

    @Test
    public void should_return_path_resource_with_template() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.pathResource(Moco.template("${var}", "var", "foo.response")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is("foo.response"));
            }
        });
    }

    @Test
    public void should_return_path_resource_with_template_and_charset() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.pathResource(Moco.template("${var}", "var", "gbk.response"), Charset.forName("GBK")));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                URL resource = Resources.getResource("gbk.response");
                InputStream stream = resource.openStream();
                Assert.assertThat(helper.getAsBytes(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is(ByteStreams.toByteArray(stream)));
            }
        });
    }

    @Test
    public void should_return_redirect_with_template() throws Exception {
        server.get(Moco.by(Moco.uri("/"))).response("foo");
        server.request(Moco.by(Moco.uri("/redirectTemplate"))).redirectTo(Moco.template("${var}", "var", RemoteTestUtils.root()));
        server.redirectTo(Moco.template("${var}", "var", RemoteTestUtils.root()));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/redirectTemplate")), CoreMatchers.is("foo"));
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/anything")), CoreMatchers.is("foo"));
            }
        });
    }

    @Test
    public void should_generate_response_with_now() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${now('yyyy-MM-dd')}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/template")), CoreMatchers.is(format.format(date)));
            }
        });
    }

    @Test
    public void should_throw_exception_for_now_without_format() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${now()}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.remoteUrl("/template"));
                Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(400));
            }
        });
    }

    @Test
    public void should_generate_response_with_random() throws Exception {
        server.request(Moco.by(Moco.uri("/random"))).response(Moco.template("${random()}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = helper.get(RemoteTestUtils.remoteUrl("/random"));
                try {
                    double result = Double.parseDouble(response);
                    Assert.assertThat(result, Matchers.greaterThan(0.0));
                    Assert.assertThat(result, Matchers.lessThan(1.0));
                } catch (NumberFormatException e) {
                    Assert.fail();
                }
            }
        });
    }

    @Test
    public void should_generate_response_with_random_with_range() throws Exception {
        server.request(Moco.by(Moco.uri("/random"))).response(Moco.template("${random(100)}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = helper.get(RemoteTestUtils.remoteUrl("/random"));
                try {
                    double result = Double.parseDouble(response);
                    Assert.assertThat(result, Matchers.lessThan(100.0));
                    Assert.assertThat(result, Matchers.greaterThan(0.0));
                } catch (NumberFormatException e) {
                    Assert.fail();
                }
            }
        });
    }

    @Test
    public void should_() {
        double result = 1.01;
        DecimalFormat format = new DecimalFormat("###.######");
        String finalResult = format.format(result);
        System.out.println(finalResult);
    }

    @Test
    public void should_generate_response_with_random_with_data_format() throws Exception {
        server.request(Moco.by(Moco.uri("/random"))).response(Moco.template("${random('###.######')}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = helper.get(RemoteTestUtils.remoteUrl("/random"));
                try {
                    String target = Iterables.get(Splitter.on('.').split(response), 1);
                    Assert.assertThat(target.length(), Matchers.lessThanOrEqualTo(6));
                    double result = Double.parseDouble(response);
                    Assert.assertThat(result, Matchers.lessThan(1.0));
                } catch (NumberFormatException e) {
                    Assert.fail();
                }
            }
        });
    }

    @Test
    public void should_generate_response_with_random_with_range_and_data_format() throws Exception {
        server.request(Moco.by(Moco.uri("/random"))).response(Moco.template("${random(100, '###.######')}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                String response = helper.get(RemoteTestUtils.remoteUrl("/random"));
                try {
                    double result = Double.parseDouble(response);
                    Assert.assertThat(result, Matchers.lessThan(100.0));
                    Assert.assertThat(result, Matchers.greaterThan(0.0));
                    String target = Iterables.get(Splitter.on('.').split(response), 1);
                    Assert.assertThat(target.length(), Matchers.lessThanOrEqualTo(6));
                } catch (NumberFormatException e) {
                    Assert.fail();
                }
            }
        });
    }

    @Test
    public void should_throw_exception_for_random_with_range_less_than_0() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${random(-10)}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                HttpResponse response = helper.getResponse(RemoteTestUtils.remoteUrl("/template"));
                Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(400));
            }
        });
    }

    @Test
    public void should_return_json() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.json.code} ${req.json.message}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                Assert.assertThat(helper.postContent(RemoteTestUtils.remoteUrl("/template"), "{\n\t\"code\":1,\n\t\"message\":\"message\"\n}"), CoreMatchers.is("1 message"));
            }
        });
    }

    @Test
    public void should_throw_exception_for_unknown_json() throws Exception {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${req.json.code} ${req.json.message}"));
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws IOException {
                HttpResponse response = helper.getResponse(RemoteTestUtils.remoteUrl("/template"));
                Assert.assertThat(response.getStatusLine().getStatusCode(), CoreMatchers.is(400));
            }
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_for_reserved_name_as_variable_nem() {
        server.request(Moco.by(Moco.uri("/template"))).response(Moco.template("${random}", "random", "bar"));
    }
}

