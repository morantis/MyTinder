package Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import Models.GridItem;
import bagga.com.traction.R;

/**
 * Created by Davin12x on 16-06-26.
 */
public class GridViewAdapter extends ArrayAdapter<GridItem> {

    private Context context;
    ViewHolder holder;

    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();
    private int layoutResourceId;
    private int textViewResourc;

    public GridViewAdapter(Context context, int resource,  int textViewResourceId, ArrayList<GridItem> objects) {
        super(context, resource,textViewResourceId, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.mGridData = objects;
        this.textViewResourc = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService((Activity.LAYOUT_INFLATER_SERVICE));
            view = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) view.findViewById(R.id.titleCard);
            holder.image = (ImageView) view.findViewById(R.id.imageCard);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        GridItem item = mGridData.get(position);
        //Setting Name
        System.out.println(item.getName());
        holder.imageTitle.setText(item.getName());
        //Setting image
        Glide.with(context).load(item.getImage()).asBitmap().override(200,300)
                .into(holder.image);

        return view;
    }

    @Override
    public int getCount() {
        return mGridData.size();
    }


    static class ViewHolder {
    TextView imageTitle;
    ImageView image;
    ImageView opaqueImage;
}
}
