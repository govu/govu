/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu;

import com.govu.engine.db.DB;
import com.govu.engine.db.db4o.DB4OProvider;
import com.govu.httpserver.HttpServerPipelineFactory;
import com.govu.application.WebApplication;
import com.govu.command.DeleteCommand;
import com.govu.command.DeployCommand;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
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

    public static String VERSION = "0.0.4";
    public static String root;
    public static String dbRoot;
    public static String webRoot;
    public static Logger logger;
    public static int PORT;
    public static Set<WebApplication> apps = new HashSet<>();
    public static Config config;
    private DB db;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            String command = args[0].toLowerCase();
            if (command.equals("deploy")) {
                new DeployCommand().process(args);
            } else if (command.equals("delete")) {
                new DeleteCommand().process(args);
            } else {
                System.out.println("Unknown govu command: " + args[0]);
            }
        } else {
            //Start govu web server
            Govu govu = new Govu();
        }
    }

    public Govu() {
        logger = Logger.getLogger("govu");
        root = System.getProperty("user.dir");
        dbRoot = root + "/db";
        webRoot = root + "/web";
        logger.debug("Staring Govu Server " + VERSION + "...");

        try {
            config = new Config();
            config.readProperties();
        } catch (IOException | InterruptedException ex) {
            logger.error("Error reading config file", ex);
        }


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
        ServerBootstrap httpBootStrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        httpBootStrap.setOption("child.tcpNoDelay", true);
        httpBootStrap.setPipelineFactory(new HttpServerPipelineFactory());
        httpBootStrap.bind(new InetSocketAddress(PORT));

        logger.debug("Govu started successfully. Happy coding!");
    }

    public static WebApplication getWebApp(String host, String path) {
        for (Iterator<WebApplication> it = apps.iterator(); it.hasNext();) {
            WebApplication app = it.next();
            if ((app.getDomain() != null && app.getDomain().equals(host))
                    || (app.getDomain() == null && path.startsWith(app.getRootPath()))) {
                return app;
            }
        }
        return null;
    }
    
    public static WebApplication getWebApp(String name) {
        for (Iterator<WebApplication> it = apps.iterator(); it.hasNext();) {
            WebApplication app = it.next();
            if (app.getName().equals(name)){
                return app;
            }
        }
        return null;
    }
}
