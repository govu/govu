/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module.file;

import com.govu.engine.render.Renderer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author Mehmet Ecevit <mecevit@gmail.com>
 */
public class SaveFile extends BaseFunction {

    private Renderer renderer;

    public SaveFile(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        try {
            String filePath = renderer.getApp().getAbsolutePath() + args[0].toString();
            String content = args[1].toString();
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        throw new IOException("Couldn't create directory:"+filePath);
                    }
                }
                    
                if (!file.createNewFile()) {
                    throw new IOException("Couldn't create file:"+filePath);
                }
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(content);
            output.close();
        } catch (IOException ex) {
            cx.evaluateString(scope, "throw { error: \"ioError\", msg: \"" + ex.getMessage() + "\" };", "<cmd>", 0, null);
        }
        return super.call(cx, scope, thisObj, args); //To change body of generated methods, choose Tools | Templates.
    }
}
