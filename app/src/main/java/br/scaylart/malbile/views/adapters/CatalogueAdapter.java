package br.scaylart.malbile.views.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;

import java.util.ArrayList;

import br.scaylart.malbile.R;
import br.scaylart.malbile.reader.model.MangaEden;
import br.scaylart.malbile.utils.PaletteBitmapTarget;
import br.scaylart.malbile.utils.PaletteBitmapTranscoder;
import br.scaylart.malbile.utils.PaletteUtils;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.PaletteBitmapWrapper;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.CatalogueViewHolder> {
    public ArrayList<MangaEden> records;
    Context context;

    public CatalogueAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CatalogueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.item_catalogue, parent, false);

        return new CatalogueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CatalogueViewHolder holder, int position) {
        MangaEden item = records.get(position);

        holder.renderView(context, item);
    }

    public void setRecords(ArrayList<MangaEden> newRecords) {
        if (records == newRecords)
            return;
        records = newRecords;

        if (records != null) {
            notifyDataSetChanged();
        }
    }

    public MangaEden getDataByIndex(int position) {
        if (records != null) {
            return records.get(position);
        } else
            return null;
    }

    @Override
    public int getItemCount() {
        return (records != null) ? records.size() : 0;
    }

    public static class CatalogueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mThumbnailImageView;
        private View mMaskView;
        private TextView mNameTextView;
        private LinearLayout mFooterView;

        private int mDefaultPrimary = -1;
        private int mDefaultAccent = -1;
        private Drawable mPlaceHolderDrawable;
        private Drawable mErrorHolderDrawable;

        public CatalogueViewHolder(View v) {
            super(v);
            mThumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnailImageView);
            mMaskView = itemView.findViewById(R.id.maskImageView);
            mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mFooterView = (LinearLayout) itemView.findViewById(R.id.footerLinearLayout);
        }

        @Override
        public void onClick(View v) {

        }

        public void renderView(Context context, MangaEden manga) {
            if (mDefaultPrimary < 0) {
                mDefaultPrimary = context.getResources().getColor(R.color.primaryBlue500);
            }
            if (mDefaultAccent < 0) {
                mDefaultAccent = context.getResources().getColor(R.color.accentPinkA200);
            }
            if (mPlaceHolderDrawable == null) {
                mPlaceHolderDrawable = context.getResources().getDrawable(R.drawable.ic_image_white_48dp);
                mPlaceHolderDrawable.setColorFilter(mDefaultAccent, PorterDuff.Mode.MULTIPLY);
            }
            if (mErrorHolderDrawable == null) {
                mErrorHolderDrawable = context.getResources().getDrawable(R.drawable.ic_error_white_48dp);
                mErrorHolderDrawable.setColorFilter(mDefaultAccent, PorterDuff.Mode.MULTIPLY);
            }

            setName(manga.getTitle());
            setMask(mDefaultPrimary);
            setFooter(mDefaultPrimary);
            setThumbnail(context, manga.getImageUrl(), mDefaultAccent);
        }

        private void setThumbnail(Context context, String thumbnailUrl, final int defaultColor) {
            mThumbnailImageView.setScaleType(ImageView.ScaleType.CENTER);

            Glide.with(context)
                    .load(thumbnailUrl)
                    .asBitmap()
                    .transcode(new PaletteBitmapTranscoder(), PaletteBitmapWrapper.class)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .animate(android.R.anim.fade_in)
                    .placeholder(mPlaceHolderDrawable)
                    .error(mErrorHolderDrawable)
                    .fitCenter()
                    .into(new PaletteBitmapTarget(mThumbnailImageView) {
                        @Override
                        public void onResourceReady(PaletteBitmapWrapper resource, GlideAnimation<? super PaletteBitmapWrapper> glideAnimation) {
                            mThumbnailImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            super.onResourceReady(resource, glideAnimation);

                            int color = PaletteUtils.getColorWithDefault(resource.getPalette(), defaultColor);
                            setMask(color);
                            setFooter(color);
                        }
                    });
        }

        private void setMask(int color) {
            GradientDrawable maskDrawable = new GradientDrawable();
            maskDrawable.setColor(color);
            mMaskView.setBackgroundDrawable(maskDrawable);
        }

        private void setName(String name) {
            mNameTextView.setText(name);
        }

        private void setFooter(int color) {
            GradientDrawable footerDrawable = new GradientDrawable();
            footerDrawable.setCornerRadii(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f, 4.0f});
            footerDrawable.setColor(color);
            mFooterView.setBackgroundDrawable(footerDrawable);
        }
    }
}
