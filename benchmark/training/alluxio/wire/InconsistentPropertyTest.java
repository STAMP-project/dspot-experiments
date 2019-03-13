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
package alluxio.wire;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link InconsistentProperty}.
 */
public class InconsistentPropertyTest {
    @Test
    public void proto() {
        InconsistentProperty inconsistentProperty = InconsistentPropertyTest.createInconsistentProperty();
        InconsistentProperty other = inconsistentProperty.fromProto(inconsistentProperty.toProto());
        checkEquality(inconsistentProperty, other);
    }

    @Test
    public void testToString() {
        InconsistentProperty inconsistentProperty = InconsistentPropertyTest.createInconsistentProperty();
        String result = inconsistentProperty.toString();
        Assert.assertFalse(result.contains("Optional"));
        String expected = "InconsistentProperty{key=my_key, values=" + ("no value set (workerHostname1:workerPort, workerHostname2:workerPort), " + "some_value (masterHostname1:port, masterHostname2:port)}");
        Assert.assertEquals(expected, result);
    }
}

