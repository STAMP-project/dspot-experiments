package net.bytebuddy.agent;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;


public class AttacherTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Test
    public void testPseudoAttachment() throws Exception {
        AttacherTest.PseudoAttacher.ERROR.set(null);
        Attacher.main(new String[]{ AttacherTest.PseudoAttacher.class.getName(), AttacherTest.FOO, AttacherTest.BAR, "=" + (AttacherTest.QUX), AttacherTest.BAZ });
        if ((AttacherTest.PseudoAttacher.ERROR.get()) != null) {
            throw new AssertionError(AttacherTest.PseudoAttacher.ERROR.get());
        }
    }

    @Test
    public void testPseudoAttachmentEmptyArgument() throws Exception {
        AttacherTest.PseudoAttacherNoArgument.ERROR.set(null);
        Attacher.main(new String[]{ AttacherTest.PseudoAttacherNoArgument.class.getName(), AttacherTest.FOO, AttacherTest.BAR, "" });
        if ((AttacherTest.PseudoAttacherNoArgument.ERROR.get()) != null) {
            throw new AssertionError(AttacherTest.PseudoAttacherNoArgument.ERROR.get());
        }
    }

    @Test
    public void testPseudoAttachmentMissingArgument() throws Exception {
        AttacherTest.PseudoAttacherNoArgument.ERROR.set(null);
        Attacher.main(new String[]{ AttacherTest.PseudoAttacherNoArgument.class.getName(), AttacherTest.FOO, AttacherTest.BAR });
        if ((AttacherTest.PseudoAttacherNoArgument.ERROR.get()) != null) {
            throw new AssertionError(AttacherTest.PseudoAttacherNoArgument.ERROR.get());
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testConstructorThrowsException() throws Exception {
        Constructor<?> constructor = Attacher.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            throw ((Exception) (exception.getCause()));
        }
    }

    @SuppressWarnings("unused")
    public static class PseudoAttacher {
        static final ThreadLocal<String> ERROR = new ThreadLocal<String>();

        public static AttacherTest.PseudoAttacher attach(String processId) {
            if (!(processId.equals(AttacherTest.FOO))) {
                AttacherTest.PseudoAttacher.ERROR.set(("Unexpected process id: " + processId));
            }
            return new AttacherTest.PseudoAttacher();
        }

        public void loadAgent(String path, String argument) {
            if (!(path.equals(AttacherTest.BAR))) {
                AttacherTest.PseudoAttacher.ERROR.set(("Unexpected file: " + path));
            } else
                if (!(argument.equals((((AttacherTest.QUX) + " ") + (AttacherTest.BAZ))))) {
                    AttacherTest.PseudoAttacher.ERROR.set(("Unexpected argument: " + argument));
                }

        }

        public void detach() {
        }
    }

    @SuppressWarnings("unused")
    public static class PseudoAttacherNoArgument {
        static final ThreadLocal<String> ERROR = new ThreadLocal<String>();

        public static AttacherTest.PseudoAttacherNoArgument attach(String processId) {
            if (!(processId.equals(AttacherTest.FOO))) {
                AttacherTest.PseudoAttacherNoArgument.ERROR.set(("Unexpected process id: " + processId));
            }
            return new AttacherTest.PseudoAttacherNoArgument();
        }

        public void loadAgent(String path, String argument) {
            if (!(path.equals(AttacherTest.BAR))) {
                AttacherTest.PseudoAttacherNoArgument.ERROR.set(("Unexpected file: " + path));
            } else
                if (argument != null) {
                    AttacherTest.PseudoAttacherNoArgument.ERROR.set(("Unexpected argument: " + argument));
                }

        }

        public void detach() {
        }
    }
}

