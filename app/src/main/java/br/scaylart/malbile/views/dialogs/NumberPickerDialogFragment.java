package br.scaylart.malbile.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.R;

public class NumberPickerDialogFragment extends DialogFragment {
    NumberPicker numberPicker;
    private onUpdateClickListener callback;

    private View makeNumberPicker() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
        int max = 0;
        try {
            max = getArguments().getInt("max");
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        int current = getArguments().getInt("current");

        ((TextView) view.findViewById(R.id.dialogTitle)).setText(getArguments().getString("title"));

        numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(max != 0 ? max : 9999);
        numberPicker.setMinValue(0);
        numberPicker.setValue(current);
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setView(makeNumberPicker());
        //builder.setTitle(getArguments().getString("title"));
        builder.setPositiveButton(R.string.dialog_label_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                numberPicker.clearFocus();
                callback.onUpdatedFromDialogPicker(numberPicker.getValue(), getArguments().getInt("id"));
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
     * @return NumberPickerDialogFragment This will return the dialog itself to make init simple
     */
    public NumberPickerDialogFragment setOnSendClickListener(onUpdateClickListener callback) {
        this.callback = callback;
        return this;
    }

    /**
     * The interface for callback
     */
    public interface onUpdateClickListener {
        void onUpdatedFromDialogPicker(int number, int id);
    }
}