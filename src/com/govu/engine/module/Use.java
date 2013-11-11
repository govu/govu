/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module;

import com.govu.Govu;
import com.govu.engine.render.Renderer;
import com.govu.httpserver.HttpServerHandler;
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
public class Use extends BaseFunction {

    private Renderer renderer;
    public Use(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        try {
            String code = FileUtils.readFileToString(new File(renderer.getApp().getAbsolutePath() + "/Model/" + args[0] + ".js"), "UTF-8");
            code += args[0] + ".prototype.save = function() { com.govu.engine.db.DB.saveObj(this,'" + args[0] + "') };";
            code += args[0] + ".prototype.delete = function() {com.govu.engine.db.DB.deleteObj(this,'" + args[0] + "') };";
            code += args[0] + ".getAll = function() { return com.govu.engine.db.DB.getAll("+ args[0] +",'" + args[0] + "'); };";
            code += args[0] + ".get = function(model) { return com.govu.engine.db.DB.find(model,'" + args[0] + "'); };";
            code += args[0] + ".search = function(model) { return com.govu.engine.db.DB.search(model,"+ args[0] +",'" + args[0] + "'); };";
            code += args[0] + ".delete = function(model) { return com.govu.engine.db.DB.delete(model,'" + args[0] + "'); };";
            cx.evaluateString(scope, code, "<cmd>", 0, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Use.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Use.class.getName()).log(Level.SEVERE, null, ex);
        }
        return super.call(cx, scope, thisObj, args); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFunctionName() {
        return "use";
    }
    
    
}
