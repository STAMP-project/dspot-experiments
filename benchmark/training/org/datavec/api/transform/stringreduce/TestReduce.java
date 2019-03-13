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
package org.datavec.api.transform.stringreduce;


import StringReduceOp.APPEND;
import StringReduceOp.MERGE;
import StringReduceOp.PREPEND;
import StringReduceOp.REPLACE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.datavec.api.transform.StringReduceOp;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Alex on 21/03/2016.
 */
public class TestReduce {
    @Test
    public void testReducerDouble() {
        List<List<Writable>> inputs = new ArrayList<>();
        inputs.add(Arrays.asList(((Writable) (new Text("1"))), new Text("2")));
        inputs.add(Arrays.asList(((Writable) (new Text("1"))), new Text("2")));
        inputs.add(Arrays.asList(((Writable) (new Text("1"))), new Text("2")));
        Map<StringReduceOp, String> exp = new LinkedHashMap<>();
        exp.put(MERGE, "12");
        exp.put(APPEND, "12");
        exp.put(PREPEND, "21");
        exp.put(REPLACE, "2");
        for (StringReduceOp op : exp.keySet()) {
            Schema schema = new Schema.Builder().addColumnString("key").addColumnString("column").build();
            StringReducer reducer = build();
            reducer.setInputSchema(schema);
            List<Writable> out = reducer.reduce(inputs);
            Assert.assertEquals(3, out.size());
            Assert.assertEquals(exp.get(op), out.get(0).toString());
        }
    }
}

