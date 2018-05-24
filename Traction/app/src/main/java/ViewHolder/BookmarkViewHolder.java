package ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import Models.BookmarkModel;
import bagga.com.traction.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Davin12x on 16-07-09.
 */
public class BookmarkViewHolder extends RecyclerView.ViewHolder {

    private CircleImageView imageView;
    private TextView message,address;
    View v;

    public BookmarkViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        imageView = (CircleImageView)itemView.findViewById(R.id.bookmark_image);
        message = (TextView)itemView.findViewById(R.id.bookmark_activityName);
        address = (TextView)itemView.findViewById(R.id.bookmark_addreess);
    }

    public void updateUI(BookmarkModel model) {
        message.setText(model.getName());
        address.setText(model.getAddress());
        Glide.with(v.getContext())
                .load(model.getImageUrl())
                .centerCrop()
                .crossFade()
                .into(imageView);
    }
}
