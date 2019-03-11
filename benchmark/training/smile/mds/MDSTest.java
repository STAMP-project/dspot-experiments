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
package smile.mds;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class MDSTest {
    double[][] eurodist = new double[][]{ new double[]{ 0, 3313, 2963, 3175, 3339, 2762, 3276, 2610, 4485, 2977, 3030, 4532, 2753, 3949, 2865, 2282, 2179, 3000, 817, 3927, 1991 }, new double[]{ 3313, 0, 1318, 1326, 1294, 1498, 2218, 803, 1172, 2018, 1490, 1305, 645, 636, 521, 1014, 1365, 1033, 1460, 2868, 1802 }, new double[]{ 2963, 1318, 0, 204, 583, 206, 966, 677, 2256, 597, 172, 2084, 690, 1558, 1011, 925, 747, 285, 1511, 1616, 1175 }, new double[]{ 3175, 1326, 204, 0, 460, 409, 1136, 747, 2224, 714, 330, 2052, 739, 1550, 1059, 1077, 977, 280, 1662, 1786, 1381 }, new double[]{ 3339, 1294, 583, 460, 0, 785, 1545, 853, 2047, 1115, 731, 1827, 789, 1347, 1101, 1209, 1160, 340, 1794, 2196, 1588 }, new double[]{ 2762, 1498, 206, 409, 785, 0, 760, 1662, 2436, 460, 269, 2290, 714, 1764, 1035, 911, 583, 465, 1497, 1403, 937 }, new double[]{ 3276, 2218, 966, 1136, 1545, 760, 0, 1418, 3196, 460, 269, 2971, 1458, 2498, 1778, 1537, 1104, 1176, 2050, 650, 1455 }, new double[]{ 2610, 803, 677, 747, 853, 1662, 1418, 0, 1975, 1118, 895, 1936, 158, 1439, 425, 328, 591, 513, 995, 2068, 1019 }, new double[]{ 4485, 1172, 2256, 2224, 2047, 2436, 3196, 1975, 0, 2897, 2428, 676, 1817, 698, 1693, 2185, 2565, 1971, 2631, 3886, 2974 }, new double[]{ 2977, 2018, 597, 714, 1115, 460, 460, 1118, 2897, 0, 550, 2671, 1159, 2198, 1479, 1238, 805, 877, 1751, 949, 1155 }, new double[]{ 3030, 1490, 172, 330, 731, 269, 269, 895, 2428, 550, 0, 2280, 863, 1730, 1183, 1098, 851, 457, 1683, 1500, 1205 }, new double[]{ 4532, 1305, 2084, 2052, 1827, 2290, 2971, 1936, 676, 2671, 2280, 0, 1178, 668, 1762, 2250, 2507, 1799, 2700, 3231, 2937 }, new double[]{ 2753, 645, 690, 739, 789, 714, 1458, 158, 1817, 1159, 863, 1178, 0, 1281, 320, 328, 724, 471, 1048, 2108, 1157 }, new double[]{ 3949, 636, 1558, 1550, 1347, 1764, 2498, 1439, 698, 2198, 1730, 668, 1281, 0, 1157, 1724, 2010, 1273, 2097, 3188, 2409 }, new double[]{ 2865, 521, 1011, 1059, 1101, 1035, 1778, 425, 1693, 1479, 1183, 1762, 320, 1157, 0, 618, 1109, 792, 1011, 2428, 1363 }, new double[]{ 2282, 1014, 925, 1077, 1209, 911, 1537, 328, 2185, 1238, 1098, 2250, 328, 1724, 618, 0, 331, 856, 586, 2187, 898 }, new double[]{ 2179, 1365, 747, 977, 1160, 583, 1104, 591, 2565, 805, 851, 2507, 724, 2010, 1109, 331, 0, 821, 946, 1754, 428 }, new double[]{ 3000, 1033, 285, 280, 340, 465, 1176, 513, 1971, 877, 457, 1799, 471, 1273, 792, 856, 821, 0, 1476, 1827, 1249 }, new double[]{ 817, 1460, 1511, 1662, 1794, 1497, 2050, 995, 2631, 1751, 1683, 2700, 1048, 2097, 1011, 586, 946, 1476, 0, 2707, 1209 }, new double[]{ 3927, 2868, 1616, 1786, 2196, 1403, 650, 2068, 3886, 949, 1500, 3231, 2108, 3188, 2428, 2187, 1754, 1827, 2707, 0, 2105 }, new double[]{ 1991, 1802, 1175, 1381, 1588, 937, 1455, 1019, 2974, 1155, 1205, 2937, 1157, 2409, 1363, 898, 428, 1249, 1209, 2105, 0 } };

    public MDSTest() {
    }

    /**
     * Test of learn method, of class MDS.
     */
    @Test
    public void testLearn_doubleArrArr() {
        System.out.println("learn");
        double[] eigs = new double[]{ 1.95383770895E7, 1.1856555334E7 };
        double[][] points = new double[][]{ new double[]{ 2290.27468, 1798.80293 }, new double[]{ -825.38279, 546.81148 }, new double[]{ 59.183341, -367.08135 }, new double[]{ -82.845973, -429.91466 }, new double[]{ -352.499435, -290.90843 }, new double[]{ 293.689633, -405.31194 }, new double[]{ 681.931545, -1108.64478 }, new double[]{ -9.423364, 240.406 }, new double[]{ -2048.449113, 642.45854 }, new double[]{ 561.10897, -773.36929 }, new double[]{ 164.921799, -549.36704 }, new double[]{ -1935.040811, 49.12514 }, new double[]{ -226.423236, 187.08779 }, new double[]{ -1423.353697, 305.87513 }, new double[]{ -299.49871, 388.80726 }, new double[]{ 260.878046, 416.67381 }, new double[]{ 587.675679, 81.18224 }, new double[]{ -156.836257, -211.13911 }, new double[]{ 709.413282, 1109.36665 }, new double[]{ 839.445911, -1836.79055 }, new double[]{ 911.2305, 205.9302 } };
        MDS mds = new MDS(eurodist);
        for (int i = 0; i < (eigs.length); i++) {
            System.out.print(((eigs[i]) + " "));
        }
        System.out.println("==============");
        for (int i = 0; i < (mds.getEigenValues().length); i++) {
            System.out.print(((mds.getEigenValues()[i]) + " "));
        }
        System.out.println();
        Assert.assertTrue(Math.equals(eigs, mds.getEigenValues(), 1.0E-4));
        double[][] coords = mds.getCoordinates();
        for (int i = 0; i < (points.length); i++) {
            for (int j = 0; j < (points[0].length); j++) {
                Assert.assertEquals(Math.abs(points[i][j]), Math.abs(coords[i][j]), 0.01);
            }
        }
    }

    /**
     * Test of learn method, of class MDS.
     */
    @Test
    public void testLearn_doubleArrArr_double() {
        System.out.println("learn");
        double[] eigs = new double[]{ 4.22749738E7, 3.16661864E7 };
        double[][] points = new double[][]{ new double[]{ 2716.56182, 3549.216493 }, new double[]{ -1453.753109, 455.895291 }, new double[]{ 217.426476, -1073.442137 }, new double[]{ 1.682974, -1135.742982 }, new double[]{ -461.875781, -871.913389 }, new double[]{ 594.256798, -1029.818247 }, new double[]{ 1271.216005, -1622.039302 }, new double[]{ -88.721376, 4.068005 }, new double[]{ -3059.18099, 836.535103 }, new double[]{ 1056.316198, -1350.037932 }, new double[]{ 445.663432, -1304.392098 }, new double[]{ -2866.160085, 211.043554 }, new double[]{ -436.147722, -140.147837 }, new double[]{ -2300.753691, 234.863677 }, new double[]{ -586.877042, 217.428075 }, new double[]{ 336.906562, 350.948939 }, new double[]{ 928.407679, -112.132182 }, new double[]{ -193.653844, -847.157498 }, new double[]{ 908.6821, 1742.395923 }, new double[]{ 1499.140467, -1897.522865 }, new double[]{ 1319.918808, 295.010834 } };
        MDS mds = new MDS(eurodist, 2, true);
        Assert.assertTrue(Math.equals(eigs, mds.getEigenValues(), 0.1));
        double[][] coords = mds.getCoordinates();
        for (int i = 0; i < (points.length); i++) {
            for (int j = 0; j < (points[0].length); j++) {
                Assert.assertEquals(Math.abs(points[i][j]), Math.abs(coords[i][j]), 0.01);
            }
        }
    }
}

