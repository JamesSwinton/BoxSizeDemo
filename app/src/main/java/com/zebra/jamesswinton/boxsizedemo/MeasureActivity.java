package com.zebra.jamesswinton.boxsizedemo;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mysize.surfaceui.SurfaceUISDK;
import com.mysize.surfaceui.framework.models.CalculationResult;
import com.mysize.surfaceui.framework.models.MeasureUnit;
import com.zebra.jamesswinton.boxsizedemo.databinding.ActivityMeasureLocalBinding;
import com.zebra.jamesswinton.boxsizedemo.swipetodelete.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.zebra.jamesswinton.boxsizedemo.BoxSizeService.GET_MEASUREMENT;
import static com.zebra.jamesswinton.boxsizedemo.BoxSizeService.GET_VOLUME;
import static com.zebra.jamesswinton.boxsizedemo.BoxSizeService.LAUNCH_SERVICE;

public class MeasureActivity extends Activity {

    // Debugging
    private static final String TAG = "MeasureActivity";

    // Constants
    private static final int PERMISSIONS_REQUEST = 10;
    private static final int OVERLAY_REQUEST_CODE = 2000;
    private static final String[] PERMISSIONS = { WRITE_EXTERNAL_STORAGE };
    private static final Handler mHandler = new Handler();
    private static final int MEASUREMENT_DELAY = 1500; // Mandatory delay between measurements

    // Private Variables
    private ActivityMeasureLocalBinding mDataBinding = null;

    // Recycler View Components
    private List<Measurement> mMeasurements = new ArrayList<>();
    private MeasurementAdapter mMeasurementAdapter = null;
    private ItemTouchHelper mItemTouchHelper = null;

    // Measurement Components
    private boolean mCalculateVolume = false;
    private boolean mStartedFromService = false;

    private int mNumberOfMeasurementsTaken = 0;
    private Measurement mVolumeMeasurement;
    private double mVolumeMeasurement1 = 0;
    private double mVolumeMeasurement2 = 0;
    private double mVolumeMeasurement3 = 0;


    /**
     * Lifecycle Methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_measure_local);
        setActionBar(mDataBinding.layoutToolbar.toolbar);

        // Get Permissions
        if (!checkStandardPermissions()) {
            // Request Permissions
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST);
        }

        // Get Overlay Permission
        if (!checkOverlayPermission()) {
            requestOverlayPermission();
        }

        // Init Measure Adapter
        mMeasurementAdapter = new MeasurementAdapter(mMeasurements);
        mDataBinding.measurementRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDataBinding.measurementRecyclerView.setAdapter(mMeasurementAdapter);
        mItemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(this,
                mMeasurementAdapter));
        mItemTouchHelper.attachToRecyclerView(mDataBinding.measurementRecyclerView);

        // Init Volume Listener
        mDataBinding.calculateVolumeCheckbox.setOnCheckedChangeListener(
                (compoundButton, calculateVolume) -> mCalculateVolume = calculateVolume);

        // Init Measure Listener
        mDataBinding.startMeasureButton.setOnClickListener(view -> {
            if (mCalculateVolume) {
                showLoadingView(true);
                mNumberOfMeasurementsTaken = 0;
            }

            SurfaceUISDK.getInstance().start(MeasureActivity.this, MeasureUnit.CM);
        });

        // Check Intent
        Intent launchIntent = getIntent();
        if (launchIntent != null && launchIntent.getExtras() != null) {
            if (launchIntent.getBooleanExtra(LAUNCH_SERVICE, false)) {
                startService(new Intent(MeasureActivity.this, BoxSizeService.class));
                finish();
            } else if (launchIntent.getStringExtra(GET_MEASUREMENT) != null) {
                // Set Calculate Volume
                mCalculateVolume = false;

                // Set Started from Service
                mStartedFromService = true;

                // Start Measure
                SurfaceUISDK.getInstance().start(MeasureActivity.this, MeasureUnit.CM);
            } else if (launchIntent.getStringExtra(GET_VOLUME) != null) {
                // Set Calculate Volume
                mCalculateVolume = true;

                // Set Started from Service
                mStartedFromService = true;

                // Show Loading View
                showLoadingView(true);

                // Set Num of Measurements
                mNumberOfMeasurementsTaken = 0;

                // Start Measure
                SurfaceUISDK.getInstance().start(MeasureActivity.this, MeasureUnit.CM);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public boolean checkStandardPermissions() {
        boolean permissionsGranted = true;
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                permissionsGranted = false;
                break;
            }
        }

        return permissionsGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);

        // Handle Permissions Request
        if (requestCode == PERMISSIONS_REQUEST) {
            Log.i(TAG, "Permissions Request Complete - checking permissions granted...");

            // Validate Permissions State
            boolean permissionsGranted = true;
            if (results.length > 0) {
                for (int result : results) {
                    if (result != PERMISSION_GRANTED) {
                        permissionsGranted = false;
                    }
                }
            } else {
                permissionsGranted = false;
            }

            // Check Permissions were granted & Load slide images or exit
            if (permissionsGranted) {
                Log.i(TAG, "Permissions Granted");
            } else {
                Log.e(TAG, "Permissions Denied - Exiting App");
                // Explain reason
                Toast.makeText(this, "Please enable all permissions to run this app",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private boolean checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        } return true;
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_REQUEST_CODE);
    }

    /**
     * Menu Options
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calibrate: {
                SurfaceUISDK.getInstance().startCalibration(MeasureActivity.this);
                return true;
            }
            case R.id.launch_service: {
                // Confirm
                CustomDialog.showCustomDialog(this, CustomDialog.DialogType.INFO,
                        "Start Service",
                        "Starting a service will quit this application & launch a " +
                                "foreground service, are you sure you want to continue?",
                        "OK", (dialogInterface, i) -> {
                    startService(new Intent(MeasureActivity.this, BoxSizeService.class));
                    finish();
                        }, "CANCEL", null);
                return true;
            }
        } return super.onOptionsItemSelected(item);
    }

    /**
     * Handle Measurement Values
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if Permissions or handle measure
        if (requestCode == OVERLAY_REQUEST_CODE) {
            if (!checkOverlayPermission()) {
                requestOverlayPermission();
            }
            return;
        }

        // Get Result
        final CalculationResult result = SurfaceUISDK.getInstance().onActivityResult(requestCode, resultCode, data);

        // Verify Result
        if (result != null && result.getResult() > 0) {
            // Handle Volume
            if (mCalculateVolume) {
                switch (mNumberOfMeasurementsTaken) {
                    case 0: {
                        mHandler.postDelayed(() -> {
                            // Store Measurement
                            mVolumeMeasurement1 = result.getResult();

                            // Update Holder
                            mNumberOfMeasurementsTaken++;

                            // Restart Measurement
                            SurfaceUISDK.getInstance().start(MeasureActivity.this, MeasureUnit.CM);
                        }, MEASUREMENT_DELAY);
                        break;
                    }
                    case 1: {
                        mHandler.postDelayed(() -> {
                            // Store Measurement
                            mVolumeMeasurement2 = result.getResult();

                            // Update Holder
                            mNumberOfMeasurementsTaken++;

                            // Restart Measurement
                            SurfaceUISDK.getInstance().start(MeasureActivity.this, MeasureUnit.CM);
                        }, MEASUREMENT_DELAY);
                        break;
                    }
                    case 2: {
                        // Store Measurement
                        mVolumeMeasurement3 = result.getResult();

                        // Calculate Total
                        double volume = mVolumeMeasurement1 * mVolumeMeasurement2 * mVolumeMeasurement3;

                        // Init measurement
                        Measurement measurement = new Measurement(mVolumeMeasurement1,
                                mVolumeMeasurement2, mVolumeMeasurement3, volume);
                        mMeasurements.add(measurement);
                        mMeasurementAdapter.reloadMeasurements(mMeasurements);

                        // Remove Loading View
                        showLoadingView(false);

                        // Exit if required
                        if (mStartedFromService) {
                            new WriteToCSVAsync(new WriteToCsvCallback() {
                                @Override
                                public void onComplete() {
                                    Toast.makeText(MeasureActivity.this,
                                            "Measurement written to file", Toast.LENGTH_LONG)
                                            .show();
                                    finish();
                                }

                                @Override
                                public void onError(String e) {
                                    Toast.makeText(MeasureActivity.this,
                                            "Error writing to file: " + e, Toast.LENGTH_LONG)
                                            .show();
                                    finish();
                                }
                            }, new String[] {measurement.getCsvFormattedMeasurement1(),
                                    measurement.getCsvFormattedMeasurement2(),
                                    measurement.getCsvFormattedMeasurement3(),
                                    measurement.getCsvFormattedTotal()}).execute();
                        }
                        break;
                    }
                }
            } else {
                Measurement measurement = new Measurement(result.getResult());
                mMeasurements.add(measurement);
                mMeasurementAdapter.reloadMeasurements(mMeasurements);

                // Exit if required
                if (mStartedFromService) {
                    new WriteToCSVAsync(new WriteToCsvCallback() {
                        @Override
                        public void onComplete() {
                            Toast.makeText(MeasureActivity.this,
                                    "Measurement written to file", Toast.LENGTH_LONG)
                                    .show();
                            finish();
                        }

                        @Override
                        public void onError(String e) {
                            Toast.makeText(MeasureActivity.this,
                                    "Error writing to file: " + e, Toast.LENGTH_LONG)
                                    .show();
                            finish();
                        }
                    }, new String[] {measurement.getCsvFormattedTotal()}).execute();
                }
            }
        } else {
            Log.e(TAG, "No Result Returned");
            Toast.makeText(this, "Could not get a measurement", Toast.LENGTH_LONG).show();

            // Remove Loading View
            showLoadingView(false);

            // Exit if required
            if (mStartedFromService) {
                finish();
            }
        }
    }

    /**
     * UI Utilties
     */

    private void showLoadingView(boolean showView) {
        mDataBinding.loadingLayout.setVisibility(showView ? View.VISIBLE : View.GONE);
    }

}
