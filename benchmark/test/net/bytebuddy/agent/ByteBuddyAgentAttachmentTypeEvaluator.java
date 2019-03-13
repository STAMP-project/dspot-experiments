package net.bytebuddy.agent;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static net.bytebuddy.agent.ByteBuddyAgent.AttachmentTypeEvaluator.Disabled.INSTANCE;


public class ByteBuddyAgentAttachmentTypeEvaluator {
    @Test
    public void testDisabled() throws Exception {
        Assert.assertThat(INSTANCE.requiresExternalAttachment("foo"), CoreMatchers.is(false));
    }
}

