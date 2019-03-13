package org.javaee7.cdi.events;


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
        // send a new greet, default greet "Willkommen" should be overwritten
        sender.send("Welcome");
        // receiver must not belongs to the dependent pseudo-scope since we are checking the result
        Assert.assertEquals("Welcome", receiver.getGreet());
    }
}

