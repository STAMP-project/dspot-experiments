package imageeditlibrary.editimage;


import Bitmap.Config.ARGB_8888;
import EditCache.EDIT_CACHE_SIZE;
import EditCache.ListModify;
import android.graphics.Bitmap;
import com.xinlan.imageeditlibrary.editimage.widget.EditCache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by panyi on 2017/11/15.
 */
// @RunWith(JUnit4.class)
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EditCacheTest {
    private EditCache editCache;

    int flag1 = 100;

    int flag2 = 100;

    @Test
    public void testEditCacheSizeDefault() throws Exception {
        Assert.assertEquals(EDIT_CACHE_SIZE, editCache.getEditCacheSize());
    }

    @Test
    public void testEditCacheSize() throws Exception {
        int size = 100;
        EditCache cache = new EditCache(size);
        Assert.assertEquals(size, cache.getEditCacheSize());
    }

    // @Test
    // public void testEditCachePushSize() throws Exception {
    // Bitmap bit1 = createTestBitmap();
    // Bitmap bit2 = createTestBitmap();
    // Bitmap bit3 = createTestBitmap();
    // Bitmap bit4 = createTestBitmap();
    // Bitmap bit5 = createTestBitmap();
    // editCache.push(bit1);
    // editCache.push(bit2);
    // editCache.push(bit3);
    // editCache.push(bit4);
    // editCache.push(bit5);
    // assertEquals(editCache.getEditCacheSize(), editCache.getSize());
    // }
    @Test
    public void test_EditCache_PushSame() throws Exception {
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        Bitmap bit3 = createTestBitmap();
        Bitmap bit4 = createTestBitmap();
        Bitmap bit5 = createTestBitmap();
        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);
        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);
        // assertEquals(bit5 , );
        Assert.assertEquals(5, editCache.getSize());
    }

    @Test
    public void test_EditCache_Pop3() throws Exception {
        prepareCache();
        Bitmap insertBit1 = Bitmap.createBitmap(1024, 500, ARGB_8888);
        editCache.push(insertBit1);
        Bitmap insertBit2 = Bitmap.createBitmap(1024, 500, ARGB_8888);
        editCache.push(insertBit2);
        Assert.assertEquals(editCache.getEditCacheSize(), editCache.getSize());
    }

    @Test
    public void test_EditCache_PushSame2() throws Exception {
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        editCache.push(bit1);
        editCache.push(bit2);
        // assertEquals(bit2, editCache.pop());
        Assert.assertEquals(2, editCache.getSize());
    }

    @Test
    public void test_EditCache_removeAll() throws Exception {
        prepare();
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        Bitmap bit3 = createTestBitmap();
        Bitmap bit4 = createTestBitmap();
        Bitmap bit5 = createTestBitmap();
        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);
        editCache.removeAll();
        Assert.assertEquals(0, editCache.getSize());
        Assert.assertEquals(bit1.isRecycled(), true);
        Assert.assertEquals(bit2.isRecycled(), true);
        Assert.assertEquals(bit3.isRecycled(), true);
        Assert.assertEquals(bit4.isRecycled(), true);
        Assert.assertEquals(bit5.isRecycled(), true);
    }

    @Test
    public void test_EditCache_get_next_and_pre_Bitmap() throws Exception {
        Bitmap bit1 = createTestBitmap();
        Bitmap bit2 = createTestBitmap();
        Bitmap bit3 = createTestBitmap();
        Bitmap bit4 = createTestBitmap();
        Bitmap bit5 = createTestBitmap();
        Bitmap bit6 = createTestBitmap();
        Bitmap bit7 = createTestBitmap();
        Bitmap bit8 = createTestBitmap();
        Bitmap bit9 = createTestBitmap();
        Bitmap bit10 = createTestBitmap();
        Bitmap bit11 = createTestBitmap();
        Bitmap bit12 = createTestBitmap();
        Bitmap bit13 = createTestBitmap();
        Bitmap bit14 = createTestBitmap();
        Bitmap bit15 = createTestBitmap();
        editCache.push(bit1);
        editCache.push(bit2);
        editCache.push(bit3);
        editCache.push(bit4);
        editCache.push(bit5);
        editCache.push(bit6);
        editCache.push(bit7);
        editCache.push(bit8);
        editCache.push(bit9);
        editCache.push(bit10);
        editCache.push(bit11);
        editCache.push(bit12);
        editCache.push(bit13);
        editCache.push(bit14);
        editCache.push(bit15);
    }

    @Test
    public void test_EditCache_isPointTo() {
        Assert.assertEquals(true, editCache.isPointToLastElem());
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Assert.assertEquals(true, editCache.isPointToLastElem());
        editCache.push(createTestBitmap());
        Assert.assertEquals(true, editCache.isPointToLastElem());
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Assert.assertEquals(true, editCache.isPointToLastElem());
    }

    @Test
    public void test_EditCache_isPointLast_First() {
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);
        // editCache.push(bit1);
        Assert.assertEquals(true, editCache.isPointToLastElem());
    }

    @Test
    public void test_EditCache_Current_Point1() {
        prepareCache();
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);
        Assert.assertEquals(bit3, editCache.getNextCurrentBit());
        Assert.assertEquals(bit2, editCache.getNextCurrentBit());
        Assert.assertEquals(bit1, editCache.getNextCurrentBit());
        // editCache.getPreCurrentBit();
        Assert.assertEquals(bit2, editCache.getPreCurrentBit());
        Assert.assertEquals(bit3, editCache.getPreCurrentBit());
        Assert.assertEquals(bit4, editCache.getPreCurrentBit());
    }

    @Test
    public void test_EditCache_Current_Point2() {
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);
        Assert.assertEquals(bit3, editCache.getNextCurrentBit());
        Assert.assertEquals(bit2, editCache.getNextCurrentBit());
        Assert.assertEquals(bit1, editCache.getNextCurrentBit());
    }

    @Test
    public void test_EditCache_Current_Point3() {
        int size = 5;
        prepareCache(size);
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);
        Assert.assertEquals(bit4, editCache.getCurBit());
        Assert.assertEquals(bit3, editCache.getNextCurrentBit());
        Assert.assertEquals(bit2, editCache.getNextCurrentBit());
        Assert.assertEquals(bit1, editCache.getNextCurrentBit());
        editCache.getNextCurrentBit();
        Bitmap bit = createTestBitmap();
        editCache.push(bit);
        Assert.assertEquals(size, editCache.getCur());
        Assert.assertEquals(bit, editCache.getCurBit());
    }

    @Test
    public void test_EditCache_Current_Point4() {
        int size = 5;
        prepareCache(size);
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);
        Assert.assertEquals(bit4, editCache.getCurBit());
        Assert.assertEquals(bit3, editCache.getNextCurrentBit());
        Assert.assertEquals(bit2, editCache.getNextCurrentBit());
        Assert.assertEquals(bit1, editCache.getNextCurrentBit());
        Assert.assertEquals(size, editCache.getCur());
    }

    @Test
    public void test_EditCache_Current_Point5() {
        int size = 10;
        prepareCache(size);
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Bitmap bit2 = createTestBitmap();
        editCache.push(bit2);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit3);
        Bitmap bit4 = createTestBitmap();
        editCache.push(bit4);
        Bitmap bit5 = createTestBitmap();
        editCache.push(bit5);
        Assert.assertEquals(bit4, editCache.getNextCurrentBit());
        Assert.assertEquals(bit3, editCache.getNextCurrentBit());
        Assert.assertEquals(bit2, editCache.getNextCurrentBit());
        Assert.assertEquals(bit1, editCache.getNextCurrentBit());
        editCache.getNextCurrentBit();
        Bitmap bit6 = createTestBitmap();
        editCache.push(bit6);
        Assert.assertEquals(bit6, editCache.getCurBit());
        Assert.assertEquals(true, bit1.isRecycled());
        Assert.assertEquals(true, bit2.isRecycled());
        Assert.assertEquals(true, bit3.isRecycled());
        Assert.assertEquals(true, bit4.isRecycled());
        Assert.assertEquals(6, editCache.getSize());
    }

    @Test
    public void test_EditCache_observer1() {
        // prepareCache(10);
        EditCache.ListModify modify1 = new EditCache.ListModify() {
            @Override
            public void onCacheListChange(EditCache cache) {
                // assertEquals();
                flag1 = 200;
            }
        };
        EditCache.ListModify modify2 = new EditCache.ListModify() {
            @Override
            public void onCacheListChange(EditCache cache) {
                flag2 = 200;
            }
        };
        editCache.addObserver(modify1);
        editCache.addObserver(modify2);
        editCache.addObserver(modify1);
        Bitmap bit1 = createTestBitmap();
        editCache.push(bit1);
        Assert.assertEquals(200, flag1);
        Assert.assertEquals(200, flag2);
        flag1 = 100;
        flag2 = 100;
        // editCache.removeObserver(modify1);
        Bitmap b = editCache.getNextCurrentBit();
        Assert.assertEquals(200, flag1);
        Assert.assertEquals(200, flag2);
        flag1 = 300;
        flag2 = 300;
        editCache.getPreCurrentBit();
        Assert.assertEquals(200, flag1);
        Assert.assertEquals(200, flag2);
        flag1 = 400;
        flag2 = 400;
        editCache.removeObserver(modify1);
        Bitmap bit3 = createTestBitmap();
        editCache.push(bit1);
        Assert.assertEquals(400, flag1);
        Assert.assertEquals(200, flag2);
    }

    @Test
    public void test_EditCache_observer2() {
        flag1 = 100;
        flag2 = 100;
        EditCache.ListModify modify1 = new EditCache.ListModify() {
            @Override
            public void onCacheListChange(EditCache cache) {
                // assertEquals();
                flag1 = 200;
            }
        };
        EditCache.ListModify modify2 = new EditCache.ListModify() {
            @Override
            public void onCacheListChange(EditCache cache) {
                flag2 = 200;
            }
        };
        editCache.addObserver(modify1);
        editCache.addObserver(modify2);
        flag1 = 100;
        flag2 = 100;
        prepareCache(10);
        Assert.assertEquals(200, flag1);
        Assert.assertEquals(200, flag2);
        editCache.removeObserver(modify1);
        editCache.removeObserver(modify2);
        flag1 = 2;
        flag2 = 2;
        editCache.getNextCurrentBit();
        editCache.getPreCurrentBit();
        Assert.assertEquals(2, flag1);
        Assert.assertEquals(2, flag2);
    }

    @Test
    public void test_check() {
        Assert.assertEquals(false, editCache.checkNextBitExist());
        Assert.assertEquals(false, editCache.checkPreBitExist());
        prepareCache(3);
        Assert.assertEquals(true, editCache.checkNextBitExist());
        Assert.assertEquals(false, editCache.checkPreBitExist());
        Assert.assertEquals(true, editCache.checkNextBitExist());
        editCache.getNextCurrentBit();
        Assert.assertEquals(true, editCache.checkPreBitExist());
        Assert.assertEquals(true, editCache.checkNextBitExist());
        editCache.getNextCurrentBit();
        Assert.assertEquals(false, editCache.checkNextBitExist());
        editCache.getNextCurrentBit();
        prepareCache(1);
        Assert.assertEquals(false, editCache.checkPreBitExist());
        Assert.assertEquals(false, editCache.checkNextBitExist());
    }
}

