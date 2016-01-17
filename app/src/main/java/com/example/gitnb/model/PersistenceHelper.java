package com.example.gitnb.model;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import com.example.gitnb.utils.FileUtils;

/**
 * Created by yw on 2015/5/4.
 */
public class PersistenceHelper {
    private static final String TAG = "PersistenceHelper";

    public static boolean saveObject(Context cxt, Serializable obj, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = cxt.openFileOutput(file, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }
    
    public static boolean deleteObject(Context cxt, String file) {
        try {
        	return cxt.deleteFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
        }
    }

    public static <T extends TModel> boolean saveModel(Context cxt, T obj, String file) {
        return saveObject(cxt, obj, file);
    }

    public static <T extends TModel> boolean saveModelList(Context cxt, ArrayList<T> objs, String file) {
        return saveObject(cxt, objs, file);
    }

    public static Serializable loadObject(Context cxt, String file) {
        if (!FileUtils.isExistDataCache(cxt, file))
            return null;

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = cxt.openFileInput(file);
            ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            return (Serializable) obj;
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof InvalidClassException) {
                File data = cxt.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }

        return null;
    }

    public static <T extends TModel> T loadModel(Context cxt, String file) {
        return (T) loadObject(cxt, file);
    }

    public static <T extends TModel> ArrayList<T> loadModelList(Context cxt, String file) {
        return (ArrayList<T>) loadObject(cxt, file);
    }

}
