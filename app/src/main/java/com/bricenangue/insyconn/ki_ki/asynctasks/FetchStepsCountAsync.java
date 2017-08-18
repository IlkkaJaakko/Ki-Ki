package com.bricenangue.insyconn.ki_ki.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bricenangue.insyconn.ki_ki.interfaceClasses.StepCounterInterface;
import com.bricenangue.insyconn.ki_ki.activities.FitnessActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import static java.util.concurrent.TimeUnit.SECONDS;


public class FetchStepsCountAsync extends AsyncTask<Void,Void,Long> {

    private GoogleApiClient mClient;
    private Context context;
    private StepCounterInterface stepCounterInterface;


    public FetchStepsCountAsync(GoogleApiClient client, Context context, StepCounterInterface stepCounterInterface ){
        mClient = client;
        this.context =context;
        this.stepCounterInterface = stepCounterInterface;
    }

    @Override
    protected Long doInBackground(Void... voids) {
        long total = 0;

        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
        DailyTotalResult totalResult = result.await(1, SECONDS);
        if (totalResult.getStatus().isSuccess()) {
            DataSet totalSet = totalResult.getTotal();
            total = totalSet.isEmpty()
                    ? 0
                    : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
        } else {
            Log.w(FitnessActivity.TAG, "There was a problem getting the step count.");
        }

        Log.i(FitnessActivity.TAG, "Total steps count recorded : " + total);


        return total;
    }

    @Override
    protected void onPostExecute(Long total) {
        super.onPostExecute(total);
        stepCounterInterface.getSteps(total);

    }
}
