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
package org.apache.mahout.math;


import Functions.PLUS;
import org.apache.mahout.math.function.DoubleDoubleFunction;
import org.junit.Test;


public final class OldQRDecompositionTest extends MahoutTestCase {
    @Test
    public void rank1() {
        Matrix x = new DenseMatrix(3, 3);
        x.viewRow(0).assign(new double[]{ 1, 2, 3 });
        x.viewRow(1).assign(new double[]{ 2, 4, 6 });
        x.viewRow(2).assign(new double[]{ 3, 6, 9 });
        OldQRDecomposition qr = new OldQRDecomposition(x);
        assertFalse(qr.hasFullRank());
        OldQRDecompositionTest.assertEquals(0, new DenseVector(new double[]{ 3.741657, 7.483315, 11.22497 }).aggregate(qr.getR().viewRow(0), PLUS, new DoubleDoubleFunction() {
            @Override
            public double apply(double arg1, double arg2) {
                return (Math.abs(arg1)) - (Math.abs(arg2));
            }
        }), 1.0E-5);
    }

    @Test
    public void fullRankTall() {
        Matrix x = OldQRDecompositionTest.matrix();
        OldQRDecomposition qr = new OldQRDecomposition(x);
        assertTrue(qr.hasFullRank());
        Matrix rRef = OldQRDecompositionTest.reshape(new double[]{ -2.99129686445138, 0, 0, 0, 0, -0.0282260628674372, -2.38850244769059, 0, 0, 0, 0.733739310355871, 1.48042000631646, 2.29051263117895, 0, 0, -0.0394082168269326, 0.282829484207801, -0.00438521041803086, -2.90823198084203, 0, 0.923669647838536, 1.76679276072492, 0.637690104222683, -0.225890909498753, -1.35732293800944 }, 5, 5);
        Matrix r = qr.getR();
        OldQRDecompositionTest.assertEquals(rRef, r, 1.0E-8);
        Matrix qRef = OldQRDecompositionTest.reshape(new double[]{ -0.165178287646573, 0.0510035857637869, 0.13985915987379, -0.120173729496501, -0.453198314345324, 0.644400679630493, -0.503117990820608, 0.24968739845381, 0.323968339146224, -0.465266080134262, 0.276508948773268, -0.687909700644343, 0.0544048888907195, -0.0166677718378263, 0.171309755790717, 0.310339001630029, 0.674790532821663, 0.0058166082200493, -0.381707516461884, 0.300504956413142, -0.105751091334003, 0.410450870871096, 0.31113446615821, 0.179338172684956, 0.361951807617901, 0.763921725548796, 0.380327892605634, -0.287274944594054, 0.0311604042556675, 0.0386096858143961, 0.0387156960650472, -0.232975755728917, 0.0358178276684149, 0.173105775703199, 0.327321867815603, 0.328671945345279, -0.36015879836344, -0.444261660176044, 0.09438499563253, 0.646216148583769 }, 8, 5);
        OldQRDecompositionTest.printMatrix("qRef", qRef);
        Matrix q = qr.getQ();
        OldQRDecompositionTest.printMatrix("q", q);
        OldQRDecompositionTest.assertEquals(qRef, q, 1.0E-8);
        Matrix x1 = qr.solve(OldQRDecompositionTest.reshape(new double[]{ -0.0178247686747641, 0.68631714634098, -0.335464858468858, 1.50249941751569, -0.669901640772149, -0.977025038942455, -1.18857546169856, -1.24792900492054 }, 8, 1));
        Matrix xref = OldQRDecompositionTest.reshape(new double[]{ -0.0127440093664874, 0.655825940180799, -0.100755415991702, -0.0349559562697406, -0.190744297762028 }, 5, 1);
        OldQRDecompositionTest.printMatrix("x1", x1);
        OldQRDecompositionTest.printMatrix("xref", xref);
        OldQRDecompositionTest.assertEquals(xref, x1, 1.0E-8);
    }

    @Test
    public void fullRankWide() {
        Matrix x = OldQRDecompositionTest.matrix().transpose();
        OldQRDecomposition qr = new OldQRDecomposition(x);
        assertFalse(qr.hasFullRank());
        Matrix rActual = qr.getR();
        Matrix rRef = OldQRDecompositionTest.reshape(new double[]{ -2.42812464965842, 0, 0, 0, 0, 0.303587286111356, -2.91663643494775, 0, 0, 0, -0.201812474153156, -0.765485720168378, 1.09989373598954, 0, 0, 1.47980701097885, -0.637545820524326, -1.55519859337935, 0.844655127991726, 0, 0.0248883129453161, 0.00115010570270549, -0.236340588891252, -0.092924118200147, 1.42910099545547, -1.1678472412429, 0.531245845248056, 0.351978196071514, -1.03241474816555, -2.20223861735426, -0.887809959067632, 0.189731251982918, -0.504321849233586, 0.490484123999836, 1.21266692336743, -0.633888169775463, 1.04738559065986, 0.284041239547031, 0.578183510077156, -0.942314870832456 }, 5, 8);
        OldQRDecompositionTest.printMatrix("rRef", rRef);
        OldQRDecompositionTest.printMatrix("rActual", rActual);
        OldQRDecompositionTest.assertEquals(rRef, rActual, 1.0E-8);
        Matrix qRef = OldQRDecompositionTest.reshape(new double[]{ -0.203489262374627, 0.316761677948356, -0.784155643293468, 0.394321494579, -0.29641971170211, 0.0311283614803723, -0.34755265020736, 0.137138511478328, 0.848579887681972, 0.373287266507375, -0.39603700561249, -0.787812566647329, -0.377864833067864, -0.275080943427399, 0.0636764674878229, 0.0763976893309043, -0.318551137554327, 0.286407036668598, 0.206004127289883, -0.876482672226889, 0.89159476695423, -0.238213616975551, -0.376141107880836, -0.0794701657055114, 0.0227025098210165 }, 5, 5);
        Matrix q = qr.getQ();
        OldQRDecompositionTest.printMatrix("qRef", qRef);
        OldQRDecompositionTest.printMatrix("q", q);
        OldQRDecompositionTest.assertEquals(qRef, q, 1.0E-8);
        Matrix x1 = qr.solve(OldQRDecompositionTest.b());
        Matrix xRef = OldQRDecompositionTest.reshape(new double[]{ -0.182580239668147, -0.437233627652114, 0.138787653097464, 0.672934739896228, -0.131420217069083, 0, 0, 0 }, 8, 1);
        OldQRDecompositionTest.printMatrix("xRef", xRef);
        OldQRDecompositionTest.printMatrix("x", x1);
        OldQRDecompositionTest.assertEquals(xRef, x1, 1.0E-8);
    }
}

