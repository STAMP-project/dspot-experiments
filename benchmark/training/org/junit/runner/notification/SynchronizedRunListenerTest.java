package org.junit.runner.notification;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link SynchronizedRunListener}.
 *
 * @author kcooney (Kevin Cooney)
 */
public class SynchronizedRunListenerTest {
    private static class MethodSignature {
        private final Method fMethod;

        private final String fName;

        private final List<Class<?>> fParameterTypes;

        public MethodSignature(Method method) {
            fMethod = method;
            fName = method.getName();
            fParameterTypes = Arrays.asList(method.getParameterTypes());
        }

        @Override
        public String toString() {
            return fMethod.toString();
        }

        @Override
        public int hashCode() {
            return fName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (!(obj instanceof SynchronizedRunListenerTest.MethodSignature)) {
                return false;
            }
            SynchronizedRunListenerTest.MethodSignature that = ((SynchronizedRunListenerTest.MethodSignature) (obj));
            return (fName.equals(that.fName)) && (fParameterTypes.equals(that.fParameterTypes));
        }
    }

    @Test
    public void overridesAllMethodsInRunListener() {
        Set<SynchronizedRunListenerTest.MethodSignature> runListenerMethods = getAllDeclaredMethods(RunListener.class);
        Set<SynchronizedRunListenerTest.MethodSignature> synchronizedRunListenerMethods = getAllDeclaredMethods(SynchronizedRunListener.class);
        Assert.assertTrue(synchronizedRunListenerMethods.containsAll(runListenerMethods));
    }

    private static class NamedListener extends RunListener {
        private final String fName;

        public NamedListener(String name) {
            fName = name;
        }

        @Override
        public String toString() {
            return "NamedListener";
        }

        @Override
        public int hashCode() {
            return fName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if ((this) == obj) {
                return true;
            }
            if (!(obj instanceof SynchronizedRunListenerTest.NamedListener)) {
                return false;
            }
            SynchronizedRunListenerTest.NamedListener that = ((SynchronizedRunListenerTest.NamedListener) (obj));
            return this.fName.equals(that.fName);
        }
    }

    @Test
    public void namedListenerCorrectlyImplementsEqualsAndHashCode() {
        SynchronizedRunListenerTest.NamedListener listener1 = new SynchronizedRunListenerTest.NamedListener("blue");
        SynchronizedRunListenerTest.NamedListener listener2 = new SynchronizedRunListenerTest.NamedListener("blue");
        SynchronizedRunListenerTest.NamedListener listener3 = new SynchronizedRunListenerTest.NamedListener("red");
        Assert.assertTrue(listener1.equals(listener1));
        Assert.assertTrue(listener2.equals(listener2));
        Assert.assertTrue(listener3.equals(listener3));
        Assert.assertFalse(listener1.equals(null));
        Assert.assertFalse(listener1.equals(new Object()));
        Assert.assertTrue(listener1.equals(listener2));
        Assert.assertTrue(listener2.equals(listener1));
        Assert.assertFalse(listener1.equals(listener3));
        Assert.assertFalse(listener3.equals(listener1));
        Assert.assertEquals(listener1.hashCode(), listener2.hashCode());
        Assert.assertNotEquals(listener1.hashCode(), listener3.hashCode());
    }

    @Test
    public void toStringDelegates() {
        SynchronizedRunListenerTest.NamedListener listener = new SynchronizedRunListenerTest.NamedListener("blue");
        Assert.assertEquals("NamedListener", listener.toString());
        Assert.assertEquals("NamedListener (with synchronization wrapper)", wrap(listener).toString());
    }

    @Test
    public void equalsDelegates() {
        SynchronizedRunListenerTest.NamedListener listener1 = new SynchronizedRunListenerTest.NamedListener("blue");
        SynchronizedRunListenerTest.NamedListener listener2 = new SynchronizedRunListenerTest.NamedListener("blue");
        SynchronizedRunListenerTest.NamedListener listener3 = new SynchronizedRunListenerTest.NamedListener("red");
        Assert.assertEquals(wrap(listener1), wrap(listener1));
        Assert.assertEquals(wrap(listener1), wrap(listener2));
        Assert.assertNotEquals(wrap(listener1), wrap(listener3));
        Assert.assertNotEquals(wrap(listener1), listener1);
        Assert.assertNotEquals(listener1, wrap(listener1));
    }

    @Test
    public void hashCodeDelegates() {
        SynchronizedRunListenerTest.NamedListener listener = new SynchronizedRunListenerTest.NamedListener("blue");
        Assert.assertEquals(listener.hashCode(), wrap(listener).hashCode());
    }
}

