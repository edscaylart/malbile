package br.scaylart.malbile.views.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.QueryManager;
import br.scaylart.malbile.controllers.networks.BaseService;
import br.scaylart.malbile.models.BaseRecord;
import br.scaylart.malbile.utils.wrappers.RequestWrapper;
import br.scaylart.malbile.views.adapters.SimilarAdapter;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SimilarDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private onListClickListener callback;
    private SimilarAdapter<BaseRecord> adapter;
    private ListView mListView;
    private RelativeLayout mEmptyRelativeLayout;
    private RequestWrapper mRequest;
    private BaseRecord selectedRecord;

    Subscription mQuerySubscription;
    ArrayList<BaseRecord> listarray = new ArrayList<>();

    private View makeSimilarPicker() {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_similar_series, null);

        mListView = (ListView) view.findViewById(R.id.listView);
        mEmptyRelativeLayout = (RelativeLayout) view.findViewById(R.id.emptyRelativeLayout);

        adapter = new SimilarAdapter<>(getActivity(), listarray);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);

        mRequest = getArguments().getParcelable("request");

        initializeEmptyRelativeLayout();

        queryReviewFromNetwork();

        String type = mRequest.getListType().equals(BaseService.ListType.ANIME) ? "anime" : "manga";
        ((TextView) view.findViewById(R.id.dialogTitle)).setText("Choose a similar " + type + " title");

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), getTheme());
        builder.setView(makeSimilarPicker());
        builder.setPositiveButton(R.string.dialog_label_update, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (selectedRecord != null) {
                    callback.onSelectSimilarDialogPicker(selectedRecord);
                    dismiss();
                }
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

    public void initializeEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setImageResource(R.drawable.ic_file_download_white_48dp);
            ((ImageView) mEmptyRelativeLayout.findViewById(R.id.emptyImageView)).setColorFilter(getResources().getColor(R.color.accentPinkA200), PorterDuff.Mode.MULTIPLY);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.emptyTextView)).setText(R.string.no_library);
            ((TextView) mEmptyRelativeLayout.findViewById(R.id.instructionsTextView)).setText(R.string.library_instructions);

            showEmptyRelativeLayout();
        }
    }

    public void hideEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.GONE);
        }
    }

    public void showEmptyRelativeLayout() {
        if (mEmptyRelativeLayout != null) {
            mEmptyRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedRecord = adapter.getItem(position);
        adapter.setSelectedIndex(position);
    }

    public void queryReviewFromNetwork() {
        if (mQuerySubscription != null) {
            mQuerySubscription.unsubscribe();
            mQuerySubscription = null;
        }

        mQuerySubscription = QueryManager.querySimilarLibrary(mRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<BaseRecord>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(ArrayList<BaseRecord> records) {
                        listarray = records;
                        if (adapter != null) {
                            adapter.supportAddAll(listarray);
                            adapter.notifyDataSetChanged();
                        }

                        if (records != null && records.size() > 0) {
                            hideEmptyRelativeLayout();
                        } else {
                            showEmptyRelativeLayout();
                        }
                    }
                });
    }

    /**
     * Set the Callback for update purpose.
     *
     * @param callback The activity/fragment where the callback is located
     * @return NumberPickerDialogFragment This will return the dialog itself to make init simple
     */
    public SimilarDialogFragment setOnSendClickListener(onListClickListener callback) {
        this.callback = callback;
        return this;
    }

    /**
     * The interface for callback
     */
    public interface onListClickListener {
        void onSelectSimilarDialogPicker(BaseRecord record);
    }
}
