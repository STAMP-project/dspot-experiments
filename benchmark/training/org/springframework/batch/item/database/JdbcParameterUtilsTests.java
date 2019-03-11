/**
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.batch.item.database;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Risberg
 */
public class JdbcParameterUtilsTests {
    @Test
    public void testCountParameterPlaceholders() {
        Assert.assertEquals(0, JdbcParameterUtils.countParameterPlaceholders(null, null));
        Assert.assertEquals(0, JdbcParameterUtils.countParameterPlaceholders("", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("?", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The \"big\" ? \'bad wolf\'", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big ?? bad wolf", null));
        Assert.assertEquals(3, JdbcParameterUtils.countParameterPlaceholders("The big ? ? bad ? wolf", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The \"big?\" \'ba\'\'ad?\' ? wolf", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders(":parameter", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The \"big\" :parameter \'bad wolf\'", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The big :parameter :parameter bad wolf", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big :parameter :newpar :parameter bad wolf", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big :parameter, :newpar, :parameter bad wolf", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The \"big:\" \'ba\'\'ad:p\' :parameter wolf", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("&parameter", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The \"big\" &parameter \'bad wolf\'", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The big &parameter &parameter bad wolf", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big &parameter &newparameter &parameter bad wolf", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big &parameter, &newparameter, &parameter bad wolf", null));
        Assert.assertEquals(1, JdbcParameterUtils.countParameterPlaceholders("The \"big &x  \" \'ba\'\'ad&p\' &parameter wolf", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big :parameter, &newparameter, &parameter bad wolf", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big :parameter, &sameparameter, &sameparameter bad wolf", null));
        Assert.assertEquals(2, JdbcParameterUtils.countParameterPlaceholders("The big :parameter, :sameparameter, :sameparameter bad wolf", null));
        Assert.assertEquals(0, JdbcParameterUtils.countParameterPlaceholders("xxx & yyy", null));
        List<String> l = new ArrayList<>();
        Assert.assertEquals(3, JdbcParameterUtils.countParameterPlaceholders("select :par1, :par2 :par3", l));
        Assert.assertEquals(3, l.size());
    }
}

