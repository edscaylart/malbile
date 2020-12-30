package br.scaylart.malbile.views.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.scaylart.malbile.R;
import br.scaylart.malbile.views.NavDrawerItem;
import br.scaylart.malbile.views.NavMenuItem;
import br.scaylart.malbile.views.NavMenuSection;

public class NavigationAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;

    private List<NavDrawerItem> mNavigationItems;
    private int mCurrentPosition;

    public NavigationAdapter(Context context, List<NavDrawerItem> navigationItems, int currentPosition) {
        mContext = context;

        this.inflater = LayoutInflater.from(context);

        mNavigationItems = navigationItems;
        if (mNavigationItems == null) {
            mNavigationItems = new ArrayList<NavDrawerItem>();
        }

        mCurrentPosition = currentPosition;
        if (mCurrentPosition < 0 || mCurrentPosition > mNavigationItems.size() - 1) {
            mCurrentPosition = 0;
        }
    }

    @Override
    public int getCount() {
        return mNavigationItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavigationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View currentView = convertView;

        NavDrawerItem currentNavigationItem = mNavigationItems.get(position);

        if (currentView == null) {
            if (currentNavigationItem.getType() == NavMenuItem.ITEM_TYPE) {
                currentView = inflater.inflate(R.layout.navdrawer_item, parent, false);
                viewHolder = new ViewHolder(currentView, NavMenuItem.ITEM_TYPE);
            } else {
                currentView = inflater.inflate(R.layout.navdrawer_section, parent, false);
                viewHolder = new ViewHolder(currentView, NavMenuSection.SECTION_TYPE);
            }

            currentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) currentView.getTag();
        }

        if (currentNavigationItem.getType() == NavMenuItem.ITEM_TYPE) {
            if (position == mCurrentPosition) {
                viewHolder.renderView(mContext, currentNavigationItem, mContext.getResources().getColor(R.color.accentPinkA200), mContext.getResources().getColor(R.color.accentPinkA200));
            } else {
                viewHolder.renderView(mContext, currentNavigationItem, mContext.getResources().getColor(R.color.icon), mContext.getResources().getColor(R.color.secondaryText));
            }
        } else if (currentNavigationItem.getType() == NavMenuSection.SECTION_TYPE) {
            viewHolder.renderView(mContext, currentNavigationItem, 0, mContext.getResources().getColor(R.color.secondaryText));
        }

        return currentView;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int newPosition) {
        mCurrentPosition = newPosition;

        notifyDataSetInvalidated();
    }

    private static class ViewHolder {
        private ImageView mIconImageView;
        private TextView mTitleTextView;

        public ViewHolder(View itemView, int type) {
            if (type == NavMenuItem.ITEM_TYPE)
                mIconImageView = (ImageView) itemView.findViewById(R.id.navmenu_icon);
            mTitleTextView = (TextView) itemView.findViewById(R.id.navmenu_label);
        }

        public void renderView(Context context, NavDrawerItem navigationItem, int iconColor, int textColor) {
            if (navigationItem.getType() == NavMenuItem.ITEM_TYPE)
                setIcon(context.getResources().getDrawable(navigationItem.getIcon()), iconColor);
            setTitle(context.getResources().getString(navigationItem.getLabel()), textColor);
        }

        private void setIcon(Drawable icon, int iconColor) {
            if (mIconImageView != null && icon != null) {
                mIconImageView.setImageDrawable(icon);
                mIconImageView.setColorFilter(iconColor, PorterDuff.Mode.MULTIPLY);
            }
        }

        private void setTitle(String text, int textColor) {
            mTitleTextView.setText(text);
            mTitleTextView.setTextColor(textColor);
        }
    }
}
