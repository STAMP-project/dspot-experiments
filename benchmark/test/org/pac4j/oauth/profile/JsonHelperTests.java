package org.pac4j.oauth.profile;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.facebook.FacebookObject;


/**
 * This class tests the {@link JsonHelper} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class JsonHelperTests implements TestsConstants {
    private static final String GOOD_TEXT_JSON = ((("{ \"" + (KEY)) + "\" : \"") + (VALUE)) + "\" }";

    private static final String GOOD_BOOLEAN_JSON = ((("{ \"" + (KEY)) + "\" : ") + (Boolean.TRUE)) + " }";

    private static final String GOOD_NUMBER_JSON = ("{ \"" + (KEY)) + "\" : 1 }";

    private static final String GOOD_NODE_JSON = ((("{ \"" + (KEY)) + "\" : ") + (JsonHelperTests.GOOD_TEXT_JSON)) + " }";

    private static final String BAD_JSON = "this_is_definitively_not_a_json_text";

    @Test
    public void testGetFirstNodeOk() {
        Assert.assertNotNull(JsonHelper.getFirstNode(JsonHelperTests.GOOD_TEXT_JSON));
    }

    @Test
    public void testGetFirstNodeKo() {
        Assert.assertNull(JsonHelper.getFirstNode(JsonHelperTests.BAD_JSON));
    }

    @Test
    public void testGetText() {
        Assert.assertEquals(VALUE, JsonHelper.getElement(JsonHelper.getFirstNode(JsonHelperTests.GOOD_TEXT_JSON), KEY));
    }

    @Test
    public void testGetNull() {
        Assert.assertNull(JsonHelper.getElement(null, KEY));
    }

    @Test
    public void testGetBadKey() {
        Assert.assertNull(JsonHelper.getElement(JsonHelper.getFirstNode(JsonHelperTests.GOOD_TEXT_JSON), ("bad" + (KEY))));
    }

    @Test
    public void testGetBoolean() {
        Assert.assertEquals(Boolean.TRUE, JsonHelper.getElement(JsonHelper.getFirstNode(JsonHelperTests.GOOD_BOOLEAN_JSON), KEY));
    }

    @Test
    public void testGetNumber() {
        Assert.assertEquals(1, JsonHelper.getElement(JsonHelper.getFirstNode(JsonHelperTests.GOOD_NUMBER_JSON), KEY));
    }

    @Test
    public void testGetNode() {
        Assert.assertEquals(JsonHelper.getFirstNode(JsonHelperTests.GOOD_TEXT_JSON), JsonHelper.getElement(JsonHelper.getFirstNode(JsonHelperTests.GOOD_NODE_JSON), KEY));
    }

    @Test
    public void testToJSONString() {
        final FacebookObject object = new FacebookObject();
        object.setId(ID);
        object.setName(NAME);
        Assert.assertEquals("\"{\\\"id\\\":\\\"id\\\",\\\"name\\\":\\\"name\\\"}\"", JsonHelper.toJSONString(JsonHelper.toJSONString(object)));
    }
}

