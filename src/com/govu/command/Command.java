/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
public class Command {

    public static String DeployHost = "http://codegovu.com";
    private ArrayList<NameValuePair> postParameters;

    public Command() {
        Logger.getLogger("org.apache.http").setLevel(org.apache.log4j.Level.OFF);
        postParameters = new ArrayList<>();
    }

    public void process(String[] args) {
    }

    void addParameter(String key, String value) {
        postParameters.add(new BasicNameValuePair(key, value));
    }

    String post(String method) throws UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(DeployHost + "/" + method);

        post.setEntity(new UrlEncodedFormEntity(postParameters));
        HttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        return rd.readLine();
    }
}
