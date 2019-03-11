package com.tinkerpop.blueprints.impls.orient;


import com.orientechnologies.orient.server.OServer;
import com.tinkerpop.blueprints.Vertex;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OrientGraphMultithreadRemoteTest {
    private static final String serverPort = System.getProperty("orient.server.port", "3080");

    private static OServer server;

    private static String oldOrientDBHome;

    private static String serverHome;

    private OrientGraphFactory graphFactory;

    @Test
    public void testThreadingInsert() throws InterruptedException {
        List<Thread> threads = new ArrayList<Thread>();
        int threadCount = 8;
        final int recordsPerThread = 20;
        long records = threadCount * recordsPerThread;
        try {
            for (int t = 0; t < threadCount; t++) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        for (int i = 0; i < recordsPerThread; i++) {
                            OrientGraph graph = graphFactory.getTx();// get an instance from the pool

                            try {
                                Vertex v1 = graph.addVertex(null);
                                v1.setProperty("name", "b");
                                // v1.setProperty("blob", blob);
                                graph.commit();// commits transaction

                            } catch (Exception ex) {
                                try {
                                    graph.rollback();
                                } catch (Exception ex1) {
                                    System.out.println(("rollback exception! " + ex));
                                }
                                System.out.println(("operation exception! " + ex));
                                ex.printStackTrace(System.out);
                            } finally {
                                graph.shutdown();
                            }
                        }
                    }
                };
                threads.add(thread);
                thread.start();
            }
        } catch (Exception ex) {
            System.err.println(("instance exception! " + ex));
            System.out.println(("instance exception! " + ex));
            ex.printStackTrace(System.err);
        } finally {
            for (Thread t : threads) {
                t.join();
            }
        }
        OrientGraph graph = graphFactory.getTx();
        Assert.assertEquals(graph.countVertices(), records);
        graph.shutdown();
    }
}

