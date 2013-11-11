/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module;

import com.govu.engine.render.Renderer;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit
 */
public class Redirect extends BaseFunction {

    private Renderer renderer;

    public Redirect(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        cx.evaluateString(scope, "throw { error: \"redirect\", path: \""+ renderer.escape(args[0].toString()) +"\" };", "<cmd>", 0, null);
        return super.call(cx, scope, thisObj, args);
    }

    @Override
    public String getFunctionName() {
        return "redirect";
    }
}
