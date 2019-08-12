package com.example.apple.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import java.io.InputStream;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<ResponseObject> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;

    MyRecyclerViewAdapter(Context context, List<ResponseObject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    public void setElements(List<ResponseObject> data){
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view,this.mData);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = mData.get(position).getId();
        String title = mData.get(position).getTitle();
        String createdOn = mData.get(position).getCreatedOn();
        int rating = mData.get(position).getRating();

        String url = "https://media.giphy.com/media/"+id+"/giphy.gif";
        holder.textViewTitle.setText(title);
        holder.textViewDate.setText(createdOn);
        holder.ratingBar.setRating(rating);
        new DownloadImageTask((ImageView) holder.imageViewGif).execute(url);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,RatingBar.OnRatingBarChangeListener {
        List<ResponseObject> data;
        TextView textViewTitle;
        TextView textViewDate;
        ImageView imageViewGif;
        Button buttonShare;
        RatingBar ratingBar;

        ViewHolder(final View itemView,List<ResponseObject> data) {
            super(itemView);
            this.data=data;
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            imageViewGif = itemView.findViewById(R.id.imageViewGif);
            buttonShare = (Button)itemView.findViewById(R.id.buttonShare);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingBar);
            buttonShare.setOnClickListener(this);
            ratingBar.setOnRatingBarChangeListener(this);
        }

        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            int position = getAdapterPosition();
            String id = data.get(position).getId();
            data.get(position).setRating((int)rating);
            mClickListener.onRatingChanged(id,(int)rating);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            String id = data.get(position).getId();
            String title = data.get(position).getTitle();
            mClickListener.onShareClick(id,title);
        }
    }

    String getItem(int id) {
        return mData.get(id).getId();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onShareClick(String id,String title);
        void onRatingChanged(String id,int rating);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}