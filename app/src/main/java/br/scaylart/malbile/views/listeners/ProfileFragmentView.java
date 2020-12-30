package br.scaylart.malbile.views.listeners;

import br.scaylart.malbile.views.activities.ProfileActivity;

public interface ProfileFragmentView {
    ProfileActivity getContext();

    void initializeViews();

    void initializeData(String username);
}
