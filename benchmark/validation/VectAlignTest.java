

import PathParser.PathDataNode;
import VectAlign.Mode.BASE;
import VectAlign.Mode.LINEAR;
import VectAlign.Mode.SUB_BASE;
import VectAlign.Mode.SUB_LINEAR;
import android.support.v7.graphics.drawable.PathParser;
import com.bonnyfone.vectalign.PathNodeUtils;
import com.bonnyfone.vectalign.VectAlign;
import com.bonnyfone.vectalign.techniques.BaseFillMode;
import com.bonnyfone.vectalign.techniques.NWAlignment;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by ziby on 11/08/15.
 */
public class VectAlignTest {
    String star = "M 48,54 L 31,42 15,54 21,35 6,23 25,23 32,4 40,23 58,23 42,35 z";

    String pentagon = "M 48,54 L 31,54 15,54 10,35 6,23 25,10 32,4 40,10 58,23 54,35 z";

    String square = "M 10,10 L 10,10 10,10 50,10 50,10 50,10 50,50 50,50 50,50 10,50 10,50 10,50 z";

    String starEquivalent = "M 48,54 M 48,54 M 48,54 L 31,42 L 31,42 L 31,42 15,54 21,35 6,23 25,23 32,4 40,23 40,23 40,23 58,23 42,35 z z";

    String complex = "M32.8897783,115.66321 C26.50733,119.454449 21.3333333,116.509494 21.3333333,109.083329 L21.3333333,20.2501707 C21.3333333,12.8249638 26.5016153,9.87565618 32.8897783,13.6702897 L107.184497,57.8021044 C113.566945,61.5933434 113.57266,67.7367615 107.184497,71.5313951 L32.8897783,115.66321 z";

    String megaStar = "M 18.363636,11.818181 l 6.780273,10.042729 5.875151,-10.597692 0.27442,12.114173 10.672036,-5.738999 -6.31856,10.339453 12.080626,0.941786 -10.905441,5.28203 9.653701,7.323559 L 34.44592,40.07282 38.607715,51.452976 29.272727,43.727272 26.621276,55.550904 22.945015,44.004753 14.322134,52.517942 17.471787,40.817166 5.6151801,43.317035 14.590755,35.176546 3.2648108,30.869404 15.216625,28.873751 8.0172516,19.127085 19.150689,23.909874 z";

    String pentagonAlter = "M 48,54 L 31,54 15,54 10,35 6,23 C 25,10 25,10 25,10 L 32,4 40,10 58,23 54,35 z";

    final String supportSample = "m20,200l100,90l180-180l-35-35l-145,145l-60-60l-40,40z V 10 h 11 l 2,2 v 5";

    String arrow = "M 12, 4 L 10.59,5.41 L 16.17,11 L 18.99,11 L 12,4 z M 4, 11 L 4, 13 L 18.99, 13 L 20, 12 L 18.99, 11 L 4, 11 z M 12,20 L 10.59, 18.59 L 16.17, 13 L 18.99, 13 L 12, 20z";

    String rombo = "M -6.5454547,11.454545 L 16.23507,17.929393 38.909091,11.090909 32.434243,33.871434 39.272727,56.545455 16.492202,50.070607 -6.1818193,56.909091 0.29302906,34.128566 z";

    PathDataNode[] pentagonData = PathParser.createNodesFromPathData(pentagon);

    PathDataNode[] pentagonAlterData = PathParser.createNodesFromPathData(pentagonAlter);

    PathDataNode[] supportData = PathParser.createNodesFromPathData(supportSample);

    PathDataNode[] starData = PathParser.createNodesFromPathData(star);

    PathDataNode[] arrowData = PathParser.createNodesFromPathData(arrow);

    PathDataNode[] complexData = PathParser.createNodesFromPathData(complex);

    PathDataNode[] megaStarData = PathParser.createNodesFromPathData(megaStar);

    PathDataNode[] romboData = PathParser.createNodesFromPathData(rombo);

    PathDataNode[] squareData = PathParser.createNodesFromPathData(square);

    @Test
    public void testCanMorph() throws Exception {
        System.out.println("Testing canMorph()...");
        boolean shouldMorph = VectAlign.canMorph(star, pentagon);
        Assert.assertTrue(shouldMorph);
        boolean shouldNotMorph = VectAlign.canMorph(square, pentagon);
        Assert.assertFalse(shouldNotMorph);
    }

    @Test
    public void testIsEquivalent() throws Exception {
        System.out.println("Testing isEquivalent()...");
        Assert.assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(star)), PathNodeUtils.transform(PathParser.createNodesFromPathData(starEquivalent))));
        Assert.assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(star)), PathNodeUtils.transform(PathParser.createNodesFromPathData(star), 2, true)));
        Assert.assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar)), PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar), 2, true)));
        Assert.assertTrue(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar)), PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar), 5, true)));
        Assert.assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(megaStar)), PathNodeUtils.transform(PathParser.createNodesFromPathData(star))));
        Assert.assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(pentagonAlter)), PathNodeUtils.transform(PathParser.createNodesFromPathData(star))));
        Assert.assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(complex)), PathNodeUtils.transform(PathParser.createNodesFromPathData(supportSample))));
        Assert.assertFalse(PathNodeUtils.isEquivalent(PathNodeUtils.transform(PathParser.createNodesFromPathData(arrow)), PathNodeUtils.transform(PathParser.createNodesFromPathData(square))));
    }

    @Test
    public void testTransformations() throws Exception {
        System.out.println("Testing transformations...");
        PathParser[] nodesFromPathData = PathParser.createNodesFromPathData(complex);
        ArrayList<PathParser.PathDataNode> transform = PathNodeUtils.transform(nodesFromPathData);
        String s = PathNodeUtils.pathNodesToString(transform);
        PathParser[] nodesFromPathData2 = PathParser.createNodesFromPathData(s);
        ArrayList<PathParser.PathDataNode> transform2 = PathNodeUtils.transform(nodesFromPathData2);
        Assert.assertTrue((((transform != null) && (transform2 != null)) && ((transform.size()) == (transform2.size()))));
        for (int i = 0; i < (transform.size()); i++)
            Assert.assertTrue(transform.get(i).isEqual(transform2.get(i)));

    }

    @Test
    public void testCalculatePenPosition() throws Exception {
        System.out.println("Testing calculatePenPosition()...");
        float[][] penPos = PathNodeUtils.calculatePenPosition(PathNodeUtils.transform(supportData));
        Assert.assertTrue(((penPos[((penPos.length) - 1)][0]) == 33.0F));
        Assert.assertTrue(((penPos[((penPos.length) - 1)][1]) == 17.0F));
    }

    @Test
    public void testPenPositionInvariant() throws Exception {
        System.out.println("Testing final pen position...");
        float[][] penPos1 = PathNodeUtils.calculatePenPosition(PathNodeUtils.transform(supportData));
        float[][] penPos2 = PathNodeUtils.calculatePenPosition(PathNodeUtils.transform(starData));
        ArrayList<PathParser.PathDataNode> transform1 = PathNodeUtils.transform(supportData, 2, true);
        ArrayList<PathParser.PathDataNode> transform2 = PathNodeUtils.transform(starData, 2, true);
        NWAlignment nwexp = new NWAlignment(transform1, transform2);
        nwexp.align();
        BaseFillMode fillMode = new BaseFillMode();
        fillMode.fillInjectedNodes(nwexp.getAlignedFrom(), nwexp.getAlignedTo());
        float[][] penPos1b = PathNodeUtils.calculatePenPosition(nwexp.getAlignedFrom());
        float[][] penPos2b = PathNodeUtils.calculatePenPosition(nwexp.getAlignedTo());
        if (((((penPos1[((penPos1.length) - 1)][0]) == (penPos1b[((penPos1b.length) - 1)][0])) && ((penPos1[((penPos1.length) - 1)][1]) == (penPos1b[((penPos1b.length) - 1)][1]))) && ((penPos2[((penPos2.length) - 1)][0]) == (penPos2b[((penPos2b.length) - 1)][0]))) && ((penPos2[((penPos2.length) - 1)][1]) == (penPos2b[((penPos2b.length) - 1)][1]))) {
            Assert.assertTrue(true);
        } else {
            System.out.println("PROBLEM during injection!");
            System.out.println("PenPos from");
            StringBuffer sb = new StringBuffer();
            int i = 0;
            for (float[] coord : penPos1) {
                sb.append(((((((++i) + "p. ") + (coord[0])) + " , ") + (coord[1])) + "\n"));
            }
            System.out.println(sb.toString());
            System.out.println("PenPos fromAfter");
            sb = new StringBuffer();
            i = 0;
            for (float[] coord : penPos1b) {
                sb.append(((((((++i) + "p. ") + (coord[0])) + " , ") + (coord[1])) + "\n"));
            }
            System.out.println(sb.toString());
            System.out.println("PenPos to");
            sb = new StringBuffer();
            i = 0;
            for (float[] coord : penPos2) {
                sb.append(((((((++i) + "p. ") + (coord[0])) + " , ") + (coord[1])) + "\n"));
            }
            System.out.println(sb.toString());
            System.out.println("PenPos toAfter");
            sb = new StringBuffer();
            i = 0;
            for (float[] coord : penPos2b) {
                sb.append(((((((++i) + "p. ") + (coord[0])) + " , ") + (coord[1])) + "\n"));
            }
            System.out.println(sb.toString());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void testRandomBaseAligns() throws Exception {
        testRandomAligns(BASE);
    }

    @Test
    public void testRandomLinearInterpolateAligns() throws Exception {
        testRandomAligns(LINEAR);
    }

    @Test
    public void testRandomSubBaseAligns() throws Exception {
        testRandomAligns(SUB_BASE);
    }

    @Test
    public void testRandomSubLinearAligns() throws Exception {
        testRandomAligns(SUB_LINEAR);
    }
}

