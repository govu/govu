/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.application;

import com.db4o.collections.ActivatableHashMap;
import com.govu.Govu;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.jboss.netty.handler.codec.http.Cookie;

/**
 *
 * @author Mehmet Ecevit
 */
public class WebApplication {

    private String name;
    private String rootPath;
    private String domain;
    private HashMap<String, HashMap<String, Object>> sessionContainer = new ActivatableHashMap<>();
    private Set<HttpCookie> newCookies;
    private Set<Cookie> cookies;

    public WebApplication(String name, String rootPath, String domain) {
        this.name = name;
        this.rootPath = rootPath;
        this.domain = domain;
        this.newCookies = new HashSet<>();
    }
    
    public void setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    private String getSessionID() {
        String sessionID = getCookie("SESSIONID");
        if (sessionID == null) {
            sessionID = UUID.randomUUID().toString().replace("-", "");
            setCookie("SESSIONID", sessionID, 0L);
        }
        return sessionID;
    }

    public void setCookie(String key, String value, Long expireOn) {
        HttpCookie cookie = new HttpCookie(key, value);
        cookie.setMaxAge(expireOn);
        newCookies.add(cookie);
    }

    public Set<HttpCookie> getCookieEncoder() {
        return newCookies;
    }

    public String getCookie(String name) {
        for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
            Cookie cookie = it.next();
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        for (Iterator<HttpCookie> it = newCookies.iterator(); it.hasNext();) {
            HttpCookie httpCookie = it.next();
            if (httpCookie.getName().equals(name)) {
                return httpCookie.getValue();
            }
        }

        return null;
    }

    public void setSession(String key, Object value) {
        if (!sessionContainer.containsKey(getSessionID())) {
            sessionContainer.put(getSessionID(), new ActivatableHashMap<String, Object>());
        }
        sessionContainer.get(getSessionID()).put(key, value);
    }

    public Object getSession(String key) {
        if (sessionContainer.containsKey(getSessionID())) {
            return sessionContainer.get(getSessionID()).get(key);
        }
        return null;
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
        String relativePath;
        if (getDomain() != null) {
            relativePath = path;
        } else {
            relativePath = path.substring(getRootPath().length());
            if (!relativePath.startsWith("/")) {
                relativePath = "/" + relativePath;
            }
        }
        return relativePath;
    }
}