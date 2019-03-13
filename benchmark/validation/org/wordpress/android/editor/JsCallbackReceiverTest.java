package org.wordpress.android.editor;


import AppLog.T.EDITOR;
import Log.DEBUG;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(sdk = 18)
@RunWith(RobolectricTestRunner.class)
public class JsCallbackReceiverTest {
    private static final String EDITOR_LOG_TAG = "WordPress-" + (EDITOR.toString());

    private JsCallbackReceiver mJsCallbackReceiver;

    @Test
    public void testCallbacksRecognized() {
        mJsCallbackReceiver.executeCallback("callback-dom-loaded", "");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-new-field", "field-name");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-input", "arguments");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-selection-changed", "arguments");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-selection-style", "arguments");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-focus-in", "");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-focus-out", "");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-image-replaced", "arguments");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-image-tap", "arguments");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-link-tap", "arguments");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-log", "arguments");
        assertNotLogged("Unhandled callback");
        mJsCallbackReceiver.executeCallback("callback-response-string", "arguments");
        assertNotLogged("Unhandled callback");
    }

    @Test
    public void testUnknownCallbackShouldBeLogged() {
        mJsCallbackReceiver.executeCallback("callback-does-not-exist", "content");
        assertLogged(DEBUG, JsCallbackReceiverTest.EDITOR_LOG_TAG, "Unhandled callback: callback-does-not-exist:content", null);
    }

    @Test
    public void testCallbackLog() {
        mJsCallbackReceiver.executeCallback("callback-log", "msg=test-message");
        assertLogged(DEBUG, JsCallbackReceiverTest.EDITOR_LOG_TAG, "callback-log: test-message", null);
    }
}

