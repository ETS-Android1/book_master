package com.example.book_master_2.Adapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.book_master_2.Model.image_data;
import com.example.book_master_2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class image_adapter extends
        RecyclerView.Adapter<image_adapter.Article_Data_ModelViewHolder> {

    private Context mContext;
    ArrayList<image_data> article_Data_Model;

    public image_adapter(Context context, ArrayList<image_data> article_Data_Model){

        this.article_Data_Model = article_Data_Model;
        this.mContext = context;

    }


    @Override
    public Article_Data_ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.image_item_article, parent, false);
        return new Article_Data_ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Article_Data_ModelViewHolder holder, int position) {

        String imgg = article_Data_Model.get(position).name;

        imgg = imgg.replace("File:","");
        imgg = imgg.replace(".jpg","");
        imgg = imgg.replace(".svg","");
        imgg = imgg.replace(".PNG","");
        imgg = imgg.replace(".JPG","");
        imgg = imgg.replace(".gif","");
        imgg = imgg.replace(".png","");
        holder.textRepoName.setText(imgg);

        Random rnd = new Random();
        int currentColor = Color.argb(10, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.card.setCardBackgroundColor(currentColor);

        holder.textRepoDescription.setText(article_Data_Model.get(position).description);
      //  holder.textLanguage.setText("Language: " + article_Data_Model.get(position).thumb_Url);
        holder.textStars.setText("Stars: " + article_Data_Model.get(position).stargazersCount);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.ic_home_black_24dp)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();

        Glide.with(mContext).load(article_Data_Model.get(position).thumb_Url)
                 .apply(options).into(holder.thumb);
//
//        Glide.with(mContext)
//                .load(article_Data_Model.get(position).thumb_Url)
//                .placeholder(R.drawable.ic_launcher_foreground)
//                .into(holder.thumb);

    }

    public void setarticle_Data_Model(@Nullable List<image_data> repos) {
        if (repos == null) {
            return;
        }
        article_Data_Model.clear();
        article_Data_Model.addAll(repos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return article_Data_Model.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class Article_Data_ModelViewHolder extends RecyclerView.ViewHolder {

        TextView textRepoName;
        TextView textRepoDescription;
        TextView textLanguage;
        TextView textStars;
        ImageView thumb;
        CardView card;

        public Article_Data_ModelViewHolder(View itemView) {
            super(itemView);


            textRepoName = (TextView) itemView.findViewById(R.id.text_repo_name);
            textRepoDescription = (TextView) itemView.findViewById(R.id.text_repo_description);
            textLanguage = (TextView) itemView.findViewById(R.id.text_language);
            textStars = (TextView) itemView.findViewById(R.id.text_stars);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);
            card = (CardView)itemView.findViewById(R.id.card);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

}
