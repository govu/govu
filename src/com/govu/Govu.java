/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu;

import com.govu.engine.db.DB;
import com.govu.engine.db.db4o.DB4OProvider;
import com.govu.httpserver.HttpServerPipelineFactory;
import com.govu.application.WebApplication;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 *
 * @author Mehmet Ecevit
 */
public class Govu {

    public static String VERSION = "0.0.1";
    
    
    public static String root;
    public static String dbRoot;
    public static String webRoot;
    public static Logger logger;
    public static int PORT;
    public static Set<WebApplication> apps = new HashSet<>();
    private DB db;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Govu govu = new Govu();
    }

    public Govu() {

        logger = Logger.getLogger("govu");
        root = System.getProperty("user.dir");
        dbRoot = root + "/db";
        webRoot = root + "/web";
        logger.debug("Staring Govu Server "+ VERSION +"...");

        readProperties();

        

        File webDir = new File(webRoot);
        if (!webDir.exists()) {
            logger.debug("Creating web base directory");
            webDir.mkdir();
        }

        File dbDir = new File(dbRoot);
        if (!dbDir.exists()) {
            logger.debug("Creating database base directory");
            dbDir.mkdir();
        }

        //Init Database Provider
        db = new DB(new DB4OProvider());
        
        //Init http server
        ServerBootstrap restBootStrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        restBootStrap.setOption("child.tcpNoDelay", true);
        restBootStrap.setPipelineFactory(new HttpServerPipelineFactory());
        restBootStrap.bind(new InetSocketAddress(PORT));

        logger.debug("Govu started successfully. Happy coding!");
    }
    
    public static WebApplication getWebApp(String host,String path) {
        for (Iterator<WebApplication> it = apps.iterator(); it.hasNext();) {
            WebApplication app = it.next();
            if ( (app.getDomain()!=null && app.getDomain().equals(host)) ||
                (app.getDomain() == null && path.startsWith(app.getRootPath()))
                   ) {
                return app;
            }
        }
        return null;
    }

    private void readProperties() {
        File propsFile = new File(root + "/govu.properties");
        if (propsFile.exists()) {
            logger.info("Loading govu.properties");
            Properties props = new Properties();
            try {
                props.load(new FileReader(propsFile));
            } catch (FileNotFoundException ex) {
                logger.error("Error reading govu.properties", ex);
            } catch (IOException ex) {
                logger.error("Error reading govu.properties", ex);
            }

            //Read configuration
            if (props.containsKey("webRoot")) {
                String tmpRoot = props.getProperty("webRoot");
            }
            logger.debug("Webroot: " + webRoot);
            
            if (props.containsKey("port")) {
                try {
                    PORT = Integer.parseInt(props.getProperty("port"));
                } catch (Exception ex) {
                }
            }

            //Read apps
            for (Iterator<Object> it = props.keySet().iterator(); it.hasNext();) {
                String key = it.next().toString();
                if (key.startsWith("web.") && key.endsWith(".path")) {
                    String name = key.substring(key.indexOf(".")+1 , key.indexOf(".", key.indexOf(".")+1));
                    logger.info("> starting web application: " + name);
                    apps.add(new WebApplication(
                            name,
                            props.getProperty(key),
                            props.getProperty("web." + name + ".domain")));
                }
            }
        }
    }
}
