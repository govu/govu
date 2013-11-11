package com.govu.engine.db.db4o;

import com.govu.Govu;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.govu.engine.db.DatabaseProvider;
import java.util.HashMap;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mehmet Ecevit
 */
public class DB4OProvider implements DatabaseProvider {

    public static HashMap<String, ObjectContainer> dbs = new HashMap<>();

    @Override
    public void deleteObj(ScriptableObject obj, String type) {
        ObjectContainer con = getDB(type);

        final HashMap<Object, Object> map = new HashMap<>();
        for (Object id : obj.getAllIds()) {
            map.put(id, obj.get(id));
        }

        ObjectSet<HashMap<Object, Object>> maps = con.query(new Predicate<HashMap<Object, Object>>() {
            @Override
            public boolean match(HashMap<Object, Object> et) {
                for (Object key : map.keySet()) {
                    if (!(et.containsKey(key) && et.get(key).equals(map.get(key)))) {
                        return false;
                    }
                }
                return true;
            }
        });

        if (maps.hasNext()) {
            con.delete(maps.next());
            con.commit();
        }

    }

    @Override
    public void saveObj(ScriptableObject obj, String type) {
        ObjectContainer con = getDB(type);
        HashMap<Object, Object> map;
        if (obj.has("_id_", obj)) { //Update
            map = con.ext().getByID((long) obj.get("_id_"));
        } else { //Save
            map = new HashMap<>();
        }

        for (Object id : obj.getAllIds()) {
            map.put(id, obj.get(id));
        }
        con.store(map);
        con.commit();
    }

    @Override
    public Scriptable[] getAll(Function function, String type) {
        ObjectContainer con = getDB(type);
        ObjectSet<HashMap<Object, Object>> maps = con.query().execute();
        Scriptable[] models = new Scriptable[maps.size()];
        int a = 0;
        while (maps.hasNext()) {
            HashMap<Object, Object> map2 = maps.next();
            Scriptable model = function.construct(Context.getCurrentContext(), null, new Object[0]);
            for (Object obj2 : map2.keySet()) {
                String key = obj2.toString();
                Object value = map2.get(obj2);
                model.put(key, model, value);
                model.put("_id_", model, con.ext().getID(map2));
            }
            models[a] = model;
            a++;
        }
        return models;
    }

    @Override
    public void delete(ScriptableObject obj, String type) {
        //TODO delete by _id_
        ObjectContainer con = getDB(type);

        final HashMap<Object, Object> map = new HashMap<>();
        for (Object id : obj.getAllIds()) {
            map.put(id, obj.get(id));
        }
        ObjectSet<HashMap<Object, Object>> maps = con.query(new Predicate<HashMap<Object, Object>>() {
            @Override
            public boolean match(HashMap<Object, Object> et) {
                for (Object key : map.keySet()) {
                    if (!(et.containsKey(key) && et.get(key).equals(map.get(key)))) {
                        return false;
                    }
                }
                return true;
            }
        });
        while (maps.hasNext()) {
            con.delete(maps.next());
        }
        con.commit();
    }
    
    @Override
    public Scriptable[] search(ScriptableObject obj,Function function, String type) {
        ObjectContainer con = getDB(type);

        final HashMap<Object, Object> map = new HashMap<>();
        for (Object id : obj.getAllIds()) {
            map.put(id, obj.get(id));
        }

        ObjectSet<HashMap<Object, Object>> maps = con.query(new Predicate<HashMap<Object, Object>>() {
            @Override
            public boolean match(HashMap<Object, Object> et) {
                for (Object key : map.keySet()) {
                    if (!(et.containsKey(key) && et.get(key).equals(map.get(key)))) {
                        return false;
                    }
                }
                return true;
            }
        });

        Scriptable[] models = new Scriptable[maps.size()];
        int a=0;
        while (maps.hasNext()) {
            HashMap<Object, Object> map2 = maps.next();
            Scriptable model = function.construct(Context.getCurrentContext(),null, new Object[0]);
            for (Object obj2 : map2.keySet()) {
                String key = obj2.toString();
                Object value = map2.get(obj2);
                model.put(key, model, value);
            }
            model.put("_id_", model, con.ext().getID(map2));
            models[a] = model;
            a++;
        }
        return  models;
        
    }

    @Override
    public Scriptable get(ScriptableObject obj, String type) {
        ObjectContainer con = getDB(type);

        final HashMap<Object, Object> map = new HashMap<>();
        for (Object id : obj.getAllIds()) {
            map.put(id, obj.get(id));
        }

        ObjectSet<HashMap<Object, Object>> maps = con.query(new Predicate<HashMap<Object, Object>>() {
            @Override
            public boolean match(HashMap<Object, Object> et) {
                for (Object key : map.keySet()) {
                    if (!(et.containsKey(key) && et.get(key).equals(map.get(key)))) {
                        return false;
                    }
                }
                return true;
            }
        });

        if (maps.hasNext()) {
            HashMap<Object, Object> map2 = maps.next();
            for (Object obj2 : map2.keySet()) {
                String key = obj2.toString();
                Object value = map2.get(obj2);
                obj.put(key, obj, value);
                obj.put("_id_", obj, con.ext().getID(map2));
            }
            return obj;
        } else {
            return null;
        }
    }

    private ObjectContainer getDB(String type) {
        if (dbs.containsKey(type)) {
            return dbs.get(type);
        } else {
            ObjectContainer obj = Db4o.openFile(Govu.dbRoot + "/" + type + ".data");
            dbs.put(type, obj);
            return obj;
        }
    }
}
