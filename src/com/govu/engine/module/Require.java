/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module;

import com.govu.engine.render.Renderer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit
 */
public class Require extends BaseFunction {
    
    private Renderer renderer;
    
    public Require(Renderer renderer) {
        this.renderer = renderer;
    }
    
    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        try {
            String code = FileUtils.readFileToString(new File( renderer.getApp().getAbsolutePath() +"/" + args[0]),"UTF-8");
            cx.evaluateString(scope, code , "<cmd>", 0, null);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Use.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Use.class.getName()).log(Level.SEVERE, null, ex);
        }
        return super.call(cx, scope, thisObj, args); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getFunctionName() {
        return "require";
    }
}
