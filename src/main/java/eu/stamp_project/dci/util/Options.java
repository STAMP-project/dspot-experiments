package eu.stamp_project.dci.util;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 31/08/18
 */
public interface Options {

    public JSAP initJSAP() throws JSAPException;

}
