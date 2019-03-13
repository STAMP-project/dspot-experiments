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
package org.drools.core.meta;


import Lit.ADD;
import Lit.REMOVE;
import Lit.SET;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.factmodel.traits.Entity;
import org.drools.core.meta.org.test.AnotherKlass;
import org.drools.core.meta.org.test.AnotherKlassImpl;
import org.drools.core.meta.org.test.AnotherKlass_;
import org.drools.core.meta.org.test.Klass;
import org.drools.core.meta.org.test.KlassImpl;
import org.drools.core.meta.org.test.Klass_;
import org.drools.core.meta.org.test.SubKlass;
import org.drools.core.meta.org.test.SubKlassImpl;
import org.drools.core.meta.org.test.SubKlass_;
import org.drools.core.metadata.Identifiable;
import org.drools.core.metadata.MetadataContainer;
import org.drools.core.metadata.With;
import org.drools.core.test.model.Person;
import org.junit.Assert;
import org.junit.Test;

import static org.drools.core.meta.org.test.AnotherKlass_.AnotherKlass_Don.getUri;
import static org.drools.core.meta.org.test.Klass_.Klass_NewInstance.call;
import static org.drools.core.meta.org.test.SubKlass_.SubKlass_Modify.getModificationMask;


public class MetadataTest {
    @Test
    public void testKlassAndSubKlassWithImpl() {
        SubKlass ski = new SubKlassImpl();
        ski.setSubProp(42);
        ski.setProp("hello");
        SubKlass_ sk = new SubKlass_(ski);
        Assert.assertEquals(42, ((int) (sk.subProp.get(ski))));
        Assert.assertEquals("hello", sk.prop.get(ski));
        sk.modify().prop("bye").subProp((-99)).call();
        Assert.assertEquals((-99), ((int) (sk.subProp.get(ski))));
        Assert.assertEquals("bye", sk.prop.get(ski));
    }

    @Test
    public void testKlassAndSubKlassWithHolderImpl() {
        SubKlassImpl ski = new SubKlassImpl();
        ski.setSubProp(42);
        ski.setProp("hello");
        SubKlass_ sk = ski.get_();
        Assert.assertEquals(42, ((int) (sk.subProp.get(ski))));
        Assert.assertEquals("hello", sk.prop.get(ski));
        sk.modify().prop("bye").subProp((-99)).call();
        Assert.assertEquals((-99), ((int) (sk.subProp.get(ski))));
        Assert.assertEquals("bye", sk.prop.get(ski));
    }

    @Test
    public void testKlassAndSubKlassWithInterfaces() {
        SubKlass ski = new MetadataTest.Foo();
        ski.setSubProp(42);
        ski.setProp("hello");
        SubKlass_ sk = new SubKlass_(ski);
        Assert.assertEquals(42, ((int) (sk.subProp.get(ski))));
        Assert.assertEquals("hello", sk.prop.get(ski));
        sk.modify().subProp((-99)).prop("bye").call();
        Assert.assertEquals((-99), ((int) (sk.subProp.get(ski))));
        Assert.assertEquals("bye", sk.prop.get(ski));
        System.out.println(((MetadataTest.Foo) (ski)).map);
        Map tgt = new HashMap();
        tgt.put("prop", "bye");
        tgt.put("subProp", (-99));
        Assert.assertEquals(tgt, ((MetadataTest.Foo) (ski)).map);
    }

    @Test
    public void testMetaPropertiesWithManyKlasses() {
        SubKlass ski = new MetadataTest.Foo();
        ski.setSubProp(42);
        ski.setProp("hello");
        SubKlass_ sk = new SubKlass_(ski);
        AnotherKlass aki = new AnotherKlassImpl();
        aki.setNum(1);
        AnotherKlass_ ak = new AnotherKlass_(aki);
        sk.modify().subProp((-99)).prop("bye").call();
        ak.modify().num((-5)).call();
        Assert.assertEquals((-5), aki.getNum());
        Assert.assertEquals((-99), ((int) (ski.getSubProp())));
    }

    @Test
    public void testMetadataInternals() {
        SubKlass_<SubKlass> sk = new SubKlass_(new SubKlassImpl());
        Klass_<Klass> k = new Klass_(new KlassImpl());
        AnotherKlass_<AnotherKlass> ak = new AnotherKlass_(new AnotherKlassImpl());
        Assert.assertEquals(4, ak.getMetaClassInfo().getProperties().length);
        Assert.assertEquals(4, sk.getMetaClassInfo().getProperties().length);
        Assert.assertEquals(4, k.getMetaClassInfo().getProperties().length);
        Assert.assertEquals("subProp", sk.getMetaClassInfo().getProperties()[2].getName());
    }

    @Test
    public void testMetadataModifyStyle() {
        SubKlassImpl ski = new SubKlassImpl();
        SubKlass_.modify(ski).prop("hello").subProp(42).call();
        Assert.assertEquals("hello", ski.getProp());
        Assert.assertEquals(42, ((int) (ski.getSubProp())));
    }

    @Test
    public void testModificationMask() {
        SubKlassImpl ski = new SubKlassImpl();
        SubKlass_.SubKlass_Modify task = SubKlass_.modify(ski).prop("hello").subProp(42);
        task.call();
        Assert.assertEquals("144", getModificationMask().toString());
        SubKlass_.SubKlass_Modify task2 = SubKlass_.modify(ski).prop("hello");
        task2.call();
        Assert.assertEquals("16", getModificationMask().toString());
        SubKlass_.SubKlass_Modify task3 = SubKlass_.modify(ski).subProp(42);
        task3.call();
        Assert.assertEquals("128", getModificationMask().toString());
    }

    @Test
    public void testURIs() {
        AnotherKlassImpl aki = new AnotherKlassImpl();
        Assert.assertEquals(URI.create("http://www.test.org#AnotherKlass"), aki.get_().getMetaClassInfo().getUri());
        Assert.assertEquals(URI.create("http://www.test.org#AnotherKlass?num"), aki.get_().num.getUri());
        URI uri = AnotherKlass_.getIdentifier(AnotherKlass_, aki);
        Assert.assertEquals(URI.create(("http://www.test.org#AnotherKlass/AnotherKlassImpl/" + (System.identityHashCode(aki)))), uri);
        Assert.assertEquals(URI.create(((uri.toString()) + "/modify?num")), AnotherKlass_.modify(aki).num(33).getUri());
        Assert.assertTrue(uri.toString().startsWith(aki.get_().getMetaClassInfo().getUri().toString()));
        Assert.assertEquals(URI.create("http://www.test.org#SubKlass/123?create"), SubKlass_.newSubKlass(URI.create("http://www.test.org#SubKlass/123")).getUri());
        Assert.assertEquals(URI.create("123?don=org.drools.core.meta.org.test.AnotherKlass"), getUri());
    }

    @Test
    public void testNewInstance() {
        Klass klass = call();
        Assert.assertNotNull(klass);
        Assert.assertTrue((klass instanceof KlassImpl));
        SubKlass klass2 = SubKlass_.newSubKlass(URI.create("test2")).subProp(42).prop("hello").call();
        Assert.assertEquals("hello", klass2.getProp());
        Assert.assertEquals(42, ((int) (klass2.getSubProp())));
    }

    @Test
    public void testURIsOnLegacyClasses() {
        Person p = new Person();
        URI uri = MetadataContainer.getIdentifier(p);
        Assert.assertEquals(URI.create(((((("urn:" + (p.getClass().getPackage().getName())) + "/") + (p.getClass().getSimpleName())) + "/") + (System.identityHashCode(p)))), uri);
    }

    @Test
    public void testDon() {
        Entity entity = new Entity("123");
        entity._setDynamicProperties(new HashMap());
        entity._getDynamicProperties().put("prop", "hello");
        Klass klass = Klass_.donKlass(entity).setTraitFactory(new org.drools.core.util.StandaloneTraitFactory(ProjectClassLoader.createProjectClassLoader())).call();
        Assert.assertEquals("hello", klass.getProp());
    }

    @Test
    public void testDonWithAttributes() {
        Entity entity = new Entity("123");
        entity._setDynamicProperties(new HashMap());
        SubKlass klass = SubKlass_.donSubKlass(entity).setTraitFactory(new org.drools.core.util.StandaloneTraitFactory(ProjectClassLoader.createProjectClassLoader())).prop("hello").subProp(32).call();
        Assert.assertEquals("hello", klass.getProp());
        Assert.assertEquals(32, ((int) (klass.getSubProp())));
    }

    @Test
    public void testInitWithModifyArgs() {
        AnotherKlass aki = AnotherKlass_.newAnotherKlass("000").call();
        SubKlass ski = SubKlass_.newSubKlass(URI.create("123"), With.with(aki)).prop("hello").subProp(42).another(aki).call();
        Klass ki = call();
        Assert.assertEquals("hello", ski.getProp());
        Assert.assertEquals(42, ((int) (ski.getSubProp())));
        Assert.assertEquals(aki, ski.getAnother());
    }

    @Test
    public void testCollectionOrientedProperties() {
        AnotherKlass aki0 = AnotherKlass_.newAnotherKlass("000").call();
        AnotherKlass aki1 = AnotherKlass_.newAnotherKlass("001").call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass("002").call();
        AnotherKlass aki3 = AnotherKlass_.newAnotherKlass("003").call();
        AnotherKlass aki4 = AnotherKlass_.newAnotherKlass("004").call();
        ArrayList<AnotherKlass> initial = new ArrayList(Arrays.asList(aki0, aki1));
        SubKlass ski = SubKlass_.newSubKlass(URI.create("123")).links(initial, SET).links(aki1, REMOVE).links(aki2, ADD).links(Arrays.asList(aki3, aki4), REMOVE).call();
        Assert.assertEquals(Arrays.asList(aki0, aki2), ski.getLinks());
    }

    @Test
    public void testOneToOneProperty() {
        AnotherKlass aki0 = AnotherKlass_.newAnotherKlass("000").call();
        Klass klass = call();
        Klass_.modify(klass, With.with(aki0)).another(aki0).call();
        Assert.assertSame(klass.getAnother(), aki0);
        Assert.assertSame(klass, aki0.getTheKlass());
        Klass klass1 = call();
        AnotherKlass_.modify(aki0).theKlass(klass1).call();
        Assert.assertSame(aki0, klass1.getAnother());
        Assert.assertSame(klass1, aki0.getTheKlass());
        Klass_.modify(klass).another(null).call();
        Assert.assertNull(klass.getAnother());
        Assert.assertNull(aki0.getTheKlass());
    }

    @Test
    public void testOneToManyProperty() {
        AnotherKlass aki = AnotherKlass_.newAnotherKlass("000").call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass("999").call();
        Klass klass1 = call();
        Klass klass2 = call();
        AnotherKlass_.modify(aki, With.with(klass1, klass2)).manyKlasses(new ArrayList(Arrays.asList(klass1, klass2)), SET).call();
        Assert.assertSame(aki, klass1.getOneAnother());
        Assert.assertSame(aki, klass2.getOneAnother());
        AnotherKlass_.modify(aki2).manyKlasses(klass1, ADD).call();
        Assert.assertSame(aki2, klass1.getOneAnother());
        Assert.assertSame(aki, klass2.getOneAnother());
        Assert.assertFalse(aki.getManyKlasses().contains(klass1));
        Assert.assertTrue(aki2.getManyKlasses().contains(klass1));
        Assert.assertTrue(aki.getManyKlasses().contains(klass2));
        AnotherKlass_.modify(aki2).manyKlasses(klass1, REMOVE).call();
        Assert.assertNull(klass1.getOneAnother());
        Assert.assertFalse(aki2.getManyKlasses().contains(klass1));
    }

    @Test
    public void testManyToOneProperty() {
        AnotherKlass aki = AnotherKlass_.newAnotherKlass("000").call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass("999").call();
        Klass klass1 = call();
        Klass klass2 = call();
        Klass_.modify(klass1).oneAnother(aki).call();
        Klass_.modify(klass2).oneAnother(aki).call();
        Assert.assertSame(aki, klass1.getOneAnother());
        Assert.assertSame(aki, klass2.getOneAnother());
        Assert.assertEquals(Arrays.asList(klass1, klass2), aki.getManyKlasses());
        Klass_.modify(klass1).oneAnother(aki2).call();
        Assert.assertSame(aki2, klass1.getOneAnother());
        Assert.assertEquals(Arrays.asList(klass1), aki2.getManyKlasses());
        Assert.assertEquals(Arrays.asList(klass2), aki.getManyKlasses());
    }

    @Test
    public void testManyToManyProperty() {
        AnotherKlass aki1 = AnotherKlass_.newAnotherKlass("000").call();
        AnotherKlass aki2 = AnotherKlass_.newAnotherKlass("999").call();
        Klass klass1 = call();
        Klass klass2 = call();
        Klass_.modify(klass1).manyOthers(aki1, ADD).call();
        Klass_.modify(klass1).manyOthers(aki2, ADD).call();
        AnotherKlass_.modify(aki2).manyMoreKlasses(klass2, ADD).call();
        AnotherKlass_.modify(aki1).manyMoreKlasses(klass2, ADD).call();
        Assert.assertTrue(klass1.getManyAnothers().contains(aki1));
        Assert.assertTrue(klass1.getManyAnothers().contains(aki2));
        Assert.assertTrue(klass2.getManyAnothers().contains(aki1));
        Assert.assertTrue(klass2.getManyAnothers().contains(aki2));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass1));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass2));
        Assert.assertTrue(aki2.getManyMoreKlasses().contains(klass1));
        Assert.assertTrue(aki2.getManyMoreKlasses().contains(klass2));
        AnotherKlass_.modify(aki2).manyMoreKlasses(klass2, REMOVE).call();
        Assert.assertTrue(klass1.getManyAnothers().contains(aki1));
        Assert.assertTrue(klass1.getManyAnothers().contains(aki2));
        Assert.assertTrue(klass2.getManyAnothers().contains(aki1));
        Assert.assertFalse(klass2.getManyAnothers().contains(aki2));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass1));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass2));
        Assert.assertTrue(aki2.getManyMoreKlasses().contains(klass1));
        Assert.assertFalse(aki2.getManyMoreKlasses().contains(klass2));
        AnotherKlass_.modify(aki2).manyMoreKlasses(klass2, ADD).call();
        Assert.assertTrue(klass1.getManyAnothers().contains(aki1));
        Assert.assertTrue(klass1.getManyAnothers().contains(aki2));
        Assert.assertTrue(klass2.getManyAnothers().contains(aki1));
        Assert.assertTrue(klass2.getManyAnothers().contains(aki2));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass1));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass2));
        Assert.assertTrue(aki2.getManyMoreKlasses().contains(klass1));
        Assert.assertTrue(aki2.getManyMoreKlasses().contains(klass2));
        AnotherKlass_.modify(aki2).manyMoreKlasses(klass2, SET).call();
        Assert.assertTrue(klass1.getManyAnothers().contains(aki1));
        Assert.assertFalse(klass1.getManyAnothers().contains(aki2));
        Assert.assertTrue(klass2.getManyAnothers().contains(aki1));
        Assert.assertTrue(klass2.getManyAnothers().contains(aki2));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass1));
        Assert.assertTrue(aki1.getManyMoreKlasses().contains(klass2));
        Assert.assertFalse(aki2.getManyMoreKlasses().contains(klass1));
        Assert.assertTrue(aki2.getManyMoreKlasses().contains(klass2));
    }

    public static class Foo implements SubKlass , Identifiable {
        private URI uri;

        public Foo() {
            this("123");
        }

        public Foo(String uri) {
            this.uri = URI.create(uri);
        }

        public Map<String, Object> map = new HashMap<String, Object>();

        @Override
        public String getProp() {
            return ((String) (map.get("prop")));
        }

        @Override
        public void setProp(String value) {
            map.put("prop", value);
        }

        @Override
        public AnotherKlass getAnother() {
            return ((AnotherKlass) (map.get("another")));
        }

        @Override
        public void setAnother(AnotherKlass another) {
            map.put("another", another);
        }

        @Override
        public AnotherKlass getOneAnother() {
            return null;
        }

        @Override
        public void setOneAnother(AnotherKlass another) {
        }

        @Override
        public List<AnotherKlass> getManyAnothers() {
            return null;
        }

        @Override
        public void setManyAnothers(List<AnotherKlass> anothers) {
        }

        @Override
        public Integer getSubProp() {
            return ((Integer) (map.get("subProp")));
        }

        @Override
        public void setSubProp(Integer value) {
            map.put("subProp", value);
        }

        @Override
        public List<AnotherKlass> getLinks() {
            return null;
        }

        @Override
        public void setLinks(List<AnotherKlass> links) {
        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public Object getId() {
            return uri;
        }
    }
}

