package com.example.book_master_2.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
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
import com.example.book_master_2.Model.atricle_data;
import com.example.book_master_2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import info.bliki.wiki.model.WikiModel;

public class category_adapter extends
        RecyclerView.Adapter<category_adapter.Article_Data_ModelViewHolder> {

    private Context mContext;
    ArrayList<atricle_data> atricledata;

    public category_adapter(Context context, ArrayList<atricle_data> atricledata){

        this.atricledata = atricledata;
        this.mContext = context;

    }


    @Override
    public Article_Data_ModelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_article, parent, false);
        return new Article_Data_ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Article_Data_ModelViewHolder holder, int position) {
        holder.textRepoName.setText(atricledata.get(position).name);

        WikiModel wikiModel =
                new WikiModel("https://www.mywiki.com/wiki/${image}",
                        "https://www.mywiki.com/wiki/${title}");
        String htmlStr = wikiModel.render(atricledata.get(position).description);

        String a = htmlStr;
        a = a.replace("{{","");
        a = a.replace("}}","");
        a = a.replace("File:","");
        a = a.replace(".jpg","");
        a = a.replace(".svg","");
        a = a.replace(".PNG","");
        a = a.replace(".JPG","");
        a = a.replace(".gif","");
        a = a.replace(".png","");
        holder.textRepoDescription.setText(Html.fromHtml(a));
      //  holder.textLanguage.setText("Language: " + article_Data_Model.get(position).thumb_Url);
        holder.textStars.setText("Stars: " + atricledata.get(position).stargazersCount);


        RequestOptions options = new RequestOptions()
                .centerCrop()
                .error(R.drawable.categories)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH)
                .dontAnimate()
                .dontTransform();

        Glide.with(mContext).load(atricledata.get(position).thumb_Url)
                 .apply(options).into(holder.thumb);
//
//        Glide.with(mContext)
//                .load(article_Data_Model.get(position).thumb_Url)
//                .placeholder(R.drawable.ic_launcher_foreground)
//                .into(holder.thumb);

        Random rnd = new Random();
        int currentColor = Color.argb(128, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.c1.setCardBackgroundColor(currentColor);

    }

    public void setarticle_Data_Model(@Nullable List<atricle_data> repos) {
        if (repos == null) {
            return;
        }
        atricledata.clear();
        atricledata.addAll(repos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount(){
        return atricledata.size();
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
        CardView c1;

        public Article_Data_ModelViewHolder(View itemView) {
            super(itemView);

            c1 = itemView.findViewById(R.id.c1);

            textRepoName = (TextView) itemView.findViewById(R.id.text_repo_name);
            textRepoDescription = (TextView) itemView.findViewById(R.id.text_repo_description);
            textLanguage = (TextView) itemView.findViewById(R.id.text_language);
            textStars = (TextView) itemView.findViewById(R.id.text_stars);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });



        }
    }

}
