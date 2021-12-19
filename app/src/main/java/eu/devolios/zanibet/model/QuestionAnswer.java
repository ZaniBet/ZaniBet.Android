package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Gromat Luidgi on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionAnswer implements Serializable {

        private String question;
        private String answer;

        public QuestionAnswer(){

        }

        public String getQuestion() {
            return this.question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return this.answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

}
