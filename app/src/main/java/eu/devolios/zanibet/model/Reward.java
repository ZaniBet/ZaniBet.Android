package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reward implements Serializable {

    private String _id;
    private String name;
    private String brand;
    private float price;
    private float value;

    public Reward(){

    }

    public static Reward convertFromMap(Object object){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Reward> jsonAdapter = moshi.adapter(Reward.class);
        return jsonAdapter.fromJsonValue(object);
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getPrice() {
        return this.price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
