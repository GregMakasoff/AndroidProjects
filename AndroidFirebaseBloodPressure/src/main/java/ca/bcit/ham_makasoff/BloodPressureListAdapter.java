package ca.bcit.ham_makasoff;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BloodPressureListAdapter extends ArrayAdapter<BloodPressure> {
    private Activity context;
    private List<BloodPressure> bloodPressureList;

    public BloodPressureListAdapter(Activity context, List<BloodPressure> tasksList) {
        super(context, R.layout.list_layout, tasksList);
        this.context = context;
        this.bloodPressureList = tasksList;
    }

    public BloodPressureListAdapter(Context context, int resource, List<BloodPressure> objects, Activity context1, List<BloodPressure> tasksList) {
        super(context, resource, objects);
        this.context = context1;
        this.bloodPressureList = tasksList;
    }

    private String dateToString (Date date) {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MMM/yyyy");
        String strDt = simpleDate.format(date);
        return strDt;
    }

    private String timeToString (Date date) {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("HH:mm");
        String strDt = simpleDate.format(date);
        return strDt;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView tvUserID = listViewItem.findViewById(R.id.textViewUserID);
        TextView tvSystolic = listViewItem.findViewById(R.id.textViewSystolic);
        TextView tvDiastolic = listViewItem.findViewById(R.id.textViewDiastolic);
        TextView tvDate = listViewItem.findViewById(R.id.textViewDate);
        TextView tvTime = listViewItem.findViewById(R.id.textViewTime);
        TextView tvCondition = listViewItem.findViewById(R.id.textViewCondition);

        BloodPressure bp = bloodPressureList.get(position);
        tvUserID.setText(context.getString(R.string.tvUserID) + bp.getUserID());
        tvSystolic.setText(context.getString(R.string.tvSystolic) + bp.getSystolic());
        tvDiastolic.setText(context.getString(R.string.tvDiastolic) + bp.getDiastolic());
        tvDate.setText(context.getString(R.string.tvDate) + dateToString(bp.getDate()));
        tvTime.setText(context.getString(R.string.tvTime) + timeToString(bp.getTime()));
        tvCondition.setText(context.getString(R.string.tvCondition) + bp.getCondition());

        return listViewItem;
    }

}
