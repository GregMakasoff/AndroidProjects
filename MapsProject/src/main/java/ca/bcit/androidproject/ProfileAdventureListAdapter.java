package ca.bcit.androidproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProfileAdventureListAdapter extends ArrayAdapter<Adventure> {
    private Context mContext;
    private int mResource;

    public ProfileAdventureListAdapter(@NonNull Context context, int resource, @NonNull List<Adventure> objects) {
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

        TextView tvTitle = (TextView) convertView.findViewById(R.id.text_view_adventure_title_profile);
        TextView tvRating = (TextView) convertView.findViewById(R.id.text_view_adventure_rating_profile);
        Button btnEdit = (Button) convertView.findViewById(R.id.button_edit_profile);

        assert adventure != null;
        tvTitle.setText(adventure.getTitle());
        tvRating.setText(String.format("%1.1f", adventure.getRating()));

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditAdventure.class);
                intent.putExtra("adventure", adventure);
                mContext.startActivity(intent);
            }
        });


        return convertView;
    }
}
