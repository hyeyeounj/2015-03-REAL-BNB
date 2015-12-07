package net.balbum.baby.lib.retrofit;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by hyes on 2015. 11. 15..
 */
public class ServiceGenerator {

    public static final String API_BASE_URL = "http://192.168.0.14:8080/";
//    public static final String API_BASE_URL = "http://dev.balbum.net";
//    public static final String API_BASE_URL = "http://10.73.38.222:8080";


    private static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(API_BASE_URL)
            .setClient(new OkClient(new OkHttpClient()));

    public static <S> S createService(Class<S> serviceClass) {
        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }
}
