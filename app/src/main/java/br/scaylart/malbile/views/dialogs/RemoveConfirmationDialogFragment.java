package br.scaylart.malbile.views.dialogs;

import android.app.AlertDialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import br.scaylart.malbile.R;
import br.scaylart.malbile.views.activities.DetailActivity;

public class RemoveConfirmationDialogFragment extends DialogFragment {

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_remove);
        builder.setMessage(R.string.dialog_message_remove);
        builder.setNegativeButton(R.string.dialog_label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.dialog_label_remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                ((DetailActivity) getActivity()).onRemoveConfirmed();
                dismiss();
            }
        });
        return builder.create();
    }
}