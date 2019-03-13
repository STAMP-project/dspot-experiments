/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.conf;


import Source.CLUSTER_DEFAULT;
import Source.DEFAULT;
import Source.RUNTIME;
import Source.Type.SITE_PROPERTY;
import Source.Type.SYSTEM_PROPERTY;
import Source.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests enum Source.
 */
public class SourceTest {
    @Test
    public void compareTo() {
        Assert.assertEquals((-1), UNKNOWN.compareTo(DEFAULT));
        Assert.assertEquals((-1), DEFAULT.compareTo(CLUSTER_DEFAULT));
        Assert.assertEquals((-1), CLUSTER_DEFAULT.compareTo(Source.siteProperty("")));
        Assert.assertEquals((-1), SITE_PROPERTY.compareTo(SYSTEM_PROPERTY));
        Assert.assertEquals((-1), Source.SYSTEM_PROPERTY.compareTo(RUNTIME));
    }
}

