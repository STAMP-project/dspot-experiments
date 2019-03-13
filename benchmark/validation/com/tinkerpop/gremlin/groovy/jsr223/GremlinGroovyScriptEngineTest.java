package com.tinkerpop.gremlin.groovy.jsr223;


import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Index;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.pipes.util.Pipeline;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import junit.framework.Assert;
import junit.framework.TestCase;


/**
 *
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GremlinGroovyScriptEngineTest extends TestCase {
    public void testGremlinLoading() throws Exception {
        ScriptEngine engine = new GremlinGroovyScriptEngine();
        List list = new ArrayList();
        engine.put("g", TinkerGraphFactory.createTinkerGraph());
        engine.put("list", list);
        TestCase.assertEquals(list.size(), 0);
        engine.eval("g.v(1).outE.inV._.fill(list)");
        TestCase.assertEquals(list.size(), 3);
    }

    public void testImports() throws Exception {
        ScriptEngine engine = new GremlinGroovyScriptEngine();
        engine.eval("Vertex.class.getName()");
        engine.eval("new TinkerGraph()");
        engine.eval("TinkerGraphFactory.createTinkerGraph().V.hasNot('age',null).has('age',T.gt,25).count()");
        engine.eval("TinkerGraphFactory.createTinkerGraph().getVertex(1).getVertices(OUT)");
        engine.eval("TinkerGraphFactory.createTinkerGraph().getVertex(1).getEdges(BOTH)");
        engine.eval("TinkerGraphFactory.createTinkerGraph().getVertex(1).getEdges(IN)");
        engine.eval("Direction.OUT.toString(); Direction.IN.toString(); Direction.BOTH.toString()");
        engine.eval("SUCCESS.toString(); FAILURE.toString()");
        engine.eval("TransactionalGraph.Conclusion.SUCCESS.toString(); TransactionalGraph.Conclusion.FAILURE.toString()");
    }

    public void testBindings() throws Exception {
        final TinkerGraph g = TinkerGraphFactory.createTinkerGraph();
        final ScriptEngine engine = new GremlinGroovyScriptEngine();
        TestCase.assertTrue(((engine.eval("g = TinkerGraphFactory.createTinkerGraph()")) instanceof TinkerGraph));
        TestCase.assertTrue(((engine.get("g")) instanceof TinkerGraph));
        TestCase.assertEquals(engine.eval("g.v(1)"), g.getVertex(1));
        final Bindings bindings = engine.createBindings();
        bindings.put("g", g);
        bindings.put("s", "marko");
        bindings.put("f", 0.5F);
        bindings.put("i", 1);
        bindings.put("b", true);
        bindings.put("l", 100L);
        bindings.put("d", 1.55555);
        TestCase.assertEquals(g.getEdge(7), engine.eval("g.E.has('weight',f).next()", bindings));
        TestCase.assertEquals(g.getVertex(1), engine.eval("g.V.has('name',s).next()", bindings));
        TestCase.assertEquals(g.getVertex(1), engine.eval("g.V.sideEffect{it.bbb=it.name=='marko'}.iterate();g.V.has('bbb',b).next()", bindings));
        TestCase.assertEquals(g.getVertex(1), engine.eval("g.V.sideEffect{it.iii=it.name=='marko'?1:0}.iterate();g.V.has('iii',i).next()", bindings));
        TestCase.assertEquals(g.getVertex(1), engine.eval("g.V.sideEffect{it.lll=it.name=='marko'?100l:0l}.iterate();g.V.has('lll',l).next()", bindings));
        TestCase.assertEquals(g.getVertex(1), engine.eval("g.V.sideEffect{it.ddd=it.name=='marko'?1.55555d:0}.iterate();g.V.has('ddd',d).next()", bindings));
    }

    public void testThreadSafetyOnEngine() throws Exception {
        final ScriptEngine engine = new GremlinGroovyScriptEngine(10);
        int runs = 500;
        final CountDownLatch latch = new CountDownLatch(runs);
        final List<String> names = Arrays.asList("marko", "peter", "josh", "vadas", "stephen", "pavel", "matthias");
        final Random random = new Random();
        for (int i = 0; i < runs; i++) {
            new Thread() {
                public void run() {
                    String name = names.get(random.nextInt(((names.size()) - 1)));
                    try {
                        final Bindings bindings = engine.createBindings();
                        bindings.put("g", TinkerGraphFactory.createTinkerGraph());
                        bindings.put("name", name);
                        final Object result = engine.eval("pipe = g.V('name',name); if(pipe.hasNext()) { pipe.out.count() } else { null }", bindings);
                        if (((name.equals("stephen")) || (name.equals("pavel"))) || (name.equals("matthias")))
                            TestCase.assertNull(result);
                        else
                            TestCase.assertNotNull(result);

                    } catch (ScriptException e) {
                        // System.out.println(e);
                        TestCase.assertFalse(true);
                    }
                    latch.countDown();
                }
            }.start();
        }
        latch.await();
    }

    public void testThreadSafetyOnCompiledScript() throws Exception {
        final GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine(10);
        final CompiledScript script = engine.compile("pipe = g.V('name',name); if(pipe.hasNext()) { pipe.out.count() } else { null }");
        int runs = 500;
        final CountDownLatch latch = new CountDownLatch(runs);
        final List<String> names = Arrays.asList("marko", "peter", "josh", "vadas", "stephen", "pavel", "matthias");
        final Random random = new Random();
        for (int i = 0; i < runs; i++) {
            new Thread() {
                public void run() {
                    String name = names.get(random.nextInt(((names.size()) - 1)));
                    try {
                        final Bindings bindings = engine.createBindings();
                        bindings.put("g", TinkerGraphFactory.createTinkerGraph());
                        bindings.put("name", name);
                        Object result = script.eval(bindings);
                        if (((name.equals("stephen")) || (name.equals("pavel"))) || (name.equals("matthias")))
                            TestCase.assertNull(result);
                        else
                            TestCase.assertNotNull(result);

                    } catch (ScriptException e) {
                        // System.out.println(e);
                        TestCase.assertFalse(true);
                    }
                    latch.countDown();
                }
            }.start();
        }
        latch.await();
    }

    public void testEngineVsCompiledCosts() throws Exception {
        final GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();
        Bindings bindings = engine.createBindings();
        bindings.put("g", TinkerGraphFactory.createTinkerGraph());
        int runs = 1000;
        long totalTime = 0L;
        for (int i = 0; i < runs; i++) {
            long time = System.currentTimeMillis();
            CompiledScript script = engine.compile("g.v(1).out().count()");
            script.eval(bindings);
            totalTime += (System.currentTimeMillis()) - time;
        }
        System.out.println(((("Multi-compiled script runtime for " + runs) + " runs: ") + totalTime));
        totalTime = 0L;
        for (int i = 0; i < runs; i++) {
            long time = System.currentTimeMillis();
            engine.eval("g.v(1).out().count()", bindings);
            totalTime += (System.currentTimeMillis()) - time;
        }
        System.out.println(((("Evaluated script runtime for " + runs) + " runs: ") + totalTime));
        totalTime = 0L;
        CompiledScript script = engine.compile("g.v(1).out().count()");
        for (int i = 0; i < runs; i++) {
            long time = System.currentTimeMillis();
            script.eval(bindings);
            totalTime += (System.currentTimeMillis()) - time;
        }
        System.out.println(((("Compiled script runtime for " + runs) + " runs: ") + totalTime));
    }

    /**
     * Tries to force the out of memory exceptions that commonly seem to happen with scriptengine.
     */
    public void testBlowTheHeap() throws ScriptException {
        final GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();
        final Graph g = TinkerGraphFactory.createTinkerGraph();
        final String[] gremlins = new String[]{ "g.v(xxx).out.toList()", "g.v(xxx).in.toList()", "g.v(xxx).out.loop(1){true}{true}.toList()", "g.v(xxx).groupCount.cap.next()" };
        /**
         * ****************START PARAMETERIZE GREMLIN***************
         * parameterized gremlin doesn't blow the heap
         */
        long parameterizedStartTime = System.currentTimeMillis();
        System.out.println("Try to blow the heap with parameterized Gremlin.");
        try {
            for (int ix = 0; ix < 50001; ix++) {
                final Bindings bindings = engine.createBindings();
                bindings.put("g", g);
                bindings.put("xxx", ((ix % 4) + 1));
                Object x = engine.eval(gremlins[(ix % 4)], bindings);
                if ((ix > 0) && ((ix % 5000) == 0)) {
                    System.out.println(String.format("%s scripts processed in %s (ms) - rate %s (ms/q).", ix, ((System.currentTimeMillis()) - parameterizedStartTime), ((Double.valueOf(((System.currentTimeMillis()) - parameterizedStartTime))) / (Double.valueOf(ix)))));
                }
            }
        } catch (OutOfMemoryError oome) {
            TestCase.assertTrue(false);
        }
        /**
         * ***********************DONE PARAMETERIZE GREMLIN****************
         */
        long notParameterizedStartTime = System.currentTimeMillis();
        System.out.println("Try to blow the heap with non-parameterized Gremlin.");
        try {
            for (int ix = 0; ix < 15001; ix++) {
                final Bindings bindings = engine.createBindings();
                bindings.put("g", g);
                engine.eval(String.format("g.v(%s)", ix), bindings);
                if ((ix > 0) && ((ix % 5000) == 0)) {
                    System.out.println(String.format("%s scripts processed in %s (ms) - rate %s (ms/q).", ix, ((System.currentTimeMillis()) - notParameterizedStartTime), ((Double.valueOf(((System.currentTimeMillis()) - notParameterizedStartTime))) / (Double.valueOf(ix)))));
                }
            }
        } catch (OutOfMemoryError oome) {
            TestCase.assertTrue(false);
        }
    }

    public void testFunctionsUsedInClosure() throws ScriptException {
        GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();
        final Graph g = TinkerGraphFactory.createTinkerGraph();
        final Bindings bindings = engine.createBindings();
        bindings.put("g", g);
        // this works on its own when the function and the line that uses it is in one "script".  this is the
        // current workaround
        TestCase.assertEquals(g.getVertex(2), engine.eval("def isVadas(v){v.name=='vadas'};g.V.filter{isVadas(it)}.next()", bindings));
        // let's reset this piece and make sure isVadas is not hanging around.
        engine = new GremlinGroovyScriptEngine();
        // validate that isVadas throws an exception since it is not defined
        try {
            engine.eval("isVadas(g.v(2))", bindings);
            // fail the test if the above doesn't throw an exception
            TestCase.fail();
        } catch (Exception ex) {
            // this is good...we want this. it means isVadas isn't hanging about
        }
        // now...define the function separately on its own in one script
        engine.eval("def isVadas(v){v.name=='vadas'}", bindings);
        // make sure the function works on its own...no problem
        TestCase.assertEquals(true, engine.eval("isVadas(g.v(2))", bindings));
        // make sure the function works in a closure...this generates a StackOverflowError
        TestCase.assertEquals(g.getVertex(2), engine.eval("g.V.filter{isVadas(it)}.next()", bindings));
    }

    public void testGremlinScriptEngineWithScriptBaseClass() throws Exception {
        ScriptEngine engine = new GremlinGroovyScriptEngine();
        List list = new ArrayList();
        engine.put("g", TinkerGraphFactory.createTinkerGraph());
        engine.put("list", list);
        TestCase.assertEquals(list.size(), 0);
        engine.eval("g.V.fill(list)");
        TestCase.assertEquals(6, list.size());
        list.clear();
        TinkerGraph tinkerGraph = TinkerGraphFactory.createTinkerGraph();
        Vertex marko = tinkerGraph.getVertex(1L);
        Assert.assertEquals("marko", marko.getProperty("name"));
        marko.setProperty("deleted", new Date());
        engine = new GremlinGroovyScriptEngine("com.tinkerpop.gremlin.groovy.basescript.GremlinGroovyScriptBaseClassForTest");
        engine.put("g", tinkerGraph);
        engine.put("list", list);
        TestCase.assertEquals(list.size(), 0);
        String groovy = "g.V.fill(list)";
        groovy = ("useInterceptor( GremlinGroovyPipeline, com.tinkerpop.gremlin.groovy.basescript.GremlinGroovyPipelineInterceptor) {" + groovy) + "}";
        engine.eval(groovy);
        TestCase.assertEquals(5, list.size());
    }

    public void testGremlinScriptEngineWithUTF8Characters() throws Exception {
        ScriptEngine engine = new GremlinGroovyScriptEngine();
        TestCase.assertEquals("??", engine.eval("'??'"));
    }

    public void testUTF8Query() throws Exception {
        TinkerGraph graph = new TinkerGraph();
        Index<Vertex> index = graph.createIndex("nodes", Vertex.class);
        Vertex nonUtf8 = graph.addVertex("1");
        nonUtf8.setProperty("name", "marko");
        nonUtf8.setProperty("age", 29);
        index.put("name", "marko", nonUtf8);
        Vertex utf8Name = graph.addVertex("2");
        utf8Name.setProperty("name", "??");
        utf8Name.setProperty("age", 32);
        index.put("name", "??", utf8Name);
        graph.addVertex(utf8Name);
        graph.addEdge("12", nonUtf8, utf8Name, "created").setProperty("weight", 0.2F);
        ScriptEngine engine = new GremlinGroovyScriptEngine();
        engine.put("g", graph);
        Pipeline eval = ((Pipeline) (engine.eval("g.idx(\"nodes\")[[\'name\' : \'marko\']]")));
        TestCase.assertEquals(nonUtf8, eval.next());
        eval = ((Pipeline) (engine.eval("g.idx(\"nodes\")[[\'name\' : \'\u8f49\u6ce8\']]")));
        TestCase.assertEquals(utf8Name, eval.next());
    }
}

