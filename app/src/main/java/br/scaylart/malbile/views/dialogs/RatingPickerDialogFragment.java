package br.scaylart.malbile.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import br.scaylart.malbile.R;

public class RatingPickerDialogFragment extends DialogFragment implements RatingBar.OnRatingBarChangeListener {
    RatingBar ratingBar;
    TextView flavourText;
    private onUpdateClickListener callback;

    private View makeRatingBarPicker() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_rating_picker, null);

        int current = getArguments().getInt("current");

        ratingBar = (RatingBar) view.findViewById(R.id.dialogRatingBar);
        ratingBar.setRating((float) current / 2);

        flavourText = (TextView) view.findViewById(R.id.ratingBarFlavourText);
        flavourText.setText(getResources().getStringArray(R.array.detail_personal_ratings)[current]);

        ratingBar.setOnRatingBarChangeListener(this);

        ((TextView) view.findViewById(R.id.dialogTitle)).setText(getArguments().getString("title"));

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setView(makeRatingBarPicker());
       // builder.setTitle(getArguments().getString("title"));
        builder.setPositiveButton(R.string.dialog_label_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                ratingBar.clearFocus();
                int rating = (int) ((float) Math.ceil(ratingBar.getRating() * (float) 2.0));
                callback.onUpdatedFromDialogPicker(rating, getArguments().getInt("id"));
                dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dismiss();
            }
        });

        return builder.create();
    }

    /**
     * Set the Callback for update purpose.
     *
     * @param callback The activity/fragment where the callback is located
     * @return RatingPickerDialogFragment This will return the dialog itself to make init simple
     */
    public RatingPickerDialogFragment setOnSendClickListener(onUpdateClickListener callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        int i = (int) ((float) Math.ceil(rating * (float) 2.0));
        flavourText.setText(getResources().getStringArray(R.array.detail_personal_ratings)[i]);
    }

    /**
     * The interface for callback
     */
    public interface onUpdateClickListener {
        void onUpdatedFromDialogPicker(int number, int id);
    }
}
