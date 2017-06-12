package com.bricenangue.insyconn.ki_ki.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bricenangue.insyconn.ki_ki.activity.FitnessActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FetchCaloriesAsync extends AsyncTask<Object, Object, ArrayList<Long>> {

    private GoogleApiClient mClient;

    public FetchCaloriesAsync(GoogleApiClient client) {
        mClient = client;
    }


    protected ArrayList<Long> doInBackground(Object... params) {
        ArrayList<Long> mResult = new ArrayList<>();
        float total = 0;
        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_CALORIES_EXPENDED);
        DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
        if (totalResult.getStatus().isSuccess()) {
            DataSet totalSet = totalResult.getTotal();
            if (totalSet != null) {
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                Log.d(FitnessActivity.TAG, "Total calories got " + total);
            }
        } else {
            Log.w(FitnessActivity.TAG, "There was a problem getting the calories.");
        }


        return mResult;
    }


    @Override
    protected void onPostExecute(ArrayList<Long> results) {
        super.onPostExecute(results);
    }
}
