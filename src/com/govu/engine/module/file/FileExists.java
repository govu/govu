/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module.file;

import com.govu.engine.module.Use;
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
 * @author Mehmet Ecevit <mecevit@gmail.com>
 */
public class FileExists extends BaseFunction {

    private Renderer renderer;

    public FileExists(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return new File(renderer.getApp().getAbsolutePath()+"/" + args[0]).exists();
    }
}
