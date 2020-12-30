package br.scaylart.malbile.views.dialogs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import br.scaylart.malbile.R;
import br.scaylart.malbile.views.activities.MainActivity;

public class LogoutConfirmationDialogFragment extends DialogFragment {
    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title_logout);
        builder.setMessage(R.string.dialog_message_logout);
        builder.setNegativeButton(R.string.dialog_label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.dialog_label_logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                ((MainActivity) getActivity()).onLogoutConfirmed();
                dismiss();
            }
        });
        return builder.create();
    }
}
