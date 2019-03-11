package org.wildfly.clustering.web.session;


import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ImmutableHttpSessionAdapterTestCase {
    private final ImmutableSession session = Mockito.mock(ImmutableSession.class);

    private final ServletContext context = Mockito.mock(ServletContext.class);

    private final HttpSession httpSession = new ImmutableHttpSessionAdapter(this.session, this.context);

    @Test
    public void getId() {
        String expected = "session";
        Mockito.when(this.session.getId()).thenReturn(expected);
        String result = this.httpSession.getId();
        Assert.assertSame(expected, result);
    }

    @Test
    public void getCreationTime() {
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        Instant now = Instant.now();
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getCreationTime()).thenReturn(now);
        long result = this.httpSession.getCreationTime();
        Assert.assertEquals(now.toEpochMilli(), result);
    }

    @Test
    public void getLastAccessedTime() {
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        Instant now = Instant.now();
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getLastAccessedTime()).thenReturn(now);
        long result = this.httpSession.getLastAccessedTime();
        Assert.assertEquals(now.toEpochMilli(), result);
    }

    @Test
    public void getMaxInactiveInterval() {
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        Duration interval = Duration.of(100L, ChronoUnit.SECONDS);
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.getMaxInactiveInterval()).thenReturn(interval);
        int result = this.httpSession.getMaxInactiveInterval();
        Assert.assertEquals(interval.getSeconds(), result);
    }

    @Test
    public void setMaxInactiveInterval() {
        this.httpSession.setMaxInactiveInterval(10);
        Mockito.verifyZeroInteractions(this.session);
    }

    @Test
    public void getServletContext() {
        Assert.assertSame(this.context, this.httpSession.getServletContext());
    }

    @Test
    public void getAttributeNames() {
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        Set<String> expected = new TreeSet<>();
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getAttributeNames()).thenReturn(expected);
        Enumeration<String> result = this.httpSession.getAttributeNames();
        Assert.assertEquals(new ArrayList<>(expected), Collections.list(result));
    }

    @Test
    public void getAttribute() {
        SessionAttributes attributes = Mockito.mock(SessionAttributes.class);
        String name = "name";
        Object expected = new Object();
        Mockito.when(this.session.getAttributes()).thenReturn(attributes);
        Mockito.when(attributes.getAttribute(name)).thenReturn(expected);
        Object result = this.httpSession.getAttribute(name);
        Assert.assertSame(expected, result);
    }

    @Test
    public void setAttribute() {
        this.httpSession.setAttribute("name", "value");
        Mockito.verifyZeroInteractions(this.session);
    }

    @Test
    public void removeAttribute() {
        this.httpSession.removeAttribute("name");
        Mockito.verifyZeroInteractions(this.session);
    }

    @Test
    public void invalidate() {
        this.httpSession.invalidate();
        Mockito.verifyZeroInteractions(this.session);
    }

    @Test
    public void isNew() {
        SessionMetaData metaData = Mockito.mock(SessionMetaData.class);
        Mockito.when(this.session.getMetaData()).thenReturn(metaData);
        Mockito.when(metaData.isNew()).thenReturn(true, false);
        Assert.assertTrue(this.httpSession.isNew());
        Assert.assertFalse(this.httpSession.isNew());
    }
}

