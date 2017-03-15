package com.linxcool.andbase.shower;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.linxcool.andbase.BaseActivity;
import com.linxcool.andbase.demo.R;
import com.linxcool.andbase.retrofit.ParamsInterceptor;
import com.linxcool.andbase.retrofit.Reply;
import com.linxcool.andbase.rx.RxHelper;
import com.linxcool.andbase.rx.RxLoadingObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Example for Retrofit with RxJava
 * <pre>
 *  <a href="http://square.github.io/retrofit/">http://square.github.io/retrofit/</a>
 *  <a href="https://github.com/square/retrofit/tree/master/retrofit-adapters/rxjava2">https://github.com/square/retrofit/tree/master/retrofit-adapters/rxjava2</a>
 * </pre>
 * Created by huchanghai on 2017/3/15.
 */
public class RetrofitActivity extends BaseActivity implements Shower {

    public class Category {
        String cid;
        String name;

        @Override
        public String toString() {
            return name;
        }
    }

    public interface MobService {
        String URL_HOST = "http://apicloud.mob.com/";

        //不使用RxJava时，返回值是Call，而跟RxJava结合，返回值应为Observable
        @GET("wx/article/category/query")
        Observable<Reply<List<Category>>> queryCategorys(@Query("key") String key);
    }

    public MobService createMobService() {
        ParamsInterceptor.setLogable(true);
        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new ParamsInterceptor())
                .build();
        return new Retrofit.Builder().baseUrl(MobService.URL_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient)
                .build()
                .create(MobService.class);
    }

    @Override
    public String getName() {
        return "Retrofit2";
    }

    MobService mobService;
    GridView gridView;
    List<Category> categorieList;
    ArrayAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_blank);
        enableHomeback();
        setTitle(getName());

        mobService = createMobService();

        categorieList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<Category>(this, android.R.layout.simple_list_item_1, categorieList);

        gridView = new GridView(this);
        gridView.setAdapter(categoryAdapter);
        addView(gridView);

        loadQuerys();
    }

    protected void loadQuerys() {
        RxLoadingObserver observer = new RxLoadingObserver<Reply<List<Category>>>(this) {
            @Override
            public void onNext(Reply<List<Category>> value) {
                if (value == null) {
                    onError(new Exception("server return null data"));
                    return;
                }
                List<Category> list = value.getData();
                if (list == null) {
                    onError(new Exception("server return null data"));
                    return;
                }
                categorieList.clear();
                categorieList.addAll(list);
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                e.printStackTrace();
            }
        };

        mobService.queryCategorys("1bd59e6e0ec00")
                .compose(RxHelper.<Reply<List<Category>>>scheduleIo2UiThread())
                .subscribe(observer);
    }

}
