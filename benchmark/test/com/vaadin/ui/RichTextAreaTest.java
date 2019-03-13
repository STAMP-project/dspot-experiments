package com.vaadin.ui;


import com.vaadin.server.ServerRpcManager;
import com.vaadin.server.ServerRpcManager.RpcInvocationException;
import com.vaadin.shared.ui.richtextarea.RichTextAreaServerRpc;
import com.vaadin.tests.util.MockUI;
import org.junit.Assert;
import org.junit.Test;


public class RichTextAreaTest {
    @Test
    public void initiallyEmpty() {
        RichTextArea tf = new RichTextArea();
        Assert.assertTrue(tf.isEmpty());
    }

    @Test
    public void setValueServerWhenReadOnly() {
        RichTextArea tf = new RichTextArea();
        tf.setReadOnly(true);
        tf.setValue("foo");
        Assert.assertEquals("foo", tf.getValue());
    }

    @Test
    public void diffStateAfterClientSetValueWhenReadOnly() {
        UI ui = new MockUI();
        // If the client has a non-readonly text field which is set to read-only
        // on the server, then any update from the client must cause both the
        // readonly state and the old value to be sent
        RichTextArea rta = new RichTextArea();
        ui.setContent(rta);
        rta.setValue("bar");
        rta.setReadOnly(true);
        ComponentTest.syncToClient(rta);
        // Client thinks the field says "foo" but it won't be updated because
        // the field is readonly
        ServerRpcManager.getRpcProxy(rta, RichTextAreaServerRpc.class).setText("foo");
        // The real value will be sent back as long as the field is marked as
        // dirty and diffstate contains what the client has
        Assert.assertEquals("foo", getDiffStateString(rta, "value"));
        Assert.assertTrue("Component should be marked dirty", ComponentTest.isDirty(rta));
    }

    @Test
    public void setValueClientNotSentBack() throws RpcInvocationException {
        UI ui = new MockUI();
        RichTextArea rta = new RichTextArea();
        ui.setContent(rta);
        rta.setValue("bar");
        ComponentTest.updateDiffState(rta);
        ServerRpcManager.getRpcProxy(rta, RichTextAreaServerRpc.class).setText("foo");
        Assert.assertEquals("foo", getDiffStateString(rta, "value"));
    }

    @Test
    public void setValueClientRefusedWhenReadOnly() {
        RichTextArea tf = new RichTextArea();
        tf.setValue("bar");
        tf.setReadOnly(true);
        tf.setValue("foo", true);
        Assert.assertEquals("bar", tf.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void setValue_nullValue_throwsNPE() {
        RichTextArea tf = new RichTextArea();
        tf.setValue(null);
    }

    @Test
    public void emptyAfterClear() {
        RichTextArea tf = new RichTextArea();
        tf.setValue("foobar");
        Assert.assertFalse(tf.isEmpty());
        tf.clear();
        Assert.assertTrue(tf.isEmpty());
    }
}

