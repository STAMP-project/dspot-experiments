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
package org.apache.mahout.math.ssvd;


import Functions.ABS;
import Functions.PLUS;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.mahout.common.MahoutTestCase;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SingularValueDecomposition;
import org.apache.mahout.math.Vector;
import org.junit.Test;


public final class SequentialOutOfCoreSvdTest extends MahoutTestCase {
    private File tmpDir;

    @Test
    public void testSingularValues() throws IOException {
        Matrix A = SequentialOutOfCoreSvdTest.lowRankMatrix(tmpDir, "A", 200, 970, 1020);
        List<File> partsOfA = Arrays.asList(tmpDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                return fileName.matches("A-.*");
            }
        }));
        // rearrange A to make sure we don't depend on lexical ordering.
        partsOfA = Lists.reverse(partsOfA);
        SequentialOutOfCoreSvd s = new SequentialOutOfCoreSvd(partsOfA, tmpDir, 100, 210);
        SequentialBigSvd svd = new SequentialBigSvd(A, 100);
        Vector reference = viewPart(0, 6);
        Vector actual = viewPart(0, 6);
        SequentialOutOfCoreSvdTest.assertEquals(0, reference.minus(actual).maxValue(), 1.0E-9);
        s.computeU(partsOfA, tmpDir);
        Matrix u = SequentialOutOfCoreSvdTest.readBlockMatrix(Arrays.asList(tmpDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                return fileName.matches("U-.*");
            }
        })));
        s.computeV(tmpDir, A.columnSize());
        Matrix v = SequentialOutOfCoreSvdTest.readBlockMatrix(Arrays.asList(tmpDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String fileName) {
                return fileName.matches("V-.*");
            }
        })));
        // The values in A are pretty big so this is a pretty tight relative tolerance
        SequentialOutOfCoreSvdTest.assertEquals(0, A.minus(u.times(new org.apache.mahout.math.DiagonalMatrix(s.getSingularValues())).times(v.transpose())).aggregate(PLUS, ABS), 1.0E-7);
    }

    @Test
    public void testLeftVectors() throws IOException {
        Matrix A = SequentialOutOfCoreSvdTest.lowRankMatrixInMemory(20, 20);
        SequentialBigSvd s = new SequentialBigSvd(A, 6);
        SingularValueDecomposition svd = new SingularValueDecomposition(A);
        // can only check first few singular vectors
        Matrix u1 = svd.getU().viewPart(0, 20, 0, 3).assign(ABS);
        Matrix u2 = s.getU().viewPart(0, 20, 0, 3).assign(ABS);
        SequentialOutOfCoreSvdTest.assertEquals(u1, u2);
    }

    @Test
    public void testRightVectors() throws IOException {
        Matrix A = SequentialOutOfCoreSvdTest.lowRankMatrixInMemory(20, 20);
        SequentialBigSvd s = new SequentialBigSvd(A, 6);
        SingularValueDecomposition svd = new SingularValueDecomposition(A);
        Matrix v1 = svd.getV().viewPart(0, 20, 0, 3).assign(ABS);
        Matrix v2 = s.getV().viewPart(0, 20, 0, 3).assign(ABS);
        SequentialOutOfCoreSvdTest.assertEquals(v1, v2);
    }
}

