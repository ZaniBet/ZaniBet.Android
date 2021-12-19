package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 16/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payout implements Serializable{

    private String _id;
    private String createdAt;
    private String updatedAt;
    private String reference;
    private String kind;
    private Object target;
    private String status;
    private PayoutInvoice invoice;

    public Payout(){

    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }


    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PayoutInvoice getInvoice() {
        return invoice;
    }

    public void setInvoice(PayoutInvoice invoice) {
        this.invoice = invoice;
    }
}
