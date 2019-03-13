/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.core.injection.bean;


import BeanInjectionInfo.Property;
import BeanLevelInfo.DIMENSION;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.injection.MetaBeanLevel1;
import org.pentaho.di.core.injection.MetaBeanLevel2;


public class BeanInjectorTest {
    @Test
    public void allocateCollectionField_List() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        // should set other field based on this size
        mbl1.getSub().setFilenames(new String[]{ "one", "two", "three", "four" });
        Assert.assertNull(mbl1.getSub().getAscending());
        bi.allocateCollectionField(mbl1.getSub(), bii, "ASCENDING_LIST");
        Assert.assertEquals(4, mbl1.getSub().getAscending().size());
    }

    @Test
    public void allocateCollectionField_Array() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        // should set other field based on this size
        mbl1.getSub().setAscending(Arrays.asList(new Boolean[]{ true, false }));
        Assert.assertNull(mbl1.getSub().getFilenames());
        bi.allocateCollectionField(mbl1.getSub(), bii, "FILENAME_ARRAY");
        Assert.assertEquals(2, mbl1.getSub().getFilenames().length);
    }

    @Test
    public void allocateCollectionField_NonCollection() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        // sizes of other fields shouldn't change
        Assert.assertNull(mbl1.getSub().getFilenames());
        Assert.assertNull(mbl1.getSub().getAscending());
        bi.allocateCollectionField(mbl1.getSub(), bii, "SEPARATOR");
        Assert.assertNull(mbl1.getSub().getFilenames());
        Assert.assertNull(mbl1.getSub().getAscending());
    }

    @Test
    public void allocateCollectionField_Property_Array_IntiallyNull() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        BeanInjectionInfo.Property arrayProperty = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("FILENAME_ARRAY")).findFirst().orElse(null);
        Assert.assertNull(mbl1.getSub().getFilenames());
        bi.allocateCollectionField(arrayProperty, mbl1.getSub(), 7);
        Assert.assertEquals(7, mbl1.getSub().getFilenames().length);
    }

    @Test
    public void allocateCollectionField_Property_Array_IntiallyEmpty() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setFilenames(/* empty on purpose */
        new String[]{  }/* empty on purpose */
        );
        BeanInjectionInfo.Property arrayProperty = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("FILENAME_ARRAY")).findFirst().orElse(null);
        Assert.assertEquals(0, mbl1.getSub().getFilenames().length);
        bi.allocateCollectionField(arrayProperty, mbl1.getSub(), 7);
        Assert.assertEquals(7, mbl1.getSub().getFilenames().length);
    }

    @Test
    public void allocateCollectionField_Property_List_IntiallyNull() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        BeanInjectionInfo.Property listProperty = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("ASCENDING_LIST")).findFirst().orElse(null);
        Assert.assertNull(mbl1.getSub().getAscending());
        bi.allocateCollectionField(listProperty, mbl1.getSub(), 6);
        Assert.assertEquals(6, mbl1.getSub().getAscending().size());
    }

    @Test
    public void allocateCollectionField_Property_List_IntiallyEmpty() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setAscending(new ArrayList<>());
        BeanInjectionInfo.Property listProperty = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("ASCENDING_LIST")).findFirst().orElse(null);
        Assert.assertEquals(0, mbl1.getSub().getAscending().size());
        bi.allocateCollectionField(listProperty, mbl1.getSub(), 6);
        Assert.assertEquals(6, mbl1.getSub().getAscending().size());
    }

    @Test
    public void isCollection_True() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        BeanInjectionInfo.Property collectionProperty = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("FILENAME_ARRAY")).findFirst().orElse(null);
        Assert.assertTrue(bi.isCollection(collectionProperty));
    }

    @Test
    public void isCollection_False() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        BeanInjectionInfo.Property seperatorProperty = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("SEPARATOR")).findFirst().orElse(null);
        Assert.assertFalse(bi.isCollection(seperatorProperty));
    }

    @Test
    public void isCollection_BeanLevelInfo() {
        BeanInjector bi = new BeanInjector(null);
        BeanLevelInfo bli_list = new BeanLevelInfo();
        bli_list.dim = DIMENSION.LIST;
        Assert.assertTrue(bi.isCollection(bli_list));
        BeanLevelInfo bli_array = new BeanLevelInfo();
        bli_array.dim = DIMENSION.ARRAY;
        Assert.assertTrue(bi.isCollection(bli_array));
        BeanLevelInfo bli_none = new BeanLevelInfo();
        bli_list.dim = DIMENSION.NONE;
        Assert.assertFalse(bi.isCollection(bli_none));
    }

    @Test
    public void isArray() {
        BeanInjector bi = new BeanInjector(null);
        BeanLevelInfo bli_list = new BeanLevelInfo();
        bli_list.dim = DIMENSION.LIST;
        Assert.assertFalse(bi.isArray(bli_list));
        BeanLevelInfo bli_array = new BeanLevelInfo();
        bli_array.dim = DIMENSION.ARRAY;
        Assert.assertTrue(bi.isArray(bli_array));
        BeanLevelInfo bli_none = new BeanLevelInfo();
        bli_list.dim = DIMENSION.NONE;
        Assert.assertFalse(bi.isArray(bli_none));
    }

    @Test
    public void isList() {
        BeanInjector bi = new BeanInjector(null);
        BeanLevelInfo bli_list = new BeanLevelInfo();
        bli_list.dim = DIMENSION.LIST;
        Assert.assertTrue(bi.isList(bli_list));
        BeanLevelInfo bli_array = new BeanLevelInfo();
        bli_array.dim = DIMENSION.ARRAY;
        Assert.assertFalse(bi.isList(bli_array));
        BeanLevelInfo bli_none = new BeanLevelInfo();
        bli_list.dim = DIMENSION.NONE;
        Assert.assertFalse(bi.isList(bli_none));
    }

    @Test
    public void getFinalPath_Null() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        BeanInjectionInfo.Property noPathProperty = bii.new Property("name", "groupName", Collections.EMPTY_LIST);
        Assert.assertNull(bi.getFinalPath(noPathProperty));
    }

    @Test
    public void getFinalPath_Found() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        BeanInjectionInfo.Property seperatorProperty = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("SEPARATOR")).findFirst().orElse(null);
        Assert.assertEquals(seperatorProperty.getPath().get(2), bi.getFinalPath(seperatorProperty));
    }

    @Test
    public void getProperty_Found() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        BeanInjectionInfo.Property actualProperty = bi.getProperty(bii, "SEPARATOR");
        Assert.assertNotNull(actualProperty);
        Assert.assertEquals("SEPARATOR", actualProperty.getName());
    }

    @Test
    public void getProperty_NotFound() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        BeanInjectionInfo.Property actualProperty = bi.getProperty(bii, "DOES_NOT_EXIST");
        Assert.assertNull(actualProperty);
    }

    @Test
    public void getGroupProperties_NonEmptyGroup() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setFilenames(new String[]{ "file1", "file2", "file3" });
        mbl1.getSub().setAscending(Arrays.asList(new Boolean[]{ true, false, false, true }));
        mbl1.getSub().setSeparator("/");
        List<BeanInjectionInfo.Property> actualProperties = bi.getGroupProperties(bii, "FILENAME_LINES2");
        Assert.assertNotNull(actualProperties);
        Assert.assertEquals(2, actualProperties.size());
        Assert.assertNotNull(bii.getProperties().values().stream().filter(( p) -> p.getName().equals("ASCENDING_LIST")));
        Assert.assertNotNull(bii.getProperties().values().stream().filter(( p) -> p.getName().equals("FILENAME_ARRAY")));
    }

    @Test
    public void getGroupProperties_GroupDoesNotExist() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setFilenames(new String[]{ "file1", "file2", "file3" });
        mbl1.getSub().setAscending(Arrays.asList(new Boolean[]{ true, false, false, true }));
        mbl1.getSub().setSeparator("/");
        List<BeanInjectionInfo.Property> actualProperties = bi.getGroupProperties(bii, "GLOBAL_DOES_NOT_EXIST");
        Assert.assertNotNull(actualProperties);
        Assert.assertEquals(0, actualProperties.size());
    }

    @Test
    public void getMaxSize_Collections() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setFilenames(new String[]{ "file1", "file2", "file3" });
        mbl1.getSub().setAscending(Arrays.asList(new Boolean[]{ true, false, false, true }));
        mbl1.getSub().setSeparator("/");
        Assert.assertEquals(new Integer(4), bi.getMaxSize(bii.getProperties().values(), mbl1.getSub()));
    }

    @Test
    public void getMaxSize_OnlyOneField() {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setSeparator("/");
        Assert.assertEquals(new Integer(1), bi.getMaxSize(bii.getProperties().values(), mbl1.getSub()));
    }

    @Test
    public void getCollectionSize_Property_Array() throws Exception {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setFilenames(new String[]{ "file1", "file2", "file3" });
        BeanInjectionInfo.Property property = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("FILENAME_ARRAY")).findFirst().orElse(null);
        Assert.assertEquals(3, bi.getCollectionSize(property, mbl1.getSub()));
    }

    @Test
    public void getCollectionSize_Property_List() throws Exception {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setAscending(Arrays.asList(new Boolean[]{ true, false, false, true }));
        BeanInjectionInfo.Property property = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("ASCENDING_LIST")).findFirst().orElse(null);
        Assert.assertEquals(4, bi.getCollectionSize(property, mbl1.getSub()));
    }

    @Test
    public void getCollectionSize_BeanLevelInfo_Exception() throws Exception {
        BeanInjector bi = new BeanInjector(null);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setAscending(Arrays.asList(new Boolean[]{ true, false, false, true }));
        BeanLevelInfo mockBeanLevelInfo = Mockito.mock(BeanLevelInfo.class);
        Mockito.doThrow(new RuntimeException("SOME ERROR")).when(mockBeanLevelInfo).getField();
        Assert.assertEquals((-1), bi.getCollectionSize(mockBeanLevelInfo, mbl1));
    }

    @Test
    public void getCollectionSize_Property_NonCollection() throws Exception {
        BeanInjector bi = new BeanInjector(null);
        BeanInjectionInfo bii = new BeanInjectionInfo(MetaBeanLevel1.class);
        MetaBeanLevel1 mbl1 = new MetaBeanLevel1();
        mbl1.setSub(new MetaBeanLevel2());
        mbl1.getSub().setAscending(Arrays.asList(new Boolean[]{ true, false, false, true }));
        BeanInjectionInfo.Property property = bii.getProperties().values().stream().filter(( p) -> p.getName().equals("SEPARATOR")).findFirst().orElse(null);
        Assert.assertEquals((-1), bi.getCollectionSize(property, mbl1.getSub()));
    }
}

