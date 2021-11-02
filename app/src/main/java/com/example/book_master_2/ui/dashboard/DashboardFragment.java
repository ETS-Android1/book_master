package com.example.book_master_2.ui.dashboard;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.book_master_2.Adapter.image_adapter;
import com.example.book_master_2.local_db;
import com.example.book_master_2.Model.image_data;
import com.example.book_master_2.R;
import com.example.book_master_2.conn_check;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DashboardFragment extends Fragment{

    int previousTotal=0;
    private boolean loading = true;
    private DashboardViewModel dashboardViewModel;

    private LinearLayoutManager _sGridLayoutManager;
    ArrayList<image_data> article_Data_Model = new ArrayList();
    image_adapter recyclerAdapter;
    RecyclerView recyclerView;

    View view;


    local_db localdb;
    private Context context;

    private ProgressBar progressBar;
    private TextView messagearea;
    private Disposable subscription;

    String continue_variable = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initializing component's
        final TextView textView = view.findViewById(R.id.messagearea);
        messagearea = view.findViewById(R.id.messagearea);
        recyclerView = view.findViewById(R.id.list_view_repos);
        progressBar = view.findViewById(R.id.progress);

        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        context = getActivity();

        // Initializing database
        localdb = new local_db(context);

        progressBar.setVisibility(View.VISIBLE);
        messagearea.setVisibility(View.GONE);

        recyclerAdapter = new image_adapter(getContext(), article_Data_Model);
        recyclerView.setHasFixedSize(true);
        _sGridLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(_sGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

               int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = _sGridLayoutManager.getItemCount();
               int firstVisibleItemIndex = _sGridLayoutManager.findFirstVisibleItemPosition();

                //synchronizew loading state when item count changes
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        Call_server(continue_variable);
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading)
                    if ((totalItemCount - visibleItemCount) <= firstVisibleItemIndex) {
                        // Loading NOT in progress and end of list has been reached
                        // also triggered if not enough items to fill the screen
                        // if you start loading
                        loading = true;
                    } else if (firstVisibleItemIndex == 0){
                        // top of list reached
                        // if you start loading
                        loading = true;
                        SetDataToadapter(continue_variable);
                    }
            }
    });



        //Calling API Data using Volley request
        if (conn_check.isConnectionAvailable(context)) {

            Call_server(continue_variable);
        }else{
            SetDataToadapter(continue_variable);
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }


    public void Call_server(String continue_variable) {
        try {
            final String[] data_of_continue = {""};
            progressBar.setVisibility(View.VISIBLE);

            RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
            String url = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=timestamp%7Cuser%7Curl&generator=categorymembers&gcmtype=file&gcmtitle=Category:Featured_pictures_on_Wikimedia_Commons&format=json&utf8" + continue_variable;//Helpers.getappUrl(this); // <----enter your post url here
            StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        localdb.open();
                        //Receive API data response
                        JSONObject parentArray = new JSONObject(response);
                        //Convert API String data to Json Object
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject row = parentArray.getJSONObject("query");
                            JSONObject continue_row = parentArray.getJSONObject("continue");

                            try {
                                JSONObject jObject = row.getJSONObject("pages");
                                Iterator<String> keys = jObject.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    JSONObject innerJObject = jObject.getJSONObject(key);
                                    JSONArray myListsAll = new JSONArray(innerJObject.getString("imageinfo"));
                                    for (int ai = 0; ai < myListsAll.length(); ai++) {
                                        JSONObject jsonobject = (JSONObject) myListsAll.get(ai);
                                        String descriptionurl = jsonobject.optString("descriptionurl");

                                        if (conn_check.isConnectionAvailable(context)) {
                                            article_Data_Model.add(new image_data(Integer.valueOf(innerJObject.getString("pageid")),
                                                    innerJObject.getString("title"),
                                                    jsonobject.optString("user"),
                                                    jsonobject.optString("descriptionurl"),
                                                    jsonobject.optString("url"),
                                                    0));
                                        }
                                        if(localdb.GetVbyID_Sync(innerJObject.getString("pageid")).getCount()>0){
                                        }else {
                                            localdb.insert(innerJObject.getString("title"), jsonobject.optString("descriptionurl"), innerJObject.getString("pageid"),
                                                    jsonobject.optString("url"), getDate(),getTime(), "1","IMG");

                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            data_of_continue[0] = "&gcmcontinue=" + String.valueOf(continue_row.getString("gcmcontinue")) + "&continue=" + String.valueOf(continue_row.getString("continue"));
                        }
                        localdb.close();
                        SetDataToadapter(data_of_continue[0]);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map<String, String> getParams() {
                    Map<String, String> MyData = new HashMap<String, String>();
                    return MyData;
                }
            };

            MyRequestQueue.add(MyStringRequest);
        } catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(getContext(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
    }

    private void SetDataToadapter(String data_of_continue) {

        if (!conn_check.isConnectionAvailable(context)) {
            localdb.open();
            Cursor cursor = localdb.GetAll("IMG");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    article_Data_Model.add(new image_data(
                            Integer.valueOf(String.valueOf(cursor.getString(3))),
                            String.valueOf(cursor.getString(1)),
                            "",
                            String.valueOf(cursor.getString(2)),
                            String.valueOf(cursor.getString(4)), 0));

                }
            }

            localdb.close();
        }

        continue_variable = data_of_continue;
        Observable.just(article_Data_Model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new io.reactivex.Observer<ArrayList>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(ArrayList arrayList) {
                        recyclerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //error handling made simple
                        messagearea.setText(messagearea.getText().toString() + "\n" + "OnError");
                        progressBar.setVisibility(View.INVISIBLE);
                        messagearea.setText(messagearea.getText().toString() + "\n" + "Hidding Progressbar");
                    }

                    @Override
                    public void onComplete() {
                        messagearea.setText(messagearea.getText().toString() + "\n" + "OnComplete");
                        progressBar.setVisibility(View.INVISIBLE);

                        messagearea.setText(messagearea.getText().toString() + "\n" + "Hidding Progressbar");
                    }
                });

    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }
}