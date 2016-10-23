package com.example.thinha.newyorktimesarticlesearch.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thinha.newyorktimesarticlesearch.R;
import com.example.thinha.newyorktimesarticlesearch.model.Article;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ThiNha on 10/21/2016.
 */
public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int NORMAL = 0;
    private final int NO_IMAGE = 1;
    List<Article> mArticles;
    private ListClickListener mListClickListener;


    public interface ListClickListener {
        void onArticleItemClick(Article article);
    }

    public void setListClickListener(ListClickListener listClickListener) {
        mListClickListener = listClickListener;
    }

    public ArticleAdapter()
    {
        this.mArticles= new ArrayList<>();
    }

    public void setArticle(List<Article> articles)
    {
        mArticles.clear();
        mArticles.addAll(articles);
        notifyDataSetChanged();
    }

    public void addArticle (List<Article> articles)
    {
        mArticles.addAll(articles);
        notifyItemRangeInserted(mArticles.size(),articles.size());
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case NO_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_article_no_image, parent, false);

                return new NoImageViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_article, parent, false);
                return new ViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mArticles.get(position);
        if (article.getThumbNail()!=null && !article.getThumbNail().isEmpty())
        {
            return NORMAL;
        }
        return NO_IMAGE;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Article article = mArticles.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListClickListener != null)
                    mListClickListener.onArticleItemClick(article);
            }
        });

        switch (getItemViewType(position))
        {
            case NO_IMAGE:
                bindNoImage(article, (NoImageViewHolder)holder);
                break;
            default:
                bindNormal(article, (ViewHolder)holder);
                break;
        }

    }

    private void bindNormal(Article article, ViewHolder holder) {
        holder.tvTitle.setText(article.getHeadLine());
        Glide.with(holder.ivResult.getContext())
                .load(article.getThumbNail())
                .into(holder.ivResult);

    }

    private void bindNoImage(Article article, NoImageViewHolder holder) {
        holder.tvTitle.setText(article.getHeadLine());
    }


    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.ivResult)
        ImageView ivResult;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public ViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);

        }
    }

    public static class NoImageViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        public NoImageViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }

}
