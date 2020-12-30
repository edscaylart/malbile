package br.scaylart.malbile.views.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.factories.DefaultFactory;
import br.scaylart.malbile.reader.model.Chapter;

public class ChapterListingsAdapter extends BaseAdapter {
    private Context mContext;
    private Cursor mCursor;

    private List<String> mRecentChapterUrls;

    private int mColor;

    public ChapterListingsAdapter(Context context) {
        mContext = context;
        mRecentChapterUrls = new ArrayList<>();
        mCursor = null;

        mColor = mContext.getResources().getColor(R.color.primaryBlue500);
    }

    @Override
    public int getCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);

            return Chapter.fromCursor(mCursor);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCursor(Cursor newCursor) {
        if (mCursor == newCursor) {
            return;
        }

        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;
        if (mCursor != null) {
            notifyDataSetChanged();
        } else {
            notifyDataSetInvalidated();
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCursor == null) {
            throw new IllegalStateException("Null Cursor");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Can't Move Cursor to Position " + position);
        }

        ViewHolder viewHolder;
        View currentView = convertView;

        if (currentView == null) {
            currentView = LayoutInflater.from(mContext).inflate(R.layout.item_chapter, parent, false);
            viewHolder = new ViewHolder(currentView);
            currentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) currentView.getTag();
        }

        Chapter currentChapter = (Chapter) getItem(position);
        if (mRecentChapterUrls != null && mRecentChapterUrls.contains(currentChapter.getUrl())) {
            viewHolder.renderView(currentChapter, mContext.getResources().getColor(R.color.secondaryText), true);
        } else {
            viewHolder.renderView(currentChapter, mColor, false);
        }

        return currentView;
    }

    public List<String> getRecentChapterUrls() {
        return mRecentChapterUrls;
    }

    public void setRecentChapterUrls(List<String> recentChapterUrls) {
        mRecentChapterUrls = recentChapterUrls;

        notifyDataSetChanged();
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    private static class ViewHolder {
        private TextView mNameTextView;
        private TextView mDateTextView;
        private TextView mNewTextView;

        public ViewHolder(View itemView) {
            mNameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            mDateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            mNewTextView = (TextView) itemView.findViewById(R.id.newTextView);
        }

        public void renderView(Chapter chapter, int color, boolean alreadyRead) {
            setName(chapter.getTitle());
            setDate(chapter.getDate());
            setNew(chapter.isNewChapter());

            applyColorOverlay(color);

            if (alreadyRead) {
                mNameTextView.setTypeface(mNameTextView.getTypeface(), Typeface.BOLD_ITALIC);
            } else {
                mNameTextView.setTypeface(mNameTextView.getTypeface(), Typeface.BOLD);
            }
        }

        private void setName(String name) {
            mNameTextView.setText(name);
        }

        private void setDate(long date) {
            if (date != DefaultFactory.Chapter.DEFAULT_DATE) {
                Date updatedDate = new Date(date);
                DateFormat createdDateFormatter = DateFormat.getDateInstance();

                mDateTextView.setText(createdDateFormatter.format(updatedDate));
            } else {
                mDateTextView.setText(R.string.chapter_list_item_no_date);
            }
        }

        private void setNew(boolean isNew) {
            if (isNew) {
                mNewTextView.setVisibility(View.VISIBLE);
            } else {
                mNewTextView.setVisibility(View.INVISIBLE);
            }
        }

        private void applyColorOverlay(int color) {
            mNameTextView.setTextColor(color);
            mNewTextView.setTextColor(color);
        }
    }
}
