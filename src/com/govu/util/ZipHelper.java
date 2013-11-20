/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Mehmet Ecevit
 */
public class ZipHelper {

    public ZipHelper() {
    }

    public ByteArrayOutputStream zipDir(String dir) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ZipOutputStream zip = new ZipOutputStream(out);
        addDirectory("", zip, new File(dir));
        zip.close();

        return out;
    }

    private static void addDirectory(String root, ZipOutputStream zout, File fileSource) throws IOException {
        File[] files = fileSource.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                zout.putNextEntry(new ZipEntry(root + files[i].getName() + "/"));
                addDirectory(root + files[i].getName() + "/", zout, files[i]);
                continue;
            }
            byte[] buffer = new byte[1024];
            FileInputStream fin = new FileInputStream(files[i]);

            zout.putNextEntry(new ZipEntry(root + files[i].getName()));
            int length;

            while ((length = fin.read(buffer)) > 0) {
                zout.write(buffer, 0, length);
            }
            zout.closeEntry();
            fin.close();
        }

    }
}
