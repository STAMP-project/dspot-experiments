/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.netlib;


import org.junit.Assert;
import org.junit.Test;
import smile.math.matrix.Matrix;
import smile.math.matrix.SVD;


/**
 *
 *
 * @author Haifeng Li
 */
public class SVDTest {
    public SVDTest() {
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose1() {
        System.out.println("decompose symm");
        double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };
        double[] s = new double[]{ 1.7498382, 0.3165784, 0.1335834 };
        double[][] U = new double[][]{ new double[]{ 0.6881997, -0.07121225, 0.722018 }, new double[]{ 0.3700456, 0.89044952, -0.2648886 }, new double[]{ 0.6240573, -0.44947578, -0.6391588 } };
        double[][] V = new double[][]{ new double[]{ 0.6881997, -0.07121225, 0.722018 }, new double[]{ 0.3700456, 0.89044952, -0.2648886 }, new double[]{ 0.6240573, -0.44947578, -0.6391588 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-7));
        Assert.assertEquals(U.length, result.getU().nrows());
        Assert.assertEquals(U[0].length, result.getU().ncols());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        Assert.assertEquals(V[0].length, result.getV().ncols());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose2() {
        System.out.println("decompose asymm");
        double[][] A = new double[][]{ new double[]{ 1.1972088, -1.8391378, 0.3019585, -1.1165701, -1.7210814, 0.4918882, -0.04247433 }, new double[]{ 0.06605075, 1.0315583, 0.8294362, -0.3646043, -1.6038017, -0.918811, -0.6376034 }, new double[]{ -1.02637715, 1.0747931, -0.8089055, -0.4726863, -0.2064826, -0.3325532, 0.17966051 }, new double[]{ -1.45817729, -0.8942353, 0.3459245, 1.5068363, -2.0180708, -0.369635, -1.19575563 }, new double[]{ -0.07318103, -0.2783787, 1.2237598, 0.1995332, 0.2545336, -0.1392502, -1.88207227 }, new double[]{ 0.88248425, -0.9360321, 0.1393172, 0.1393281, -0.3277873, -0.5553013, 1.63805985 }, new double[]{ 0.12641406, -0.8710055, -0.2712301, 0.2296515, 1.1781535, -0.2158704, -0.27529472 } };
        double[] s = new double[]{ 3.8589375, 3.4396766, 2.6487176, 2.2317399, 1.5165054, 0.8109055, 0.2706515 };
        double[][] U = new double[][]{ new double[]{ -0.3082776, 0.77676231, 0.01330514, 0.23231424, -0.47682758, 0.13927109, 0.02640713 }, new double[]{ -0.4013477, -0.0911205, 0.4875444, 0.47371793, 0.40636608, 0.24600706, -0.37796295 }, new double[]{ 0.0599719, -0.31406586, 0.45428229, -0.08071283, -0.38432597, 0.57320261, 0.45673993 }, new double[]{ -0.7694214, -0.12681435, -0.05536793, -0.62189972, -0.02075522, -0.01724911, -0.03681864 }, new double[]{ -0.3319069, -0.17984404, -0.54466777, 0.45335157, 0.19377726, 0.12333423, 0.55003852 }, new double[]{ 0.1259351, 0.49087824, 0.16349687, -0.32080176, 0.64828744, 0.20643772, 0.38812467 }, new double[]{ 0.1491884, 0.01768604, -0.47884363, -0.14108924, 0.03922507, 0.73034065, -0.43965505 } };
        double[][] V = new double[][]{ new double[]{ -0.2122609, -0.54650056, 0.08071332, -0.43239135, -0.2925067, 0.141455, 0.59769207 }, new double[]{ -0.1943605, 0.63132116, -0.54059857, -0.3708997, -0.1363031, 0.2892641, 0.17774114 }, new double[]{ 0.3031265, -0.06182488, 0.18579097, -0.38606409, -0.5364911, 0.2983466, -0.58642548 }, new double[]{ 0.1844063, 0.24425278, 0.25923756, 0.59043765, -0.4435443, 0.3959057, 0.37019098 }, new double[]{ -0.7164205, 0.30694911, 0.58264743, -0.07458095, -0.114214, -0.1311972, -0.13124764 }, new double[]{ -0.1103067, -0.106336, 0.18257905, -0.03638501, 0.5722925, 0.7784398, -0.09153611 }, new double[]{ -0.5156083, -0.36573746, -0.4761334, 0.41342817, -0.2659765, 0.1654796, -0.32346758 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-7));
        Assert.assertEquals(U.length, result.getU().nrows());
        Assert.assertEquals(U[0].length, result.getU().ncols());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        Assert.assertEquals(V[0].length, result.getV().ncols());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose3() {
        System.out.println("decompose m = n+1");
        double[][] A = new double[][]{ new double[]{ 1.1972088, -1.8391378, 0.3019585, -1.1165701, -1.7210814, 0.4918882 }, new double[]{ 0.06605075, 1.0315583, 0.8294362, -0.3646043, -1.6038017, -0.918811 }, new double[]{ -1.02637715, 1.0747931, -0.8089055, -0.4726863, -0.2064826, -0.3325532 }, new double[]{ -1.45817729, -0.8942353, 0.3459245, 1.5068363, -2.0180708, -0.369635 }, new double[]{ -0.07318103, -0.2783787, 1.2237598, 0.1995332, 0.2545336, -0.1392502 }, new double[]{ 0.88248425, -0.9360321, 0.1393172, 0.1393281, -0.3277873, -0.5553013 }, new double[]{ 0.12641406, -0.8710055, -0.2712301, 0.2296515, 1.1781535, -0.2158704 } };
        double[] s = new double[]{ 3.6447007, 3.1719019, 2.4155022, 1.6952749, 1.0349052, 0.6735233 };
        double[][] U = new double[][]{ new double[]{ -0.66231606, 0.51980064, -0.26908096, -0.33255132, 0.1998343961, 0.25344461 }, new double[]{ -0.30950323, -0.38356363, -0.57342388, 0.43584295, -0.2842953084, 0.06773874 }, new double[]{ 0.17209598, -0.40152786, -0.2554974, -0.47194228, -0.1795895194, 0.6096016 }, new double[]{ -0.58855512, -0.52801793, 0.59486615, -0.13721651, -4.042427E-4, -0.01414006 }, new double[]{ -0.06838272, 0.03221968, 0.14785619, 0.64819245, 0.3955572924, 0.53374206 }, new double[]{ -0.23683786, 0.25613876, 0.07459517, 0.19208798, -0.7235935956, -0.10586201 }, new double[]{ 0.16959559, 0.27570548, 0.39014092, 0.02900709, -0.4085787191, 0.51310416 } };
        double[][] V = new double[][]{ new double[]{ -0.08624942, 0.642381656, -0.35639657, 0.2600624, -0.303192728, -0.5415995 }, new double[]{ 0.46728106, -0.567452824, -0.56054543, 0.1717478, 0.067268188, -0.3337846 }, new double[]{ -0.26399674, -0.005897261, -0.02438536, 0.8302504, 0.448103782, 0.1989057 }, new double[]{ -0.03389306, -0.296652409, 0.68563317, 0.2309273, -0.145824242, -0.6051146 }, new double[]{ 0.83642784, 0.352498963, 0.2930534, 0.2264531, 0.006202435, 0.1973149 }, new double[]{ 0.06127719, 0.230326187, 0.04693098, -0.3300697, 0.825499232, -0.3880689 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-7));
        Assert.assertEquals(U.length, result.getU().nrows());
        Assert.assertEquals(U[0].length, result.getU().ncols());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        Assert.assertEquals(V[0].length, result.getV().ncols());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose4() {
        System.out.println("decompose m = n+2");
        double[][] A = new double[][]{ new double[]{ 1.1972088, -1.8391378, 0.3019585, -1.1165701, -1.7210814 }, new double[]{ 0.06605075, 1.0315583, 0.8294362, -0.3646043, -1.6038017 }, new double[]{ -1.02637715, 1.0747931, -0.8089055, -0.4726863, -0.2064826 }, new double[]{ -1.45817729, -0.8942353, 0.3459245, 1.5068363, -2.0180708 }, new double[]{ -0.07318103, -0.2783787, 1.2237598, 0.1995332, 0.2545336 }, new double[]{ 0.88248425, -0.9360321, 0.1393172, 0.1393281, -0.3277873 }, new double[]{ 0.12641406, -0.8710055, -0.2712301, 0.2296515, 1.1781535 } };
        double[] s = new double[]{ 3.6392869, 3.0965326, 2.4131673, 1.6285557, 0.7495616 };
        double[][] U = new double[][]{ new double[]{ -0.68672751, -0.4707769, -0.27062524, 0.30518577, 0.355857 }, new double[]{ -0.28422169, 0.33969351, -0.56700359, -0.38788214, -0.15789372 }, new double[]{ 0.18880503, 0.39049353, -0.26448028, 0.50872376, 0.42411327 }, new double[]{ -0.56957699, 0.56761727, 0.58111879, 0.11662686, -0.01347444 }, new double[]{ -0.06682433, -0.04559753, 0.15586923, -0.68802278, 0.60990585 }, new double[]{ -0.23677832, -0.29935481, 0.09428368, -0.0322448, -0.50781217 }, new double[]{ 0.16440378, -0.31082218, 0.40550635, 0.09794049, 0.1962738 } };
        double[][] V = new double[][]{ new double[]{ -0.1064632, -0.66842275, -0.33744231, -0.1953744, -0.6243702 }, new double[]{ 0.48885825, 0.546411345, -0.57018018, -0.2348795, -0.2866678 }, new double[]{ -0.26164973, 0.002221196, -0.01788181, -0.9049532, 0.3350739 }, new double[]{ -0.02353895, 0.306904408, 0.68247803, -0.2353931, -0.6197333 }, new double[]{ 0.82502638, -0.40056263, 0.30810911, -0.1797507, 0.177875 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-7));
        Assert.assertEquals(U.length, result.getU().nrows());
        Assert.assertEquals(U[0].length, result.getU().ncols());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        Assert.assertEquals(V[0].length, result.getV().ncols());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose5() {
        System.out.println("decompose m = n+3");
        double[][] A = new double[][]{ new double[]{ 1.1972088, -1.8391378, 0.3019585, -1.1165701 }, new double[]{ 0.06605075, 1.0315583, 0.8294362, -0.3646043 }, new double[]{ -1.02637715, 1.0747931, -0.8089055, -0.4726863 }, new double[]{ -1.45817729, -0.8942353, 0.3459245, 1.5068363 }, new double[]{ -0.07318103, -0.2783787, 1.2237598, 0.1995332 }, new double[]{ 0.88248425, -0.9360321, 0.1393172, 0.1393281 }, new double[]{ 0.12641406, -0.8710055, -0.2712301, 0.2296515 } };
        double[] s = new double[]{ 3.2188437, 2.5504483, 1.7163918, 0.9212875 };
        double[][] U = new double[][]{ new double[]{ -0.739071, 0.15540183, 0.093738524, -0.60555964 }, new double[]{ 0.1716777, 0.26405327, -0.616548935, -0.11171885 }, new double[]{ 0.4583068, 0.19615535, 0.365025826, -0.56118537 }, new double[]{ 0.1185448, -0.88710768, -0.004538332, -0.24629659 }, new double[]{ -0.1055393, -0.19831478, -0.634814754, -0.26986239 }, new double[]{ -0.3836089, -0.06331799, 0.006896881, 0.41026537 }, new double[]{ -0.2047156, -0.19326474, 0.273456965, 0.06389058 } };
        double[][] V = new double[][]{ new double[]{ -0.5820171, 0.4822386, -0.12201399, 0.6432842 }, new double[]{ 0.773472, 0.4993237, -0.27962029, 0.2724507 }, new double[]{ -0.1670058, -0.1563235, -0.94966302, -0.2140379 }, new double[]{ 0.1873664, -0.702627, -0.07117046, 0.6827473 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-7));
        Assert.assertEquals(U.length, result.getU().nrows());
        Assert.assertEquals(U[0].length, result.getU().ncols());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        Assert.assertEquals(V[0].length, result.getV().ncols());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose6() {
        System.out.println("decompose m = n-1");
        double[][] A = new double[][]{ new double[]{ 1.1972088, -1.8391378, 0.3019585, -1.1165701, -1.7210814, 0.4918882, -0.04247433 }, new double[]{ 0.06605075, 1.0315583, 0.8294362, -0.3646043, -1.6038017, -0.918811, -0.6376034 }, new double[]{ -1.02637715, 1.0747931, -0.8089055, -0.4726863, -0.2064826, -0.3325532, 0.17966051 }, new double[]{ -1.45817729, -0.8942353, 0.3459245, 1.5068363, -2.0180708, -0.369635, -1.19575563 }, new double[]{ -0.07318103, -0.2783787, 1.2237598, 0.1995332, 0.2545336, -0.1392502, -1.88207227 }, new double[]{ 0.88248425, -0.9360321, 0.1393172, 0.1393281, -0.3277873, -0.5553013, 1.63805985 } };
        double[] s = new double[]{ 3.8244094, 3.4392541, 2.3784254, 2.1694244, 1.5150752, 0.4743856 };
        double[][] U = new double[][]{ new double[]{ 0.31443091, 0.77409564, -0.06404561, 0.2362505, 0.48411517, 0.08732402 }, new double[]{ 0.37429954, -0.08997642, 0.33948894, 0.740303, -0.37663472, -0.2159842 }, new double[]{ -0.08460683, -0.30944648, 0.49768196, 0.1798789, 0.40657776, 0.67211259 }, new double[]{ 0.78096534, -0.13597601, 0.27845058, -0.5407806, 0.01748391, -0.03632677 }, new double[]{ 0.35762337, -0.18789909, -0.68652942, 0.167081, -0.20242003, 0.54459792 }, new double[]{ -0.12678093, 0.49307962, 0.29000123, -0.2083774, -0.64590538, 0.44281315 } };
        double[][] V = new double[][]{ new double[]{ -0.2062642, 0.54825338, -0.2795672, 0.3408979, -0.2925761, -0.412469519 }, new double[]{ -0.251635, -0.62127194, 0.28319612, 0.5322251, -0.1297528, -0.410270389 }, new double[]{ 0.3043552, 0.05848409, -0.35475338, 0.2434904, -0.5456797, 0.040325347 }, new double[]{ 0.204716, -0.2497463, 0.01491918, -0.6588368, -0.461658, -0.465507184 }, new double[]{ -0.6713331, -0.30795185, -0.57548345, -0.1976943, -0.1242132, 0.261610893 }, new double[]{ -0.112221, 0.10728081, -0.28476779, -0.1527923, 0.5474147, -0.612188492 }, new double[]{ -0.544346, 0.37590198, 0.55072289, -0.2115256, -0.2675392, -0.003003781 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-7));
        Assert.assertEquals(U.length, result.getU().nrows());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose7() {
        System.out.println("decompose m = n-2");
        double[][] A = new double[][]{ new double[]{ 1.1972088, -1.8391378, 0.3019585, -1.1165701, -1.7210814, 0.4918882, -0.04247433 }, new double[]{ 0.06605075, 1.0315583, 0.8294362, -0.3646043, -1.6038017, -0.918811, -0.6376034 }, new double[]{ -1.02637715, 1.0747931, -0.8089055, -0.4726863, -0.2064826, -0.3325532, 0.17966051 }, new double[]{ -1.45817729, -0.8942353, 0.3459245, 1.5068363, -2.0180708, -0.369635, -1.19575563 }, new double[]{ -0.07318103, -0.2783787, 1.2237598, 0.1995332, 0.2545336, -0.1392502, -1.88207227 } };
        double[] s = new double[]{ 3.8105658, 3.0883849, 2.2956507, 2.0984771, 0.9019027 };
        double[][] U = new double[][]{ new double[]{ 0.4022505, -0.8371341, 0.21890033, -0.0115002, 0.29891712 }, new double[]{ 0.3628648, 0.1788073, 0.52047618, 0.66921454, -0.34294833 }, new double[]{ -0.1204081, 0.3526074, 0.512685919, -0.0315952, 0.7728679 }, new double[]{ 0.7654028, 0.3523577, -0.005786511, -0.53518467, -0.05955197 }, new double[]{ 0.325859, 0.136918, -0.646766462, 0.51439164, 0.43836489 } };
        double[][] V = new double[][]{ new double[]{ -0.134051, -0.6074832, -0.07579249, 0.38390363, -0.4471462 }, new double[]{ -0.3332981, 0.566584, 0.37922383, 0.48268873, -0.1570301 }, new double[]{ 0.3105522, -0.0324612, -0.30945577, 0.48678825, -0.3365301 }, new double[]{ 0.1820803, 0.408342, -0.35471238, -0.43842294, -0.6389961 }, new double[]{ -0.7114696, 0.1311251, -0.64046888, 0.07815179, 0.1194533 }, new double[]{ -0.1112159, -0.2728406, -0.19551704, -0.23056606, 0.1841538 }, new double[]{ -0.4720051, -0.2247534, 0.42477493, -0.36219292, -0.4534882 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-7));
        Assert.assertEquals(U.length, result.getU().nrows());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose8() {
        System.out.println("decompose m = n-3");
        double[][] A = new double[][]{ new double[]{ 1.1972088, -1.8391378, 0.3019585, -1.1165701, -1.7210814, 0.4918882, -0.04247433 }, new double[]{ 0.06605075, 1.0315583, 0.8294362, -0.3646043, -1.6038017, -0.918811, -0.6376034 }, new double[]{ -1.02637715, 1.0747931, -0.8089055, -0.4726863, -0.2064826, -0.3325532, 0.17966051 }, new double[]{ -1.45817729, -0.8942353, 0.3459245, 1.5068363, -2.0180708, -0.369635, -1.19575563 } };
        double[] s = new double[]{ 3.668957, 3.068763, 2.179053, 1.29311 };
        double[][] U = new double[][]{ new double[]{ -0.491888, 0.7841689, -0.1533124, 0.3458623 }, new double[]{ -0.3688033, -0.2221466, -0.8172311, -0.38310353 }, new double[]{ 0.1037476, -0.378419, -0.3438745, 0.85310363 }, new double[]{ -0.7818356, -0.4387814, 0.4363243, 0.07632262 } };
        double[][] V = new double[][]{ new double[]{ 0.11456074, 0.63620515, -0.23901163, -0.4625536 }, new double[]{ 0.36382542, -0.5493094, -0.60614838, -0.1412273 }, new double[]{ -0.22044591, 0.06740501, -0.13539726, -0.6782114 }, new double[]{ -0.14811938, -0.41609019, 0.59161667, -0.4135324 }, new double[]{ 0.81615679, -0.0096816, 0.35107473, -0.2405136 }, new double[]{ 0.09577622, 0.28606564, 0.28844835, 0.1625626 }, new double[]{ 0.32967585, 0.1841207, -0.02567023, 0.2254902 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-6));
        Assert.assertEquals(U.length, result.getU().nrows());
        for (int i = 0; i < (U.length); i++) {
            for (int j = 0; j < (U[i].length); j++) {
                Assert.assertEquals(Math.abs(U[i][j]), Math.abs(result.getU().get(i, j)), 1.0E-7);
            }
        }
        Assert.assertEquals(V.length, result.getV().nrows());
        for (int i = 0; i < (V.length); i++) {
            for (int j = 0; j < (V[i].length); j++) {
                Assert.assertEquals(Math.abs(V[i][j]), Math.abs(result.getV().get(i, j)), 1.0E-7);
            }
        }
    }

    /**
     * Test of decompose method, of class SingularValueDecomposition.
     */
    @Test
    public void testDecompose9() {
        System.out.println("decompose sparse matrix");
        double[][] A = new double[][]{ new double[]{ 1, 0, 0, 1, 0, 0, 0, 0, 0 }, new double[]{ 1, 0, 1, 0, 0, 0, 0, 0, 0 }, new double[]{ 1, 1, 0, 0, 0, 0, 0, 0, 0 }, new double[]{ 0, 1, 1, 0, 1, 0, 0, 0, 0 }, new double[]{ 0, 1, 1, 2, 0, 0, 0, 0, 0 }, new double[]{ 0, 1, 0, 0, 1, 0, 0, 0, 0 }, new double[]{ 0, 1, 0, 0, 1, 0, 0, 0, 0 }, new double[]{ 0, 0, 1, 1, 0, 0, 0, 0, 0 }, new double[]{ 0, 1, 0, 0, 0, 0, 0, 0, 1 }, new double[]{ 0, 0, 0, 0, 0, 1, 1, 1, 0 }, new double[]{ 0, 0, 0, 0, 0, 0, 1, 1, 1 }, new double[]{ 0, 0, 0, 0, 0, 0, 0, 1, 1 } };
        double[] s = new double[]{ 3.34088, 2.5417, 2.35394, 1.64453, 1.50483, 1.30638, 0.845903, 0.560134, 0.363677 };
        double[][] Vt = new double[][]{ new double[]{ 0.197393, 0.60599, 0.462918, 0.542114, 0.279469, 0.00381521, 0.0146315, 0.0241368, 0.0819574 }, new double[]{ 0.0559135, -0.165593, 0.127312, 0.231755, -0.106775, -0.192848, -0.437875, -0.615122, -0.529937 }, new double[]{ -0.11027, 0.497326, -0.207606, -0.569921, 0.50545, -0.0981842, -0.192956, -0.252904, -0.0792731 }, new double[]{ -0.949785, -0.0286489, 0.0416092, 0.267714, 0.150035, 0.0150815, 0.0155072, 0.010199, -0.0245549 }, new double[]{ -0.0456786, 0.206327, -0.378336, 0.205605, -0.327194, -0.394841, -0.349485, -0.149798, 0.601993 }, new double[]{ -0.0765936, -0.256475, 0.7244, -0.368861, 0.034813, -0.300161, -0.212201, 9.74342E-5, 0.362219 }, new double[]{ -0.177318, 0.432984, 0.23689, -0.2648, -0.672304, 0.34084, 0.152195, -0.249146, -0.0380342 }, new double[]{ -0.0143933, 0.0493053, 0.0088255, -0.0194669, -0.0583496, 0.454477, -0.761527, 0.449643, -0.0696375 }, new double[]{ -0.0636923, 0.242783, 0.0240769, -0.0842069, -0.262376, -0.619847, 0.0179752, 0.51989, -0.453507 } };
        double[][] Ut = new double[][]{ new double[]{ 0.221351, 0.197645, 0.24047, 0.403599, 0.644481, 0.265037, 0.265037, 0.300828, 0.205918, 0.0127462, 0.0361358, 0.0317563 }, new double[]{ 0.11318, 0.0720878, -0.043152, -0.0570703, 0.167301, -0.10716, -0.10716, 0.14127, -0.273647, -0.490162, -0.622785, -0.450509 }, new double[]{ -0.288958, -0.13504, 0.164429, 0.337804, -0.361148, 0.425998, 0.425998, -0.330308, 0.177597, -0.23112, -0.223086, -0.141115 }, new double[]{ -0.414751, -0.55224, -0.594962, 0.0991137, 0.333462, 0.0738122, 0.0738122, 0.188092, -0.0323519, 0.024802, 7.00072E-4, -0.00872947 }, new double[]{ 0.106275, -0.281769, 0.106755, -0.331734, 0.158955, -0.0803194, -0.0803194, -0.114785, 0.53715, -0.59417, 0.0682529, 0.300495 }, new double[]{ -0.340983, 0.495878, -0.254955, 0.384832, -0.206523, -0.169676, -0.169676, 0.272155, 0.080944, -0.392125, 0.114909, 0.277343 }, new double[]{ -0.522658, 0.0704234, 0.30224, -0.00287218, 0.165829, -0.282916, -0.282916, -0.0329941, 0.466898, 0.288317, -0.159575, -0.339495 }, new double[]{ -0.0604501, -0.00994004, 0.062328, -3.90504E-4, 0.034272, -0.0161465, -0.0161465, -0.018998, -0.0362988, 0.254568, -0.681125, 0.678418 }, new double[]{ -0.406678, -0.10893, 0.492444, 0.0123293, 0.270696, -0.0538747, -0.0538747, -0.165339, -0.579426, -0.225424, 0.231961, 0.182535 } };
        SVD result = Matrix.newInstance(A).svd();
        Assert.assertTrue(Math.equals(s, result.getSingularValues(), 1.0E-5));
        Assert.assertEquals(Ut[0].length, result.getU().nrows());
        Assert.assertEquals(Ut.length, result.getU().ncols());
        for (int i = 0; i < (Ut.length); i++) {
            for (int j = 0; j < (Ut[i].length); j++) {
                Assert.assertEquals(Math.abs(Ut[i][j]), Math.abs(result.getU().get(j, i)), 1.0E-5);
            }
        }
        Assert.assertEquals(Vt[0].length, result.getV().nrows());
        Assert.assertEquals(Vt.length, result.getV().ncols());
        for (int i = 0; i < (Vt.length); i++) {
            for (int j = 0; j < (Vt[i].length); j++) {
                Assert.assertEquals(Math.abs(Vt[i][j]), Math.abs(result.getV().get(j, i)), 1.0E-5);
            }
        }
    }

    /**
     * Test of solve method, of class SingularValueDecomposition.
     */
    @Test
    public void testSolve_doubleArr_doubleArr() {
        System.out.println("solve");
        double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };
        double[] B = new double[]{ 0.5, 0.5, 0.5 };
        double[] X = new double[]{ -0.2027027, 0.8783784, 0.472973 };
        SVD result = Matrix.newInstance(A).svd();
        double[] x = new double[B.length];
        result.solve(B, x);
        Assert.assertEquals(X.length, x.length);
        for (int i = 0; i < (X.length); i++) {
            Assert.assertEquals(X[i], x[i], 1.0E-7);
        }
    }

    /**
     * Test of solve method, of class SingularValueDecomposition.
     */
    @Test
    public void testSolve_doubleArrArr_doubleArrArr() {
        System.out.println("solve");
        double[][] A = new double[][]{ new double[]{ 0.9, 0.4, 0.7 }, new double[]{ 0.4, 0.5, 0.3 }, new double[]{ 0.7, 0.3, 0.8 } };
        double[][] B = new double[][]{ new double[]{ 0.5, 0.2 }, new double[]{ 0.5, 0.8 }, new double[]{ 0.5, 0.3 } };
        double[][] X = new double[][]{ new double[]{ -0.2027027, -1.2837838 }, new double[]{ 0.8783784, 2.2297297 }, new double[]{ 0.472973, 0.6621622 } };
        SVD result = new NLMatrix(A).svd();
        NLMatrix x = new NLMatrix(B);
        result.solve(x);
        for (int i = 0; i < (x.nrows()); i++) {
            for (int j = 0; j < (x.ncols()); j++) {
                Assert.assertEquals(X[i][j], x.get(i, j), 1.0E-7);
            }
        }
    }
}

