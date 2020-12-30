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
import br.scaylart.malbile.models.Recommendation;
import br.scaylart.malbile.models.Review;
import br.scaylart.malbile.utils.PaletteBitmapTarget;
import br.scaylart.malbile.utils.PaletteBitmapTranscoder;
import br.scaylart.malbile.utils.PaletteUtils;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.PaletteBitmapWrapper;

public class UserRecsAdapter extends RecyclerView.Adapter<UserRecsAdapter.UserRecsViewHolder> {
    public ArrayList<Recommendation> records;
    public ListType listType;
    Context context;

    public UserRecsAdapter(Context context, ListType listType) {
        this.context = context;
        this.listType = listType;
    }

    @Override
    public UserRecsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_detail_recommendation, parent, false);

        return new UserRecsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserRecsViewHolder holder, int position) {
        Recommendation item = records.get(position);
        holder.vTitle.setText(item.getTitle());
        holder.setThumbnail(context, item.getThumbnailUrl());
        holder.vRecommendedBy.setText(item.getRecommendedBy());
        holder.vCommentary.setText(item.getCommentary());
    }

    public void setRecords(ArrayList<Recommendation> newRecords) {
        if (records == newRecords)
            return;

        records = newRecords;

        if (records != null) {
            notifyDataSetChanged();
        }
    }

    public Recommendation getDataByIndex(int position) {
        if (records != null) {
            return records.get(position);
        } else
            return null;
    }

    @Override
    public int getItemCount() {
        return (records != null) ? records.size() : 0;
    }

    public static class UserRecsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView vCoverImage;
        protected TextView vTitle;
        protected TextView vRecommendedBy;
        protected TextView vCommentary;

        private int mDefaultAccent = -1;
        private Drawable mPlaceHolderDrawable;
        private Drawable mErrorHolderDrawable;

        public UserRecsViewHolder(View v) {
            super(v);
            vCoverImage = (ImageView) v.findViewById(R.id.recsImage);
            vTitle = (TextView) v.findViewById(R.id.recsTitle);
            vRecommendedBy = (TextView) v.findViewById(R.id.recommendedBY);
            vCommentary = (TextView) v.findViewById(R.id.recsCommentary);
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