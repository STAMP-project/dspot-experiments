package com.twitter.elephantbird.pig.load;


import com.twitter.elephantbird.mapreduce.input.LuceneIndexInputFormat;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.io.NullWritable;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Alex Levenson
 */
public class TestLuceneIndexLoader {
    private static class Loader extends LuceneIndexLoader<NullWritable> {
        public Loader(String[] args) {
            super(args);
        }

        @Override
        protected Tuple recordToTuple(int key, NullWritable value) {
            return null;
        }

        @Override
        protected LuceneIndexInputFormat<NullWritable> getLuceneIndexInputFormat() throws IOException {
            return null;
        }
    }

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void testConstructor() {
        try {
            new TestLuceneIndexLoader.Loader(new String[]{  });
            Assert.fail("This should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            new TestLuceneIndexLoader.Loader(new String[]{ "invalid" });
            Assert.fail("This should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            new TestLuceneIndexLoader.Loader(new String[]{ "invalid", "extra" });
            Assert.fail("This should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            new TestLuceneIndexLoader.Loader(new String[]{ "--queries" });
            Assert.fail("This should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            new TestLuceneIndexLoader.Loader(new String[]{ "--file" });
            Assert.fail("This should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        try {
            new TestLuceneIndexLoader.Loader(new String[]{ "--file", "one", "two" });
            Assert.fail("This should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        // valid constructor usages
        new TestLuceneIndexLoader.Loader(new String[]{ "--queries", "query1" });
        new TestLuceneIndexLoader.Loader(new String[]{ "--queries", "query1", "query2", "query3" });
        new TestLuceneIndexLoader.Loader(new String[]{ "--file", "src/test/resources/com/twitter/elephantbird/pig/load/queryfile.txt" });
    }

    @Test
    public void testSetLocationQueries() throws Exception {
        doTestSetLocation(new TestLuceneIndexLoader.Loader(new String[]{ "--queries", "+hello -goodbye", "+test", "+????" }));
    }

    @Test
    public void testSetLocationFileMissing() throws Exception {
        String fakeFile = new File(tempDir.getRoot(), "nonexistant").getAbsolutePath();
        try {
            doTestSetLocation(new TestLuceneIndexLoader.Loader(new String[]{ "--file", fakeFile }));
            Assert.fail("This should throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().endsWith("/nonexistant does not exist!"));
        }
    }

    @Test
    public void testSetLocationFile() throws Exception {
        doTestSetLocation(new TestLuceneIndexLoader.Loader(new String[]{ "--file", "src/test/resources/com/twitter/elephantbird/pig/load/queryfile.txt" }));
    }
}

