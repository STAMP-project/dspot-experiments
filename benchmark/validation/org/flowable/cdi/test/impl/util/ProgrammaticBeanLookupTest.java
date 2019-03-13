/**
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
package org.flowable.cdi.test.impl.util;


import javax.enterprise.inject.Alternative;
import javax.inject.Named;
import org.flowable.cdi.impl.util.ProgrammaticBeanLookup;
import org.flowable.cdi.test.impl.beans.SpecializedTestBean;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Ronny Br?unlich
 */
@RunWith(Arquillian.class)
public class ProgrammaticBeanLookupTest {
    /**
     * Because of all alternatives and specializations I have to handle deployment myself
     */
    @ArquillianResource
    private Deployer deployer;

    @Test
    public void testLookupBean() {
        deployer.deploy("normal");
        Object lookup = ProgrammaticBeanLookup.lookup("testOnly");
        Assert.assertTrue(lookup.getClass().isAssignableFrom(ProgrammaticBeanLookupTest.TestBean.class));
        deployer.undeploy("normal");
    }

    @Test
    public void testLookupShouldFindAlternative() {
        deployer.deploy("withAlternative");
        Object lookup = ProgrammaticBeanLookup.lookup("testOnly");
        Assert.assertThat(lookup.getClass().getName(), Is.is(IsEqual.equalTo(ProgrammaticBeanLookupTest.AlternativeTestBean.class.getName())));
        deployer.undeploy("withAlternative");
    }

    @Test
    public void testLookupShouldFindSpecialization() {
        deployer.deploy("withSpecialization");
        Object lookup = ProgrammaticBeanLookup.lookup("testOnly");
        Assert.assertThat(lookup.getClass().getName(), Is.is(IsEqual.equalTo(SpecializedTestBean.class.getName())));
        deployer.undeploy("withSpecialization");
    }

    @Named("testOnly")
    public static class TestBean {}

    @Alternative
    @Named("testOnly")
    public static class AlternativeTestBean extends ProgrammaticBeanLookupTest.TestBean {}
}

