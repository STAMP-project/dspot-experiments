package net.bytebuddy.utility.privilege;


import java.lang.reflect.AccessibleObject;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class SetAccessibleActionTest {
    private static final String BAR = "bar";

    @Test
    public void testAccessAction() throws Exception {
        SetAccessibleActionTest.AccessibleObjectSpy accessibleObjectSpy = new SetAccessibleActionTest.AccessibleObjectSpy(SetAccessibleActionTest.Foo.class.getDeclaredField(SetAccessibleActionTest.BAR));
        MatcherAssert.assertThat(new SetAccessibleAction<SetAccessibleActionTest.AccessibleObjectSpy>(accessibleObjectSpy).run(), CoreMatchers.is(accessibleObjectSpy));
        MatcherAssert.assertThat(accessibleObjectSpy.accessible, CoreMatchers.is(true));
    }

    @SuppressWarnings("unused")
    private static class Foo {
        Object bar;

        Object qux;
    }

    private static class AccessibleObjectSpy extends AccessibleObject {
        private final AccessibleObject accessibleObject;

        public boolean accessible;

        public AccessibleObjectSpy(AccessibleObject accessibleObject) {
            this.accessibleObject = accessibleObject;
        }

        public void setAccessible(boolean flag) {
            accessible = flag;
            accessibleObject.setAccessible(flag);
        }
    }
}

