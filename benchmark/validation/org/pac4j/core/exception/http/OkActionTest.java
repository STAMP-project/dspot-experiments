package org.pac4j.core.exception.http;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link OkAction}.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public class OkActionTest implements TestsConstants {
    @Test
    public void testPost() {
        final OkAction action = OkAction.buildFormContentFromUrlAndData(TestsConstants.CALLBACK_URL, null);
        Assert.assertEquals(((((("<html>\n<body>\n<form action=\"" + (TestsConstants.CALLBACK_URL)) + "\" name=\"f\" method=\"post\">\n") + "<input value=\'POST\' type=\'submit\' />\n</form>\n") + "<script type=\'text/javascript\'>document.forms[\'f\'].submit();</script>\n") + "</body>\n</html>\n"), action.getContent());
    }

    @Test
    public void testPostWithData() {
        final Map<String, String[]> map = new HashMap<>();
        map.put(TestsConstants.NAME, new String[]{ TestsConstants.VALUE });
        final OkAction action = OkAction.buildFormContentFromUrlAndData(TestsConstants.CALLBACK_URL, map);
        Assert.assertEquals((((((((((("<html>\n<body>\n<form action=\"" + (TestsConstants.CALLBACK_URL)) + "\" name=\"f\" method=\"post\">\n") + "<input type=\'hidden\' name=\"") + (TestsConstants.NAME)) + "\" value=\"") + (TestsConstants.VALUE)) + "\" />\n") + "<input value=\'POST\' type=\'submit\' />\n</form>\n") + "<script type=\'text/javascript\'>document.forms[\'f\'].submit();</script>\n") + "</body>\n</html>\n"), action.getContent());
    }
}

