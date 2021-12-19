package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 02/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class Setting implements Serializable {

    public String setting;
    public Object value;

    public Setting(){

    }

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
