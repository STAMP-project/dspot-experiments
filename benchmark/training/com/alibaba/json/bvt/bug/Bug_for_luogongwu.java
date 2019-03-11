package com.alibaba.json.bvt.bug;


import SerializeConfig.globalInstance;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/06/2017.
 */
public class Bug_for_luogongwu extends TestCase {
    public void test_for_issue() throws Exception {
        List<Bug_for_luogongwu.IflowItemImage> imageList = new ArrayList<Bug_for_luogongwu.IflowItemImage>();
        Bug_for_luogongwu.IflowItemImage image = new Bug_for_luogongwu.IflowItemImage();
        image.id = "72c7275c6b";
        imageList.add(image);
        imageList = new ArrayList();
        image = new Bug_for_luogongwu.IflowItemImage();
        image.id = "72c7275c6c";
        imageList.add(image);
        // force ASM
        boolean asm = globalInstance.isAsmEnable();
        globalInstance.setAsmEnable(true);
        // Test ASM
        Bug_for_luogongwu.Foo foo = new Bug_for_luogongwu.Foo();
        foo.thumbnails = imageList;
        String jsonString = JSON.toJSONString(foo);
        System.out.println(jsonString);
        Bug_for_luogongwu.Foo foo1 = JSON.parseObject(jsonString, Bug_for_luogongwu.Foo.class);
        TestCase.assertEquals(1, foo1.thumbnails.size());
        TestCase.assertNotNull(foo1.thumbnails.get(0));
        TestCase.assertSame(foo1.getThumbnail(), foo1.thumbnails.get(0));
        // test Not ASM
        globalInstance.setAsmEnable(false);
        Bug_for_luogongwu.FooNotAsm fooNotAsm = new Bug_for_luogongwu.FooNotAsm();
        fooNotAsm.thumbnails = imageList;
        jsonString = JSON.toJSONString(foo);
        System.out.println(jsonString);
        Bug_for_luogongwu.FooNotAsm fooNotAsm1 = JSON.parseObject(jsonString, Bug_for_luogongwu.FooNotAsm.class);
        TestCase.assertEquals(1, fooNotAsm1.thumbnails.size());
        TestCase.assertNotNull(fooNotAsm1.thumbnails.get(0));
        TestCase.assertSame(fooNotAsm1.getThumbnail(), fooNotAsm1.thumbnails.get(0));
        // restore
        globalInstance.setAsmEnable(asm);
    }

    @JSONType(asm = false)
    public static class FooNotAsm {
        @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
        public List<Bug_for_luogongwu.IflowItemImage> thumbnails;

        public Bug_for_luogongwu.IflowItemImage getThumbnail() {
            return ((thumbnails) != null) && ((thumbnails.size()) > 0) ? thumbnails.get(0) : null;
        }
    }

    public static class Foo {
        @JSONField(serialzeFeatures = SerializerFeature.DisableCircularReferenceDetect)
        public List<Bug_for_luogongwu.IflowItemImage> thumbnails;

        public Bug_for_luogongwu.IflowItemImage getThumbnail() {
            return ((thumbnails) != null) && ((thumbnails.size()) > 0) ? thumbnails.get(0) : null;
        }
    }

    public static class IflowItemImage {
        public String id;
    }
}

