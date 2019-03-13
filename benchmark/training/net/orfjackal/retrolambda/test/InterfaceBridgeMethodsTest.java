/**
 * Copyright ? 2013-2014 Esko Luontola <www.orfjackal.net>
 */
/**
 * This software is released under the Apache License 2.0.
 */
/**
 * The license text is at http://www.apache.org/licenses/LICENSE-2.0
 */
package net.orfjackal.retrolambda.test;


import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class InterfaceBridgeMethodsTest {
    /**
     * JDK 8 adds a bridge method to an interface when it overrides a method
     * from the parent interface and refines its return type. This uses Java 8's
     * default methods feature, which won't work on Java 7 and below, so we have
     * to remove it for it - this makes the bytecode same as what JDK 7 produces.
     */
    @Test
    @SuppressWarnings("Convert2Lambda")
    public void will_remove_bridge_methods_from_interfaces() {
        InterfaceBridgeMethodsTest.BridgeChild child = new InterfaceBridgeMethodsTest.BridgeChild() {
            @Override
            public String foo() {
                return "foo";
            }
        };
        MatcherAssert.assertThat("direct call", child.foo(), Matchers.is("foo"));
        MatcherAssert.assertThat("bridged call", ((InterfaceBridgeMethodsTest.BridgeParent) (child)).foo(), Matchers.is(((Object) ("foo"))));
    }

    public interface BridgeParent {
        Object foo();
    }

    public interface BridgeChild extends InterfaceBridgeMethodsTest.BridgeParent {
        @Override
        String foo();// refined return type

    }
}

