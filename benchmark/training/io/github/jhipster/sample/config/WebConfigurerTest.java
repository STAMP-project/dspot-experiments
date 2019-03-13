package io.github.jhipster.sample.config;


import HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpHeaders.ACCESS_CONTROL_MAX_AGE;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import HttpHeaders.VARY;
import JHipsterConstants.SPRING_PROFILE_DEVELOPMENT;
import JHipsterConstants.SPRING_PROFILE_PRODUCTION;
import JHipsterProperties.Http.Version.V_2_0;
import UndertowOptions.ENABLE_HTTP2;
import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.web.filter.CachingHttpHeadersFilter;
import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.io.FilenameUtils;
import org.h2.server.web.WebServlet;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xnio.OptionMap;


/**
 * Unit tests for the WebConfigurer class.
 *
 * @see WebConfigurer
 */
public class WebConfigurerTest {
    private WebConfigurer webConfigurer;

    private MockServletContext servletContext;

    private MockEnvironment env;

    private JHipsterProperties props;

    @Test
    public void testStartUpProdServletContext() throws ServletException {
        env.setActiveProfiles(SPRING_PROFILE_PRODUCTION);
        webConfigurer.onStartup(servletContext);
        Mockito.verify(servletContext).addFilter(ArgumentMatchers.eq("cachingHttpHeadersFilter"), ArgumentMatchers.any(CachingHttpHeadersFilter.class));
        Mockito.verify(servletContext, Mockito.never()).addServlet(ArgumentMatchers.eq("H2Console"), ArgumentMatchers.any(WebServlet.class));
    }

    @Test
    public void testStartUpDevServletContext() throws ServletException {
        env.setActiveProfiles(SPRING_PROFILE_DEVELOPMENT);
        webConfigurer.onStartup(servletContext);
        Mockito.verify(servletContext, Mockito.never()).addFilter(ArgumentMatchers.eq("cachingHttpHeadersFilter"), ArgumentMatchers.any(CachingHttpHeadersFilter.class));
        Mockito.verify(servletContext).addServlet(ArgumentMatchers.eq("H2Console"), ArgumentMatchers.any(WebServlet.class));
    }

    @Test
    public void testCustomizeServletContainer() {
        env.setActiveProfiles(SPRING_PROFILE_PRODUCTION);
        UndertowServletWebServerFactory container = new UndertowServletWebServerFactory();
        webConfigurer.customize(container);
        assertThat(get("abs")).isEqualTo("audio/x-mpeg");
        assertThat(get("html")).isEqualTo("text/html;charset=utf-8");
        assertThat(get("json")).isEqualTo("text/html;charset=utf-8");
        if ((container.getDocumentRoot()) != null) {
            assertThat(container.getDocumentRoot().getPath()).isEqualTo(FilenameUtils.separatorsToSystem("target/www"));
        }
        Builder builder = Undertow.builder();
        container.getBuilderCustomizers().forEach(( c) -> c.customize(builder));
        OptionMap.Builder serverOptions = ((OptionMap.Builder) (ReflectionTestUtils.getField(builder, "serverOptions")));
        assertThat(serverOptions.getMap().get(ENABLE_HTTP2)).isNull();
    }

    @Test
    public void testUndertowHttp2Enabled() {
        props.getHttp().setVersion(V_2_0);
        UndertowServletWebServerFactory container = new UndertowServletWebServerFactory();
        webConfigurer.customize(container);
        Builder builder = Undertow.builder();
        container.getBuilderCustomizers().forEach(( c) -> c.customize(builder));
        OptionMap.Builder serverOptions = ((OptionMap.Builder) (ReflectionTestUtils.getField(builder, "serverOptions")));
        assertThat(serverOptions.getMap().get(ENABLE_HTTP2)).isTrue();
    }

    @Test
    public void testCorsFilterOnApiPath() throws Exception {
        props.getCors().setAllowedOrigins(Collections.singletonList("*"));
        props.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        props.getCors().setAllowedHeaders(Collections.singletonList("*"));
        props.getCors().setMaxAge(1800L);
        props.getCors().setAllowCredentials(true);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController()).addFilters(webConfigurer.corsFilter()).build();
        mockMvc.perform(options("/api/test-cors").header(ORIGIN, "other.domain.com").header(ACCESS_CONTROL_REQUEST_METHOD, "POST")).andExpect(status().isOk()).andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com")).andExpect(header().string(VARY, "Origin")).andExpect(header().string(ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE")).andExpect(header().string(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")).andExpect(header().string(ACCESS_CONTROL_MAX_AGE, "1800"));
        mockMvc.perform(get("/api/test-cors").header(ORIGIN, "other.domain.com")).andExpect(status().isOk()).andExpect(header().string(ACCESS_CONTROL_ALLOW_ORIGIN, "other.domain.com"));
    }

    @Test
    public void testCorsFilterOnOtherPath() throws Exception {
        props.getCors().setAllowedOrigins(Collections.singletonList("*"));
        props.getCors().setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        props.getCors().setAllowedHeaders(Collections.singletonList("*"));
        props.getCors().setMaxAge(1800L);
        props.getCors().setAllowCredentials(true);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController()).addFilters(webConfigurer.corsFilter()).build();
        mockMvc.perform(get("/test/test-cors").header(ORIGIN, "other.domain.com")).andExpect(status().isOk()).andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testCorsFilterDeactivated() throws Exception {
        props.getCors().setAllowedOrigins(null);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController()).addFilters(webConfigurer.corsFilter()).build();
        mockMvc.perform(get("/api/test-cors").header(ORIGIN, "other.domain.com")).andExpect(status().isOk()).andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    public void testCorsFilterDeactivated2() throws Exception {
        props.getCors().setAllowedOrigins(new ArrayList());
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new WebConfigurerTestController()).addFilters(webConfigurer.corsFilter()).build();
        mockMvc.perform(get("/api/test-cors").header(ORIGIN, "other.domain.com")).andExpect(status().isOk()).andExpect(header().doesNotExist(ACCESS_CONTROL_ALLOW_ORIGIN));
    }
}

