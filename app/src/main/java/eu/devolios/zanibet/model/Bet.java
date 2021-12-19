package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bet implements Serializable{

    private Object fixture;
    private int result;
    private String type;
    private String status;
    private int winner;

    public Bet(){

    }

    public Object getFixture() {
        return this.fixture;
    }

    public void setFixture(Object fixture) {
        this.fixture = fixture;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
