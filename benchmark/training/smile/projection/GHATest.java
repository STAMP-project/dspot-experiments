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
package smile.projection;


import org.junit.Assert;
import org.junit.Test;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.Matrix;


/**
 *
 *
 * @author Haifeng Li
 */
@SuppressWarnings("unused")
public class GHATest {
    double[][] USArrests = new double[][]{ // Murder Assault UrbanPop Rape
    new double[]{ 13.2, 236, 58, 21.2 }// Alabama
    // Alabama
    // Alabama
    , new double[]{ 10.0, 263, 48, 44.5 }// Alaska
    // Alaska
    // Alaska
    , new double[]{ 8.1, 294, 80, 31.0 }// Arizona
    // Arizona
    // Arizona
    , new double[]{ 8.8, 190, 50, 19.5 }// Arkansas
    // Arkansas
    // Arkansas
    , new double[]{ 9.0, 276, 91, 40.6 }// California
    // California
    // California
    , new double[]{ 7.9, 204, 78, 38.7 }// Colorado
    // Colorado
    // Colorado
    , new double[]{ 3.3, 110, 77, 11.1 }// Connecticut
    // Connecticut
    // Connecticut
    , new double[]{ 5.9, 238, 72, 15.8 }// Delaware
    // Delaware
    // Delaware
    , new double[]{ 15.4, 335, 80, 31.9 }// Florida
    // Florida
    // Florida
    , new double[]{ 17.4, 211, 60, 25.8 }// Georgia
    // Georgia
    // Georgia
    , new double[]{ 5.3, 46, 83, 20.2 }// Hawaii
    // Hawaii
    // Hawaii
    , new double[]{ 2.6, 120, 54, 14.2 }// Idaho
    // Idaho
    // Idaho
    , new double[]{ 10.4, 249, 83, 24.0 }// Illinois
    // Illinois
    // Illinois
    , new double[]{ 7.2, 113, 65, 21.0 }// Indiana
    // Indiana
    // Indiana
    , new double[]{ 2.2, 56, 57, 11.3 }// Iowa
    // Iowa
    // Iowa
    , new double[]{ 6.0, 115, 66, 18.0 }// Kansas
    // Kansas
    // Kansas
    , new double[]{ 9.7, 109, 52, 16.3 }// Kentucky
    // Kentucky
    // Kentucky
    , new double[]{ 15.4, 249, 66, 22.2 }// Louisiana
    // Louisiana
    // Louisiana
    , new double[]{ 2.1, 83, 51, 7.8 }// Maine
    // Maine
    // Maine
    , new double[]{ 11.3, 300, 67, 27.8 }// Maryland
    // Maryland
    // Maryland
    , new double[]{ 4.4, 149, 85, 16.3 }// Massachusetts
    // Massachusetts
    // Massachusetts
    , new double[]{ 12.1, 255, 74, 35.1 }// Michigan
    // Michigan
    // Michigan
    , new double[]{ 2.7, 72, 66, 14.9 }// Michigan
    // Michigan
    // Michigan
    , new double[]{ 16.1, 259, 44, 17.1 }// Mississippi
    // Mississippi
    // Mississippi
    , new double[]{ 9.0, 178, 70, 28.2 }// Missouri
    // Missouri
    // Missouri
    , new double[]{ 6.0, 109, 53, 16.4 }// Montana
    // Montana
    // Montana
    , new double[]{ 4.3, 102, 62, 16.5 }// Nebraska
    // Nebraska
    // Nebraska
    , new double[]{ 12.2, 252, 81, 46.0 }// Nevada
    // Nevada
    // Nevada
    , new double[]{ 2.1, 57, 56, 9.5 }// New Hampshire
    // New Hampshire
    // New Hampshire
    , new double[]{ 7.4, 159, 89, 18.8 }// New Jersey
    // New Jersey
    // New Jersey
    , new double[]{ 11.4, 285, 70, 32.1 }// New Mexico
    // New Mexico
    // New Mexico
    , new double[]{ 11.1, 254, 86, 26.1 }// New York
    // New York
    // New York
    , new double[]{ 13.0, 337, 45, 16.1 }// North Carolina
    // North Carolina
    // North Carolina
    , new double[]{ 0.8, 45, 44, 7.3 }// North Dakota
    // North Dakota
    // North Dakota
    , new double[]{ 7.3, 120, 75, 21.4 }// Ohio
    // Ohio
    // Ohio
    , new double[]{ 6.6, 151, 68, 20.0 }// Oklahoma
    // Oklahoma
    // Oklahoma
    , new double[]{ 4.9, 159, 67, 29.3 }// Oregon
    // Oregon
    // Oregon
    , new double[]{ 6.3, 106, 72, 14.9 }// Pennsylvania
    // Pennsylvania
    // Pennsylvania
    , new double[]{ 3.4, 174, 87, 8.3 }// Rhode Island
    // Rhode Island
    // Rhode Island
    , new double[]{ 14.4, 279, 48, 22.5 }// South Carolina
    // South Carolina
    // South Carolina
    , new double[]{ 3.8, 86, 45, 12.8 }// South Dakota
    // South Dakota
    // South Dakota
    , new double[]{ 13.2, 188, 59, 26.9 }// Tennessee
    // Tennessee
    // Tennessee
    , new double[]{ 12.7, 201, 80, 25.5 }// Texas
    // Texas
    // Texas
    , new double[]{ 3.2, 120, 80, 22.9 }// Utah
    // Utah
    // Utah
    , new double[]{ 2.2, 48, 32, 11.2 }// Vermont
    // Vermont
    // Vermont
    , new double[]{ 8.5, 156, 63, 20.7 }// Virginia
    // Virginia
    // Virginia
    , new double[]{ 4.0, 145, 73, 26.2 }// Washington
    // Washington
    // Washington
    , new double[]{ 5.7, 81, 39, 9.3 }// West Virginia
    // West Virginia
    // West Virginia
    , new double[]{ 2.6, 53, 66, 10.8 }// Wisconsin
    // Wisconsin
    // Wisconsin
    , new double[]{ 6.8, 161, 60, 15.6 }// Wyoming
    // Wyoming
    // Wyoming
     };

    double[][] loadings = new double[][]{ new double[]{ -0.0417043206282872, -0.0448216562696701, -0.0798906594208108, -0.994921731246978 }, new double[]{ -0.995221281426497, -0.058760027857223, 0.0675697350838043, 0.0389382976351601 }, new double[]{ -0.0463357461197108, 0.97685747990989, 0.200546287353866, -0.0581691430589319 }, new double[]{ -0.075155500585547, 0.200718066450337, -0.974080592182491, 0.0723250196376097 } };

    double[] eigenvalues = new double[]{ 7011.114851, 201.992366, 42.112651, 6.164246 };

    public GHATest() {
    }

    /**
     * Test of learn method, of class GHA.
     */
    @Test
    public void testLearn() {
        System.out.println("learn");
        int k = 3;
        double[] mu = colMeans(USArrests);
        DenseMatrix cov = Matrix.newInstance(cov(USArrests));
        for (int i = 0; i < (USArrests.length); i++) {
            minus(USArrests[i], mu);
        }
        double r = 1.0E-5;
        GHA gha = new GHA(4, k, r);
        for (int iter = 1, t = 0; iter <= 1000; iter++) {
            double error = 0.0;
            for (int i = 0; i < (USArrests.length); i++ , t++) {
                error += gha.learn(USArrests[i]);
            }
            error /= USArrests.length;
            if ((iter % 100) == 0) {
                System.out.format("Iter %3d, Error = %.5g%n", iter, error);
            }
        }
        DenseMatrix p = gha.getProjection();
        DenseMatrix t = p.ata();
        for (int i = 0; i < (t.nrows()); i++) {
            for (int j = 0; j < (t.ncols()); j++) {
                System.out.format("% .4f ", t.get(i, j));
            }
            System.out.println();
        }
        DenseMatrix s = p.abmm(cov).abtmm(p);
        double[] ev = new double[k];
        System.out.println("Relative error of eigenvalues:");
        for (int i = 0; i < k; i++) {
            ev[i] = (Math.abs(((eigenvalues[i]) - (s.get(i, i))))) / (eigenvalues[i]);
            System.out.format("%.4f ", ev[i]);
        }
        System.out.println();
        for (int i = 0; i < k; i++) {
            Assert.assertTrue(((ev[i]) < 0.1));
        }
        double[][] lt = transpose(loadings);
        double[] evdot = new double[k];
        double[][] pa = p.array();
        System.out.println("Dot products of learned eigenvectors to true eigenvectors:");
        for (int i = 0; i < k; i++) {
            evdot[i] = dot(lt[i], pa[i]);
            System.out.format("%.4f ", evdot[i]);
        }
        System.out.println();
        for (int i = 0; i < k; i++) {
            Assert.assertTrue(((Math.abs((1.0 - (Math.abs(evdot[i]))))) < 0.1));
        }
    }
}

