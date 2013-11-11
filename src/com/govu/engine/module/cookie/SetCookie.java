/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module.cookie;

import com.govu.engine.render.Renderer;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit
 */
public class SetCookie extends BaseFunction {

    private Renderer renderer;

    public SetCookie(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        renderer.setCookie(args[0].toString(),args[1].toString(),args.length >2 ? (Long)args[2] : 0);
        return super.call(cx, scope, thisObj, args);
    }

    
}
