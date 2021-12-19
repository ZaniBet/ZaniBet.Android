package eu.devolios.zanibet.ws;

import com.kingfisher.easy_sharedpreference_library.SharedPreferencesManager;

import eu.devolios.zanibet.model.User;
import eu.devolios.zanibet.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by Gromat Luidgi on 11/08/2017.
 */

public class AuthenticationInterceptor implements Interceptor {

    public AuthenticationInterceptor(){

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        //Log.d("AuthInterceptor", request.method());
        String requestMethod = request.method();
        String locale = Locale.getDefault().getLanguage();
        if (locale == null || locale.isEmpty()) locale = "unset";
        // Try to append to existing body, else set requestbody null
        requestBody = processApplicationJsonRequestBody(requestBody);

        // If request already has a body, append secret to it,
        // else create new body
        if(requestBody != null) {
            Request.Builder requestBuilder = request.newBuilder();
            request = requestBuilder
                    .method(requestMethod, requestBody)
                    .addHeader("Public-Key", Constants.PUBLIC_KEY)
                    .addHeader("Accept-Language", locale)
                    .addHeader("Android-App-Code", "4")
                    .addHeader("Authorization",
                            String.format("Bearer %s", User.getAccessToken()))
                    .build();
        } else {
            // Get back original body
            requestBody = request.body();
            Request.Builder requestBuilder = request.newBuilder();
            request = requestBuilder
                    .method(requestMethod, requestBody)
                    .addHeader("Public-Key", Constants.PUBLIC_KEY)
                    .addHeader("Accept-Language", locale)
                    .addHeader("Android-App-Code", "4")
                    .addHeader("Authorization",
                            String.format("Bearer %s", User.getAccessToken()))
                    .build();
        }


        return chain.proceed(request);
    }

    private String bodyToString(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

    private RequestBody processApplicationJsonRequestBody(RequestBody requestBody){
        String customReq = bodyToString(requestBody);
        try {
            JSONObject obj = new JSONObject(customReq);
            if (obj.has("client_id")){
                return RequestBody.create(requestBody.contentType(), obj.toString());
            }
            obj.put("client_id", Constants.CLIENT_ID);
            obj.put("client_secret", Constants.CLIENT_SECRET);
            return RequestBody.create(requestBody.contentType(), obj.toString());
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return null;
    }
}
