package com.example.gitnb.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by yw on 2015/5/2.
 */
public abstract class TModel implements Serializable {
    private static final long serialVersionUID = 2015050101L;

    abstract public void parse(JSONObject jsonObject) throws JSONException;
}
