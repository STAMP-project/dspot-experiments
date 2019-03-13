package hex.deeplearning;


import SparseVector.Iterator;
import org.junit.Test;
import water.util.Log;


public class NeuronsTest {
    @Test
    public void sparseTester() {
        DenseVector dv = new DenseVector(20);
        dv.set(3, 0.21F);
        dv.set(7, 0.13F);
        dv.set(18, 0.14F);
        SparseVector sv = new SparseVector(dv);
        assert (sv.size()) == 20;
        assert (sv.nnz()) == 3;
        // dense treatment
        for (int i = 0; i < (sv.size()); ++i)
            Log.info(((("sparse [" + i) + "] = ") + (sv.get(i))));

        // sparse treatment
        for (SparseVector.Iterator it = sv.begin(); !(it.equals(sv.end())); it.next()) {
            // Log.info(it.toString());
            Log.info((((it.index()) + " -> ") + (it.value())));
        }
        DenseColMatrix dcm = new DenseColMatrix(3, 5);
        dcm.set(2, 1, 3.2F);
        dcm.set(1, 3, (-1.2F));
        assert (dcm.get(2, 1)) == 3.2F;
        assert (dcm.get(1, 3)) == (-1.2F);
        assert (dcm.get(0, 0)) == 0.0F;
        DenseRowMatrix drm = new DenseRowMatrix(3, 5);
        drm.set(2, 1, 3.2F);
        drm.set(1, 3, (-1.2F));
        assert (drm.get(2, 1)) == 3.2F;
        assert (drm.get(1, 3)) == (-1.2F);
        assert (drm.get(0, 0)) == 0.0F;
        SparseColMatrix scm = new SparseColMatrix(3, 5);
        scm.set(2, 1, 3.2F);
        scm.set(1, 3, (-1.2F));
        assert (scm.get(2, 1)) == 3.2F;
        assert (scm.get(1, 3)) == (-1.2F);
        assert (scm.get(0, 0)) == 0.0F;
        SparseRowMatrix srm = new SparseRowMatrix(3, 5);
        srm.set(2, 1, 3.2F);
        srm.set(1, 3, (-1.2F));
        assert (srm.get(2, 1)) == 3.2F;
        assert (srm.get(1, 3)) == (-1.2F);
        assert (srm.get(0, 0)) == 0.0F;
    }
}

