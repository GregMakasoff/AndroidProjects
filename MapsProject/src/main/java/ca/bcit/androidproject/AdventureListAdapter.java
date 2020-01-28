package ca.bcit.androidproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class AdventureListAdapter extends ArrayAdapter<Adventure> {
    private Context mContext;
    private int mResource;

    public AdventureListAdapter(@NonNull Context context, int resource, @NonNull List<Adventure> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint({"ViewHolder", "DefaultLocale"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Adventure adventure = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.text_view_adventure_title);
        TextView tvRating = (TextView) convertView.findViewById(R.id.text_view_adventure_rating);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.text_view_adventure_description);

        assert adventure != null;
        tvTitle.setText(adventure.getTitle());
        tvRating.setText(String.format("%1.1f", adventure.getRating()));
        tvDescription.setText(adventure.getDescription());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewMapsActivity.class);
                intent.putExtra("adventure", adventure);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
