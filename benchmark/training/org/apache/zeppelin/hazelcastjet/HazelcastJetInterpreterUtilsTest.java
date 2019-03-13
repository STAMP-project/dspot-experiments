/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.hazelcastjet;


import InterpreterResult.Code.SUCCESS;
import InterpreterResult.Type.NETWORK;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.junit.Assert;
import org.junit.Test;


public class HazelcastJetInterpreterUtilsTest {
    private static final String NETWORK_RESULT_1 = "%network " + ((((((((((((((((((((("{\"nodes\":[" + "{\"id\":1,\"data\":{\"description\":\"listSource(text)\"},\"label\":\"Source\"},") + "{\"id\":2,\"data\":{\"description\":\"flat traversing\"},\"label\":\"Transform\"},") + "{\"id\":3,\"data\":{\"description\":\"filter\"},\"label\":\"Transform\"},") + "{\"id\":4,\"data\":{\"description\":\"group-and-aggregate-step1\"},") + "\"label\":\"Transform\"},") + "{\"id\":5,\"data\":{\"description\":\"group-and-aggregate-step2\"},") + "\"label\":\"Transform\"},") + "{\"id\":6,\"data\":{\"description\":\"mapSink(counts)\"},\"label\":\"Sink\"}],") + "\"edges\":[") + "{\"source\":1,\"target\":2,\"id\":1,\"data\":{\"routing\":\"UNICAST\",") + "\"distributed\":false,\"priority\":0}},") + "{\"source\":2,\"target\":3,\"id\":2,\"data\":{\"routing\":\"UNICAST\",") + "\"distributed\":false,\"priority\":0}},") + "{\"source\":3,\"target\":4,\"id\":3,\"data\":{\"routing\":\"PARTITIONED\",") + "\"distributed\":false,\"priority\":0}},") + "{\"source\":4,\"target\":5,\"id\":4,\"data\":{\"routing\":\"PARTITIONED\",") + "\"distributed\":true,\"priority\":0}},") + "{\"source\":5,\"target\":6,\"id\":5,\"data\":{\"routing\":\"UNICAST\",") + "\"distributed\":false,\"priority\":0}}],") + "\"labels\":{\"Sink\":\"#00317c\",\"Transform\":\"#ff7600\",\"Source\":\"#00317c\"},") + "\"directed\":true}");

    private static HazelcastJetInterpreter jet;

    private static InterpreterContext context;

    @Test
    public void testDisplayNetworkFromDAGUtil() {
        Pipeline p = Pipeline.create();
        p.drawFrom(Sources.<String>list("text")).flatMap(( word) -> traverseArray(word.toLowerCase().split("\\W+"))).setName("flat traversing").filter(( word) -> !(word.isEmpty())).groupingKey(wholeItem()).aggregate(counting()).drainTo(Sinks.map("counts"));
        Assert.assertEquals(HazelcastJetInterpreterUtilsTest.NETWORK_RESULT_1, HazelcastJetInterpreterUtils.displayNetworkFromDAG(p.toDag()));
    }

    @Test
    public void testStaticReplWithdisplayNetworkFromDAGUtilReturnNetworkType() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("import com.hazelcast.jet.pipeline.Pipeline;");
        out.println("import com.hazelcast.jet.pipeline.Sinks;");
        out.println("import com.hazelcast.jet.pipeline.Sources;");
        out.println("import org.apache.zeppelin.hazelcastjet.HazelcastJetInterpreterUtils;");
        out.println("import static com.hazelcast.jet.Traversers.traverseArray;");
        out.println("import static com.hazelcast.jet.aggregate.AggregateOperations.counting;");
        out.println("import static com.hazelcast.jet.function.DistributedFunctions.wholeItem;");
        out.println("public class HelloWorld {");
        out.println("  public static void main(String args[]) {");
        out.println("    Pipeline p = Pipeline.create();");
        out.println("    p.drawFrom(Sources.<String>list(\"text\"))");
        out.println("    .flatMap(word ->");
        out.println(("     traverseArray(word.toLowerCase().split(\"\\\\W+\")))" + ".setName(\"flat traversing\")"));
        out.println("    .filter(word -> !word.isEmpty())");
        out.println("    .groupingKey(wholeItem())");
        out.println("    .aggregate(counting())");
        out.println("    .drainTo(Sinks.map(\"counts\"));");
        out.println(("    System.out.println(HazelcastJetInterpreterUtils" + ".displayNetworkFromDAG(p.toDag()));"));
        out.println("  }");
        out.println("}");
        out.close();
        InterpreterResult res = HazelcastJetInterpreterUtilsTest.jet.interpret(writer.toString(), HazelcastJetInterpreterUtilsTest.context);
        Assert.assertEquals(SUCCESS, res.code());
        Assert.assertEquals(NETWORK, res.message().get(0).getType());
    }
}

