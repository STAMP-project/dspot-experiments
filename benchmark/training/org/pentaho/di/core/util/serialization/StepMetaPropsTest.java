/**
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * **************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pentaho.di.core.util.serialization;


import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionDeep;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.streaming.common.BaseStreamStepMeta;

import static java.util.Objects.hash;


public class StepMetaPropsTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testToAndFrom() {
        StepMetaPropsTest.FooMeta foo = StepMetaPropsTest.getTestFooMeta();
        StepMetaProps fromMeta = StepMetaProps.from(foo);
        StepMetaPropsTest.FooMeta toMeta = new StepMetaPropsTest.FooMeta();
        fromMeta.to(toMeta);
        Assert.assertThat(foo, CoreMatchers.equalTo(toMeta));
    }

    @Test
    public void testEncrypt() {
        StepMetaPropsTest.FooMeta foo = StepMetaPropsTest.getTestFooMeta();
        foo.password = "p@ssword";
        StepMetaProps stepMetaProps = StepMetaProps.from(foo);
        Assert.assertThat("password field should be encrypted, so should not be present in the .toString of the props", stepMetaProps.toString(), CoreMatchers.not(CoreMatchers.containsString("p@ssword")));
        StepMetaPropsTest.FooMeta toMeta = new StepMetaPropsTest.FooMeta();
        stepMetaProps.to(toMeta);
        Assert.assertThat(foo, CoreMatchers.equalTo(toMeta));
        Assert.assertThat("p@ssword", CoreMatchers.equalTo(toMeta.password));
    }

    @Test
    public void testEncryptedList() {
        StepMetaPropsTest.FooMeta foo = StepMetaPropsTest.getTestFooMeta();
        foo.securelist = Arrays.asList("shadow", "substance");
        StepMetaProps stepMetaProps = StepMetaProps.from(foo);
        Assert.assertThat("secureList should be encrypted, so raw values should not be present in the .toString of the props", stepMetaProps.toString(), CoreMatchers.not(CoreMatchers.containsString("expectedString")));
        Arrays.asList("shadow", "substance").forEach(( val) -> Assert.assertThat((val + " should be encrypted, so should not be present in the .toString of the props"), stepMetaProps.toString(), CoreMatchers.not(CoreMatchers.containsString(val))));
        StepMetaPropsTest.FooMeta toMeta = new StepMetaPropsTest.FooMeta();
        stepMetaProps.to(toMeta);
        Assert.assertThat(foo, CoreMatchers.equalTo(toMeta));
        Assert.assertThat(Arrays.asList("shadow", "substance"), CoreMatchers.equalTo(toMeta.securelist));
    }

    @Test
    public void testInjectionDeep() {
        StepMetaPropsTest.FooMeta fooMeta = StepMetaPropsTest.getTestFooMeta();
        fooMeta.deep.howDeep = 50;
        fooMeta.deep.isItDeep = false;
        StepMetaPropsTest.FooMeta toMeta = new StepMetaPropsTest.FooMeta();
        StepMetaProps.from(fooMeta).to(toMeta);
        Assert.assertThat(50, CoreMatchers.equalTo(toMeta.deep.howDeep));
        Assert.assertThat(false, CoreMatchers.equalTo(toMeta.deep.isItDeep));
    }

    @Test
    public void sensitiveFieldsCheckedAtMultipleLevels() {
        // verifies that fields below the top level can be correctly identified as Sensitive.
        class DeeperContainer {
            @Sensitive
            @Injection(name = "Sensitive")
            String sensitive = "very sensitive";

            @Injection(name = "NotSensitive")
            String notSensitive = "cold and unfeeling";
        }
        class DeepContainer {
            @InjectionDeep
            DeeperContainer deeperObj = new DeeperContainer();
        }
        Object topLevelObject = new Object() {
            @InjectionDeep
            DeepContainer deepObj = new DeepContainer();
        };
        List<String> sensitiveFields = StepMetaProps.sensitiveFields(topLevelObject.getClass());
        Assert.assertThat(sensitiveFields, CoreMatchers.equalTo(Collections.singletonList("Sensitive")));
    }

    @Test
    public void variableSubstitutionHappens() {
        // Tests that the .withVariables method allows creation of a copy of
        // the step meta with all variables substituted, both in lists and field strings,
        // and in deep meta injection
        StepMetaPropsTest.FooMeta fooMeta = StepMetaPropsTest.getTestFooMeta();
        fooMeta.field1 = "${field1Sub}";
        fooMeta.alist = Arrays.asList("noSub", "${listEntrySub}", "${listEntrySub2}", "noSubAgain");
        fooMeta.password = "${encryptedSub}";
        fooMeta.deep.deepList = Arrays.asList("deepNotSubbed", "${deepListSub}");
        StepMetaPropsTest.FooMeta newFoo = new StepMetaPropsTest.FooMeta();
        VariableSpace variables = new Variables();
        variables.setVariable("field1Sub", "my substituted value");
        variables.setVariable("listEntrySub", "list sub");
        variables.setVariable("listEntrySub2", "list sub 2");
        variables.setVariable("encryptedSub", "encrypted sub");
        variables.setVariable("deepListSub", "deep list sub");
        StepMetaProps.from(fooMeta).withVariables(variables).to(newFoo);
        Assert.assertThat("my substituted value", CoreMatchers.equalTo(newFoo.field1));
        Assert.assertThat("list sub", CoreMatchers.equalTo(newFoo.alist.get(1)));
        Assert.assertThat("list sub 2", CoreMatchers.equalTo(newFoo.alist.get(2)));
        Assert.assertThat("encrypted sub", CoreMatchers.equalTo(newFoo.password));
        Assert.assertThat("deep list sub", CoreMatchers.equalTo(newFoo.deep.deepList.get(1)));
        Assert.assertThat("noSub", CoreMatchers.equalTo(newFoo.alist.get(0)));
    }

    @InjectionSupported(localizationPrefix = "stuff", groups = { "stuffGroup" })
    static class FooMeta extends BaseStreamStepMeta {
        @Sensitive
        @Injection(name = "FIELD1", group = "stuffGroup")
        String field1 = "default";

        @Injection(name = "FIELD2", group = "stuffGroup")
        int field2 = 123;

        @Sensitive
        @Injection(name = "PassVerd")
        String password = "should.be.encrypted";

        @Injection(name = "ALIST")
        List<String> alist = new ArrayList<>();

        @Sensitive
        @Injection(name = "SECURELIST")
        List<String> securelist = new ArrayList<>();

        @Injection(name = "BOOLEANLIST")
        List<Boolean> blist = new ArrayList<>();

        @Injection(name = "IntList")
        List<Integer> ilist = new ArrayList<>();

        @InjectionDeep
        StepMetaPropsTest.FooMeta.SoooDeep deep = new StepMetaPropsTest.FooMeta.SoooDeep();

        static class SoooDeep {
            @Injection(name = "DEEP_FLAG")
            boolean isItDeep = true;

            @Injection(name = "DEPTH")
            int howDeep = 1000;

            @Injection(name = "DEEP_LIST ")
            List<String> deepList = new ArrayList<>();

            @Sensitive
            @Injection(name = "DEEP_PASSWORD")
            String password = "p@ssword";

            @Override
            public boolean equals(Object o) {
                if ((this) == o) {
                    return true;
                }
                if ((o == null) || ((getClass()) != (o.getClass()))) {
                    return false;
                }
                StepMetaPropsTest.FooMeta.SoooDeep soooDeep = ((StepMetaPropsTest.FooMeta.SoooDeep) (o));
                return ((isItDeep) == (soooDeep.isItDeep)) && ((howDeep) == (soooDeep.howDeep));
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(isItDeep, howDeep);
            }
        }

        // list and array of deep injection
        // these varieties are trickier than lists of @Injection, since each list item
        // is a composite of meta properties.
        @InjectionDeep
        List<StepMetaPropsTest.FooMeta.DeepListable> deepListables = new ArrayList<>();

        @InjectionDeep
        StepMetaPropsTest.FooMeta.DeepArrayable[] deepArrayable = new StepMetaPropsTest.FooMeta.DeepArrayable[2];

        {
            this.deepListables.add(new StepMetaPropsTest.FooMeta.DeepListable("foo", "bar"));
            this.deepListables.add(new StepMetaPropsTest.FooMeta.DeepListable("foo2", "bar2"));
            deepArrayable[0] = new StepMetaPropsTest.FooMeta.DeepArrayable("afoo", "abar");
            deepArrayable[1] = new StepMetaPropsTest.FooMeta.DeepArrayable("afoo2", "abar2");
        }

        static class DeepListable {
            DeepListable(String item, String item2) {
                this.item = item;
                this.item2 = item2;
            }

            @Override
            public boolean equals(Object o) {
                if ((this) == o) {
                    return true;
                }
                if ((o == null) || ((getClass()) != (o.getClass()))) {
                    return false;
                }
                StepMetaPropsTest.FooMeta.DeepListable that = ((StepMetaPropsTest.FooMeta.DeepListable) (o));
                return (java.util.Objects.equals(item, that.item)) && (java.util.Objects.equals(item2, that.item2));
            }

            @Override
            public int hashCode() {
                return hash(item, item2);
            }

            @Injection(name = "ITEM")
            String item;

            @Injection(name = "ITEM2")
            String item2;
        }

        static class DeepArrayable {
            DeepArrayable(String item, String item2) {
                this.item = item;
                this.item2 = item2;
            }

            @Override
            public boolean equals(Object o) {
                if ((this) == o) {
                    return true;
                }
                if ((o == null) || ((getClass()) != (o.getClass()))) {
                    return false;
                }
                StepMetaPropsTest.FooMeta.DeepArrayable that = ((StepMetaPropsTest.FooMeta.DeepArrayable) (o));
                return (java.util.Objects.equals(item, that.item)) && (java.util.Objects.equals(item2, that.item2));
            }

            @Override
            public int hashCode() {
                return hash(item, item2);
            }

            @Injection(name = "AITEM")
            String item;

            @Injection(name = "AITEM2")
            String item2;
        }

        @Override
        public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            return null;
        }

        @Override
        public StepDataInterface getStepData() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            StepMetaPropsTest.FooMeta fooMeta = ((StepMetaPropsTest.FooMeta) (o));
            return ((((((((((field2) == (fooMeta.field2)) && (java.util.Objects.equals(field1, fooMeta.field1))) && (java.util.Objects.equals(password, fooMeta.password))) && (java.util.Objects.equals(alist, fooMeta.alist))) && (java.util.Objects.equals(securelist, fooMeta.securelist))) && (java.util.Objects.equals(blist, fooMeta.blist))) && (java.util.Objects.equals(ilist, fooMeta.ilist))) && (java.util.Objects.equals(deep, fooMeta.deep))) && (java.util.Objects.equals(deepListables, fooMeta.deepListables))) && (Arrays.equals(deepArrayable, fooMeta.deepArrayable));
        }

        @Override
        public int hashCode() {
            int result = hash(field1, field2, password, alist, securelist, blist, ilist, deep, deepListables);
            result = (31 * result) + (Arrays.hashCode(deepArrayable));
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FooMeta{");
            sb.append("field1='").append(field1).append('\'');
            sb.append(", \nfield2=").append(field2);
            sb.append(", \npassword=\'").append(password).append('\'');
            sb.append(", \nalist=").append(alist);
            sb.append(", \nsecurelist=").append(securelist);
            sb.append(", \nblist=").append(blist);
            sb.append(", \nilist=").append(ilist);
            sb.append(", \ndeep=").append(deep);
            sb.append(", \ndeepListables=").append(deepListables);
            sb.append(", \ndeepArrayable=").append(Arrays.toString(deepArrayable));
            sb.append('}');
            return sb.toString();
        }

        @Override
        public RowMeta getRowMeta(String origin, VariableSpace space) {
            return null;
        }
    }
}

