/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari;


import junit.framework.TestCase;
import org.apache.ambari.eventdb.model.WorkflowContext;
import org.apache.ambari.eventdb.model.WorkflowDag;
import org.apache.ambari.log4j.hadoop.mapreduce.jobhistory.MapReduceJobHistoryUpdater;


/**
 *
 */
public class TestMapReduceJobHistoryUpdater extends TestCase {
    public void testDagMerging() {
        WorkflowDag dag1 = new WorkflowDag();
        dag1.addEntry(TestMapReduceJobHistoryUpdater.getEntry("a", "b", "c"));
        dag1.addEntry(TestMapReduceJobHistoryUpdater.getEntry("b", "d"));
        WorkflowContext one = new WorkflowContext();
        one.setWorkflowDag(dag1);
        WorkflowDag dag2 = new WorkflowDag();
        dag2.addEntry(TestMapReduceJobHistoryUpdater.getEntry("a", "d"));
        dag2.addEntry(TestMapReduceJobHistoryUpdater.getEntry("c", "e"));
        WorkflowContext two = new WorkflowContext();
        two.setWorkflowDag(dag2);
        WorkflowDag emptyDag = new WorkflowDag();
        WorkflowContext three = new WorkflowContext();
        three.setWorkflowDag(emptyDag);
        WorkflowDag mergedDag = new WorkflowDag();
        mergedDag.addEntry(TestMapReduceJobHistoryUpdater.getEntry("a", "b", "c", "d"));
        mergedDag.addEntry(TestMapReduceJobHistoryUpdater.getEntry("b", "d"));
        mergedDag.addEntry(TestMapReduceJobHistoryUpdater.getEntry("c", "e"));
        TestMapReduceJobHistoryUpdater.assertEquals(mergedDag, MapReduceJobHistoryUpdater.constructMergedDag(one, two));
        TestMapReduceJobHistoryUpdater.assertEquals(mergedDag, MapReduceJobHistoryUpdater.constructMergedDag(two, one));
        // test blank dag
        TestMapReduceJobHistoryUpdater.assertEquals(dag1, MapReduceJobHistoryUpdater.constructMergedDag(three, one));
        TestMapReduceJobHistoryUpdater.assertEquals(dag1, MapReduceJobHistoryUpdater.constructMergedDag(one, three));
        TestMapReduceJobHistoryUpdater.assertEquals(dag2, MapReduceJobHistoryUpdater.constructMergedDag(three, two));
        TestMapReduceJobHistoryUpdater.assertEquals(dag2, MapReduceJobHistoryUpdater.constructMergedDag(two, three));
        // test null dag
        TestMapReduceJobHistoryUpdater.assertEquals(dag1, MapReduceJobHistoryUpdater.constructMergedDag(new WorkflowContext(), one));
        TestMapReduceJobHistoryUpdater.assertEquals(dag1, MapReduceJobHistoryUpdater.constructMergedDag(one, new WorkflowContext()));
        TestMapReduceJobHistoryUpdater.assertEquals(dag2, MapReduceJobHistoryUpdater.constructMergedDag(new WorkflowContext(), two));
        TestMapReduceJobHistoryUpdater.assertEquals(dag2, MapReduceJobHistoryUpdater.constructMergedDag(two, new WorkflowContext()));
        // test same dag
        TestMapReduceJobHistoryUpdater.assertEquals(dag1, MapReduceJobHistoryUpdater.constructMergedDag(one, one));
        TestMapReduceJobHistoryUpdater.assertEquals(dag2, MapReduceJobHistoryUpdater.constructMergedDag(two, two));
        TestMapReduceJobHistoryUpdater.assertEquals(emptyDag, MapReduceJobHistoryUpdater.constructMergedDag(three, three));
    }
}

