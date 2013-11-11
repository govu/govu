/*
 * 
 */
package com.govu.engine.db;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author Mehmet Ecevit
 */
public interface DatabaseProvider {

    public void deleteObj(ScriptableObject obj, String type);

    public void saveObj(ScriptableObject obj, String type);

    public void delete(ScriptableObject obj, String type);
    
    public Scriptable[] getAll(Function function, String type);

    public Scriptable[] search(ScriptableObject obj, Function function, String type);

    public Scriptable get(ScriptableObject obj, String type);
    
}
