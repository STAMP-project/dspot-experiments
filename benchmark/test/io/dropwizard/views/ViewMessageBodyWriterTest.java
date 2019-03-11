package io.dropwizard.views;


import HttpHeaders.ACCEPT_LANGUAGE;
import Timer.Context;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.message.internal.HeaderValueException;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ViewMessageBodyWriterTest {
    public ContainerRequest headers = Mockito.mock(ContainerRequest.class);

    public MetricRegistry metricRegistry = Mockito.mock(MetricRegistry.class);

    public View view = Mockito.mock(View.class);

    public OutputStream stream = Mockito.mock(OutputStream.class);

    public Timer timer = Mockito.mock(Timer.class);

    public Context timerContext = Mockito.mock(Context.class);

    @Test
    public void writeToShouldUseValidRenderer() throws IOException {
        final ViewRenderer renderable = Mockito.mock(ViewRenderer.class);
        final ViewRenderer nonRenderable = Mockito.mock(ViewRenderer.class);
        final Locale locale = new Locale("en-US");
        Mockito.when(metricRegistry.timer(ArgumentMatchers.anyString())).thenReturn(timer);
        Mockito.when(timer.time()).thenReturn(timerContext);
        Mockito.when(renderable.isRenderable(view)).thenReturn(true);
        Mockito.when(nonRenderable.isRenderable(view)).thenReturn(false);
        final ViewMessageBodyWriter writer = Mockito.spy(new ViewMessageBodyWriter(metricRegistry, Arrays.asList(nonRenderable, renderable)));
        Mockito.doReturn(locale).when(writer).detectLocale(ArgumentMatchers.any());
        writer.setHeaders(Mockito.mock(HttpHeaders.class));
        writer.writeTo(view, Class.class, Class.class, new Annotation[]{  }, new MediaType(), new javax.ws.rs.core.MultivaluedHashMap(), stream);
        Mockito.verify(nonRenderable).isRenderable(view);
        Mockito.verifyNoMoreInteractions(nonRenderable);
        Mockito.verify(renderable).isRenderable(view);
        Mockito.verify(renderable).render(view, locale, stream);
        Mockito.verify(timerContext).stop();
    }

    @Test
    public void writeToShouldThrowWhenNoValidRendererFound() {
        final ViewMessageBodyWriter writer = new ViewMessageBodyWriter(metricRegistry, Collections.emptyList());
        Mockito.when(metricRegistry.timer(ArgumentMatchers.anyString())).thenReturn(timer);
        Mockito.when(timer.time()).thenReturn(timerContext);
        assertThatExceptionOfType(WebApplicationException.class).isThrownBy(() -> {
            writer.writeTo(view, .class, .class, new Annotation[]{  }, new MediaType(), new MultivaluedHashMap<>(), stream);
        }).withCauseExactlyInstanceOf(ViewRenderException.class);
        Mockito.verify(timerContext).stop();
    }

    @Test
    public void writeToShouldHandleViewRenderingExceptions() throws IOException {
        final ViewRenderer renderer = Mockito.mock(ViewRenderer.class);
        final Locale locale = new Locale("en-US");
        final ViewRenderException exception = new ViewRenderException("oops");
        Mockito.when(metricRegistry.timer(ArgumentMatchers.anyString())).thenReturn(timer);
        Mockito.when(timer.time()).thenReturn(timerContext);
        Mockito.when(renderer.isRenderable(view)).thenReturn(true);
        Mockito.doThrow(exception).when(renderer).render(view, locale, stream);
        final ViewMessageBodyWriter writer = Mockito.spy(new ViewMessageBodyWriter(metricRegistry, Collections.singletonList(renderer)));
        Mockito.doReturn(locale).when(writer).detectLocale(ArgumentMatchers.any());
        writer.setHeaders(Mockito.mock(HttpHeaders.class));
        assertThatExceptionOfType(WebApplicationException.class).isThrownBy(() -> {
            writer.writeTo(view, .class, .class, new Annotation[]{  }, new MediaType(), new MultivaluedHashMap<>(), stream);
        }).withCause(exception);
        Mockito.verify(timerContext).stop();
    }

    @Test
    public void detectLocaleShouldHandleBadlyFormedHeader() {
        Mockito.when(headers.getAcceptableLanguages()).thenThrow(HeaderValueException.class);
        final ViewMessageBodyWriter writer = new ViewMessageBodyWriter(metricRegistry, Collections.emptyList());
        assertThatExceptionOfType(WebApplicationException.class).isThrownBy(() -> {
            writer.detectLocale(headers);
        });
    }

    @Test
    public void detectLocaleShouldReturnDefaultLocaleWhenHeaderNotSpecified() {
        // We call the real methods to make sure that 'getAcceptableLanguages' returns a locale with a wildcard
        // (which is their default value). This also validates that 'detectLocale' skips wildcard languages.
        Mockito.when(headers.getAcceptableLanguages()).thenCallRealMethod();
        Mockito.when(headers.getQualifiedAcceptableLanguages()).thenCallRealMethod();
        Mockito.when(headers.getHeaderString(ACCEPT_LANGUAGE)).thenReturn(null);
        final ViewMessageBodyWriter writer = new ViewMessageBodyWriter(metricRegistry, Collections.emptyList());
        final Locale result = writer.detectLocale(headers);
        assertThat(result).isSameAs(Locale.getDefault());
    }

    @Test
    public void detectLocaleShouldReturnCorrectLocale() {
        final Locale fakeLocale = new Locale("en-US");
        Mockito.when(headers.getAcceptableLanguages()).thenReturn(Collections.singletonList(fakeLocale));
        final ViewMessageBodyWriter writer = new ViewMessageBodyWriter(metricRegistry, Collections.emptyList());
        final Locale result = writer.detectLocale(headers);
        assertThat(result).isSameAs(fakeLocale);
    }
}

