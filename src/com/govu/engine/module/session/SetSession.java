/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module.session;

import com.govu.engine.render.Renderer;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit
 */
public class SetSession extends BaseFunction {

    private Renderer renderer;

    public SetSession(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        renderer.setSession(args[0].toString(),args[1]);
        return super.call(cx, scope, thisObj, args);
    }

    
}
