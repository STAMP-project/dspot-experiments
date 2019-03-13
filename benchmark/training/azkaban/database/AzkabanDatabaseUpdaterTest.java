/**
 * Copyright 2014 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.database;


import com.google.common.io.Resources;
import java.io.File;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;


public class AzkabanDatabaseUpdaterTest {
    @Test
    public void testH2AutoCreate() throws Exception {
        final URL resourceUrl = Resources.getResource("conf/dbtesth2");
        Assert.assertNotNull(resourceUrl);
        final File resource = new File(resourceUrl.toURI());
        final String confDir = resource.getParent();
        System.out.println("1.***Now testing check");
        AzkabanDatabaseUpdater.main(new String[]{ "-c", confDir });
        System.out.println("2.***Now testing update");
        AzkabanDatabaseUpdater.main(new String[]{ "-u", "-c", confDir });
        System.out.println("3.***Now testing check again");
        AzkabanDatabaseUpdater.main(new String[]{ "-c", confDir });
        System.out.println("4.***Now testing update again");
        AzkabanDatabaseUpdater.main(new String[]{ "-c", confDir, "-u" });
        System.out.println("5.***Now testing check again");
        AzkabanDatabaseUpdater.main(new String[]{ "-c", confDir });
    }
}

