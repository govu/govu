/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module.file;

import com.govu.engine.render.Renderer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit <mecevit@gmail.com>
 */
public class ReadFile extends BaseFunction {
    
    private Renderer renderer;

    public ReadFile(Renderer renderer) {
        this.renderer = renderer;
    }
    
    
    
    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        try {
            return FileUtils.readFileToString(new File(renderer.getApp().getAbsolutePath()+"/"  + args[0]), "UTF-8");
        } catch (FileNotFoundException ex) {
            cx.evaluateString(scope, "throw { error: \"fileNotFound\", msg: \""+ ex.getMessage() +"\" };", "<cmd>", 0, null);
        } catch (IOException ex) {
            cx.evaluateString(scope, "throw { error: \"ioError\", msg: \""+ ex.getMessage() +"\" };", "<cmd>", 0, null);
        }
        return super.call(cx, scope, thisObj, args); //To change body of generated methods, choose Tools | Templates.
    }
}

