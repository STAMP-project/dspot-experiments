/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
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
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.injection;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.injection.bean.BeanInjectionInfo;
import org.pentaho.di.core.injection.bean.BeanInjector;
import org.pentaho.di.core.injection.inheritance.MetaBeanChild;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class MetaAnnotationInjectionTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String FIELD_ONE = "FIELD_ONE";

    private static final String COMPLEX_NAME = "COMPLEX_NAME";

    private static final String TEST_NAME = "TEST_NAME";

    @Test
    public void testInjectionDescription() throws Exception {
        BeanInjectionInfo ri = new BeanInjectionInfo(MetaBeanLevel1.class);
        Assert.assertEquals(3, ri.getGroups().size());
        Assert.assertEquals("", ri.getGroups().get(0).getName());
        Assert.assertEquals("FILENAME_LINES", ri.getGroups().get(1).getName());
        Assert.assertEquals("FILENAME_LINES2", ri.getGroups().get(2).getName());
        Assert.assertTrue(ri.getProperties().containsKey("SEPARATOR"));
        Assert.assertTrue(ri.getProperties().containsKey("FILENAME"));
        Assert.assertTrue(ri.getProperties().containsKey("BASE"));
        Assert.assertTrue(ri.getProperties().containsKey("FIRST"));
        Assert.assertEquals("FILENAME_LINES", ri.getProperties().get("FILENAME").getGroupName());
        Assert.assertEquals("!DESCRIPTION!", ri.getDescription("DESCRIPTION"));
    }

    @Test
    public void testInjectionSets() throws Exception {
        MetaBeanLevel1 obj = new MetaBeanLevel1();
        RowMeta meta = new RowMeta();
        meta.addValueMeta(new ValueMetaString("f1"));
        meta.addValueMeta(new ValueMetaString("f2"));
        meta.addValueMeta(new ValueMetaString("fstrint"));
        meta.addValueMeta(new ValueMetaString("fstrlong"));
        meta.addValueMeta(new ValueMetaString("fstrboolean"));// TODO STLOCALE

        List<RowMetaAndData> rows = new ArrayList<>();
        rows.add(new RowMetaAndData(meta, "<sep>", "/tmp/file.txt", "123", "1234567891213", "y"));
        rows.add(new RowMetaAndData(meta, "<sep>", "/tmp/file2.txt", "123", "1234567891213", "y"));
        BeanInjector inj = MetaAnnotationInjectionTest.buildBeanInjectorFor(MetaBeanLevel1.class);
        inj.setProperty(obj, "SEPARATOR", rows, "f1");
        inj.setProperty(obj, "FILENAME", rows, "f2");
        inj.setProperty(obj, "FILENAME_ARRAY", rows, "f2");
        inj.setProperty(obj, "FBOOLEAN", rows, "fstrboolean");
        inj.setProperty(obj, "FINT", rows, "fstrint");
        inj.setProperty(obj, "FLONG", rows, "fstrlong");
        inj.setProperty(obj, "FIRST", rows, "fstrint");
        Assert.assertEquals("<sep>", obj.getSub().getSeparator());
        Assert.assertEquals("/tmp/file.txt", obj.getSub().getFiles()[0].getName());
        Assert.assertTrue(obj.fboolean);
        Assert.assertEquals(123, obj.fint);
        Assert.assertEquals(1234567891213L, obj.flong);
        Assert.assertEquals("123", obj.getSub().first());
        Assert.assertArrayEquals(new String[]{ "/tmp/file.txt", "/tmp/file2.txt" }, obj.getSub().getFilenames());
    }

    @Test
    public void testInjectionConstant() throws Exception {
        MetaBeanLevel1 obj = new MetaBeanLevel1();
        BeanInjector inj = MetaAnnotationInjectionTest.buildBeanInjectorFor(MetaBeanLevel1.class);
        inj.setProperty(obj, "SEPARATOR", null, "<sep>");
        inj.setProperty(obj, "FINT", null, "123");
        inj.setProperty(obj, "FLONG", null, "1234567891213");
        inj.setProperty(obj, "FBOOLEAN", null, "true");
        inj.setProperty(obj, "FILENAME", null, "f1");
        inj.setProperty(obj, "FILENAME_ARRAY", null, "f2");
        Assert.assertEquals("<sep>", obj.getSub().getSeparator());
        Assert.assertTrue(obj.fboolean);
        Assert.assertEquals(123, obj.fint);
        Assert.assertEquals(1234567891213L, obj.flong);
        Assert.assertNull(obj.getSub().getFiles());
        Assert.assertNull(obj.getSub().getFilenames());
        obj.getSub().files = new MetaBeanLevel3[]{ new MetaBeanLevel3(), new MetaBeanLevel3() };
        obj.getSub().filenames = new String[]{ "", "", "" };
        inj.setProperty(obj, "FILENAME", null, "f1");
        inj.setProperty(obj, "FILENAME_ARRAY", null, "f2");
        Assert.assertEquals(2, obj.getSub().getFiles().length);
        Assert.assertEquals("f1", obj.getSub().getFiles()[0].getName());
        Assert.assertEquals("f1", obj.getSub().getFiles()[1].getName());
        Assert.assertArrayEquals(new String[]{ "f2", "f2", "f2" }, obj.getSub().getFilenames());
    }

    @Test
    public void testInjectionForArrayPropertyWithoutDefaultConstructor_class_parameter() throws KettleException {
        BeanInjector beanInjector = MetaAnnotationInjectionTest.buildBeanInjectorFor(MetaAnnotationInjectionTest.MetadataBean.class);
        MetaAnnotationInjectionTest.MetadataBean targetBean = new MetaAnnotationInjectionTest.MetadataBean();
        beanInjector.setProperty(targetBean, MetaAnnotationInjectionTest.COMPLEX_NAME, MetaAnnotationInjectionTest.createRowMetaAndData(), MetaAnnotationInjectionTest.FIELD_ONE);
        Assert.assertNotNull(targetBean.getComplexField());
        Assert.assertTrue(((targetBean.getComplexField().length) == 1));
        Assert.assertEquals(MetaAnnotationInjectionTest.TEST_NAME, targetBean.getComplexField()[0].getFieldName());
    }

    @Test
    public void testInjectionForArrayPropertyWithoutDefaultConstructor_interface_parameter() throws KettleException {
        BeanInjector beanInjector = MetaAnnotationInjectionTest.buildBeanInjectorFor(MetaAnnotationInjectionTest.MetadataBeanImplementsInterface.class);
        MetaAnnotationInjectionTest.MetadataBeanImplementsInterface targetBean = new MetaAnnotationInjectionTest.MetadataBeanImplementsInterface();
        beanInjector.setProperty(targetBean, MetaAnnotationInjectionTest.COMPLEX_NAME, MetaAnnotationInjectionTest.createRowMetaAndData(), MetaAnnotationInjectionTest.FIELD_ONE);
        Assert.assertNotNull(targetBean.getComplexField());
        Assert.assertTrue(((targetBean.getComplexField().length) == 1));
        Assert.assertEquals(MetaAnnotationInjectionTest.TEST_NAME, targetBean.getComplexField()[0].getFieldName());
    }

    @Test
    public void testWrongDeclarations() throws Exception {
        try {
            new BeanInjectionInfo(MetaBeanWrong1.class);
            Assert.fail();
        } catch (Exception ex) {
        }
        try {
            new BeanInjectionInfo(MetaBeanWrong2.class);
            Assert.fail();
        } catch (Exception ex) {
        }
        try {
            new BeanInjectionInfo(MetaBeanWrong3.class);
            Assert.fail();
        } catch (Exception ex) {
        }
        try {
            new BeanInjectionInfo(MetaBeanWrong4.class);
            Assert.fail();
        } catch (Exception ex) {
        }
        try {
            new BeanInjectionInfo(MetaBeanWrong5.class);
            Assert.fail();
        } catch (Exception ex) {
        }
        try {
            new BeanInjectionInfo(MetaBeanWrong6.class);
            Assert.fail();
        } catch (Exception ex) {
        }
        try {
            new BeanInjectionInfo(MetaBeanWrong7.class);
            Assert.fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGenerics() throws Exception {
        BeanInjectionInfo ri = new BeanInjectionInfo(MetaBeanChild.class);
        Assert.assertTrue(((ri.getProperties().size()) == 7));
        Assert.assertTrue(ri.getProperties().containsKey("BASE_ITEM_NAME"));
        Assert.assertTrue(ri.getProperties().containsKey("ITEM_CHILD_NAME"));
        Assert.assertTrue(ri.getProperties().containsKey("A"));
        Assert.assertTrue(ri.getProperties().containsKey("ITEM.BASE_ITEM_NAME"));
        Assert.assertTrue(ri.getProperties().containsKey("ITEM.ITEM_CHILD_NAME"));
        Assert.assertTrue(ri.getProperties().containsKey("SUB.BASE_ITEM_NAME"));
        Assert.assertTrue(ri.getProperties().containsKey("SUB.ITEM_CHILD_NAME"));
        Assert.assertEquals(String.class, ri.getProperties().get("A").getPropertyClass());
    }

    private static interface MetadataInterface {}

    @InjectionSupported(localizationPrefix = "", groups = "COMPLEX")
    public static class MetadataBean {
        @InjectionDeep
        private MetaAnnotationInjectionTest.ComplexField[] complexField;

        public MetaAnnotationInjectionTest.ComplexField[] getComplexField() {
            return complexField;
        }

        public void setComplexField(MetaAnnotationInjectionTest.ComplexField[] complexField) {
            this.complexField = complexField;
        }
    }

    public static class ComplexField {
        @Injection(name = "COMPLEX_NAME", group = "COMPLEX")
        private String fieldName;

        private final MetaAnnotationInjectionTest.MetadataBean parentMeta;

        public ComplexField(MetaAnnotationInjectionTest.MetadataBean parentMeta) {
            this.parentMeta = parentMeta;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public MetaAnnotationInjectionTest.MetadataBean getParentMeta() {
            return parentMeta;
        }
    }

    @InjectionSupported(localizationPrefix = "", groups = "COMPLEX")
    public static class MetadataBeanImplementsInterface implements MetaAnnotationInjectionTest.MetadataInterface {
        @InjectionDeep
        private MetaAnnotationInjectionTest.ComplexFieldWithInterfaceArg[] complexField;

        public MetaAnnotationInjectionTest.ComplexFieldWithInterfaceArg[] getComplexField() {
            return complexField;
        }

        public void setComplexField(MetaAnnotationInjectionTest.ComplexFieldWithInterfaceArg[] complexField) {
            this.complexField = complexField;
        }
    }

    public static class ComplexFieldWithInterfaceArg {
        @Injection(name = "COMPLEX_NAME", group = "COMPLEX")
        private String fieldName;

        private final MetaAnnotationInjectionTest.MetadataInterface parentMeta;

        public ComplexFieldWithInterfaceArg(MetaAnnotationInjectionTest.MetadataInterface parentMeta) {
            this.parentMeta = parentMeta;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public MetaAnnotationInjectionTest.MetadataInterface getParentMeta() {
            return parentMeta;
        }
    }
}

