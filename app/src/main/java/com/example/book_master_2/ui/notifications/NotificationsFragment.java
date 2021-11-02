package com.example.book_master_2.ui.notifications;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.book_master_2.Adapter.category_adapter;
import com.example.book_master_2.local_db;
import com.example.book_master_2.Model.atricle_data;
import com.example.book_master_2.R;
import com.example.book_master_2.conn_check;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationsFragment extends Fragment {

    ArrayList<atricle_data> atricledata = new ArrayList();
    private LinearLayoutManager mLinearLayoutManager;

    category_adapter recyclerAdapter;
    RecyclerView recyclerView;


    private ProgressBar progressBar;
    private TextView messagearea;
    private Disposable subscription;

    String continue_variable="";
    local_db localdb;
    private Context context;

    private NotificationsViewModel notificationsViewModel;
    View view;

    @SuppressLint("WrongConstant")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Initializing component's
        final TextView textView = view.findViewById(R.id.messagearea);

        progressBar = view.findViewById(R.id.progress);
        messagearea = view.findViewById(R.id.messagearea);
        recyclerView = view.findViewById(R.id.list_view_repos);


        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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

        recyclerAdapter = new category_adapter(getContext(), atricledata);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);



        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 5 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    //you have reached to the bottom of your recycler view
                    progressBar.setVisibility(View.VISIBLE);

                    mLinearLayoutManager.scrollToPosition(atricledata.size());
                    if (conn_check.isConnectionAvailable(context)) {

                        Call_server(continue_variable);
                    }else{
                        SetDataToadapter(continue_variable);
                    }
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
            String url = "https://en.wikipedia.org/w/api.php?action=query&list=allcategories&acprefix=&format=json"+continue_variable;//Helpers.getappUrl(this); // <----enter your post url here

            StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject parentArray = new JSONObject(response);
                        localdb.open();
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject row = parentArray.getJSONObject("query");
                            JSONObject continue_row = parentArray.getJSONObject("continue");
                            try
                            {
                                JSONArray myListsAll = new JSONArray(row.getString("allcategories"));
                                for (int ai = 0; ai < myListsAll.length(); ai++) {
                                    JSONObject jsonobject = (JSONObject) myListsAll.get(ai);
                                    if (conn_check.isConnectionAvailable(context)) {
                                        atricledata.add(new atricle_data(
                                                Integer.valueOf(ai),
                                                jsonobject.getString("*"),
                                                "",
                                                "",
                                                "", 0));
                                    }
                                    if(localdb.GetVbyID_Sync(String.valueOf(ai)).getCount()>0){
                                    }else {
                                        localdb.insert(jsonobject.getString("*"), "", String.valueOf(ai),
                                                "", getDate(),getTime(), "1","CAT");
                                    }
                                }
                            }
                            catch (JSONException e)
                            {   e.printStackTrace();    }
                            data_of_continue[0] = "&accontinue=" + String.valueOf(continue_row.getString("accontinue")) + "&continue=" + String.valueOf(continue_row.getString("continue"));
                        }
                        SetDataToadapter(data_of_continue[0]);
                        localdb.close();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
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
            progressBar.setVisibility(View.GONE);
            ex.printStackTrace();   Toast.makeText(getContext(), "Data not loaded, please try after sometime!", Toast.LENGTH_LONG).show();
        }
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


    private void SetDataToadapter(String data_of_continue) {
        if (!conn_check.isConnectionAvailable(context)) {
            localdb.open();
            Cursor cursor = localdb.GetAll("CAT");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    atricledata.add(new atricle_data(
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
        Observable.just(atricledata)
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
                        messagearea.setText(messagearea.getText().toString() +"\n" +"OnError" );
                        progressBar.setVisibility(View.INVISIBLE);

                        messagearea.setText(messagearea.getText().toString() +"\n" +"Hidding Progressbar" );
                    }
                    @Override
                    public void onComplete() {

                        messagearea.setText(messagearea.getText().toString() +"\n" +"OnComplete" );
                        progressBar.setVisibility(View.INVISIBLE);
                        messagearea.setText(messagearea.getText().toString() +"\n" +"Hidding Progressbar" );
                    }
                });

    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isDisposed()) {
            subscription.dispose();
        }
    }

}