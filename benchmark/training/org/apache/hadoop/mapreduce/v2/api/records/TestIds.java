/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.v2.api.records;


import TaskType.MAP;
import TaskType.REDUCE;
import org.junit.Assert;
import org.junit.Test;


public class TestIds {
    @Test
    public void testJobId() {
        long ts1 = 1315890136000L;
        long ts2 = 1315890136001L;
        JobId j1 = createJobId(ts1, 2);
        JobId j2 = createJobId(ts1, 1);
        JobId j3 = createJobId(ts2, 1);
        JobId j4 = createJobId(ts1, 2);
        Assert.assertTrue(j1.equals(j4));
        Assert.assertFalse(j1.equals(j2));
        Assert.assertFalse(j1.equals(j3));
        Assert.assertTrue(((j1.compareTo(j4)) == 0));
        Assert.assertTrue(((j1.compareTo(j2)) > 0));
        Assert.assertTrue(((j1.compareTo(j3)) < 0));
        Assert.assertTrue(((j1.hashCode()) == (j4.hashCode())));
        Assert.assertFalse(((j1.hashCode()) == (j2.hashCode())));
        Assert.assertFalse(((j1.hashCode()) == (j3.hashCode())));
        JobId j5 = createJobId(ts1, 231415);
        Assert.assertEquals((("job_" + ts1) + "_0002"), j1.toString());
        Assert.assertEquals((("job_" + ts1) + "_231415"), j5.toString());
    }

    @Test
    public void testTaskId() {
        long ts1 = 1315890136000L;
        long ts2 = 1315890136001L;
        TaskId t1 = createTaskId(ts1, 1, 2, MAP);
        TaskId t2 = createTaskId(ts1, 1, 2, REDUCE);
        TaskId t3 = createTaskId(ts1, 1, 1, MAP);
        TaskId t4 = createTaskId(ts1, 1, 2, MAP);
        TaskId t5 = createTaskId(ts2, 1, 1, MAP);
        Assert.assertTrue(t1.equals(t4));
        Assert.assertFalse(t1.equals(t2));
        Assert.assertFalse(t1.equals(t3));
        Assert.assertFalse(t1.equals(t5));
        Assert.assertTrue(((t1.compareTo(t4)) == 0));
        Assert.assertTrue(((t1.compareTo(t2)) < 0));
        Assert.assertTrue(((t1.compareTo(t3)) > 0));
        Assert.assertTrue(((t1.compareTo(t5)) < 0));
        Assert.assertTrue(((t1.hashCode()) == (t4.hashCode())));
        Assert.assertFalse(((t1.hashCode()) == (t2.hashCode())));
        Assert.assertFalse(((t1.hashCode()) == (t3.hashCode())));
        Assert.assertFalse(((t1.hashCode()) == (t5.hashCode())));
        TaskId t6 = createTaskId(ts1, 324151, 54643747, REDUCE);
        Assert.assertEquals((("task_" + ts1) + "_0001_m_000002"), t1.toString());
        Assert.assertEquals((("task_" + ts1) + "_324151_r_54643747"), t6.toString());
    }

    @Test
    public void testTaskAttemptId() {
        long ts1 = 1315890136000L;
        long ts2 = 1315890136001L;
        TaskAttemptId t1 = createTaskAttemptId(ts1, 2, 2, MAP, 2);
        TaskAttemptId t2 = createTaskAttemptId(ts1, 2, 2, REDUCE, 2);
        TaskAttemptId t3 = createTaskAttemptId(ts1, 2, 2, MAP, 3);
        TaskAttemptId t4 = createTaskAttemptId(ts1, 2, 2, MAP, 1);
        TaskAttemptId t5 = createTaskAttemptId(ts1, 2, 1, MAP, 3);
        TaskAttemptId t6 = createTaskAttemptId(ts1, 2, 2, MAP, 2);
        Assert.assertTrue(t1.equals(t6));
        Assert.assertFalse(t1.equals(t2));
        Assert.assertFalse(t1.equals(t3));
        Assert.assertFalse(t1.equals(t5));
        Assert.assertTrue(((t1.compareTo(t6)) == 0));
        Assert.assertTrue(((t1.compareTo(t2)) < 0));
        Assert.assertTrue(((t1.compareTo(t3)) < 0));
        Assert.assertTrue(((t1.compareTo(t4)) > 0));
        Assert.assertTrue(((t1.compareTo(t5)) > 0));
        Assert.assertTrue(((t1.hashCode()) == (t6.hashCode())));
        Assert.assertFalse(((t1.hashCode()) == (t2.hashCode())));
        Assert.assertFalse(((t1.hashCode()) == (t3.hashCode())));
        Assert.assertFalse(((t1.hashCode()) == (t5.hashCode())));
        TaskAttemptId t7 = createTaskAttemptId(ts2, 5463346, 4326575, REDUCE, 54375);
        Assert.assertEquals((("attempt_" + ts1) + "_0002_m_000002_2"), t1.toString());
        Assert.assertEquals((("attempt_" + ts2) + "_5463346_r_4326575_54375"), t7.toString());
    }
}

