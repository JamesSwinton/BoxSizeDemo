package com.zebra.jamesswinton.boxsizedemo;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteToCSVAsync extends AsyncTask<Void, Void, Void> {

    // Debugging
    private static final String TAG = "";

    // Constants
    private Handler mHandler = new Handler(Looper.getMainLooper());

    // Private Variables
    private WriteToCsvCallback mCallback;
    private String[] mValues;

    // Public Variables

    public WriteToCSVAsync(WriteToCsvCallback writeToCsvCallback, String[] values) {
        this.mCallback = writeToCsvCallback;
        this.mValues = values;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... objects) {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "dimensions.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer;

        // File exist
        FileWriter mFileWriter;
        try {
            if (f.exists() && !f.isDirectory()) {
                mFileWriter = new FileWriter(filePath, true);
                writer = new CSVWriter(mFileWriter, ',', CSVWriter.NO_QUOTE_CHARACTER,
                        '"', "\n");
            } else {
                writer = new CSVWriter(new FileWriter(filePath), ',', CSVWriter.NO_QUOTE_CHARACTER,
                        '"', "\n");
            }
            writer.writeNext(mValues);
            writer.close();
            mHandler.post(() -> mCallback.onComplete());
        } catch (IOException e) {
            e.printStackTrace();
            mHandler.post(() -> mCallback.onError(e.getMessage()));
        }

        // Empty Return
        return null;
    }

    @Override
    protected void onPostExecute(Void o) {
        super.onPostExecute(o);
    }
}
