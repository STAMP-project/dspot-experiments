package fj.data;


import IOFunctions.closeReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class IOFunctionsTest {
    @Test
    public void bracket_happy_path() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        Reader reader = new StringReader("Read OK") {
            @Override
            public void close() {
                super.close();
                closed.set(true);
            }
        };
        IO<String> bracketed = IOFunctions.IOFunctions.bracket(() -> reader, closeReader, ( r) -> () -> new BufferedReader(r).readLine());
        Assert.assertThat(bracketed.run(), CoreMatchers.is("Read OK"));
        Assert.assertThat(closed.get(), CoreMatchers.is(true));
    }

    @Test
    public void bracket_exception_path() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        Reader reader = new StringReader("Read OK") {
            @Override
            public void close() {
                super.close();
                closed.set(true);
                throw new IllegalStateException("Should be suppressed");
            }
        };
        IO<String> bracketed = IOFunctions.IOFunctions.bracket(() -> reader, closeReader, ( r) -> () -> {
            throw new IllegalArgumentException("OoO");
        });
        try {
            bracketed.run();
            Assert.fail("Exception expected");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.is("OoO"));
        }
        Assert.assertThat(closed.get(), CoreMatchers.is(true));
    }

    @Test
    public void testTraverseIO() throws IOException {
        String[] as = new String[]{ "foo1", "bar2", "foobar3" };
        Stream<String> stream = Stream.arrayStream(as);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        stream.traverseIO(IOFunctions.IOFunctions::stdoutPrint).run();
        System.setOut(originalOut);
        Assert.assertThat(outContent.toString(), CoreMatchers.is("foobar3bar2foo1"));
    }

    @Test
    public void testSequenceWhile() throws IOException {
        java.io.BufferedReader r = new java.io.BufferedReader(new StringReader("foo1\nbar2\nfoobar3"));
        Stream<IO<String>> s1 = Stream.repeat(() -> r.readLine());
        IO<Stream<String>> io = sequenceWhile(s1, ( s) -> !(s.equals("foobar3")));
        Assert.assertThat(io.run(), CoreMatchers.is(Stream.cons("foo1", () -> cons("bar2", () -> Stream.nil()))));
    }

    @Test
    public void testForeach() throws IOException {
        Stream<IO<String>> s1 = Stream.repeat(() -> "foo1");
        IO<Stream<String>> io = sequence(s1.take(2));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        runSafe(io).foreach(( s) -> runSafe(stdoutPrint(s)));
        System.setOut(originalOut);
        Assert.assertThat(outContent.toString(), CoreMatchers.is("foo1foo1"));
    }

    @Test
    public void testReplicateM() throws IOException {
        final IO<String> is = () -> new BufferedReader(new StringReader("foo")).readLine();
        Assert.assertThat(replicateM(is, 3).run(), CoreMatchers.is(List.list("foo", "foo", "foo")));
    }

    @Test
    public void testLift() throws IOException {
        final IO<String> readName = () -> new BufferedReader(new StringReader("foo")).readLine();
        final F<String, IO<String>> upperCaseAndPrint = F1Functions.<String, IO<String>, String>o(this::println).f(String::toUpperCase);
        final IO<String> readAndPrintUpperCasedName = IOFunctions.IOFunctions.bind(readName, upperCaseAndPrint);
        Assert.assertThat(readAndPrintUpperCasedName.run(), CoreMatchers.is("FOO"));
    }
}

