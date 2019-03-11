package org.stagemonitor.web.servlet.eum;


import MockTracer.SpanBuilder;
import io.opentracing.mock.MockSpan;
import io.opentracing.mock.MockTracer;
import java.util.HashMap;
import org.junit.Test;
import org.stagemonitor.web.servlet.eum.ClientSpanMetadataTagProcessor.ClientSpanMetadataDefinition;


public class ClientSpanMetadataTagProcessorTest {
    private MockTracer tracer;

    private SpanBuilder spanBuilder;

    private HashMap<String, ClientSpanMetadataDefinition> whitelistedValues;

    private HashMap<String, String[]> servletParameters;

    private ClientSpanMetadataTagProcessor clientSpanMetadataTagProcessor;

    @Test
    public void processSpanBuilderImpl_testSpecificLengthString() throws Exception {
        addMetadataDefinition("lengthLimitedTag", "string(10)");
        addServletParameter("m_lengthLimitedTag", "0123456789ZZZZ");
        processSpanBuilderImpl(spanBuilder, servletParameters);
        assertThat(tracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = tracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).hasSize(1);
        assertThat(mockSpan.tags().get("lengthLimitedTag")).isEqualTo("0123456789");
    }

    @Test
    public void processSpanBuilderImpl_testSpecificLengthStringWithSpacesInDefinition() throws Exception {
        addMetadataDefinition("lengthLimitedTagWithSpaces", "  string  (  10  )  ");
        addServletParameter("m_lengthLimitedTagWithSpaces", "0123456789ZZZZ");
        processSpanBuilderImpl(spanBuilder, servletParameters);
        assertThat(tracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = tracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).hasSize(1);
        assertThat(mockSpan.tags().get("lengthLimitedTagWithSpaces")).isEqualTo("0123456789");
    }

    @Test
    public void processSpanBuilderImpl_testNoExplicitLengthLimit() throws Exception {
        String value = "";
        for (int i = 0; i < 255; i++) {
            value += "0";
        }
        addServletParameter("m_tagWithoutExplicitLengthLimit", value);
        addMetadataDefinition("tagWithoutExplicitLengthLimit", "string");
        processSpanBuilderImpl(spanBuilder, servletParameters);
        assertThat(tracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = tracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).hasSize(1);
        assertThat(mockSpan.tags().get("tagWithoutExplicitLengthLimit").toString()).hasSize(ClientSpanTagProcessor.MAX_LENGTH);
    }

    @Test
    public void processSpanBuilderImpl_testMultipleParameters() throws Exception {
        addServletParameter("m_string", "string");
        addServletParameter("m_boolean", "true");
        addServletParameter("m_number", "42.5");
        addMetadataDefinition("string", "string");
        addMetadataDefinition("boolean", "boolean");
        addMetadataDefinition("number", "number");
        processSpanBuilderImpl(spanBuilder, servletParameters);
        assertThat(tracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = tracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).hasSize(3);
        assertThat(mockSpan.tags().get("string").toString()).isEqualTo("string");
        assertThat(mockSpan.tags().get("boolean").toString()).isEqualTo("true");
        assertThat(mockSpan.tags().get("number").toString()).isEqualTo("42.5");
    }

    @Test
    public void processSpanBuilderImpl_testDiscardsUndefinedMetadata() throws Exception {
        addServletParameter("m_tagWithoutExplicitLengthLimit", "someValue");
        processSpanBuilderImpl(spanBuilder, servletParameters);
        assertThat(tracer.finishedSpans()).hasSize(1);
        MockSpan mockSpan = tracer.finishedSpans().get(0);
        assertThat(mockSpan.tags()).isEmpty();
    }

    @Test(expected = IllegalArgumentException.class)
    public void processSpanBuilderImpl_testThrowsIllegalArgumentExceptionOnInvalidDefinition() {
        addServletParameter("m_metadataname", "something");
        addMetadataDefinition("metadataname", "invalid");
        processSpanBuilderImpl(spanBuilder, servletParameters);
    }
}

