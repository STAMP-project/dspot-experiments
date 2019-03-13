/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.junit.Test;


public abstract class AbstractBackwardChainingTest {
    protected final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AbstractBackwardChainingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test(timeout = 10000)
    public void testQueryPositional() {
        String drl = getQueryHeader();
        drl += "rule x1\n" + ((((("when\n" + "    String( this == \"go1\" )\n") + // output        ,output          ,output
        "    ?peeps($name1 : $name, $likes1 : $likes, $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x2\n" + ((((("when\n" + "    String( this == \"go2\" )\n") + // output, input      ,output
        "    ?peeps($name1, \"stilton\", $age1; )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x3\n" + (((((("when\n" + "    String( this == \"go3\" )\n") + "    $name1 : String() from \"darth\";\n ") + // input , input      ,output
        "    ?peeps($name1, \"stilton\", $age1; )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x4\n" + ((((((("when\n" + "    String( this == \"go4\" )\n") + "    $name1 : String() from \"darth\"\n ") + "    $age1 : Integer() from 200;\n ") + // input , input      ,input
        "    ?peeps($name1, \"stilton\", $age1; )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        testQuery(drl);
    }

    @Test(timeout = 10000)
    public void testQueryNamed() {
        String drl = getQueryHeader();
        drl += "rule x1\n" + ((((("when\n" + "    String( this == \"go1\" )\n") + // output        ,output          ,output
        "    ?peeps($name1 : $name, $likes1 : $likes, $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x2\n" + ((((("when\n" + "    String( this == \"go2\" )\n") + // output        ,output                ,output
        "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x3\n" + (((((("when\n" + "    String( this == \"go3\" )\n") + "    $name1 : String() from \"darth\";\n ") + // input         ,input                ,output
        "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x4\n" + ((((((("when\n" + "    String( this == \"go4\" )\n") + "    $name1 : String() from \"darth\";\n ") + "    $age1 : Integer() from 200;\n ") + // input         ,input                ,input
        "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        testQuery(drl);
    }

    @Test(timeout = 10000)
    public void testQueryMixed() {
        String drl = getQueryHeader();
        drl += "rule x1\n" + ((((("when\n" + "    String( this == \"go1\" )\n") + // output        ,output          ,output
        "    ?peeps($name1; $likes1 : $likes, $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x2\n" + ((((("when\n" + "    String( this == \"go2\" )\n") + // output        ,output                ,output
        "    ?peeps($name1, \"stilton\"; $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x3\n" + (((((("when\n" + "    String( this == \"go3\" )\n") + "    $name1 : String() from \"darth\";\n ") + // input         ,input                ,output
        "    ?peeps($name1, \"stilton\"; $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        drl += "rule x4\n" + ((((((("when\n" + "    String( this == \"go4\" )\n") + "    $name1 : String() from \"darth\"\n ") + "    $age1 : Integer() from 200;\n ") + // input         ,input                ,input
        "    ?peeps($name1; $likes : \"stilton\", $age1 : $age )\n") + "then\n") + "   list.add( $name1 + \" : \" + $age1 );\n") + "end \n");
        testQuery(drl);
    }
}

