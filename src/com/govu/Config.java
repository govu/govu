package com.govu;

import static com.govu.Govu.PORT;
import static com.govu.Govu.apps;
import static com.govu.Govu.logger;
import static com.govu.Govu.root;
import static com.govu.Govu.webRoot;
import com.govu.application.WebApplication;
import com.govu.util.FileMonitor;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mehmet Ecevit
 */
public class Config implements FileMonitor.FileListener {

    private String configFile = root + "/";
    private FileMonitor fileMonitor;

    public Config() throws IOException, InterruptedException {
        fileMonitor = new FileMonitor(1000);
        fileMonitor.addListener(this);
        fileMonitor.addFile(new File(configFile + "govu.properties"));
    }

    public void readProperties() throws IOException {
        File propsFile = new File(configFile + "govu.properties");
        if (propsFile.exists()) {
            logger.info("Loading govu.properties");
            Properties props = new Properties();
            try (FileReader fileReader = new FileReader(propsFile)) {
                props.load(fileReader);

                if (props.containsKey("webRoot")) {
                    webRoot = props.getProperty("webRoot");
                }
                logger.debug("Webroot: " + webRoot);

                if (props.containsKey("port")) {
                    try {
                        PORT = Integer.parseInt(props.getProperty("port"));
                    } catch (Exception ex) {
                    }
                }

                //Read apps
                boolean hasApp = false;
                for (Iterator<Object> it = props.keySet().iterator(); it.hasNext();) {
                    String key = it.next().toString();
                    if (key.startsWith("web.") && key.endsWith(".path")) {
                        String name = key.substring(key.indexOf(".") + 1, key.lastIndexOf("."));
                        String path = props.getProperty(key);
                        String domain = props.getProperty("web." + name + ".domain");
                        WebApplication app = Govu.getWebApp(name);
                        if (app != null) {
                            if (!app.getRootPath().equals(path) || (app.getDomain() == null ? domain != null : !app.getDomain().equals(domain))) {
                                logger.info("> updating web application: " + name);
                                app.setDomain(domain);
                                app.setRootPath(path);
                            }
                        } else {
                            logger.info("> starting web application: " + name);
                            apps.add(new WebApplication(name, path, domain));
                        }
                        hasApp = true;
                    }
                }

                //Destroy removed apps
                Set<WebApplication> _apps = new HashSet<>(apps);
                Iterator<WebApplication> appItr = _apps.iterator();
                while (appItr.hasNext()) {
                    WebApplication app = appItr.next();
                    if (!props.containsKey("web." + app.getName() + ".path")) {
                        if ((app.getName().equals("base") && hasApp) || (!app.getName().equals("base"))) {
                            System.out.println("> removing web application:" + app.getName());
                            apps.remove(app);
                        }
                    }
                }

                if (apps.isEmpty()) {
                    apps.add(new WebApplication("base", "/", null));
                }
            }
        }
    }

    @Override
    public void fileChanged(File file) {
        try {
            readProperties();
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
