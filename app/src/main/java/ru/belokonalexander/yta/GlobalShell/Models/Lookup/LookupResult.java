package ru.belokonalexander.yta.GlobalShell.Models.Lookup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alexander on 19.03.2017.
 */

public class LookupResult implements Serializable {

    @SerializedName("def")
    @Expose
    private List<Def> def = null;

    public List<Def> getDef() {
        return def;
    }

    public void setDef(List<Def> def) {
        this.def = def;
    }

    @Override
    public String toString() {
        return "LookupResult{" +
                "def=" + def +
                '}';
    }

    public boolean isEmpty() {
        return getDef().size() == 0;
    }
}
