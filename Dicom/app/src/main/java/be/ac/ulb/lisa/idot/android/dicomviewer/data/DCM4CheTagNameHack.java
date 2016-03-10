package be.ac.ulb.lisa.idot.android.dicomviewer.data;

import org.dcm4che3.data.Tag;

import java.lang.reflect.Field;
import java.util.HashMap;

public class DCM4CheTagNameHack {
    static HashMap<Object, String> map;

    private DCM4CheTagNameHack(){}

    static {
        map = new HashMap();
        Class tagClass = Tag.class;
        Field[] field = tagClass.getFields();
        for (Field f : field) {
            try {
                String tag_name = f.getName();
                if (f.getGenericType().toString()
                        .equals("int")) {
                    map.put(f.getInt(tagClass), tag_name);
                }
                if (f.getGenericType().toString()
                        .equals("long")) {
                    map.put(f.getLong(tagClass), tag_name);
                }
            } catch (IllegalArgumentException e) {}
            catch (IllegalAccessException e) {}
        }
    }

    public HashMap getMap(){
        return map;
    }

    public static String getTagName(Object object){
        String description = map.get(object);
        return description;
    }
}
