package org.bukkit.plugin.messaging;


import org.bukkit.entity.Player;
import org.bukkit.plugin.TestPlugin;
import org.junit.Assert;
import org.junit.Test;


public class StandardMessengerTest {
    private int count = 0;

    @Test
    public void testIsReservedChannel() {
        Messenger messenger = getMessenger();
        Assert.assertTrue(messenger.isReservedChannel("REGISTER"));
        Assert.assertFalse(messenger.isReservedChannel("register"));
        Assert.assertTrue(messenger.isReservedChannel("UNREGISTER"));
        Assert.assertFalse(messenger.isReservedChannel("unregister"));
        Assert.assertFalse(messenger.isReservedChannel("notReserved"));
    }

    @Test
    public void testRegisterAndUnregisterOutgoingPluginChannel() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        Assert.assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        messenger.registerOutgoingPluginChannel(plugin, "foo");
        Assert.assertTrue(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        Assert.assertFalse(messenger.isOutgoingChannelRegistered(plugin, "bar"));
        messenger.unregisterOutgoingPluginChannel(plugin, "foo");
        Assert.assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
    }

    @Test(expected = ReservedChannelException.class)
    public void testReservedOutgoingRegistration() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        messenger.registerOutgoingPluginChannel(plugin, "REGISTER");
    }

    @Test
    public void testUnregisterOutgoingPluginChannel_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        Assert.assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        messenger.registerOutgoingPluginChannel(plugin, "foo");
        messenger.registerOutgoingPluginChannel(plugin, "bar");
        Assert.assertTrue(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        Assert.assertTrue(messenger.isOutgoingChannelRegistered(plugin, "bar"));
        messenger.unregisterOutgoingPluginChannel(plugin);
        Assert.assertFalse(messenger.isOutgoingChannelRegistered(plugin, "foo"));
        Assert.assertFalse(messenger.isOutgoingChannelRegistered(plugin, "bar"));
    }

    @Test
    public void testRegisterIncomingPluginChannel() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = new TestMessageListener("foo", "bar".getBytes());
        Player player = TestPlayer.getInstance();
        PluginMessageListenerRegistration registration = messenger.registerIncomingPluginChannel(plugin, "foo", listener);
        Assert.assertTrue(registration.isValid());
        Assert.assertTrue(messenger.isIncomingChannelRegistered(plugin, "foo"));
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        Assert.assertTrue(listener.hasReceived());
        messenger.unregisterIncomingPluginChannel(plugin, "foo", listener);
        listener.reset();
        Assert.assertFalse(registration.isValid());
        Assert.assertFalse(messenger.isIncomingChannelRegistered(plugin, "foo"));
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        Assert.assertFalse(listener.hasReceived());
    }

    @Test(expected = ReservedChannelException.class)
    public void testReservedIncomingRegistration() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        messenger.registerIncomingPluginChannel(plugin, "REGISTER", new TestMessageListener("foo", "bar".getBytes()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateIncomingRegistration() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener = new TestMessageListener("foo", "bar".getBytes());
        messenger.registerIncomingPluginChannel(plugin, "baz", listener);
        messenger.registerIncomingPluginChannel(plugin, "baz", listener);
    }

    @Test
    public void testUnregisterIncomingPluginChannel_Plugin_String() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener1 = new TestMessageListener("foo", "bar".getBytes());
        TestMessageListener listener2 = new TestMessageListener("baz", "qux".getBytes());
        Player player = TestPlayer.getInstance();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin, "foo", listener1);
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin, "baz", listener2);
        Assert.assertTrue(registration1.isValid());
        Assert.assertTrue(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        Assert.assertTrue(listener1.hasReceived());
        Assert.assertTrue(listener2.hasReceived());
        messenger.unregisterIncomingPluginChannel(plugin, "foo");
        listener1.reset();
        listener2.reset();
        Assert.assertFalse(registration1.isValid());
        Assert.assertTrue(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        Assert.assertFalse(listener1.hasReceived());
        Assert.assertTrue(listener2.hasReceived());
    }

    @Test
    public void testUnregisterIncomingPluginChannel_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin = getPlugin();
        TestMessageListener listener1 = new TestMessageListener("foo", "bar".getBytes());
        TestMessageListener listener2 = new TestMessageListener("baz", "qux".getBytes());
        Player player = TestPlayer.getInstance();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin, "foo", listener1);
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin, "baz", listener2);
        Assert.assertTrue(registration1.isValid());
        Assert.assertTrue(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        Assert.assertTrue(listener1.hasReceived());
        Assert.assertTrue(listener2.hasReceived());
        messenger.unregisterIncomingPluginChannel(plugin);
        listener1.reset();
        listener2.reset();
        Assert.assertFalse(registration1.isValid());
        Assert.assertFalse(registration2.isValid());
        messenger.dispatchIncomingMessage(player, "foo", "bar".getBytes());
        messenger.dispatchIncomingMessage(player, "baz", "qux".getBytes());
        Assert.assertFalse(listener1.hasReceived());
        Assert.assertFalse(listener2.hasReceived());
    }

    @Test
    public void testGetOutgoingChannels() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        StandardMessengerTest.assertEquals(messenger.getOutgoingChannels());
        messenger.registerOutgoingPluginChannel(plugin1, "foo");
        messenger.registerOutgoingPluginChannel(plugin1, "bar");
        messenger.registerOutgoingPluginChannel(plugin2, "baz");
        messenger.registerOutgoingPluginChannel(plugin2, "baz");
        StandardMessengerTest.assertEquals(messenger.getOutgoingChannels(), "foo", "bar", "baz");
    }

    @Test
    public void testGetOutgoingChannels_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();
        messenger.registerOutgoingPluginChannel(plugin1, "foo");
        messenger.registerOutgoingPluginChannel(plugin1, "bar");
        messenger.registerOutgoingPluginChannel(plugin2, "baz");
        messenger.registerOutgoingPluginChannel(plugin2, "qux");
        StandardMessengerTest.assertEquals(messenger.getOutgoingChannels(plugin1), "foo", "bar");
        StandardMessengerTest.assertEquals(messenger.getOutgoingChannels(plugin2), "baz", "qux");
        StandardMessengerTest.assertEquals(messenger.getOutgoingChannels(plugin3));
    }

    @Test
    public void testGetIncomingChannels() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        StandardMessengerTest.assertEquals(messenger.getIncomingChannels());
        messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        StandardMessengerTest.assertEquals(messenger.getIncomingChannels(), "foo", "bar", "baz");
    }

    @Test
    public void testGetIncomingChannels_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();
        messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        messenger.registerIncomingPluginChannel(plugin2, "qux", new TestMessageListener("foo", "bar".getBytes()));
        StandardMessengerTest.assertEquals(messenger.getIncomingChannels(plugin1), "foo", "bar");
        StandardMessengerTest.assertEquals(messenger.getIncomingChannels(plugin2), "baz", "qux");
        StandardMessengerTest.assertEquals(messenger.getIncomingChannels(plugin3));
    }

    @Test
    public void testGetIncomingChannelRegistrations_Plugin() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration3 = messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration4 = messenger.registerIncomingPluginChannel(plugin2, "qux", new TestMessageListener("foo", "bar".getBytes()));
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin1), registration1, registration2);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin2), registration3, registration4);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannels(plugin3));
    }

    @Test
    public void testGetIncomingChannelRegistrations_String() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration3 = messenger.registerIncomingPluginChannel(plugin2, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration4 = messenger.registerIncomingPluginChannel(plugin2, "bar", new TestMessageListener("foo", "bar".getBytes()));
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations("foo"), registration1, registration3);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations("bar"), registration2, registration4);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations("baz"));
    }

    @Test
    public void testGetIncomingChannelRegistrations_Plugin_String() {
        Messenger messenger = getMessenger();
        TestPlugin plugin1 = getPlugin();
        TestPlugin plugin2 = getPlugin();
        TestPlugin plugin3 = getPlugin();
        PluginMessageListenerRegistration registration1 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration2 = messenger.registerIncomingPluginChannel(plugin1, "foo", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration3 = messenger.registerIncomingPluginChannel(plugin1, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration4 = messenger.registerIncomingPluginChannel(plugin2, "bar", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration5 = messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        PluginMessageListenerRegistration registration6 = messenger.registerIncomingPluginChannel(plugin2, "baz", new TestMessageListener("foo", "bar".getBytes()));
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin1, "foo"), registration1, registration2);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin1, "bar"), registration3);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin2, "bar"), registration4);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin2, "baz"), registration5, registration6);
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin1, "baz"));
        StandardMessengerTest.assertEquals(messenger.getIncomingChannelRegistrations(plugin3, "qux"));
    }
}

