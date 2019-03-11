/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.datavec.api.transform;


import MathOp.Add;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.collection.ListStringRecordReader;
import org.datavec.api.split.ListStringSplit;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.transform.nlp.TextToCharacterIndexTransform;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.IntWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;


public class TestTransformProcess {
    @Test
    public void testExecution() {
        Schema schema = new Schema.Builder().addColumnsString("col").addColumnsDouble("col2").build();
        Map<Character, Integer> m = TestTransformProcess.defaultCharIndex();
        TransformProcess transformProcess = new TransformProcess.Builder(schema).doubleMathOp("col2", Add, 1.0).build();
        List<Writable> in = Arrays.<Writable>asList(new Text("Text"), new DoubleWritable(2.0));
        List<Writable> exp = Arrays.<Writable>asList(new Text("Text"), new DoubleWritable(3.0));
        List<Writable> out = transformProcess.execute(in);
        Assert.assertEquals(exp, out);
    }

    @Test
    public void testExecuteToSequence() {
        Schema schema = new Schema.Builder().addColumnsString("action").build();
        Map<Character, Integer> m = TestTransformProcess.defaultCharIndex();
        TransformProcess transformProcess = removeAllColumnsExceptFor("action").convertToSequence().transform(new TextToCharacterIndexTransform("action", "action_sequence", m, true)).build();
        String s = "in text";
        List<Writable> input = Collections.<Writable>singletonList(new Text(s));
        List<List<Writable>> expSeq = new ArrayList<>(s.length());
        for (int i = 0; i < (s.length()); i++) {
            expSeq.add(Collections.<Writable>singletonList(new IntWritable(m.get(s.charAt(i)))));
        }
        List<List<Writable>> out = transformProcess.executeToSequence(input);
        Assert.assertEquals(expSeq, out);
    }

    @Test
    public void testInferColumns() throws Exception {
        List<List<String>> categories = Arrays.asList(Arrays.asList("a", "d"), Arrays.asList("b", "e"), Arrays.asList("c", "f"));
        RecordReader listReader = new ListStringRecordReader();
        listReader.initialize(new ListStringSplit(categories));
        List<String> inferredSingle = TransformProcess.inferCategories(listReader, 0);
        Assert.assertEquals(3, inferredSingle.size());
        listReader.initialize(new ListStringSplit(categories));
        Map<Integer, List<String>> integerListMap = TransformProcess.inferCategories(listReader, new int[]{ 0, 1 });
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(3, integerListMap.get(i).size());
        }
    }
}

