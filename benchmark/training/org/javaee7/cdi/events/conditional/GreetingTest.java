package org.javaee7.cdi.events.conditional;


import javax.inject.Inject;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Radim Hanus
 */
@RunWith(Arquillian.class)
public class GreetingTest {
    @Inject
    private EventSender sender;

    @Inject
    private EventReceiver receiver;

    @Test
    public void test() throws Exception {
        Assert.assertThat(sender, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(sender, CoreMatchers.instanceOf(GreetingSender.class));
        Assert.assertThat(receiver, CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(receiver, CoreMatchers.instanceOf(GreetingReceiver.class));
        // send a new greet but the receiver is not instantiated yet
        sender.send("Welcome");
        // default greet should be available (note that receiver has just been instantiated)
        Assert.assertEquals("Willkommen", receiver.getGreet());
        // send a new greet again
        sender.send("Welcome");
        // observer method was called so that new greet should be available
        Assert.assertEquals("Welcome", receiver.getGreet());
    }
}

