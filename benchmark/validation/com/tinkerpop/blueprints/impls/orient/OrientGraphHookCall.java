package com.tinkerpop.blueprints.impls.orient;


import OrientEdgeType.CLASS_NAME;
import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.record.ORecord;
import com.tinkerpop.blueprints.Vertex;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by tglman on 04/01/16.
 */
public class OrientGraphHookCall {
    private static int vertexCreatedCnt;

    private static int vertexUpdatedCnt;

    private static int edgeCreatedCnt;

    private static int edgeUpdatedCnt;

    @Test
    public void testHook() {
        OrientBaseGraph graph = new OrientGraph(("memory:" + (OrientGraphHookCall.class.getSimpleName())));
        graph.getRawGraph().registerHook(new ORecordHook() {
            @Override
            public void onUnregister() {
            }

            @Override
            public RESULT onTrigger(TYPE iType, ORecord iRecord) {
                switch (iType) {
                    case AFTER_CREATE :
                        {
                            if (getSchemaClass().isSubClassOf(CLASS_NAME)) {
                                (OrientGraphHookCall.edgeCreatedCnt)++;
                            } else {
                                (OrientGraphHookCall.vertexCreatedCnt)++;
                            }
                            break;
                        }
                    case AFTER_UPDATE :
                        {
                            if (getSchemaClass().isSubClassOf(CLASS_NAME)) {
                                (OrientGraphHookCall.edgeUpdatedCnt)++;
                            } else {
                                (OrientGraphHookCall.vertexUpdatedCnt)++;
                            }
                            break;
                        }
                    default :
                        {
                        }
                }
                return null;
            }

            @Override
            public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
                return null;
            }
        });
        try {
            Vertex v1 = graph.addVertex("v", ((String) (null)));
            graph.commit();
            Assert.assertEquals(1, OrientGraphHookCall.vertexCreatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.vertexUpdatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeCreatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeUpdatedCnt);
            v1.setProperty("p1a", "v1a");
            graph.commit();
            Assert.assertEquals(1, OrientGraphHookCall.vertexCreatedCnt);
            Assert.assertEquals(1, OrientGraphHookCall.vertexUpdatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeCreatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeUpdatedCnt);
            Vertex v2 = graph.addVertex("v", ((String) (null)));
            graph.commit();
            Assert.assertEquals(2, OrientGraphHookCall.vertexCreatedCnt);
            Assert.assertEquals(1, OrientGraphHookCall.vertexUpdatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeCreatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeUpdatedCnt);
            v2.setProperty("p2a", "v2a");
            graph.commit();
            Assert.assertEquals(2, OrientGraphHookCall.vertexCreatedCnt);
            Assert.assertEquals(2, OrientGraphHookCall.vertexUpdatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeCreatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeUpdatedCnt);
            v1.addEdge("e", v2);
            graph.commit();
            Assert.assertEquals(2, OrientGraphHookCall.vertexCreatedCnt);
            Assert.assertEquals(4, OrientGraphHookCall.vertexUpdatedCnt);
            Assert.assertEquals(1, OrientGraphHookCall.edgeCreatedCnt);
            Assert.assertEquals(0, OrientGraphHookCall.edgeUpdatedCnt);
        } finally {
            graph.shutdown();
        }
    }
}

