package pl.droidsonroids.gif;


import Drawable.Callback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.droidsonroids.gif.MultiCallback.CallbackWeakReference;


@RunWith(MockitoJUnitRunner.class)
public class CallbackWeakReferenceTest {
    @Mock
    Callback callback;

    @Mock
    Callback anotherCallback;

    @Test
    public void testEquals() throws Exception {
        final CallbackWeakReference reference = new CallbackWeakReference(callback);
        final CallbackWeakReference anotherReference = new CallbackWeakReference(callback);
        assertThat(reference).isEqualTo(anotherReference);
    }

    @Test
    public void testNotEqualReferences() throws Exception {
        final CallbackWeakReference reference = new CallbackWeakReference(callback);
        final CallbackWeakReference anotherReference = new CallbackWeakReference(anotherCallback);
        assertThat(reference).isNotEqualTo(anotherReference);
    }

    @Test
    public void testNotEqualDifferentObjects() throws Exception {
        final CallbackWeakReference reference = new CallbackWeakReference(callback);
        assertThat(reference).isNotEqualTo(null);
        assertThat(reference).isNotEqualTo(callback);
    }

    @Test
    public void testHashCode() throws Exception {
        final CallbackWeakReference reference = new CallbackWeakReference(callback);
        final CallbackWeakReference anotherReference = new CallbackWeakReference(callback);
        assertThat(reference.hashCode()).isEqualTo(anotherReference.hashCode());
    }

    @Test
    public void testHashCodeNull() throws Exception {
        final CallbackWeakReference reference = new CallbackWeakReference(callback);
        final CallbackWeakReference anotherReference = new CallbackWeakReference(null);
        assertThat(reference.hashCode()).isNotEqualTo(anotherReference.hashCode());
    }
}

