package br.scaylart.malbile.controllers.events;

public class SelectPageEvent {
    private int mSelectedPage;

    public SelectPageEvent(int selectedPage) {
        mSelectedPage = selectedPage;
    }

    public int getSelectPage() {
        return mSelectedPage;
    }
}
