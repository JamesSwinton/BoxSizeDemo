package com.zebra.jamesswinton.boxsizedemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.security.acl.NotOwnerException;
import java.util.List;

public class MeasurementAdapter extends RecyclerView.Adapter {

    // Debugging
    private static final String TAG = "MeasurementAdapter";

    // Constants
    private static final int NO_MEASUREMENT_VIEW_HOLDER = 0;
    private static final int SINGLE_MEASUREMENT_VIEW_HOLDER = 1;
    private static final int VOLUME_MEASUREMENT_VIEW_HOLDER = 2;

    // Private Variables
    private List<Measurement> mMeasurements;

    // Public Variables

    public MeasurementAdapter(List<Measurement> measurements) {
        this.mMeasurements = measurements;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case SINGLE_MEASUREMENT_VIEW_HOLDER: {
                return new SingleMeasurementViewHolder(LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.adapter_single_measurement, parent, false));
            }
            case VOLUME_MEASUREMENT_VIEW_HOLDER: {
                return new VolumeMeasurementViewHolder(LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.adapter_volume_measurement, parent, false));
            }
            default: {
                return new NoMeasurementViewHolder(LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.adapter_no_measurement, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Handle ViewHolder Types
        if (holder instanceof SingleMeasurementViewHolder) {
            // Cast
            SingleMeasurementViewHolder vh = (SingleMeasurementViewHolder) holder;

            // Populate
            vh.measurement.setText(mMeasurements.get(position).getFormattedTotal());
        } else if (holder instanceof VolumeMeasurementViewHolder) {
            // Cast
            VolumeMeasurementViewHolder vh = (VolumeMeasurementViewHolder) holder;

            // Populate
            vh.measurement1.setText(mMeasurements.get(position).getFormattedMeasurement1());
            vh.measurement2.setText(mMeasurements.get(position).getFormattedMeasurement2());
            vh.measurement3.setText(mMeasurements.get(position).getFormattedMeasurement3());
            vh.volume.setText(mMeasurements.get(position).getFormattedTotal());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMeasurements == null || mMeasurements.isEmpty() ? NO_MEASUREMENT_VIEW_HOLDER :
                (mMeasurements.get(position).getMeasure_type() == Measurement.MEASURE_TYPE.SINGLE
                        ? SINGLE_MEASUREMENT_VIEW_HOLDER : VOLUME_MEASUREMENT_VIEW_HOLDER);
    }

    @Override
    public int getItemCount() {
        return mMeasurements == null || mMeasurements.isEmpty() ? 1 : mMeasurements.size();
    }

    /**
     * Helpers
     */

    public void addMeasurement(Measurement measurement) {
        this.mMeasurements.add(measurement);
        this.notifyItemInserted(mMeasurements.indexOf(measurement));
    }

    public void reloadMeasurements(List<Measurement> measurements) {
        this.mMeasurements = measurements;
        this.notifyDataSetChanged();
    }

    public void deleteMeasurement(int position) {
        mMeasurements.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * View Holders
     */

    private class NoMeasurementViewHolder extends RecyclerView.ViewHolder {
        private NoMeasurementViewHolder(View view) {
            super(view);
        }
    }

    private class SingleMeasurementViewHolder extends RecyclerView.ViewHolder {

        // Views
        private TextView measurement;

        private SingleMeasurementViewHolder(View view) {
            super(view);
            this.measurement = view.findViewById(R.id.measurement);
        }
    }

    private class VolumeMeasurementViewHolder extends RecyclerView.ViewHolder {

        // Views
        private TextView measurement1, measurement2, measurement3, volume;

        private VolumeMeasurementViewHolder(View view) {
            super(view);
            this.measurement1 = view.findViewById(R.id.measurement_one);
            this.measurement2 = view.findViewById(R.id.measurement_two);
            this.measurement3 = view.findViewById(R.id.measurement_three);
            this.volume = view.findViewById(R.id.volume);
        }
    }
}
