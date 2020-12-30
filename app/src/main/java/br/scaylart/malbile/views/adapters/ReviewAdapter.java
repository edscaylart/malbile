package br.scaylart.malbile.views.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;

import java.util.ArrayList;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.models.Review;
import br.scaylart.malbile.utils.PaletteBitmapTarget;
import br.scaylart.malbile.utils.PaletteBitmapTranscoder;
import br.scaylart.malbile.utils.PaletteUtils;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.PaletteBitmapWrapper;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    public ArrayList<Review> records;
    public ListType listType;
    Context context;

    public ReviewAdapter(Context context, ListType listType) {
        this.context = context;
        this.listType = listType;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_detail_review, parent, false);

        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review item = records.get(position);
        holder.vUsername.setText(item.getUsername());
        holder.setThumbnail(context, item.getThumbnailUrl());

        holder.vData.setText(item.getData());
        String helpful = StringUtils.getString(R.string.detail_review_foundhelpful)
                .replace("[0]", item.getFoundHelpful());
        holder.vHelpful.setText(helpful);
        holder.vCommentary.setText(item.getCommentaryLess());
    }

    public void setRecords(ArrayList<Review> newRecords) {
        if (records == newRecords)
            return;

        records = newRecords;

        if (records != null) {
            notifyDataSetChanged();
        }
    }

    public Review getDataByIndex(int position) {
        if (records != null) {
            return records.get(position);
        } else
            return null;
    }

    @Override
    public int getItemCount() {
        return (records != null) ? records.size() : 0;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView vCoverImage;
        protected TextView vUsername;
        protected TextView vData;
        protected TextView vHelpful;
        protected TextView vCommentary;

        private int mDefaultAccent = -1;
        private Drawable mPlaceHolderDrawable;
        private Drawable mErrorHolderDrawable;

        public ReviewViewHolder(View v) {
            super(v);
            vCoverImage = (ImageView) v.findViewById(R.id.reviewImage);
            vUsername = (TextView) v.findViewById(R.id.reviewUsername);
            vData = (TextView) v.findViewById(R.id.reviewData);
            vHelpful = (TextView) v.findViewById(R.id.reviewFoundHelpful);
            vCommentary = (TextView) v.findViewById(R.id.reviewCommentary);
        }

        @Override
        public void onClick(View v) {

        }

        private void setThumbnail(Context context, String thumbnailUrl) {
            vCoverImage.setScaleType(ImageView.ScaleType.CENTER);

            if (mDefaultAccent < 0) {
                mDefaultAccent = context.getResources().getColor(R.color.accentPinkA200);
            }
            if (mPlaceHolderDrawable == null) {
                mPlaceHolderDrawable = context.getResources().getDrawable(R.drawable.abc_ic_menu_copy_mtrl_am_alpha); //ic_image_white_48dp
                mPlaceHolderDrawable.setColorFilter(mDefaultAccent, PorterDuff.Mode.MULTIPLY);
            }
            if (mErrorHolderDrawable == null) {
                mErrorHolderDrawable = context.getResources().getDrawable(R.drawable.abc_ic_menu_copy_mtrl_am_alpha); //ic_error_white_48dp
                mErrorHolderDrawable.setColorFilter(mDefaultAccent, PorterDuff.Mode.MULTIPLY);
            }

            Glide.with(context)
                    .load(thumbnailUrl)
                    .asBitmap()
                    .transcode(new PaletteBitmapTranscoder(), PaletteBitmapWrapper.class)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .animate(android.R.anim.fade_in)
                    .placeholder(mPlaceHolderDrawable)
                    .error(mErrorHolderDrawable)
                    .fitCenter()
                    .into(new PaletteBitmapTarget(vCoverImage) {
                        @Override
                        public void onResourceReady(PaletteBitmapWrapper resource, GlideAnimation<? super PaletteBitmapWrapper> glideAnimation) {
                            vCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            super.onResourceReady(resource, glideAnimation);

                            int color = PaletteUtils.getColorWithDefault(resource.getPalette(), mDefaultAccent);
                        }
                    });
        }
    }
}