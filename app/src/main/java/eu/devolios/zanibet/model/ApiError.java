package eu.devolios.zanibet.model;

import android.content.Context;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.util.Objects;

import eu.devolios.zanibet.R;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by Gromat Luidgi on 12/11/2017.
 */

public class ApiError {

    private int code;
    private int status;
    private String title;
    private String message;


    public ApiError(int code, int status, String title, String detail){
        this.code = code;
        this.status = status;
        this.title = title;
        this.message = detail;
    }

    public ApiError(){

    }

    public static ApiError parseError(Response<?> response) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ApiError> jsonAdapter = moshi.adapter(ApiError.class);
        ApiError error;

        try {
            assert response.errorBody() != null;
            error = jsonAdapter.fromJson(response.errorBody().source());
        } catch (Exception e) {
            e.printStackTrace();
            error = new ApiError();
            error.setStatus(response.code());
            return error;
        }

        return error;
    }

    public static ApiError parseHttpError(Context context, Throwable throwable){
        ApiError apiError = new ApiError();
        try {
            if (throwable instanceof HttpException){
                HttpException error = (HttpException)throwable;
                switch (error.code()){
                    case 401:
                        User.clearUserPreference();
                        break;
                    case 403:
                        User.clearUserPreference();
                        apiError.setMessage(context.getString(R.string.err_authentification_failled));
                        break;
                    default:
                        apiError.setMessage(throwable.getMessage());
                }
            } else {
                apiError = networkError(context);
            }
        } catch (Exception e) {
            apiError = networkError(context);
            return apiError;
        }

        return apiError;
    }

    public static ApiError parseRxError(Context context, Throwable throwable) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ApiError> jsonAdapter = moshi.adapter(ApiError.class);
        ApiError apiError = new ApiError();

        try {
            if (throwable instanceof HttpException){
                HttpException error = (HttpException)throwable;
                switch (error.code()){
                    case 401:
                        User.clearUserPreference();
                        break;
                    case 403:
                        User.clearUserPreference();
                        apiError.setMessage(context.getString(R.string.err_authentification_failled));
                        break;
                    default:
                        apiError = jsonAdapter.fromJson(Objects.requireNonNull(error.response().errorBody()).string());
                }
            } else {
                apiError = networkError(context);
            }
        } catch (Exception e) {
            apiError = networkError(context);
            return apiError;
        }

        return apiError;
    }

    public static ApiError networkError(Context context){
        ApiError apiError = new ApiError();
        apiError.setStatus(-1);
        apiError.setCode(-1);
        apiError.setTitle(context.getString(R.string.err_title_oups));
        apiError.setMessage(context.getString(R.string.err_network));
        return apiError;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String detail) {
        this.message = detail;
    }
}
