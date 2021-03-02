package com.zebra.jamesswinton.boxsizedemo.swipetodelete;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.jamesswinton.boxsizedemo.Measurement;
import com.zebra.jamesswinton.boxsizedemo.MeasurementAdapter;
import com.zebra.jamesswinton.boxsizedemo.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    // Debugging
    private static final String TAG = "SwipeToDeleteCallback";

    // Constants


    // Private Variables
    private Context mContext = null;
    private MeasurementAdapter mMeasurementAdapter = null;
    private Drawable mDeleteIcon = null;
    private ColorDrawable mDeleteColour = null;

    /**
     * Constructor
     */

    public SwipeToDeleteCallback(Context context, MeasurementAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mMeasurementAdapter = adapter;
        mContext = context;
        mDeleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        mDeleteColour = new ColorDrawable(Color.RED);
    }


    /**
     * ItemTouchHelper callbacks
     */

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mMeasurementAdapter.deleteMeasurement(position);
        Toast.makeText(mContext, "Measurement Deleted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        // Get View
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        // Handle Background Colour
        if (dX > 0) { // Swiping to the right
            mDeleteColour.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            mDeleteColour.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            mDeleteColour.setBounds(0, 0, 0, 0);
        } mDeleteColour.draw(c);

        // Handle Icons
        int iconMargin = (itemView.getHeight() - mDeleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - mDeleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + mDeleteIcon.getIntrinsicHeight();
        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + mDeleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            mDeleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            mDeleteColour.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - mDeleteIcon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            mDeleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            mDeleteColour.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            mDeleteColour.setBounds(0, 0, 0, 0);
        }

        mDeleteColour.draw(c);
        mDeleteIcon.draw(c);
    }
}
