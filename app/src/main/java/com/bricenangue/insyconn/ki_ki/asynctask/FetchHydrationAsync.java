package com.bricenangue.insyconn.ki_ki.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.bricenangue.insyconn.ki_ki.HydrationInterface;
import com.bricenangue.insyconn.ki_ki.activity.FitnessActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.data.Field.FIELD_VOLUME;

/**
 * get hydration information
 */
public class FetchHydrationAsync extends AsyncTask<Void,Void,Float> {

    private GoogleApiClient mClient;
    private HydrationInterface hydrationInterface;

    public FetchHydrationAsync(GoogleApiClient client, HydrationInterface hydrationInterface){
        mClient = client;
        this.hydrationInterface=hydrationInterface;
    }

    @Override
    protected Float doInBackground(Void... voids) {
        ArrayList<Long> mResult = new ArrayList<>();
        float total = 0;
        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.AGGREGATE_HYDRATION);

        DailyTotalResult totalResult = result.await(1, TimeUnit.SECONDS);

        if (totalResult.getStatus().isSuccess()) {
            DataSet totalSet = totalResult.getTotal();
            if (totalSet != null) {
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(FIELD_VOLUME).asFloat();
                Log.d(FitnessActivity.TAG,"Total water count got after saved "+total);
            }
        } else {
            Log.w(FitnessActivity.TAG, "There was a problem getting the calories." + totalResult);
        }
        return total;
    }

    @Override
    protected void onPostExecute(Float hydration) {
        super.onPostExecute(hydration);
        hydrationInterface.getHydrationData(hydration);

    }
}
