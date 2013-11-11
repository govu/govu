/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module;

import java.io.StringWriter;
import java.util.UUID;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit
 */
public class UniqueID extends BaseFunction {
    public UniqueID() {
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
}
