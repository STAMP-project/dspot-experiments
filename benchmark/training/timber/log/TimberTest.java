package timber.log;


import Build.VERSION;
import Build.VERSION_CODES;
import Log.ASSERT;
import Log.DEBUG;
import Log.ERROR;
import Log.INFO;
import Log.VERBOSE;
import Log.WARN;
import Timber.DebugTree;
import Timber.Tree;
import android.util.Log;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


// 
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TimberTest {
    // NOTE: This class references the line number. Keep it at the top so it does not change.
    @Test
    public void debugTreeCanAlterCreatedTag() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String createStackElementTag(@NotNull
            StackTraceElement element) {
                return ((super.createStackElementTag(element)) + ':') + (element.getLineNumber());
            }
        });
        Timber.d("Test");
        TimberTest.assertLog().hasDebugMessage("TimberTest:41", "Test").hasNoMoreMessages();
    }

    @Test
    public void recursion() {
        Timber.Tree timber = Timber.asTree();
        try {
            Timber.plant(timber);
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("Cannot plant Timber into itself.");
        }
        try {
            Timber.plant(new Timber.Tree[]{ timber });
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().isEqualTo("Cannot plant Timber into itself.");
        }
    }

    @Test
    public void treeCount() {
        // inserts trees and checks if the amount of returned trees matches.
        assertThat(Timber.treeCount()).isEqualTo(0);
        for (int i = 1; i < 50; i++) {
            Timber.plant(new Timber.DebugTree());
            assertThat(Timber.treeCount()).isEqualTo(i);
        }
        Timber.uprootAll();
        assertThat(Timber.treeCount()).isEqualTo(0);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullTree() {
        Timber.Tree nullTree = null;
        try {
            Timber.plant(nullTree);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessageThat().isEqualTo("tree == null");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void nullTreeArray() {
        Timber[] nullTrees = null;
        try {
            Timber.plant(nullTrees);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessageThat().isEqualTo("trees == null");
        }
        nullTrees = new Timber.Tree[]{ null };
        try {
            Timber.plant(nullTrees);
            Assert.fail();
        } catch (NullPointerException e) {
            assertThat(e).hasMessageThat().isEqualTo("trees contains null");
        }
    }

    @Test
    public void forestReturnsAllPlanted() {
        Timber.DebugTree tree1 = new Timber.DebugTree();
        Timber.DebugTree tree2 = new Timber.DebugTree();
        Timber.plant(tree1);
        Timber.plant(tree2);
        assertThat(Timber.forest()).containsExactly(tree1, tree2);
    }

    @Test
    public void forestReturnsAllTreesPlanted() {
        Timber.DebugTree tree1 = new Timber.DebugTree();
        Timber.DebugTree tree2 = new Timber.DebugTree();
        Timber.plant(tree1, tree2);
        assertThat(Timber.forest()).containsExactly(tree1, tree2);
    }

    @Test
    public void uprootThrowsIfMissing() {
        try {
            Timber.uproot(new Timber.DebugTree());
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageThat().startsWith("Cannot uproot tree which is not planted: ");
        }
    }

    @Test
    public void uprootRemovesTree() {
        Timber.DebugTree tree1 = new Timber.DebugTree();
        Timber.DebugTree tree2 = new Timber.DebugTree();
        Timber.plant(tree1);
        Timber.plant(tree2);
        Timber.d("First");
        Timber.uproot(tree1);
        Timber.d("Second");
        TimberTest.assertLog().hasDebugMessage("TimberTest", "First").hasDebugMessage("TimberTest", "First").hasDebugMessage("TimberTest", "Second").hasNoMoreMessages();
    }

    @Test
    public void uprootAllRemovesAll() {
        Timber.DebugTree tree1 = new Timber.DebugTree();
        Timber.DebugTree tree2 = new Timber.DebugTree();
        Timber.plant(tree1);
        Timber.plant(tree2);
        Timber.d("First");
        Timber.uprootAll();
        Timber.d("Second");
        TimberTest.assertLog().hasDebugMessage("TimberTest", "First").hasDebugMessage("TimberTest", "First").hasNoMoreMessages();
    }

    @Test
    public void noArgsDoesNotFormat() {
        Timber.plant(new Timber.DebugTree());
        Timber.d("te%st");
        TimberTest.assertLog().hasDebugMessage("TimberTest", "te%st").hasNoMoreMessages();
    }

    @Test
    public void debugTreeTagGeneration() {
        Timber.plant(new Timber.DebugTree());
        Timber.d("Hello, world!");
        TimberTest.assertLog().hasDebugMessage("TimberTest", "Hello, world!").hasNoMoreMessages();
    }

    class ThisIsAReallyLongClassName {
        void run() {
            Timber.d("Hello, world!");
        }
    }

    @Config(sdk = 23)
    @Test
    public void debugTreeTagTruncation() {
        Timber.plant(new Timber.DebugTree());
        new TimberTest.ThisIsAReallyLongClassName().run();
        TimberTest.assertLog().hasDebugMessage("TimberTest$ThisIsAReall", "Hello, world!").hasNoMoreMessages();
    }

    @Config(sdk = 24)
    @Test
    public void debugTreeTagNoTruncation() {
        Timber.plant(new Timber.DebugTree());
        new TimberTest.ThisIsAReallyLongClassName().run();
        TimberTest.assertLog().hasDebugMessage("TimberTest$ThisIsAReallyLongClassName", "Hello, world!").hasNoMoreMessages();
    }

    @Test
    public void debugTreeTagGenerationStripsAnonymousClassMarker() {
        Timber.plant(new Timber.DebugTree());
        new Runnable() {
            @Override
            public void run() {
                Timber.d("Hello, world!");
                new Runnable() {
                    @Override
                    public void run() {
                        Timber.d("Hello, world!");
                    }
                }.run();
            }
        }.run();
        TimberTest.assertLog().hasDebugMessage("TimberTest", "Hello, world!").hasDebugMessage("TimberTest", "Hello, world!").hasNoMoreMessages();
    }

    @Test
    public void debugTreeGeneratedTagIsLoggable() {
        Timber.plant(new Timber.DebugTree() {
            private static final int MAX_TAG_LENGTH = 23;

            @Override
            protected void log(int priority, String tag, @NotNull
            String message, Throwable t) {
                try {
                    Assert.assertTrue(Log.isLoggable(tag, priority));
                    if ((VERSION.SDK_INT) < (VERSION_CODES.N)) {
                        Assert.assertTrue(((tag.length()) <= (MAX_TAG_LENGTH)));
                    }
                } catch (IllegalArgumentException e) {
                    Assert.fail(e.getMessage());
                }
                super.log(priority, tag, message, t);
            }
        });
        class ClassNameThatIsReallyReallyReallyLong {
            {
                Timber.i("Hello, world!");
            }
        }
        new ClassNameThatIsReallyReallyReallyLong();
        TimberTest.assertLog().hasInfoMessage("TimberTest$1ClassNameTh", "Hello, world!").hasNoMoreMessages();
    }

    @Test
    public void debugTreeCustomTag() {
        Timber.plant(new Timber.DebugTree());
        Timber.tag("Custom").d("Hello, world!");
        TimberTest.assertLog().hasDebugMessage("Custom", "Hello, world!").hasNoMoreMessages();
    }

    @Test
    public void messageWithException() {
        Timber.plant(new Timber.DebugTree());
        NullPointerException datThrowable = TimberTest.truncatedThrowable(NullPointerException.class);
        Timber.e(datThrowable, "OMFG!");
        TimberTest.assertExceptionLogged(ERROR, "OMFG!", "java.lang.NullPointerException");
    }

    @Test
    public void exceptionOnly() {
        Timber.plant(new Timber.DebugTree());
        Timber.v(TimberTest.truncatedThrowable(IllegalArgumentException.class));
        TimberTest.assertExceptionLogged(VERBOSE, null, "java.lang.IllegalArgumentException", "TimberTest", 0);
        Timber.i(TimberTest.truncatedThrowable(NullPointerException.class));
        TimberTest.assertExceptionLogged(INFO, null, "java.lang.NullPointerException", "TimberTest", 1);
        Timber.d(TimberTest.truncatedThrowable(UnsupportedOperationException.class));
        TimberTest.assertExceptionLogged(DEBUG, null, "java.lang.UnsupportedOperationException", "TimberTest", 2);
        Timber.w(TimberTest.truncatedThrowable(UnknownHostException.class));
        TimberTest.assertExceptionLogged(WARN, null, "java.net.UnknownHostException", "TimberTest", 3);
        Timber.e(TimberTest.truncatedThrowable(ConnectException.class));
        TimberTest.assertExceptionLogged(ERROR, null, "java.net.ConnectException", "TimberTest", 4);
        Timber.wtf(TimberTest.truncatedThrowable(AssertionError.class));
        TimberTest.assertExceptionLogged(ASSERT, null, "java.lang.AssertionError", "TimberTest", 5);
    }

    @Test
    public void exceptionOnlyCustomTag() {
        Timber.plant(new Timber.DebugTree());
        Timber.tag("Custom").v(TimberTest.truncatedThrowable(IllegalArgumentException.class));
        TimberTest.assertExceptionLogged(VERBOSE, null, "java.lang.IllegalArgumentException", "Custom", 0);
        Timber.tag("Custom").i(TimberTest.truncatedThrowable(NullPointerException.class));
        TimberTest.assertExceptionLogged(INFO, null, "java.lang.NullPointerException", "Custom", 1);
        Timber.tag("Custom").d(TimberTest.truncatedThrowable(UnsupportedOperationException.class));
        TimberTest.assertExceptionLogged(DEBUG, null, "java.lang.UnsupportedOperationException", "Custom", 2);
        Timber.tag("Custom").w(TimberTest.truncatedThrowable(UnknownHostException.class));
        TimberTest.assertExceptionLogged(WARN, null, "java.net.UnknownHostException", "Custom", 3);
        Timber.tag("Custom").e(TimberTest.truncatedThrowable(ConnectException.class));
        TimberTest.assertExceptionLogged(ERROR, null, "java.net.ConnectException", "Custom", 4);
        Timber.tag("Custom").wtf(TimberTest.truncatedThrowable(AssertionError.class));
        TimberTest.assertExceptionLogged(ASSERT, null, "java.lang.AssertionError", "Custom", 5);
    }

    @Test
    public void exceptionFromSpawnedThread() throws InterruptedException {
        Timber.plant(new Timber.DebugTree());
        final NullPointerException datThrowable = TimberTest.truncatedThrowable(NullPointerException.class);
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                Timber.e(datThrowable, "OMFG!");
                latch.countDown();
            }
        }.start();
        latch.await();
        TimberTest.assertExceptionLogged(ERROR, "OMFG!", "java.lang.NullPointerException");
    }

    @Test
    public void nullMessageWithThrowable() {
        Timber.plant(new Timber.DebugTree());
        NullPointerException datThrowable = TimberTest.truncatedThrowable(NullPointerException.class);
        Timber.e(datThrowable, null);
        TimberTest.assertExceptionLogged(ERROR, "", "java.lang.NullPointerException");
    }

    @Test
    public void chunkAcrossNewlinesAndLimit() {
        Timber.plant(new Timber.DebugTree());
        Timber.d((((((TimberTest.repeat('a', 3000)) + '\n') + (TimberTest.repeat('b', 6000))) + '\n') + (TimberTest.repeat('c', 3000))));
        TimberTest.assertLog().hasDebugMessage("TimberTest", TimberTest.repeat('a', 3000)).hasDebugMessage("TimberTest", TimberTest.repeat('b', 4000)).hasDebugMessage("TimberTest", TimberTest.repeat('b', 2000)).hasDebugMessage("TimberTest", TimberTest.repeat('c', 3000)).hasNoMoreMessages();
    }

    @Test
    public void nullMessageWithoutThrowable() {
        Timber.plant(new Timber.DebugTree());
        Timber.d(null);
        TimberTest.assertLog().hasNoMoreMessages();
    }

    @Test
    public void logMessageCallback() {
        final List<String> logs = new ArrayList<>();
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected void log(int priority, String tag, @NotNull
            String message, Throwable t) {
                logs.add(((((priority + " ") + tag) + " ") + message));
            }
        });
        Timber.v("Verbose");
        Timber.tag("Custom").v("Verbose");
        Timber.d("Debug");
        Timber.tag("Custom").d("Debug");
        Timber.i("Info");
        Timber.tag("Custom").i("Info");
        Timber.w("Warn");
        Timber.tag("Custom").w("Warn");
        Timber.e("Error");
        Timber.tag("Custom").e("Error");
        Timber.wtf("Assert");
        Timber.tag("Custom").wtf("Assert");
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        // 
        assertThat(logs).containsExactly("2 TimberTest Verbose", "2 Custom Verbose", "3 TimberTest Debug", "3 Custom Debug", "4 TimberTest Info", "4 Custom Info", "5 TimberTest Warn", "5 Custom Warn", "6 TimberTest Error", "6 Custom Error", "7 TimberTest Assert", "7 Custom Assert");
    }

    @Test
    public void logAtSpecifiedPriority() {
        Timber.plant(new Timber.DebugTree());
        Timber.log(VERBOSE, "Hello, World!");
        Timber.log(DEBUG, "Hello, World!");
        Timber.log(INFO, "Hello, World!");
        Timber.log(WARN, "Hello, World!");
        Timber.log(ERROR, "Hello, World!");
        Timber.log(ASSERT, "Hello, World!");
        TimberTest.assertLog().hasVerboseMessage("TimberTest", "Hello, World!").hasDebugMessage("TimberTest", "Hello, World!").hasInfoMessage("TimberTest", "Hello, World!").hasWarnMessage("TimberTest", "Hello, World!").hasErrorMessage("TimberTest", "Hello, World!").hasAssertMessage("TimberTest", "Hello, World!").hasNoMoreMessages();
    }

    @Test
    public void formatting() {
        Timber.plant(new Timber.DebugTree());
        Timber.v("Hello, %s!", "World");
        Timber.d("Hello, %s!", "World");
        Timber.i("Hello, %s!", "World");
        Timber.w("Hello, %s!", "World");
        Timber.e("Hello, %s!", "World");
        Timber.wtf("Hello, %s!", "World");
        TimberTest.assertLog().hasVerboseMessage("TimberTest", "Hello, World!").hasDebugMessage("TimberTest", "Hello, World!").hasInfoMessage("TimberTest", "Hello, World!").hasWarnMessage("TimberTest", "Hello, World!").hasErrorMessage("TimberTest", "Hello, World!").hasAssertMessage("TimberTest", "Hello, World!").hasNoMoreMessages();
    }

    // Explicitly testing deprecated variant.
    @SuppressWarnings("deprecation")
    @Test
    public void isLoggableControlsLogging() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected boolean isLoggable(int priority) {
                return priority == (Log.INFO);
            }
        });
        Timber.v("Hello, World!");
        Timber.d("Hello, World!");
        Timber.i("Hello, World!");
        Timber.w("Hello, World!");
        Timber.e("Hello, World!");
        Timber.wtf("Hello, World!");
        TimberTest.assertLog().hasInfoMessage("TimberTest", "Hello, World!").hasNoMoreMessages();
    }

    @Test
    public void isLoggableTagControlsLogging() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected boolean isLoggable(String tag, int priority) {
                return "FILTER".equals(tag);
            }
        });
        Timber.tag("FILTER").v("Hello, World!");
        Timber.d("Hello, World!");
        Timber.i("Hello, World!");
        Timber.w("Hello, World!");
        Timber.e("Hello, World!");
        Timber.wtf("Hello, World!");
        TimberTest.assertLog().hasVerboseMessage("FILTER", "Hello, World!").hasNoMoreMessages();
    }

    @Test
    public void logsUnknownHostExceptions() {
        Timber.plant(new Timber.DebugTree());
        Timber.e(TimberTest.truncatedThrowable(UnknownHostException.class), null);
        TimberTest.assertExceptionLogged(ERROR, "", "UnknownHostException");
    }

    @Test
    public void tagIsClearedWhenNotLoggable() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected boolean isLoggable(String tag, int priority) {
                return priority >= (Log.WARN);
            }
        });
        Timber.tag("NotLogged").i("Message not logged");
        Timber.w("Message logged");
        TimberTest.assertLog().hasWarnMessage("TimberTest", "Message logged").hasNoMoreMessages();
    }

    @Test
    public void logsWithCustomFormatter() {
        Timber.plant(new Timber.DebugTree() {
            @Override
            protected String formatMessage(@NotNull
            String message, @NotNull
            Object[] args) {
                return String.format(("Test formatting: " + message), args);
            }
        });
        Timber.d("Test message logged. %d", 100);
        TimberTest.assertLog().hasDebugMessage("TimberTest", "Test formatting: Test message logged. 100");
    }

    @Test
    public void nullArgumentObjectArray() {
        Timber.plant(new Timber.DebugTree());
        Timber.v("Test", ((Object[]) (null)));
        TimberTest.assertLog().hasVerboseMessage("TimberTest", "Test").hasNoMoreMessages();
    }

    private static final class LogAssert {
        private final List<LogItem> items;

        private int index = 0;

        private LogAssert(List<LogItem> items) {
            this.items = items;
        }

        public TimberTest.LogAssert hasVerboseMessage(String tag, String message) {
            return hasMessage(VERBOSE, tag, message);
        }

        public TimberTest.LogAssert hasDebugMessage(String tag, String message) {
            return hasMessage(DEBUG, tag, message);
        }

        public TimberTest.LogAssert hasInfoMessage(String tag, String message) {
            return hasMessage(INFO, tag, message);
        }

        public TimberTest.LogAssert hasWarnMessage(String tag, String message) {
            return hasMessage(WARN, tag, message);
        }

        public TimberTest.LogAssert hasErrorMessage(String tag, String message) {
            return hasMessage(ERROR, tag, message);
        }

        public TimberTest.LogAssert hasAssertMessage(String tag, String message) {
            return hasMessage(ASSERT, tag, message);
        }

        private TimberTest.LogAssert hasMessage(int priority, String tag, String message) {
            LogItem item = items.get(((index)++));
            assertThat(item.type).isEqualTo(priority);
            assertThat(item.tag).isEqualTo(tag);
            assertThat(item.msg).isEqualTo(message);
            return this;
        }

        public void hasNoMoreMessages() {
            assertThat(items).hasSize(index);
        }
    }
}

