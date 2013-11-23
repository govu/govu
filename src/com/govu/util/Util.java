/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.util;

import java.util.regex.Pattern;

/**
 *
 * @author Mehmet Ecevit
 */
public class Util {

    public static boolean isDomainValid(String domain) {
        return DOMAIN_NAME_PATTERN.matcher(domain).matches();
    }
    
    public static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
}
