/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module;

import java.io.StringWriter;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit
 */
public class Print extends BaseFunction {

    private StringWriter writer;

    public Print(StringWriter writer) {
        this.writer = writer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args[0] == null) {
            writer.write("undefined");
        } else {
            writer.write(args[0].toString());
        }
        return super.call(cx, scope, thisObj, args); //To change body of generated methods, choose Tools | Templates.
    }
}
