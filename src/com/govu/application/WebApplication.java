/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.application;

import com.govu.Govu;

/**
 *
 * @author Mehmet Ecevit
 */
public class WebApplication {

    private String name;
    private String rootPath;
    private String domain;

    public WebApplication(String name, String rootPath, String domain) {
        this.name = name;
        this.rootPath = rootPath;
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getAbsolutePath() {
        return Govu.webRoot + getRootPath();
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

   

    public String getRelativePath(String path) {
        String relativePath = null;
        if (getDomain()!= null) {
            relativePath = path;
        } else {
            relativePath = path.substring(getRootPath().length());
            if (relativePath.equals("")) {
                relativePath = "/";
            }
        }
        return relativePath;
    }
}
