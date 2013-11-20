/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.command;

import com.govu.util.ZipHelper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

/**
 *
 * @author Mehmet Ecevit
 */
public class DeployCommand extends Command {

    

    public DeployCommand() {
        super();
    }

    @Override
    public void process(String[] args) {
        
        if (args.length < 3) {
            System.out.println("Invalid arguments for deploy");
        }
        try {
            String path = args[1] + "/";
            String domain = args[2];
            String password = args.length > 3 ? args[3] : null;

            System.out.println("Path: " + path);
            System.out.println("Domain: " + domain);
            if (password != null) {
                System.out.println("Password: " + password);
            }

            File zipDir = new File(path);
            if (!zipDir.exists()) {
                System.out.println("Path does not exist!");
                return;
            }

            ZipHelper zipHelper = new ZipHelper();
            ByteArrayOutputStream out = zipHelper.zipDir(path);
            String zip = Base64.encodeBase64String(out.toByteArray());
            
            addParameter("domain", domain);
            addParameter("zip", zip);
            if (password != null) {
                addParameter("password", password);
            }
            
            String deployResponse =post("deploy");;

            switch (deployResponse) {
                case "ok":
                    System.out.println("Deployment is successful.");
                    break;
                case "passwordError":
                    System.out.println("Password invalid, please check your password!");
                    break;
                case "error":
                    System.out.println("Deploy failed! Please try again");
                    break;
            }
        } catch (IOException ex) {
            System.out.println("Error while compressing web app:" + ex.getMessage());
        }

    }
}
