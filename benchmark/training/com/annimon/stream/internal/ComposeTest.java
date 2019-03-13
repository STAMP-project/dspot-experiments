package com.annimon.stream.internal;


import java.io.Closeable;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ComposeTest {
    @Test
    public void testPrivateConstructor() {
        Assert.assertThat(Compose.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testComposeRunnables() {
        final int[] counter = new int[]{ 0 };
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
            }
        };
        Runnable composed = Compose.runnables(runnable, runnable);
        composed.run();
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeRunnablesWithExceptionOnFirst() {
        final int[] counter = new int[]{ 0 };
        final Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
                throw new IllegalStateException("ok");
            }
        };
        final Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
            }
        };
        Runnable composed = Compose.runnables(runnable1, runnable2);
        try {
            composed.run();
        } catch (IllegalStateException ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.is("ok"));
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeRunnablesWithExceptionOnBoth() {
        final int[] counter = new int[]{ 0 };
        final Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
                throw new IllegalStateException("1");
            }
        };
        final Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
                throw new IllegalArgumentException("2");
            }
        };
        Runnable composed = Compose.runnables(runnable1, runnable2);
        try {
            composed.run();
        } catch (IllegalStateException ex1) {
            Assert.assertThat(ex1.getMessage(), CoreMatchers.is("1"));
        } catch (IllegalArgumentException ex2) {
            Assert.fail();
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeRunnablesWithError() {
        final int[] counter = new int[]{ 0 };
        final Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
                throw new AssertionError("1");
            }
        };
        final Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                (counter[0])++;
                throw new AssertionError("2");
            }
        };
        Runnable composed = Compose.runnables(runnable1, runnable2);
        try {
            composed.run();
        } catch (AssertionError error) {
            Assert.assertThat(error.getMessage(), CoreMatchers.is("1"));
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeCloseables() {
        final int[] counter = new int[]{ 0 };
        final Closeable closeable = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
            }
        };
        Runnable composed = Compose.closeables(closeable, closeable);
        composed.run();
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeCloseablesWithExceptionOnFirst() {
        final int[] counter = new int[]{ 0 };
        final Closeable closeable1 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
                throw new IllegalStateException("ok");
            }
        };
        final Closeable closeable2 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
            }
        };
        Runnable composed = Compose.closeables(closeable1, closeable2);
        try {
            composed.run();
        } catch (IllegalStateException ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.is("ok"));
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeCloseablesWithExceptionOnSecond() {
        final int[] counter = new int[]{ 0 };
        final Closeable closeable1 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
            }
        };
        final Closeable closeable2 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
                throw new IllegalArgumentException("ok");
            }
        };
        Runnable composed = Compose.closeables(closeable1, closeable2);
        try {
            composed.run();
        } catch (IllegalArgumentException ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.is("ok"));
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeCloseablesWithWrappedExceptionOnSecond() {
        final int[] counter = new int[]{ 0 };
        final Closeable closeable1 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
            }
        };
        final Closeable closeable2 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
                throw new IOException("ok");
            }
        };
        Runnable composed = Compose.closeables(closeable1, closeable2);
        try {
            composed.run();
        } catch (RuntimeException ex) {
            Assert.assertThat(ex.getCause(), CoreMatchers.instanceOf(IOException.class));
            Assert.assertThat(ex.getCause().getMessage(), CoreMatchers.is("ok"));
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeCloseablesWithError() {
        final int[] counter = new int[]{ 0 };
        final Closeable closeable1 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
                throw new AssertionError("1");
            }
        };
        final Closeable closeable2 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
                throw new AssertionError("2");
            }
        };
        Runnable composed = Compose.closeables(closeable1, closeable2);
        try {
            composed.run();
        } catch (AssertionError error) {
            Assert.assertThat(error.getMessage(), CoreMatchers.is("1"));
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }

    @Test
    public void testComposeCloseablesWithErrorOnSecond() {
        final int[] counter = new int[]{ 0 };
        final Closeable closeable1 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
            }
        };
        final Closeable closeable2 = new Closeable() {
            @Override
            public void close() throws IOException {
                (counter[0])++;
                throw new AssertionError("ok");
            }
        };
        Runnable composed = Compose.closeables(closeable1, closeable2);
        try {
            composed.run();
        } catch (AssertionError ex) {
            Assert.assertThat(ex.getMessage(), CoreMatchers.is("ok"));
        }
        Assert.assertThat(counter[0], CoreMatchers.is(2));
    }
}

