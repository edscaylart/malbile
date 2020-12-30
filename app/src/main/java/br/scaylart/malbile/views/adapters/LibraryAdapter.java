package br.scaylart.malbile.views.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;

import java.util.ArrayList;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager.TaskJob;
import br.scaylart.malbile.controllers.networks.BaseService.ListType;
import br.scaylart.malbile.models.Anime;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.models.Manga;
import br.scaylart.malbile.utils.PaletteBitmapTarget;
import br.scaylart.malbile.utils.PaletteBitmapTranscoder;
import br.scaylart.malbile.utils.PaletteUtils;
import br.scaylart.malbile.utils.StringUtils;
import br.scaylart.malbile.utils.wrappers.PaletteBitmapWrapper;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {
    public ArrayList<? extends BaseRecord> records;
    public ListType listType;
    public TaskJob taskJob;
    Context context;

    public LibraryAdapter(Context context, ListType listType, TaskJob taskJob) {
        this.context = context;
        this.listType = listType;
        this.taskJob = taskJob;
    }

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.item_library;
        switch (taskJob) {
            case LIBRARY:
            case SEARCH:
                layout = R.layout.item_library;
                break;
            case MOSTPOPULAR:
            case TOPRATED:
                layout = R.layout.item_library_top_popular;
                break;
            case JUSTADDED:
            case UPCOMING:
                layout = R.layout.item_library_add_upcoming;
                break;
        }
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(layout, parent, false);

        return new LibraryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LibraryViewHolder holder, int position) {
        BaseRecord item = records.get(position);
        holder.vTitle.setText(item.getTitle());
        holder.setThumbnail(context, item.getImageUrl());

        if (taskJob.equals(TaskJob.LIBRARY) || taskJob.equals(TaskJob.SEARCH)) {
            if (listType.equals(ListType.ANIME)) {
                holder.vType.setText(StringUtils.getStringFromResourceArray(R.array.anime_media_type, R.string.unknown, item.getType() - 1));
                holder.vStatus.setText(StringUtils.getStringFromResourceArray(R.array.anime_media_status, R.string.unknown, item.getStatus() - 1));
                if (taskJob.equals(TaskJob.SEARCH)) {
                    holder.vScore.setVisibility(View.GONE);
                    holder.vLabelProgress.setText("Episodes:");
                    holder.vProgress.setText(String.valueOf(((Anime) item).getEpisodes()));
                    holder.vLastUpdate.setVisibility(View.GONE);
                } else {
                    holder.vScore.setVisibility(View.VISIBLE);
                    holder.vScore.setRating((float) ((Anime) item).getScore() / 2);
                    holder.vLabelProgress.setText("Progress:");
                    holder.vProgress.setText(String.valueOf(((Anime) item).getProgress()));
                    holder.vLastUpdate.setVisibility(View.VISIBLE);
                }
            } else {
                holder.vType.setText(StringUtils.getStringFromResourceArray(R.array.manga_media_type, R.string.unknown, item.getType() - 1));
                holder.vStatus.setText(StringUtils.getStringFromResourceArray(R.array.manga_media_status, R.string.unknown, item.getStatus() - 1));
                holder.vProgress.setText(String.valueOf(((Manga) item).getProgress()));
                if (taskJob.equals(TaskJob.SEARCH)) {
                    holder.vScore.setVisibility(View.GONE);
                    holder.vLabelProgress.setText("Chapters:");
                    holder.vProgress.setText(String.valueOf(((Manga) item).getChapters()));
                    holder.vLastUpdate.setVisibility(View.GONE);
                } else {
                    holder.vScore.setVisibility(View.VISIBLE);
                    holder.vLabelProgress.setText("Progress:");
                    holder.vScore.setRating((float) ((Manga) item).getScore() / 2);
                    holder.vProgress.setText(String.valueOf(((Manga) item).getProgress()));
                    holder.vLastUpdate.setVisibility(View.VISIBLE);
                }
            }
        } else if (taskJob.equals(TaskJob.MOSTPOPULAR) || taskJob.equals(TaskJob.TOPRATED)) {
            if (listType.equals(ListType.ANIME)) {
                holder.vType.setText(StringUtils.getStringFromResourceArray(R.array.anime_media_type, R.string.unknown, item.getType() - 1));
                holder.vRank.setText("#" + StringUtils.nullCheck(item.getRank()));
                holder.vLabelProgress.setText("Episodes:");
                holder.vProgress.setText(String.valueOf(((Anime) item).getEpisodes()));
                holder.vMemberScored.setText(StringUtils.nullCheck(item.getMembersScore()));
                holder.vMemberCount.setText(StringUtils.nullCheck(item.getMembersCount()));
            } else {
                holder.vType.setText(StringUtils.getStringFromResourceArray(R.array.manga_media_type, R.string.unknown, item.getType() - 1));
                holder.vRank.setText("#" + StringUtils.nullCheck(item.getRank()));
                holder.vLabelProgress.setText("Volumes:");
                holder.vRelativeType.setVisibility(View.GONE);
                holder.vProgress.setText(String.valueOf(((Manga) item).getVolumes()));
                holder.vMemberScored.setText(StringUtils.nullCheck(item.getMembersScore()));
                holder.vMemberCount.setText(StringUtils.nullCheck(item.getMembersCount()));
            }
        } else if (taskJob.equals(TaskJob.JUSTADDED) || taskJob.equals(TaskJob.UPCOMING)) {
            if (listType.equals(ListType.ANIME)) {
                holder.vType.setText(StringUtils.getStringFromResourceArray(R.array.anime_media_type, R.string.unknown, item.getType() - 1));
                holder.vStartDate.setText(StringUtils.getDate(item.getStartDate()));
                holder.vSynopsis.setText(StringUtils.nullCheck(item.getSynopsis()));
            } else {
                holder.vType.setText(StringUtils.getStringFromResourceArray(R.array.manga_media_type, R.string.unknown, item.getType() - 1));
                holder.vStartDate.setText(StringUtils.getDate(item.getStartDate()));
                holder.vSynopsis.setText(StringUtils.nullCheck(item.getSynopsis()));
            }
        }
    }

    public void setRecords(ArrayList<? extends BaseRecord> newRecords) {
        if (records == newRecords)
            return;

        records = newRecords;

        if (records != null) {
            notifyDataSetChanged();
        }
    }

    public BaseRecord getDataByIndex(int position) {
        if (records != null) {
            return records.get(position);
        } else
            return null;
    }

    @Override
    public int getItemCount() {
        return (records != null) ? records.size() : 0;
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView vCoverImage;
        protected TextView vTitle;
        protected TextView vType;
        protected RatingBar vScore;
        protected CardView vCardStatus;
        protected TextView vStatus;
        protected TextView vProgress;
        protected TextView vLastUpdate;

        protected RelativeLayout vRelativeType;
        protected TextView vRank;
        protected TextView vMemberScored;
        protected TextView vMemberCount;
        protected TextView vLabelProgress;

        protected TextView vStartDate;
        protected TextView vSynopsis;

        private int mDefaultAccent = -1;
        private Drawable mPlaceHolderDrawable;
        private Drawable mErrorHolderDrawable;

        public LibraryViewHolder(View v) {
            super(v);
            vCoverImage = (ImageView) v.findViewById(R.id.coverImage);
            vTitle = (TextView) v.findViewById(R.id.libraryTitle);
            vType = (TextView) v.findViewById(R.id.libraryType);
            vScore = (RatingBar) v.findViewById(R.id.libraryScore);
            vStatus = (TextView) v.findViewById(R.id.libraryStatus);
            vProgress = (TextView) v.findViewById(R.id.libraryProgress);
            vLastUpdate = (TextView) v.findViewById(R.id.libraryLastUpdate);
            vCardStatus = (CardView) v.findViewById(R.id.cardStatus);

            vRelativeType = (RelativeLayout) v.findViewById(R.id.relativeType);
            vRank = (TextView) v.findViewById(R.id.libraryRank);
            vMemberScored = (TextView) v.findViewById(R.id.libraryMemberScored);
            vMemberCount = (TextView) v.findViewById(R.id.libraryMemberCount);
            vLabelProgress = (TextView) v.findViewById(R.id.progressLabel);

            vStartDate = (TextView) v.findViewById(R.id.libraryStartDate);
            vSynopsis = (TextView) v.findViewById(R.id.synopsisLabel);
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
