package com.bricenangue.insyconn.ki_ki.asynctasks;


import android.os.AsyncTask;
import android.util.Log;

import com.bricenangue.insyconn.ki_ki.interfaceClasses.NutritionInterface;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.bricenangue.insyconn.ki_ki.activities.FitnessActivity.TAG;

public class FetchNutritionAsync extends AsyncTask<Void,Void,Value> {

    private GoogleApiClient mClient;
    private NutritionInterface nutritionInterface;

    public FetchNutritionAsync(GoogleApiClient client, NutritionInterface nutritionInterface){
        mClient = client;
        this.nutritionInterface = nutritionInterface;
    }
    @Override
    protected Value doInBackground(Void... voids) {
        Value nutrient=null;

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_NUTRITION, DataType.AGGREGATE_NUTRITION_SUMMARY)
                .setTimeRange(startTime,endTime, TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();

        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);

        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mClient, DataType.TYPE_NUTRITION ).await(30, TimeUnit.MINUTES);
        nutrient = showDataSet(result.getTotal());


        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e(TAG, "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {

                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                   nutrient =  showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e(TAG, "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                nutrient= showDataSet(dataSet);
            }
        }
        return nutrient;
    }

    private Value showDataSet(DataSet dataSet) {
        Log.e(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        Log.e(TAG,"DATA "+dataSet.getDataPoints());
        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e(TAG, "Data point:");
            Log.e(TAG, "\tType: " + dp.getDataType().getName());
            Log.e(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                if (field.getName().equals("nutrients")){

                    return dp.getValue(field);
                }
                Log.e(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Value nutrients) {
        super.onPostExecute(nutrients);
        nutritionInterface.getNutritionData(nutrients);

    }
}
