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
 *
 * This software consists of contributions made by many individuals,
 * listed below:
 *
 * @author Pablo Ordu?a, University of Deusto (pablo.orduna@deusto.es)
 * @author Eduardo Castillejo, University of Deusto (eduardo.castillejo@deusto.es)
 *
 * These authors would like to acknowledge the Spanish Ministry of Industry,
 * Tourism and Trade, for the support in the project TSI020301-2008-2
 * "PIRAmIDE: Personalizable Interactions with Resources on AmI-enabled
 * Mobile Dynamic Environments", leaded by Treelogic
 * ( http://www.treelogic.com/ ):
 *
 *   http://www.piramidepse.com/
 */
package com.google.zxing.oned.rss.expanded;


import com.google.zxing.client.result.ExpandedProductParsedResult;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pablo Ordu?a, University of Deusto (pablo.orduna@deusto.es)
 * @author Eduardo Castillejo, University of Deusto (eduardo.castillejo@deusto.es)
 */
public final class RSSExpandedImage2resultTestCase extends Assert {
    @Test
    public void testDecodeRow2result2() throws Exception {
        // (01)90012345678908(3103)001750
        ExpandedProductParsedResult expected = new ExpandedProductParsedResult("(01)90012345678908(3103)001750", "90012345678908", null, null, null, null, null, null, "001750", ExpandedProductParsedResult.KILOGRAM, "3", null, null, null, new HashMap<String, String>());
        RSSExpandedImage2resultTestCase.assertCorrectImage2result("2.png", expected);
    }
}

