package br.scaylart.malbile.views.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import br.scaylart.malbile.R;
import br.scaylart.malbile.views.activities.DetailActivity;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    Boolean startDate;
    DatePickerDialog mDateDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        startDate = getArguments().getBoolean("startDate");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        mDateDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        mDateDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DetailActivity) getActivity()).onDialogDismissed(startDate, mDateDialog.getDatePicker().getYear(), mDateDialog.getDatePicker().getMonth() + 1, mDateDialog.getDatePicker().getDayOfMonth());
            }
        });

        mDateDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        mDateDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.dialog_label_remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DetailActivity) getActivity()).onDialogDismissed(startDate, 0, 0, 0);
            }
        });
        return mDateDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
    }
}
