package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.adapters.HistoryListAdapter;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.utils.APIHelper;
import com.papramaki.papramaki.utils.LocalData;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends ListFragment {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    protected List<Expenditure> expenditureHistory;
    protected FloatingActionButton mFAB;
    protected DatabaseHelper mDbHelper;
    protected APIHelper mAPIHelper;

    public HistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        expenditureHistory = LocalData.history.getExpenditures();
        ArrayAdapter<Expenditure> histAdapter = new HistoryListAdapter(getContext(), expenditureHistory);
        setListAdapter(histAdapter);
        histAdapter.notifyDataSetChanged();

        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);
        mDbHelper = new DatabaseHelper(getContext());
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DeductionActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        expenditureHistory = mDbHelper.getLatestExpenditures();
        expenditureHistory = LocalData.history.getExpenditures();
        ArrayAdapter<Expenditure> histAdapter = new HistoryListAdapter(getContext(), expenditureHistory);
        setListAdapter(histAdapter);
        histAdapter.notifyDataSetChanged();
    }

//    private void getHerokuInfo() {
//        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";
////        String budgetsEndpoint = "budgets";
////        String finalUrl = apiUrl + budgetsEndpoint;
//        mAPIHelper = new APIHelper(getContext(), getActivity());
//
//        if (mAPIHelper.isNetworkAvailable()) {
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url(apiUrl)
//                    //.post("")
//                    .addHeader("Accept", "application/json")
//                    .build();
//            Call call = client.newCall(request);
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    // TODO: Handle this later
//
//                    //put in getActivity.runUiThread()
//                    Toast.makeText(getContext(), "There was an error", Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    try {
//                        final String jsonData = response.body().string();
//                        Log.v(TAG, jsonData);
//                        if (response.isSuccessful()) {
////                            final String budgetsValue = getBudgetsValue(jsonData);
//                            final List<Expenditure> history = getBudgetsValue(jsonData);
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
//                                    updateDisplay(history);
//                                }
//                            });
//                        } else {
//                            mAPIHelper.alertUserAboutError();
//                        }
//                    } catch (IOException e) {
//                        Log.e(TAG, "Exception caught: ", e);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } else {
//            Toast.makeText(getContext(), "Network is unavailable", Toast.LENGTH_LONG).show();
//        }
//    }
//
//
//    private void updateDisplay(List<Expenditure> history) {
//        // TODO: We update the view here
//        ArrayAdapter<Expenditure> histAdapter = new HistoryListAdapter(getContext(), history);
//        setListAdapter(histAdapter);
//
//    }

//    private List<Expenditure> getBudgetsValue(String jsonData) throws JSONException {
//
//        JSONArray response = new JSONArray(jsonData);
//        String returnString = "";
//        List<Expenditure> history = new ArrayList<Expenditure>();
//        int value = 0;
//
//        for (int i = 0; i < 1; i++) {
//            JSONObject currentBudget = response.getJSONObject(i);
//            JSONArray expenditures = currentBudget.getJSONArray("expenditures");
//            Expenditure expenditure;
//
//            for (int j = 0; j < expenditures.length(); j++) {
//                JSONObject currentExp = expenditures.getJSONObject(j);
//                Double amount = currentExp.getDouble("amount");
//                String category = currentExp.getString("category");
//                String date = currentExp.getString("created_at");
//                expenditure = new Expenditure(amount,category,date);
//                history.add(expenditure);
//
//                //create getExpenditure method in APIHelper
//            }
//        }
//        return history;
//
//    }

}
