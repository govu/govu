/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.engine.render;

import com.db4o.collections.ActivatableHashMap;
import com.govu.application.WebApplication;
import com.govu.engine.module.Print;
import com.govu.engine.module.Redirect;
import com.govu.engine.module.Require;
import com.govu.engine.module.Shell;
import com.govu.engine.module.UniqueID;
import com.govu.engine.module.Use;
import com.govu.engine.module.cookie.GetCookie;
import com.govu.engine.module.cookie.SetCookie;
import com.govu.engine.module.file.FileExists;
import com.govu.engine.module.file.ReadFile;
import com.govu.engine.module.file.SaveFile;
import com.govu.engine.module.session.GetSession;
import com.govu.engine.module.session.SetSession;
import com.govu.engine.render.exception.ControllerNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.jboss.netty.handler.codec.http.Cookie;
import org.mozilla.javascript.Context;

/**
 *
 * @author Mehmet Ecevit
 */
public class Renderer {

    public String redirect;
    private StringWriter writer;
    private Set<HttpCookie> newCookies;
    private Set<Cookie> cookies;
    public static HashMap<String, HashMap<String, Object>> sessionContainer = new ActivatableHashMap<>();
    private WebApplication app;
    private String type;
    private String method;
    private HashMap<String, String> query;

    public Renderer() {
    }

    public Renderer(WebApplication app, String type, String method, HashMap<String, String> query, Set<Cookie> cookies)  {
        this.app = app;
        this.type=type;
        this.method = method;
        this.newCookies = new HashSet<>();
        this.writer = new StringWriter();
        this.cookies = cookies;
        this.query = query;
        
    }

    public void render() throws IOException, ControllerNotFoundException {
        File controllerFile = new File(app.getAbsolutePath()+ "/Controller/" + type + ".js");
        if (!controllerFile.exists()) {
            throw new ControllerNotFoundException(type);
        }
        File viewFile = new File(app.getAbsolutePath() + "/View/" + type + ".html");

        String controller = FileUtils.readFileToString(controllerFile, "UTF-8");
        String view = null;
        if (viewFile.exists()) {
            view = FileUtils.readFileToString(viewFile, "UTF-8");
        }

        String code = renderView(controller, view, method, query);

        Context cx = Context.enter();

        org.mozilla.javascript.Scriptable scope = cx.initStandardObjects();

        scope.put("use", scope, new Use(this));
        scope.put("print", scope, new Print(writer));
        scope.put("require", scope, new Require(this));
        scope.put("redirect", scope, new Redirect(this));
        scope.put("uniqueID", scope, new UniqueID());
        
        scope.put("setCookie", scope, new SetCookie(this));
        scope.put("getCookie", scope, new GetCookie(this));
        
        scope.put("setSession", scope, new SetSession(this));
        scope.put("getSession", scope, new GetSession(this));

        scope.put("readFile", scope, new ReadFile(this));
        scope.put("saveFile", scope, new SaveFile(this));
        scope.put("fileExists", scope, new FileExists(this));

        scope.put("shell", scope, new Shell());
        
        cx.evaluateString(scope, code.toString(), "<cmd>", 0, null);
        Context.exit();
    }
    
    public String escape(String code) {
        code = code.replaceAll("\r", "\\\\r");
        code = code.replaceAll("\n", "\\\\n");
        code = code.replaceAll("\"", "\\\\\"");
        return code;
    }

    public String getResponse() {
        return writer.toString();
    }

    private String renderView(String controller, String view, String method, HashMap<String, String> query) {
        int index = 0;
        int start = 0;

        StringBuilder code = new StringBuilder("");
        code.append(controller + "\r\n");

        for (Iterator<String> it = query.keySet().iterator(); it.hasNext();) {
            String parameter = it.next();
            code.append(parameter + "=\"" + query.get(parameter) + "\";");
        }

        code.append(method + "();");


        if (view != null) {
            code.append("\r\n");

            while (view.indexOf("<%", index) > -1) {
                start = index;
                index = view.indexOf("<%", index);
                code.append("print(\"");
                code.append(escape(view.substring(start, index)));
                code.append("\");");
                start = index;
                index = view.indexOf("%>", index);
                code.append(view.substring(start + 2, index));
                code.append("\r\n");
                index = index + 2;
            }

            if (index < view.length()) {
                code.append("print(\"");
                code.append(escape(view.substring(index)));
                code.append("\");");
            }
        }
        return code.toString();
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

    public WebApplication getApp() {
        return app;
    }

    
}
