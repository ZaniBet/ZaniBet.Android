package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Help implements Serializable{

    private String _id;
    private String icon;
    private String subject;
    private String caption;
    //@SerializedName("qa")
    private QuestionAnswer[] qa;

    public Help(){

    }

    public String getId() {
        return this._id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public QuestionAnswer[] getQuestionAnswers() {
        return this.qa;
    }

    public void setQuestionAnswers(QuestionAnswer[] questionAnswers) {
        this.qa = questionAnswers;
    }
}
