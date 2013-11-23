/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.govu.command;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mehmet Ecevit
 */
public class DeleteCommand extends Command {

    public DeleteCommand() {
        super();
    }

    @Override
    public void process(String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid arguments for deploy");
        }
        
        try {
            String domain = args[1];
            String password = args.length > 2 ? args[2] : null;

            System.out.println("Domain: " + domain);
            if (password != null) {
                System.out.println("Password: " + password);
            }

            addParameter("domain", domain);
            if (password != null) {
                addParameter("password", password);
            }

            String deployResponse = post("delete");

            switch (deployResponse) {
                case "ok":
                    System.out.println("Delete is successful.");
                    break;
                case "passwordError":
                    System.out.println("Password invalid, please check your password!");
                    break;
                case "notExists!":
                    System.out.println("Delete failed! Application does not exist!");
                    break;
                case "error":
                    System.out.println("Delete failed! Please try again!");
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(DeleteCommand.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
