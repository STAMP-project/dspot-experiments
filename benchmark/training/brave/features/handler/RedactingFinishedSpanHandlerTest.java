package brave.features.handler;


import brave.ScopedSpan;
import brave.Tracing;
import brave.handler.FinishedSpanHandler;
import brave.handler.MutableSpan;
import brave.handler.MutableSpan.AnnotationUpdater;
import brave.handler.MutableSpan.TagUpdater;
import brave.propagation.TraceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.Test;
import zipkin2.Span;


/**
 * One reason {@link brave.handler.MutableSpan} is mutable is to support redaction
 */
public class RedactingFinishedSpanHandlerTest {
    static final Pattern SSN = Pattern.compile("[0-9]{3}\\-[0-9]{2}\\-[0-9]{4}");

    enum ValueRedactor implements AnnotationUpdater , TagUpdater {

        INSTANCE;
        @Override
        public String update(String key, String value) {
            return RedactingFinishedSpanHandlerTest.ValueRedactor.maybeUpdateValue(value);
        }

        @Override
        public String update(long timestamp, String value) {
            return RedactingFinishedSpanHandlerTest.ValueRedactor.maybeUpdateValue(value);
        }

        /**
         * Simple example of a replacement pattern, deleting entries which only include SSNs
         */
        static String maybeUpdateValue(String value) {
            Matcher matcher = RedactingFinishedSpanHandlerTest.SSN.matcher(value);
            if (matcher.find()) {
                String matched = matcher.group(0);
                if (matched.equals(value))
                    return null;

                return value.replace(matched, "xxx-xx-xxxx");
            }
            return value;
        }
    }

    List<Span> spans = new ArrayList<>();

    Tracing tracing = Tracing.newBuilder().addFinishedSpanHandler(new FinishedSpanHandler() {
        @Override
        public boolean handle(TraceContext context, MutableSpan span) {
            span.forEachTag(RedactingFinishedSpanHandlerTest.ValueRedactor.INSTANCE);
            span.forEachAnnotation(RedactingFinishedSpanHandlerTest.ValueRedactor.INSTANCE);
            return true;
        }
    }).spanReporter(spans::add).build();

    @Test
    public void showRedaction() {
        ScopedSpan span = tracing.tracer().startScopedSpan("auditor");
        try {
            span.tag("a", "1");
            span.tag("b", "912-23-1433");
            span.annotate("SSN=912-23-1433");
            span.tag("c", "3");
        } finally {
            span.finish();
        }
        // SSN tag was nuked
        assertThat(spans.get(0).tags()).containsExactly(entry("a", "1"), entry("c", "3"));
        assertThat(spans.get(0).annotations()).flatExtracting(Annotation::value).containsExactly("SSN=xxx-xx-xxxx");
    }
}

