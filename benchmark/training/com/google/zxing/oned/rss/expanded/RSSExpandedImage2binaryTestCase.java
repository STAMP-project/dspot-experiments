/**
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * These authors would like to acknowledge the Spanish Ministry of Industry,
 * Tourism and Trade, for the support in the project TSI020301-2008-2
 * "PIRAmIDE: Personalizable Interactions with Resources on AmI-enabled
 * Mobile Dynamic Environments", led by Treelogic
 * ( http://www.treelogic.com/ ):
 *
 *   http://www.piramidepse.com/
 */
package com.google.zxing.oned.rss.expanded;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pablo Ordu?a, University of Deusto (pablo.orduna@deusto.es)
 * @author Eduardo Castillejo, University of Deusto (eduardo.castillejo@deusto.es)
 */
public final class RSSExpandedImage2binaryTestCase extends Assert {
    @Test
    public void testDecodeRow2binary1() throws Exception {
        // (11)100224(17)110224(3102)000100
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("1.png", " ...X...X .X....X. .XX...X. X..X...X ...XX.X. ..X.X... ..X.X..X ...X..X. X.X....X .X....X. .....X.. X...X...");
    }

    @Test
    public void testDecodeRow2binary2() throws Exception {
        // (01)90012345678908(3103)001750
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("2.png", " ..X..... ......X. .XXX.X.X .X...XX. XXXXX.XX XX.X.... .XX.XX.X .XX.");
    }

    @Test
    public void testDecodeRow2binary3() throws Exception {
        // (10)12A
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("3.png", " .......X ..XX..X. X.X....X .......X ....");
    }

    @Test
    public void testDecodeRow2binary4() throws Exception {
        // (01)98898765432106(3202)012345(15)991231
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("4.png", " ..XXXX.X XX.XXXX. .XXX.XX. XX..X... .XXXXX.. XX.X..X. ..XX..XX XX.X.XXX X..XX..X .X.XXXXX XXXX");
    }

    @Test
    public void testDecodeRow2binary5() throws Exception {
        // (01)90614141000015(3202)000150
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("5.png", " ..X.X... .XXXX.X. XX..XXXX ....XX.. X....... ....X... ....X..X .XX.");
    }

    @Test
    public void testDecodeRow2binary10() throws Exception {
        // (01)98898765432106(15)991231(3103)001750(10)12A(422)123(21)123456(423)0123456789012
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("10.png", " .X.XX..X XX.XXXX. .XXX.XX. XX..X... .XXXXX.. XX.X..X. ..XX...X XX.X.... X.X.X.X. X.X..X.X .X....X. XX...X.. ...XX.X. .XXXXXX. .X..XX.. X.X.X... .X...... XXXX.... XX.XX... XXXXX.X. ...XXXXX .....X.X ...X.... X.XXX..X X.X.X... XX.XX..X .X..X..X .X.X.X.X X.XX...X .XX.XXX. XXX.X.XX ..X.");
    }

    @Test
    public void testDecodeRow2binary11() throws Exception {
        // (01)98898765432106(15)991231(3103)001750(10)12A(422)123(21)123456
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("11.png", " .X.XX..X XX.XXXX. .XXX.XX. XX..X... .XXXXX.. XX.X..X. ..XX...X XX.X.... X.X.X.X. X.X..X.X .X....X. XX...X.. ...XX.X. .XXXXXX. .X..XX.. X.X.X... .X...... XXXX.... XX.XX... XXXXX.X. ...XXXXX .....X.X ...X.... X.XXX..X X.X.X... ....");
    }

    @Test
    public void testDecodeRow2binary12() throws Exception {
        // (01)98898765432106(3103)001750
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("12.png", " ..X..XX. XXXX..XX X.XX.XX. .X....XX XXX..XX. X..X.... .XX.XX.X .XX.");
    }

    @Test
    public void testDecodeRow2binary13() throws Exception {
        // (01)90012345678908(3922)795
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("13.png", " ..XX..X. ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. X.X.XXXX .X..X..X ......X.");
    }

    @Test
    public void testDecodeRow2binary14() throws Exception {
        // (01)90012345678908(3932)0401234
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("14.png", " ..XX.X.. ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. X.....X. X.....X. X.X.X.XX .X...... X...");
    }

    @Test
    public void testDecodeRow2binary15() throws Exception {
        // (01)90012345678908(3102)001750(11)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("15.png", " ..XXX... ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }

    @Test
    public void testDecodeRow2binary16() throws Exception {
        // (01)90012345678908(3202)001750(11)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("16.png", " ..XXX..X ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }

    @Test
    public void testDecodeRow2binary17() throws Exception {
        // (01)90012345678908(3102)001750(13)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("17.png", " ..XXX.X. ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }

    @Test
    public void testDecodeRow2binary18() throws Exception {
        // (01)90012345678908(3202)001750(13)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("18.png", " ..XXX.XX ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }

    @Test
    public void testDecodeRow2binary19() throws Exception {
        // (01)90012345678908(3102)001750(15)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("19.png", " ..XXXX.. ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }

    @Test
    public void testDecodeRow2binary20() throws Exception {
        // (01)90012345678908(3202)001750(15)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("20.png", " ..XXXX.X ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }

    @Test
    public void testDecodeRow2binary21() throws Exception {
        // (01)90012345678908(3102)001750(17)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("21.png", " ..XXXXX. ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }

    @Test
    public void testDecodeRow2binary22() throws Exception {
        // (01)90012345678908(3202)001750(17)100312
        RSSExpandedImage2binaryTestCase.assertCorrectImage2binary("22.png", " ..XXXXXX ........ .X..XXX. X.X.X... XX.XXXXX .XXXX.X. ..XX...X .X.....X .XX..... XXXX.X.. XX..");
    }
}

