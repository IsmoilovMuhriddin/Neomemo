package uz.ismoilov.muhriddin.neomemo;

import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by ismoi on 8/3/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {
    List<ListViewItem> data;
    private View.OnClickListener mClickListener;


    public static class ViewHolder extends RecyclerView.ViewHolder  {
        // each data item is just a string in this case
        public ImageView memoImage;
        public TextView memoDate;
        public TextView memoText;
        public ViewHolder(View itemview) {
            super(itemview);
            memoImage = (ImageView)itemview.findViewById(R.id.memoImage);
            memoDate = (TextView)itemview.findViewById(R.id.memoDate);
            memoText = (TextView)itemview.findViewById(R.id.memoText);
        }


    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_item_layout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onClick(view);
            }
        });

        return vh;
    }


    public MyAdapter(List<ListViewItem> input) {
        data = input;
    }
    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        holder.memoImage.setImageDrawable(data.get(position).getMemoImage());
        holder.memoDate.setText(data.get(position).getMemoDate());
        holder.memoText.setText(data.get(position).getMemoText());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public ListViewItem getListviewItem(int position){
        ListViewItem ls = new ListViewItem();
        try {
            ls=data.get(position);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return ls;
    }



    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }
}
