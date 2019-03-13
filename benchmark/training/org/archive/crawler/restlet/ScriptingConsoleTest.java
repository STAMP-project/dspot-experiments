package org.archive.crawler.restlet;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import junit.framework.TestCase;
import org.archive.crawler.framework.CrawlJob;
import org.archive.spring.PathSharingContext;


public class ScriptingConsoleTest extends TestCase {
    // barebone CrawlJob object.
    public static class TestCrawlJob extends CrawlJob {
        public TestCrawlJob() {
            super(null);
            this.ac = new PathSharingContext(new String[0]);
        }

        @Override
        protected void scanJobLog() {
        }
    }

    CrawlJob cj;

    ScriptingConsole sc;

    public void testInitialState() {
        TestCase.assertEquals("script is empty", "", sc.getScript());
        TestCase.assertNull("exception is null", sc.getException());
    }

    public void testExecute() {
        final String script = "rawOut.println 'elk'";
        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine eng = manager.getEngineByName("groovy");
        sc.execute(eng, script);
        TestCase.assertNull("exception is null", sc.getException());
        TestCase.assertEquals("has the same script", sc.getScript(), script);
        TestCase.assertEquals("linesExecuted", 1, sc.getLinesExecuted());
        TestCase.assertEquals("rawOut", "elk\n", sc.getRawOutput());
    }

    public void testExecuteError() {
        final String script = "rawOut.println undef";
        final ScriptEngineManager manager = new ScriptEngineManager();
        final ScriptEngine eng = manager.getEngineByName("groovy");
        sc.execute(eng, script);
        TestCase.assertNotNull("exception is non-null", sc.getException());
        TestCase.assertEquals("rawOut", "", sc.getRawOutput());
        TestCase.assertEquals("linesExecuted", 0, sc.getLinesExecuted());
        // extra test - it is okay to fail this test is okay because
        // ScriptingConsole is single-use now.
        sc.execute(eng, "rawOut.println 1");
        TestCase.assertNull("exception is cleared", sc.getException());
    }
}

