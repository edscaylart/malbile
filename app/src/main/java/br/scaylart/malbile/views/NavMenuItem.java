package br.scaylart.malbile.views;

import android.content.Context;

public class NavMenuItem implements NavDrawerItem {
    public static final int ITEM_TYPE = 2;

    private int id;
    private int label;
    private int icon;

    private NavMenuItem() {
    }

    public static NavMenuItem create(int id, int label, int icon) {
        NavMenuItem item = new NavMenuItem();
        item.setId(id);
        item.setLabel(label);
        item.setIcon(icon);
        return item;
    }

    @Override
    public int getType() {
        return ITEM_TYPE;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    @Override
    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
