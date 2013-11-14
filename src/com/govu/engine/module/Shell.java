/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module;

import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit
 */
public class Shell extends BaseFunction {

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        try {
            Process p = Runtime.getRuntime().exec(args[0].toString());
            p.waitFor();
            StringWriter writer = new StringWriter();
            IOUtils.copy(p.getInputStream(), writer);
            return writer.toString();
        } catch (IOException | InterruptedException ex) {
            cx.evaluateString(scope, "throw { error: \"shellError\", msg: \""+ ex.getMessage().replace("\"", "\\\"") +"\" };", "<cmd>", 0, null);
        }
        return super.call(cx, scope, thisObj, args);
    }
}
