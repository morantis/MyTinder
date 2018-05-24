package Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import Models.BookmarkModel;
import ViewHolder.BookmarkViewHolder;
import bagga.com.traction.R;

/**
 * Created by Davin12x on 16-07-09.
 */
public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkViewHolder> {

    ArrayList<BookmarkModel> modelArrayList;

    public BookmarkAdapter(ArrayList<BookmarkModel> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    @Override
    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookmark_content, parent, false);
        BookmarkViewHolder bookmarkViewHolder = new BookmarkViewHolder(v);
        return bookmarkViewHolder;
    }

    @Override
    public void onBindViewHolder(BookmarkViewHolder holder, int position) {
        BookmarkModel model = modelArrayList.get(position);
        holder.updateUI(model);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}
