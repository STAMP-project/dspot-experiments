package net.bytebuddy.agent;


import java.io.File;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.agent.ByteBuddyAgent.AttachmentProvider.Accessor.Unavailable.INSTANCE;


public class ByteBuddyAgentAttachmentProviderTest {
    @Test
    public void testSimpleAccessor() throws Exception {
        File file = Mockito.mock(File.class);
        ByteBuddyAgent.AttachmentProvider.Accessor accessor = new ByteBuddyAgent.AttachmentProvider.Accessor.Simple.WithExternalAttachment(Void.class, Collections.singletonList(file));
        MatcherAssert.assertThat(accessor.isAvailable(), CoreMatchers.is(true));
        MatcherAssert.assertThat(accessor.getVirtualMachineType(), CoreMatchers.<Class<?>>is(Void.class));
        MatcherAssert.assertThat(accessor.getExternalAttachment().getVirtualMachineType(), CoreMatchers.is(Void.class.getName()));
        MatcherAssert.assertThat(accessor.getExternalAttachment().getClassPath(), CoreMatchers.is(Collections.singletonList(file)));
    }

    @Test(expected = IllegalStateException.class)
    public void testSimpleAccessorWithoutExternalAttachment() throws Exception {
        new ByteBuddyAgent.AttachmentProvider.Accessor.Simple.WithoutExternalAttachment(Void.class).getExternalAttachment();
    }

    @Test
    public void testUnavailableAccessor() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isAvailable(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testUnavailableAccessorThrowsExceptionForType() throws Exception {
        INSTANCE.getVirtualMachineType();
    }

    @Test(expected = IllegalStateException.class)
    public void testUnavailableAccessorThrowsExceptionForExternalAttachment() throws Exception {
        INSTANCE.getExternalAttachment();
    }
}

