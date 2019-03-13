package brave.spring.beans;


import HttpSampler.NEVER_SAMPLE;
import brave.Tracing;
import brave.http.HttpClientParser;
import brave.http.HttpServerParser;
import brave.http.HttpTracing;
import org.junit.Test;
import org.mockito.Mockito;


public class HttpTracingFactoryBeanTest {
    public static Tracing TRACING = Mockito.mock(Tracing.class);

    public static HttpClientParser CLIENT_PARSER = Mockito.mock(HttpClientParser.class);

    public static HttpServerParser SERVER_PARSER = Mockito.mock(HttpServerParser.class);

    XmlBeans context;

    @Test
    public void tracing() {
        context = new XmlBeans(((((("" + (("<bean id=\"httpTracing\" class=\"brave.spring.beans.HttpTracingFactoryBean\">\n" + "  <property name=\"tracing\">\n") + "    <util:constant static-field=\"")) + (getClass().getName())) + ".TRACING\"/>\n") + "  </property>\n") + "</bean>"));
        assertThat(context.getBean("httpTracing", HttpTracing.class)).extracting("tracing").containsExactly(HttpTracingFactoryBeanTest.TRACING);
    }

    @Test
    public void clientParser() {
        context = new XmlBeans((((((((((("" + (("<bean id=\"httpTracing\" class=\"brave.spring.beans.HttpTracingFactoryBean\">\n" + "  <property name=\"tracing\">\n") + "    <util:constant static-field=\"")) + (getClass().getName())) + ".TRACING\"/>\n") + "  </property>\n") + "  <property name=\"clientParser\">\n") + "    <util:constant static-field=\"") + (getClass().getName())) + ".CLIENT_PARSER\"/>\n") + "  </property>\n") + "</bean>"));
        assertThat(context.getBean("httpTracing", HttpTracing.class)).extracting("clientParser").containsExactly(HttpTracingFactoryBeanTest.CLIENT_PARSER);
    }

    @Test
    public void serverParser() {
        context = new XmlBeans((((((((((("" + (("<bean id=\"httpTracing\" class=\"brave.spring.beans.HttpTracingFactoryBean\">\n" + "  <property name=\"tracing\">\n") + "    <util:constant static-field=\"")) + (getClass().getName())) + ".TRACING\"/>\n") + "  </property>\n") + "  <property name=\"serverParser\">\n") + "    <util:constant static-field=\"") + (getClass().getName())) + ".SERVER_PARSER\"/>\n") + "  </property>\n") + "</bean>"));
        assertThat(context.getBean("httpTracing", HttpTracing.class)).extracting("serverParser").containsExactly(HttpTracingFactoryBeanTest.SERVER_PARSER);
    }

    @Test
    public void clientSampler() {
        context = new XmlBeans((((((((("" + (("<bean id=\"httpTracing\" class=\"brave.spring.beans.HttpTracingFactoryBean\">\n" + "  <property name=\"tracing\">\n") + "    <util:constant static-field=\"")) + (getClass().getName())) + ".TRACING\"/>\n") + "  </property>\n") + "  <property name=\"clientSampler\">\n") + "    <util:constant static-field=\"brave.http.HttpSampler.NEVER_SAMPLE\"/>\n") + "  </property>\n") + "</bean>"));
        assertThat(context.getBean("httpTracing", HttpTracing.class)).extracting("clientSampler").containsExactly(NEVER_SAMPLE);
    }

    @Test
    public void serverSampler() {
        context = new XmlBeans((((((((("" + (("<bean id=\"httpTracing\" class=\"brave.spring.beans.HttpTracingFactoryBean\">\n" + "  <property name=\"tracing\">\n") + "    <util:constant static-field=\"")) + (getClass().getName())) + ".TRACING\"/>\n") + "  </property>\n") + "  <property name=\"serverSampler\">\n") + "    <util:constant static-field=\"brave.http.HttpSampler.NEVER_SAMPLE\"/>\n") + "  </property>\n") + "</bean>"));
        assertThat(context.getBean("httpTracing", HttpTracing.class)).extracting("serverSampler").containsExactly(NEVER_SAMPLE);
    }
}

