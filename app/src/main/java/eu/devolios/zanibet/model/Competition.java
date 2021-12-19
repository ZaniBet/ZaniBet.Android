package eu.devolios.zanibet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.devolios.zanibet.utils.Constants;

/**
 * Created by Gromat Luidgi on 10/11/2017.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Competition implements Serializable {

    private String _id;
    private String code;
    private String name;
    private String logo;
    private String country;
    private int division;


    public Competition(){

    }

    private static Competition convertFromMap(Object object){
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Competition> jsonAdapter = moshi.adapter(Competition.class);
        return jsonAdapter.fromJsonValue(object);
    }

    public static Competition parseCompetition(Object object){
        if (object instanceof String){
            List<Competition> competitions = SharedPreferencesManager.getInstance().getValues("competitions", Competition[].class);
            Competition competition = new Competition();
            for (Competition c : competitions){
                if (c.getId().equals(object)){
                    competition = c;
                }
            }
            return competition;
        } else if (object instanceof Competition){
            return (Competition) object;
        } else {
            return convertFromMap(object);
        }
    }

    public static void saveCompetitions(List<Competition> competitions){
        SharedPreferencesManager.getInstance().remove("competitions");
        SharedPreferencesManager.getInstance().putValue("competitions", competitions);
    }

    public static List<Competition> getCompetitions(){
        List<Competition> competitions = SharedPreferencesManager.getInstance().getValues("competitions", Competition[].class);
        if (competitions == null){
            competitions = new ArrayList<>();
        }
        return competitions;
    }

    public static Competition getForId(String competitionId){
        List<Competition> competitionList = getCompetitions();
        Competition competition = new Competition();
        for (Competition c : competitionList){
            if (c.getId().equals(competitionId)) {
                competition = c;
                break;
            }
        }
        return competition;
    }

    public static void excludeForSingleTicket(Object object){
        Competition competition = parseCompetition(object);
        List<String> filterList = SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class);
        if (filterList == null){
            filterList = new ArrayList<>();
            filterList.add(competition.getId());
            SharedPreferencesManager.getInstance().putValue(Constants.TICKET_SINGLE_FILTER_PREF, filterList);
        } else {
            if (filterList.contains(competition.getId())) return;
            filterList.add(competition.getId());
            SharedPreferencesManager.getInstance().putValue(Constants.TICKET_SINGLE_FILTER_PREF, filterList);
        }
    }

    public static void addForSingleTicket(Object object){
        Competition competition = parseCompetition(object);
        List<String> filterList = SharedPreferencesManager.getInstance().getValues(Constants.TICKET_SINGLE_FILTER_PREF, String[].class);
        if (filterList != null){
            for (String code : filterList){
                if (code.equals(competition.getId())) {
                    filterList.remove(competition.getId());
                    break;
                }
            }
            SharedPreferencesManager.getInstance().putValue(Constants.TICKET_SINGLE_FILTER_PREF, filterList);
        }
    }

    public String getId() {
        return this._id;
    }

    @JsonSetter("_id")
    public void setId(String _id) {
        this._id = _id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDivision() {
        return division;
    }

    public void setDivision(int division) {
        this.division = division;
    }
}
