package com.example.felz.felzblog.Data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.felz.felzblog.Model.Blog;
import com.example.felz.felzblog.R;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public Context context;
    public List<Blog> blogList;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row,parent,false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Blog blog = blogList.get(position);
        String imageUrl = null;

        holder.title.setText(blog.getTitle());
        holder.desc.setText(blog.getDesc());

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
        String formattedDate = dateFormat.format(new Date(Long.valueOf(blog.getTimestamp())).getTime());

        holder.timestamp.setText(formattedDate);
        imageUrl = blog.getImage();



//        TODO Use Picasso Library to load Image

        Picasso.with(context).load(imageUrl).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView desc;
        public TextView timestamp;
        public ImageView image;
        String userId;

        public ViewHolder(@NonNull View view, Context ctx) {

            super(view);
             context = ctx;

             title = (TextView) view.findViewById(R.id.postTitleList);
             desc = (TextView) view.findViewById(R.id.postTextList);
             timestamp = (TextView) view.findViewById(R.id.timeStampList);
             image = (ImageView) view.findViewById(R.id.postImageList);
             userId = null;

             view.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
//                     Here we can go to the next Activity
                 }
             });
        }
    }
}
