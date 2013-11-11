/*
 * This file is only holds static methods pointing to current database provider
 * for easy access in javascript code
 */
package com.govu.engine.db;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author Mehmet Ecevit
 */
public class DB {
    
    public static DB current;
    private DatabaseProvider db;

    public DB(DatabaseProvider db) {
        DB.current = this;
        this.db = db;
    }
    
    public static void deleteObj(ScriptableObject obj, String type) {
        DB.current.db.deleteObj(obj, type);
    }

    public static void saveObj(ScriptableObject obj, String type) {
        DB.current.db.saveObj(obj, type);
    }

    public static void delete(ScriptableObject obj, String type) {
        DB.current.db.delete(obj, type);
    }
    
    public static Scriptable[] getAll(Function function, String type) {
        return DB.current.db.getAll(function, type);
    }

    public static Scriptable[] search(ScriptableObject obj, Function function, String type) {
        return DB.current.db.search(obj, function, type);
    }

    public static Scriptable get(ScriptableObject obj, String type) {
        return DB.current.db.get(obj, type);
    }
    
}
