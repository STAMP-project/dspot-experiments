package com.bumptech.glide.load.engine.cache;


import android.support.annotation.NonNull;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;
import com.bumptech.glide.tests.Util;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 18)
public class DiskLruCacheWrapperTest {
    private DiskCache cache;

    private byte[] data;

    private ObjectKey key;

    private File dir;

    @Test
    public void testCanInsertAndGet() throws IOException {
        cache.put(key, new DiskCache.Writer() {
            @Override
            public boolean write(@NonNull
            File file) {
                try {
                    Util.writeFile(file, data);
                } catch (IOException e) {
                    Assert.fail(e.toString());
                }
                return true;
            }
        });
        byte[] received = Util.readFile(cache.get(key), data.length);
        Assert.assertArrayEquals(data, received);
    }

    @Test
    public void testDoesNotCommitIfWriterReturnsFalse() {
        cache.put(key, new DiskCache.Writer() {
            @Override
            public boolean write(@NonNull
            File file) {
                return false;
            }
        });
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void testDoesNotCommitIfWriterWritesButReturnsFalse() {
        cache.put(key, new DiskCache.Writer() {
            @Override
            public boolean write(@NonNull
            File file) {
                try {
                    Util.writeFile(file, data);
                } catch (IOException e) {
                    Assert.fail(e.toString());
                }
                return false;
            }
        });
        Assert.assertNull(cache.get(key));
    }

    @Test
    public void testEditIsAbortedIfWriterThrows() throws IOException {
        try {
            cache.put(key, new DiskCache.Writer() {
                @Override
                public boolean write(@NonNull
                File file) {
                    throw new RuntimeException("test");
                }
            });
        } catch (RuntimeException e) {
            // Expected.
        }
        cache.put(key, new DiskCache.Writer() {
            @Override
            public boolean write(@NonNull
            File file) {
                try {
                    Util.writeFile(file, data);
                } catch (IOException e) {
                    Assert.fail(e.toString());
                }
                return true;
            }
        });
        byte[] received = Util.readFile(cache.get(key), data.length);
        Assert.assertArrayEquals(data, received);
    }

    // Tests #2465.
    @Test
    public void clearDiskCache_afterOpeningDiskCache_andDeleteDirectoryOutsideGlide_doesNotThrow() {
        Assume.assumeTrue("A file handle is likely open, so cannot delete dir", (!(Util.isWindows())));
        DiskCache cache = DiskLruCacheWrapper.create(dir, (1024 * 1024));
        cache.get(Mockito.mock(Key.class));
        DiskLruCacheWrapperTest.deleteRecursive(dir);
        cache.clear();
    }

    // Tests #2465.
    @Test
    public void get_afterDeleteDirectoryOutsideGlideAndClose_doesNotThrow() {
        Assume.assumeTrue("A file handle is likely open, so cannot delete dir", (!(Util.isWindows())));
        DiskCache cache = DiskLruCacheWrapper.create(dir, (1024 * 1024));
        cache.get(Mockito.mock(Key.class));
        DiskLruCacheWrapperTest.deleteRecursive(dir);
        cache.clear();
        cache.get(Mockito.mock(Key.class));
    }
}

