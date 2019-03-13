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


import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pablo Ordu?a, University of Deusto (pablo.orduna@deusto.es)
 */
public final class BinaryUtilTest extends Assert {
    private static final Pattern SPACE = Pattern.compile(" ");

    @Test
    public void testBuildBitArrayFromString() {
        CharSequence data = " ..X..X.. ..XXX... XXXXXXXX ........";
        BinaryUtilTest.check(data);
        data = " XXX..X..";
        BinaryUtilTest.check(data);
        data = " XX";
        BinaryUtilTest.check(data);
        data = " ....XX.. ..XX";
        BinaryUtilTest.check(data);
        data = " ....XX.. ..XX..XX ....X.X. ........";
        BinaryUtilTest.check(data);
    }

    @Test
    public void testBuildBitArrayFromStringWithoutSpaces() {
        CharSequence data = " ..X..X.. ..XXX... XXXXXXXX ........";
        BinaryUtilTest.checkWithoutSpaces(data);
        data = " XXX..X..";
        BinaryUtilTest.checkWithoutSpaces(data);
        data = " XX";
        BinaryUtilTest.checkWithoutSpaces(data);
        data = " ....XX.. ..XX";
        BinaryUtilTest.checkWithoutSpaces(data);
        data = " ....XX.. ..XX..XX ....X.X. ........";
        BinaryUtilTest.checkWithoutSpaces(data);
    }
}

