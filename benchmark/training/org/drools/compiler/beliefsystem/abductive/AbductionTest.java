/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.beliefsystem.abductive;


import EqualityKey.JUSTIFIED;
import KieServices.Factory;
import Message.Level.ERROR;
import ResourceType.DRL;
import Variable.v;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.QueryResultsImpl;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.abductive.Abducible;
import org.drools.core.beliefsystem.defeasible.Defeasible;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.runtime.rule.impl.FlatQueryResults;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.rule.Query;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.utils.KieHelper;


public class AbductionTest extends CommonTestMethodBase {
    @Test
    public void testAbductiveLogicWithConstructorArgs() {
        String droolsSource = ((((((((((((((((((((((((((((((((((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : Integer @key \n") + "   name : String @key \n") + "   value : double \n") + "   flag : boolean \n") + "end \n") + "query foo() \n") + "   @Abductive( target=Foo.class ) \n") + "end \n") + "query foo2( Integer $i, String $name ) \n") + "   @Abductive( target=Foo.class ) \n") + "   $i := Integer() from new Integer( 4 ) \n") + "   $name := String() ") + "end \n") + "query foo3( Integer $i, String $name, double $val, boolean $bool ) \n") + "   @Abductive( target=Foo.class ) \n") + "end \n") + "rule Init ") + "   salience 9999 ") + "when ") + "then ") + "   System.out.println( \'Foo zero is in\' ); \n") + "   insert( new Foo() ); \n") + "end ") + "rule R1 ") + "when ") + "   $fx : foo() ") + "then ") + "   list.add( 1 ); ") + "end \n") + "") + "rule R2 ") + "when ") + "   foo2( 4, $n ; ) ") + "then ") + "   list.add( 2 ); ") + "end \n") + "") + "rule R3 ") + "when ") + "   foo3( 42, \"test2\", $dbl, $bool ; ) ") + "then ") + "   list.add( 3 ); ") + "end \n") + "") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.insert("john");
        session.fireAllRules();
        for (Object o : session.getObjects()) {
            System.out.println((">> " + o));
        }
        System.err.println(list);
        Assert.assertEquals(Arrays.asList(1, 2, 3), list);
    }

    @Test
    public void testAbductiveLogicWithSelectiveConstructorArgs() {
        String droolsSource = (((((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : String @key \n") + "   name : String @key \n") + "   value : double \n") + "   flag : boolean \n") + "end \n") + "query foo( String $name, double $val, String $x ) \n") + "   @Abductive( target=Foo.class, args={ $x, $name } ) \n") + "end \n") + "rule R3 ") + "when ") + "   $f := foo( \"name_test\", 99.0, \"id_0\" ; ) ") + "then ") + "   list.add( $f ); ") + "end \n") + "") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.fireAllRules();
        FactType type = session.getKieBase().getFactType("org.drools.abductive.test", "Foo");
        for (Object o : session.getObjects()) {
            if (type.getFactClass().isInstance(o)) {
                Assert.assertEquals("id_0", type.get(o, "id"));
                Assert.assertEquals("name_test", type.get(o, "name"));
                Assert.assertEquals(0.0, type.get(o, "value"));
            }
        }
        System.err.println(list);
    }

    @Test
    public void testAbductiveLogicWithNonExistingArgsMapping() {
        String droolsSource = ((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : String @key \n") + "   name : String @key \n") + "end \n") + "query foo( String $name ) \n") + "   @Abductive( target=Foo.class, args={ $missing, $name } ) \n") + "end \n") + "";
        // ///////////////////////////////////
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(droolsSource, DRL);
        Results res = kieHelper.verify();
        Assert.assertEquals(1, res.getMessages(ERROR).size());
    }

    @Test
    public void testAbductiveLogicWithWrongTypeArgsMapping() {
        String droolsSource = ((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : String @key \n") + "   name : String @key \n") + "end \n") + "query foo( String $name, int $x ) \n") + "   @Abductive( target=Foo.class, args={ $x, $name } ) \n") + "end \n") + "rule R0 ") + "when ") + "   $f := foo( \"name_test\", 99 ; ) ") + "then ") + "   list.add( $f ); ") + "end \n") + "";
        // ///////////////////////////////////
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(droolsSource, DRL);
        Results res = kieHelper.verify();
        Assert.assertEquals(1, res.getMessages(ERROR).size());
    }

    @Test
    public void testBindNonAbductiveQueryError() {
        String droolsSource = "package org.drools.abductive.test; \n" + ((((((((("" + "") + "query foo() \n") + "end \n") + "rule R1 ") + "when ") + "   $x : foo( ) ") + "then ") + "end \n") + "");
        // ///////////////////////////////////
        KieServices ks = Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ks.getResources().newByteArrayResource(droolsSource.getBytes()).setSourcePath("drl1.drl").setResourceType(DRL));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        kieBuilder.buildAll();
        Results res = kieBuilder.getResults();
        Assert.assertTrue(res.hasMessages(ERROR));
    }

    @Test
    public void testAbducedReturnBinding() {
        String droolsSource = ((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.Map map; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : Integer @key \n") + "end \n") + "query foo( Integer $i ) \n") + "   @Abductive( target=Foo.class ) \n") + "   $i := Integer() ") + "end \n") + "rule R1 ") + "when ") + "   $x : foo( $v ; ) ") + "then ") + "   map.put( $v, $x ); ") + "end \n") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        Map map = new HashMap();
        session.setGlobal("map", map);
        session.insert(3);
        session.insert(42);
        session.insert(11);
        session.fireAllRules();
        System.out.println(map);
        Assert.assertTrue(map.keySet().containsAll(Arrays.asList(3, 42, 11)));
        FactType foo = session.getKieBase().getFactType("org.drools.abductive.test", "Foo");
        for (Object k : map.keySet()) {
            Object val = map.get(k);
            Assert.assertSame(foo.getFactClass(), val.getClass());
            Assert.assertEquals(k, foo.get(val, "id"));
        }
    }

    @Abducible
    public static class Bean {
        private Integer id;

        public Bean() {
            id = 0;
        }

        public Bean(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            AbductionTest.Bean bean = ((AbductionTest.Bean) (o));
            if ((id) != (bean.id))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public String toString() {
            return (("Bean{" + "id=") + (id)) + '}';
        }
    }

    @Test
    public void testAbducedKnownClass() {
        String droolsSource = (((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (AbductionTest.Bean.class.getCanonicalName())) + ";") + "global java.util.Map map; \n") + "") + "query foo( Integer $i ) \n") + "   @Abductive( target=Bean.class ) \n") + "   $i := Integer() ") + "end \n") + "rule R1 ") + "when ") + "   $x : foo( $v ; ) ") + "then ") + "   map.put( $v, $x ); ") + "end \n") + "") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        Map map = new HashMap();
        session.setGlobal("map", map);
        session.insert(42);
        session.fireAllRules();
        System.out.println(map);
        Assert.assertTrue(map.containsKey(42));
        Assert.assertEquals(new AbductionTest.Bean(42), map.get(42));
    }

    @Test
    public void testAbducedWithStatus() {
        String droolsSource = (((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (AbductionTest.Bean.class.getCanonicalName())) + ";") + "global java.util.Map map; \n") + "") + "query foo( Integer $i ) \n") + "   @Abductive( target=Bean.class ) \n") + "   $i := Integer() ") + "end \n") + "rule R1 ") + "when ") + "   $x : foo( $v ; ) ") + "then ") + "   map.put( $v, $x ); ") + "end \n") + "") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        Map map = new HashMap();
        session.setGlobal("map", map);
        session.insert(42);
        session.insert(11);
        AbductionTest.Bean b = new AbductionTest.Bean(11);
        session.insert(b);
        session.fireAllRules();
        System.out.println(map);
        Assert.assertTrue(map.keySet().containsAll(Arrays.asList(11, 42)));
        Assert.assertEquals(2, map.size());
        AbductionTest.Bean b11 = ((AbductionTest.Bean) (map.get(11)));
        InternalFactHandle f11 = ((InternalFactHandle) (session.getFactHandle(b11)));
        Assert.assertSame(b, b11);
        AbductionTest.Bean b42 = ((AbductionTest.Bean) (map.get(42)));
        InternalFactHandle f42 = ((InternalFactHandle) (session.getFactHandle(b42)));
        Assert.assertEquals(JUSTIFIED, f42.getEqualityKey().getStatus());
    }

    @Test
    public void testAbductiveLogicUnlinking() {
        String droolsSource = ((((((((((((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : Integer @key \n") + "end \n") + "query foo( Integer $i ) \n") + "   @Abductive( target=Foo.class ) \n") + "end \n") + "rule R1 ") + "when ") + "   foo( 42 ; ) ") + "   Foo( 42 ; ) ") + "then ") + "   list.add( 1 ); ") + "end \n") + "") + "rule R2 ") + "when ") + "   foo( 24 ; ) ") + "   String() ") + "   Foo( 24 ; ) ") + "then ") + "   list.add( 2 ); ") + "end \n") + "") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.fireAllRules();
        session.insert("test");
        session.fireAllRules();
        for (Object o : session.getObjects()) {
            System.out.println((">> " + o));
        }
        System.err.println(list);
        Assert.assertEquals(Arrays.asList(1, 2), list);
    }

    @Test
    public void testAbductiveLogicNoConstructorFoundError() {
        String droolsSource = ((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : Integer @key \n") + "end \n") + "query foo( String $x ) \n") + // Foo does not have a String constructor
        "   @Abductive( target=Foo.class ) \n") + "end \n") + "rule R1 ") + "when ") + "   $x : foo( \"x\" ; ) ") + "then ") + "   list.add( $x ); ") + "end \n") + "") + "";
        // ///////////////////////////////////
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(droolsSource, DRL);
        Results res = kieHelper.verify();
        Assert.assertEquals(1, res.getMessages(ERROR).size());
    }

    @Test
    public void testQueryTwice() {
        String droolsSource = (((((((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Foo \n") + "   @Abducible \n") + "   id : String @key \n") + "end \n") + "query foo1( String $x ) \n") + "   @Abductive( target=Foo.class ) \n") + "end \n") + "query foo2( String $x ) \n") + "   @Abductive( target=Foo.class ) \n") + "end \n") + "rule R1 ") + "when ") + "   $x := ?foo1( \"x\" ; ) ") + "   $x := ?foo2( \"x\" ; ) ") + "then ") + "   System.out.println( 'aaaa' ); ") + "   list.add( $x ); ") + "end \n") + "") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.fireAllRules();
        for (Object o : session.getObjects()) {
            System.out.println((">> " + o));
        }
        System.err.println(list);
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testAbductiveLogicSprinklerAndRainExample() {
        // Sprinkler & Rain, abductive version
        String droolsSource = ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "import ") + (Defeasible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Sunny id : int @key end \n") + "declare WetGrass @Abducible id : int @key end \n") + "declare Rain @Abducible id : int @key end \n") + "declare Sprinkler @Abducible id : int @key end \n") + "query wetGrass() \n") + "   @Abductive( target=WetGrass.class ) \n") + "   rain() or sprinkler() \n") + "end \n") + "query rain() \n") + "   @Abductive( target=Rain.class ) \n") + "   @Defeasible ") + "end \n") + "query sprinkler() \n") + "   @Abductive( target=Sprinkler.class ) \n") + "   @Defeasible ") + "end \n") + "rule SunIntegrityConstraint \n") + "@Direct \n") + "when \n") + "   Sunny()") + "then \n") + "   insertLogical( new Rain(), \'neg\' ); \n") + "end \n") + "rule Facts \n") + "when \n") + "then \n") + " insert( new Sunny( 0 ) ); \n") + "end \n") + "rule Raaain \n") + "when ") + "   $r : Rain( _.neg ) ") + "then \n") + "   list.add( \'no_rain_check\' ); \n") + "end \n") + "rule Main_1\n") + "when \n") + "   wetGrass() \n") + "   ( ") + "     Sprinkler() do[sprk] \n") + "     or \n") + "     Rain() do[rain] \n") + "     or \n") + "     Rain( _.neg ) do[norn] \n") + "   ) \n") + "then \n") + "then[sprk] \n") + "   list.add( \'sprinkler\' ); \n") + "   System.out.println( \"The grass is wet because the sprinkler is on \" ); \n") + "then[rain] \n") + "   list.add( \'rain\' ); \n ") + "   System.out.println( \"The grass is wet because it\'s raining! \" ); \n") + "then[norn] \n") + "   list.add( \'not rain\' ); \n") + "   System.out.println( \"The grass can\'t be wet due to rain, it\'s sunny today!!! \" ); \n") + "end \n") + "";
        // ///////////////////////////////////
        KieSession session;
        try {
            System.setProperty("drools.negatable", "on");
            session = getSessionFromString(droolsSource);
        } finally {
            System.setProperty("drools.negatable", "off");
        }
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.fireAllRules();
        System.out.println(list);
        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains("sprinkler"));
        Assert.assertTrue(list.contains("not rain"));
        Assert.assertTrue(list.contains("no_rain_check"));
        Assert.assertEquals(3, session.getObjects().size());
        int i = 0;
        Iterator it = session.getObjects().iterator();
        while (it.hasNext()) {
            i++;
            it.next();
        } 
        Assert.assertEquals(3, i);
    }

    @Test
    public void testAbductiveFactory() {
        String droolsSource = (((((((((((((((((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "") + "declare Root id : String @key end \n") + "declare TypeA extends Root @Abducible end \n") + "declare TypeB extends Root @Abducible end \n") + "query factory( String $type, String $arg, Root $out ) \n") + "   ( String( this == \"A\" ) from $type ") + "     and") + "     $out := typeA( $arg ; ) ") + "   ) ") + "   or ") + "   ( String( this == \"B\" ) from $type ") + "     and ") + "     $out := typeB( $arg ; ) ") + "   ) ") + "end \n") + "query typeA( String $x ) \n") + "   @Abductive( target=TypeA.class ) \n") + "end \n") + "query typeB( String $x ) \n") + "   @Abductive( target=TypeB.class ) \n") + "end \n") + "rule Main\n") + "when \n") + "   $s : String() ") + "   factory( $s, \"foo\", $x ; ) ") + "   Root( id == \"foo\" ) from $x ") + "then \n") + "   System.out.println( \">>>>>\" + $x ); \n") + "   list.add( $x ); \n") + "end \n") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.insert("A");
        session.fireAllRules();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("TypeA", list.get(0).getClass().getSimpleName());
    }

    @Test
    public void testNeeds() {
        // revisiting OPSJ's version of a fragment of the famous monkey&bananas AI problem
        String droolsSource = (((((((((((((((((((((((((((((((((((((((((((((((((("package org.drools.abductive.test; \n" + ("" + "import ")) + (Thing.class.getPackage().getName())) + ".*;") + "import ") + (Abducible.class.getName())) + "; \n") + "global java.util.List list; \n") + "declare Goal \n") + "   entity : Thing \n") + "   property : String \n") + "   value : Object \n") + "end \n") + "query check( Thing $thing, String $prop, Object $val ) ") + "   Thing( this == $thing, fields[ $prop ] == $val ) ") + "   or") + "   ( ") + "     need( $thing, $prop, $val ; ) ") + "     and") + "     Thing( this == $thing, fields[ $prop ] == $val ) ") + "   ) ") + "end \n ") + "query need( Thing $thing, String $prop, Object $val ) ") + "   @Abductive( target=Goal.class ) \n") + "   Thing( this == $thing, fields[ $prop ] != $val ) ") + "end \n ") + "rule HandleGoal ") + "when ") + "   $g : Goal( $m : entity, $prop : property, $val : value ) ") + "then ") + "   System.out.println( \'Satisfy \' + $g ); \n") + "   modify ( $m ) { getFields().put( $prop, $val ); } \n") + "end ") + "declare trait Monkey\n") + "   position : Integer = 1 \n ") + "end \n") + "rule Main\n") + "when \n") + "then \n") + "   System.out.println( 'Don MONKEY ' ); ") + "   Entity e = new Entity(); \n") + "   Monkey monkey = don( e, Monkey.class );") + "end \n") + "rule MoveAround ") + "when ") + "   $m : Monkey( $pos : position ) ") + "   ?check( $m, \"position\", 4 ; ) ") + "then ") + "   System.out.println( 'Monkey madness' + $m ); ") + "   list.add( $m.getPosition() ); ") + "end ") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.fireAllRules();
        for (Object o : session.getObjects()) {
            System.out.println((">>> " + o));
        }
        Assert.assertEquals(Arrays.asList(4), list);
    }

    @Test
    public void testQueryAPIs() {
        String droolsSource = ((((((((((((((((((((((("package org.drools.abductive.test; \n" + "import ") + (Abducible.class.getName())) + "; \n") + "") + "declare Foo ") + "   @Abducible ") + "   id : String ") + "end ") + "query foo( String $s ) ") + "   @Abductive( target=Foo.class ) \n") + "end \n ") + "query bar( String $s, Foo $foo ) ") + "   $foo := Foo() ") + "end \n ") + "rule MoveAround ") + "when ") + "   $s : String() ") + "   $f : foo( $s ; ) ") + "   bar( $s, $f ; ) ") + "then ") + "   delete( $s ); ") + "   System.out.println( 'Foo ' + $f ); ") + "end ") + "";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        session.insert("faa");
        session.fireAllRules();
        for (Object o : session.getObjects()) {
            System.out.println((">>> " + o));
        }
        Assert.assertEquals(1, session.getObjects().size());
        Query q1 = session.getKieBase().getQuery("org.drools.abductive.test", "foo");
        Query q2 = session.getKieBase().getQuery("org.drools.abductive.test", "bar");
        Assert.assertNotNull(q1);
        Assert.assertNotNull(q2);
        QueryResults q10res = new FlatQueryResults(((QueryResultsImpl) (session.getQueryResults("foo", "foo", null))));
        QueryResults q11res = new FlatQueryResults(((QueryResultsImpl) (session.getQueryResults("foo", "foo", v))));
        QueryResults q20res = new FlatQueryResults(((QueryResultsImpl) (session.getQueryResults("bar", "foo", v))));
        Assert.assertEquals(1, q10res.size());
        Assert.assertEquals(1, q11res.size());
        Assert.assertEquals(1, q20res.size());
        QueryResultsRow row10 = q10res.iterator().next();
        QueryResultsRow row11 = q11res.iterator().next();
        QueryResultsRow row20 = q20res.iterator().next();
        Assert.assertEquals("foo", row10.get("$s"));
        Assert.assertEquals("foo", row11.get("$s"));
        Assert.assertEquals("foo", row20.get("$s"));
        Object foo = row20.get("$foo");
        Assert.assertSame(foo, session.getObjects().iterator().next());
        // the implicit return argument, the abduced/retrieved fact, is hidden
        Assert.assertNull(row11.get(""));
    }

    @Test
    public void testCitizenshipExample() {
        // from wikipedia, abductive reasoning example
        String droolsSource = "package org.drools.abductive.test; \n" + (((((((((((((((((((((((((((((((((((((((((("" + "declare CitizenUS ") + "   name : String @key ") + "end ") + "declare Parent ") + "   parent : String @key ") + "   child : String @key ") + "end ") + "declare BornUS @Abducible name : String @key end ") + "declare BornOutsideUS @Abducible name : String @key end ") + "declare ResidentUS @Abducible name : String @key end ") + "declare NaturalizedUS @Abducible name : String @key end ") + "declare RegisteredUS @Abducible name : String @key end ") + "query extractCitizen( CitizenUS $cit ) ") + "   $cit := CitizenUS() ") + "end ") + "query citizen( String $name ) ") + "   @Abductive( target=CitizenUS.class ) ") + "   bornUS( $name ; ) ") + "   or ") + "   ( bornOutsideUS( $name ; ) and residentUS( $name ; ) and naturalizedUS( $name ; ) ) ") + "   or ") + "   ( bornOutsideUS( $name ; ) and Parent( $parent, $name ; ) and CitizenUS( $parent ; ) and registeredUS( $name ; ) ) ") + "end ") + "query bornUS( String $name ) @Abductive( target=BornUS.class ) end ") + "query bornOutsideUS( String $name ) @Abductive( target=BornOutsideUS.class ) end ") + "query residentUS( String $name ) @Abductive( target=ResidentUS.class ) end ") + "query naturalizedUS( String $name ) @Abductive( target=NaturalizedUS.class ) end ") + "query registeredUS( String $name ) @Abductive( target=RegisteredUS.class ) end ") + "rule Facts ") + "when ") + "then ") + "   insert( new CitizenUS( 'Mary' ) ); ") + "   insert( new Parent( 'Mary', 'John' ) ); ") + "   insertLogical( new ResidentUS( 'John' ), 'neg' ); ") + "end ") + "rule CheckCitizen ") + "when ") + "   $cit : ?citizen( 'John' ; ) ") + "then ") + "   System.out.println( 'John is a citizen ' + $cit ); ") + "end ") + "");
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        session.fireAllRules();
        FactType type = session.getKieBase().getFactType("org.drools.abductive.test", "CitizenUS");
        for (Object o : session.getObjects()) {
            System.out.println((">>> " + o));
            if (o.getClass().equals(type.getFactClass())) {
                InternalFactHandle h = ((InternalFactHandle) (session.getFactHandle(o)));
                String name = ((String) (type.get(o, "name")));
                if ("Mary".equals(name)) {
                    Assert.assertNull(h.getEqualityKey().getBeliefSet());
                } else
                    if ("John".equals(name)) {
                        BeliefSet bs = h.getEqualityKey().getBeliefSet();
                        Assert.assertTrue(bs.isPositive());
                        Assert.assertEquals(2, bs.size());
                    }

            }
        }
    }

    @Test
    public void testCheckForItemsExample() {
        String droolsSource = (((((((((((((((((((((((((((((((((((((((((((((((((((((((("package org.drools.abductive.test; " + "import ") + (Abducible.class.getName())) + "; ") + "global java.util.List list; ") + "declare Fruit id : int @key end ") + "declare Apple extends Fruit end ") + "declare Orange extends Fruit end ") + "declare Banana extends Fruit end ") + "declare Goal ") + "   type : Class @key ") + "end ") + "query need( Class $type ) ") + "   @Abductive( target = Goal.class ) ") + "   not Fruit( $type.getName() == this.getClass().getName() ) ") + "end ") + "query check( Class $type ) ") + "   ?need( $type ; ) ") + "   or ") + "   Fruit( $type.getName() == this.getClass().getName() ) ") + "end ") + "query checkRecipe() ") + "   check( Apple.class ; ) ") + "   check( Orange.class ; ) ") + "   check( Banana.class ; ) ") + "end ") + "rule Fridge ") + "   @Direct ") + "when ") + "then ") + "   insert( new Banana( 1 ) ); ") + // "   insert( new Apple( 2 ) ); " +
        "   insert( new Orange( 1 ) ); ") + "end ") + "rule Reminder ") + "when ") + "   accumulate( $f : Fruit() ,") + "       $c : count( $f );") + "       $c == 2 ")// works also with 1, or no fruit at all
         + "   ) ") + "   ?checkRecipe() ") + "then ") + "   System.out.println( 'You have ' + $c + ' ingredients ' ); ") + "end ") + "rule Suggest ") + "when ") + "   $g : Goal( $type ; ) ") + "then ") + "   System.out.println( 'You are missing ' + $type ); ") + "   list.add( $type.getSimpleName() ); ") + "end ") + "rule FruitSalad ") + "when ") + "   Apple() ") + "   Banana() ") + "   Orange() ") + "then ") + "   System.out.println( 'Enjoy the salad' ); ") + "end ";
        // ///////////////////////////////////
        KieSession session = getSessionFromString(droolsSource);
        List list = new ArrayList();
        session.setGlobal("list", list);
        session.fireAllRules();
        for (Object o : session.getObjects()) {
            System.out.println((">>> " + o));
        }
        Assert.assertEquals(Arrays.asList("Apple"), list);
    }
}

