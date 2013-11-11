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
public class GetSession extends BaseFunction {

    private Renderer renderer;

    public GetSession(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return renderer.getSession(args[0].toString());
    }

    
}
