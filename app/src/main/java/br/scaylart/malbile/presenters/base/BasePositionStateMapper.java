package br.scaylart.malbile.presenters.base;

import android.os.Parcelable;

public interface BasePositionStateMapper {
    Parcelable getPositionState();

    void setPositionState(Parcelable state);
}
