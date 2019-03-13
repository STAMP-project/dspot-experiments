package org.pac4j.core.profile;


import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.TestsConstants;

import static InternalAttributeHandler.PREFIX_BOOLEAN;
import static InternalAttributeHandler.PREFIX_DATE;
import static InternalAttributeHandler.PREFIX_INT;
import static InternalAttributeHandler.PREFIX_LONG;
import static InternalAttributeHandler.PREFIX_SB64;
import static InternalAttributeHandler.PREFIX_URI;


/**
 * Tests {@link InternalAttributeHandler}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class InternalAttributeHandlerTests implements TestsConstants {
    private static final boolean BOOL = true;

    private static final int INT = 1;

    private static final long LONG = 2L;

    private static final Date DATE = new Date();

    private static final java.net.URI URL;

    private static final Color COLOR = new Color(1, 1, 1);

    static {
        try {
            URL = new java.net.URI("http://www.google.com");
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void noStringify() {
        final InternalAttributeHandler handler = new InternalAttributeHandler();
        assertAttribute(handler, null, null);
        assertAttribute(handler, TestsConstants.VALUE, TestsConstants.VALUE);
        assertAttribute(handler, InternalAttributeHandlerTests.BOOL, InternalAttributeHandlerTests.BOOL);
        assertAttribute(handler, InternalAttributeHandlerTests.INT, InternalAttributeHandlerTests.INT);
        assertAttribute(handler, InternalAttributeHandlerTests.LONG, InternalAttributeHandlerTests.LONG);
        assertAttribute(handler, InternalAttributeHandlerTests.DATE, InternalAttributeHandlerTests.DATE);
        assertAttribute(handler, InternalAttributeHandlerTests.URL, InternalAttributeHandlerTests.URL);
        assertAttribute(handler, InternalAttributeHandlerTests.COLOR, InternalAttributeHandlerTests.COLOR);
    }

    @Test
    public void stringify() {
        final InternalAttributeHandler handler = new InternalAttributeHandler();
        handler.setStringify(true);
        assertAttribute(handler, null, null);
        assertAttribute(handler, TestsConstants.VALUE, TestsConstants.VALUE);
        assertAttribute(handler, InternalAttributeHandlerTests.BOOL, ((PREFIX_BOOLEAN) + (InternalAttributeHandlerTests.BOOL)));
        assertAttribute(handler, InternalAttributeHandlerTests.INT, ((PREFIX_INT) + (InternalAttributeHandlerTests.INT)));
        assertAttribute(handler, InternalAttributeHandlerTests.LONG, ((PREFIX_LONG) + (InternalAttributeHandlerTests.LONG)));
        assertAttribute(handler, InternalAttributeHandlerTests.DATE, ((PREFIX_DATE) + (new SimpleDateFormat(Converters.DATE_TZ_GENERAL_FORMAT).format(InternalAttributeHandlerTests.DATE))));
        assertAttribute(handler, InternalAttributeHandlerTests.URL, ((PREFIX_URI) + (InternalAttributeHandlerTests.URL.toString())));
        assertAttribute(handler, InternalAttributeHandlerTests.COLOR, ((PREFIX_SB64) + "rO0ABXNyABxvcmcucGFjNGouY29yZS5wcm9maWxlLkNvbG9y/5w8lvR27osCAANJAARibHVlSQAFZ3JlZW5JAANyZWR4cAAAAAEAAAABAAAAAQ=="));
    }
}

