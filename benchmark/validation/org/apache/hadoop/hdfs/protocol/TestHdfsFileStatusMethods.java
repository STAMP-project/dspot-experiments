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
package org.apache.hadoop.hdfs.protocol;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hadoop.fs.FileStatus;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test verifying that {@link HdfsFileStatus} is a superset of
 * {@link FileStatus}.
 */
public class TestHdfsFileStatusMethods {
    @Test
    public void testInterfaceSuperset() {
        Set<TestHdfsFileStatusMethods.MethodSignature> fsM = TestHdfsFileStatusMethods.signatures(FileStatus.class);
        Set<TestHdfsFileStatusMethods.MethodSignature> hfsM = TestHdfsFileStatusMethods.signatures(HdfsFileStatus.class);
        hfsM.addAll(Stream.of(HdfsFileStatus.class.getInterfaces()).flatMap(( i) -> Stream.of(i.getDeclaredMethods())).map(TestHdfsFileStatusMethods.MethodSignature::new).collect(Collectors.toSet()));
        // HdfsFileStatus is not a concrete type
        hfsM.addAll(TestHdfsFileStatusMethods.signatures(Object.class));
        Assert.assertTrue(fsM.removeAll(hfsM));
        // verify that FileStatus is a subset of HdfsFileStatus
        Assert.assertEquals(fsM.stream().map(TestHdfsFileStatusMethods.MethodSignature::toString).collect(Collectors.joining("\n")), Collections.emptySet(), fsM);
    }

    private static class MethodSignature {
        private final String name;

        private final Type rval;

        private final Type[] param;

        MethodSignature(Method m) {
            name = m.getName();
            rval = m.getGenericReturnType();
            param = m.getParameterTypes();
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        /**
         * Methods are equal iff they have the same name, return type, and params
         * (non-generic).
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TestHdfsFileStatusMethods.MethodSignature)) {
                return false;
            }
            TestHdfsFileStatusMethods.MethodSignature s = ((TestHdfsFileStatusMethods.MethodSignature) (o));
            return ((name.equals(s.name)) && (rval.equals(s.rval))) && (Arrays.equals(param, s.param));
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(rval).append(" ").append(name).append("(").append(Stream.of(param).map(Type::toString).collect(Collectors.joining(","))).append(")");
            return sb.toString();
        }
    }
}

