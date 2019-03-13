package com.vaadin.tests.event;


import com.vaadin.event.ShortcutAction;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests various things about shortcut actions.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ShortcutActionTest {
    private static final String[] KEYS = "a b c d e f g h i j k l m n o p q r s t u v w x y z".split("\\s+");

    @Test
    public void testHashCodeUniqueness() {
        HashSet<ShortcutAction> set = new HashSet<>();
        for (String modifier : new String[]{ "^", "&", "_", "&^", "&_", "_^", "&^_" }) {
            for (String key : ShortcutActionTest.KEYS) {
                ShortcutAction action = new ShortcutAction((modifier + key));
                for (ShortcutAction other : set) {
                    Assert.assertFalse(ShortcutActionTest.equals(action, other));
                }
                set.add(action);
            }
        }
    }

    @Test
    public void testModifierOrderIrrelevant() {
        for (String key : ShortcutActionTest.KEYS) {
            // two modifiers
            for (String modifier : new String[]{ "&^", "&_", "_^" }) {
                ShortcutAction action1 = new ShortcutAction((modifier + key));
                ShortcutAction action2 = new ShortcutAction((((modifier.substring(1)) + (modifier.substring(0, 1))) + key));
                Assert.assertTrue((modifier + key), ShortcutActionTest.equals(action1, action2));
            }
            // three modifiers
            ShortcutAction action1 = new ShortcutAction(("&^_" + key));
            for (String modifier : new String[]{ "&_^", "^&_", "^_&", "_^&", "_&^" }) {
                ShortcutAction action2 = new ShortcutAction((modifier + key));
                Assert.assertTrue((modifier + key), ShortcutActionTest.equals(action1, action2));
            }
        }
    }

    @Test
    public void testSameKeycodeDifferentCaptions() {
        ShortcutAction act1 = new ShortcutAction("E&xit");
        ShortcutAction act2 = new ShortcutAction("Lu&xtorpeda - Autystyczny");
        Assert.assertFalse(ShortcutActionTest.equals(act1, act2));
    }
}

