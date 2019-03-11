/**
 * Copyright 2015-2019 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin2.autoconfigure.ui;


import com.linecorp.armeria.common.MediaType;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;


public class ZipkinUiAutoConfigurationTest {
    AnnotationConfigApplicationContext context;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void indexHtmlFromClasspath() {
        context = ZipkinUiAutoConfigurationTest.createContext();
        assertThat(context.getBean(ZipkinUiAutoConfiguration.class).indexHtml).isNotNull();
    }

    @Test
    public void indexContentType() throws Exception {
        context = ZipkinUiAutoConfigurationTest.createContext();
        assertThat(serveIndex().headers().contentType()).isEqualTo(MediaType.parse("text/html"));
    }

    @Test
    public void invalidIndexHtml() {
        // I failed to make Jsoup barf, even on nonsense like: "<head wait no I changed my mind this HTML is totally invalid <<<<<<<<<<<"
        // So let's just run with a case where the file doesn't exist
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.basepath:/foo/bar");
        ZipkinUiAutoConfiguration ui = context.getBean(ZipkinUiAutoConfiguration.class);
        ui.indexHtml = new ClassPathResource("does-not-exist.html");
        thrown.expect(RuntimeException.class);
        // There's a BeanInstantiationException nested in between BeanCreationException and IOException,
        // so we go one level deeper about causes. There's no `expectRootCause`.
        thrown.expectCause(ThrowableCauseMatcher.hasCause(ThrowableCauseMatcher.hasCause(CoreMatchers.isA(BeanCreationException.class))));
        serveIndex();
    }

    @Test
    public void canOverridesProperty_defaultLookback() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.defaultLookback:100");
        assertThat(context.getBean(ZipkinUiProperties.class).getDefaultLookback()).isEqualTo(100);
    }

    @Test
    public void canOverrideProperty_logsUrl() {
        final String url = "http://mycompany.com/kibana";
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty(("zipkin.ui.logs-url:" + url));
        assertThat(context.getBean(ZipkinUiProperties.class).getLogsUrl()).isEqualTo(url);
    }

    @Test
    public void logsUrlIsNullIfOverridenByEmpty() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.logs-url:");
        assertThat(context.getBean(ZipkinUiProperties.class).getLogsUrl()).isNull();
    }

    @Test
    public void logsUrlIsNullByDefault() {
        context = ZipkinUiAutoConfigurationTest.createContext();
        assertThat(context.getBean(ZipkinUiProperties.class).getLogsUrl()).isNull();
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    public void canOverridesProperty_disable() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.enabled:false");
        context.getBean(ZipkinUiProperties.class);
    }

    @Test
    public void canOverridesProperty_searchEnabled() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.search-enabled:false");
        assertThat(context.getBean(ZipkinUiProperties.class).isSearchEnabled()).isFalse();
    }

    @Test
    public void canOverrideProperty_dependencyLowErrorRate() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.dependency.low-error-rate:0.1");
        assertThat(context.getBean(ZipkinUiProperties.class).getDependency().getLowErrorRate()).isEqualTo(0.1F);
    }

    @Test
    public void canOverrideProperty_dependencyHighErrorRate() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.dependency.high-error-rate:0.1");
        assertThat(context.getBean(ZipkinUiProperties.class).getDependency().getHighErrorRate()).isEqualTo(0.1F);
    }

    @Test
    public void defaultBaseUrl_doesNotChangeResource() throws IOException {
        context = ZipkinUiAutoConfigurationTest.createContext();
        assertThat(new ByteArrayInputStream(serveIndex().content().array())).hasSameContentAs(getClass().getResourceAsStream("/zipkin-ui/index.html"));
    }

    @Test
    public void canOverideProperty_basePath() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.basepath:/foo/bar");
        assertThat(serveIndex().contentUtf8()).contains("<base href=\"/foo/bar/\">");
    }

    @Test
    public void lensCookieOverridesIndex() {
        context = ZipkinUiAutoConfigurationTest.createContext();
        assertThat(serveIndex(new DefaultCookie("lens", "true")).contentUtf8()).contains("zipkin-lens");
    }

    @Test
    public void canOverideProperty_specialCaseRoot() {
        context = ZipkinUiAutoConfigurationTest.createContextWithOverridenProperty("zipkin.ui.basepath:/");
        assertThat(serveIndex().contentUtf8()).contains("<base href=\"/\">");
    }
}

