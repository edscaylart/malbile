package br.scaylart.malbile.controllers.events;

public class NavigationItemSelectEvent {
    private int mSelectedPosition;

    public NavigationItemSelectEvent(int selectedPosition) {
        mSelectedPosition = selectedPosition;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }
}
