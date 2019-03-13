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
package org.datavec.api.transform.transform.parse;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.datavec.api.writable.DoubleWritable;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by agibsonccc on 10/22/16.
 */
public class ParseDoubleTransformTest {
    @Test
    public void testDoubleTransform() {
        List<Writable> record = new ArrayList<>();
        record.add(new Text("0.0"));
        List<Writable> transformed = Arrays.<Writable>asList(new DoubleWritable(0.0));
        Assert.assertEquals(transformed, new ParseDoubleTransform().map(record));
    }
}

