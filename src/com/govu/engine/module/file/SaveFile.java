/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.module.file;

import com.govu.engine.render.Renderer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaArray;
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

            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().mkdirs()) {
                        throw new IOException("Couldn't create directory:" + filePath);
                    }
                }

                if (!file.createNewFile()) {
                    throw new IOException("Couldn't create file:" + filePath);
                }
            }



            if (args[1].getClass() == String.class) {
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                output.write(args[1].toString());
                output.close();
            } else {
                byte[] bytes = (byte[]) Context.toType(args[1], byte[].class);
                FileOutputStream out = new FileOutputStream(file);
                out.write(bytes);
                out.close();
            }



        } catch (IOException ex) {
            cx.evaluateString(scope, "throw { error: \"ioError\", msg: \"" + ex.getMessage() + "\" };", "<cmd>", 0, null);
        }
        return super.call(cx, scope, thisObj, args); //To change body of generated methods, choose Tools | Templates.
    }
}
