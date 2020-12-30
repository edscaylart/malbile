package br.scaylart.malbile.presenters;

import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.app.FragmentActivity;

import java.io.File;
import java.io.IOException;

import br.scaylart.malbile.BuildConfig;
import br.scaylart.malbile.MalbileApplication;
import br.scaylart.malbile.R;
import br.scaylart.malbile.controllers.MalbileManager;
import br.scaylart.malbile.presenters.listeners.SettingsPresenter;
import br.scaylart.malbile.presenters.mapper.SettingsMapper;
import br.scaylart.malbile.utils.DiskUtils;
import br.scaylart.malbile.views.listeners.SettingsView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SettingsPresenterImpl implements SettingsPresenter {
    public static final String TAG = SettingsPresenterImpl.class.getSimpleName();

    private SettingsView mSettingsView;
    private SettingsMapper mSettingsMapper;

    public SettingsPresenterImpl(SettingsView settingsView, SettingsMapper settingsMapper) {
        mSettingsView = settingsView;
        mSettingsMapper = settingsMapper;
    }

    @Override
    public void initializeDownloadDirectory() {
        ListPreference downloadPreference = mSettingsMapper.getDownloadStoragePreference();
        if (downloadPreference != null) {
            String[] downloadDirectories = DiskUtils.getStorageDirectories();

            downloadPreference.setEntries(downloadDirectories);
            downloadPreference.setEntryValues(downloadDirectories);

            downloadPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String downloadDirectory = (String)newValue;
                    if (downloadDirectory != null) {
                        File actualDirectory = new File(downloadDirectory);

                        if (!actualDirectory.equals(MalbileApplication.getInstance().getFilesDir())) {
                            boolean isWritable = actualDirectory.mkdirs();

                            try {
                                File tempFile = File.createTempFile("tempTestDirectory", "0", actualDirectory);
                                tempFile.delete();

                                isWritable = true;
                            } catch (IOException e) {
                                isWritable = false;
                            }

                            if (!isWritable) {
                                mSettingsView.toastExternalStorageError();

                                return false;
                            }
                        }
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public void initializeViews() {
        mSettingsView.initializeToolbar();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_clear_image_cache_key))) {
            clearImageCache();
            return true;
        }/* else if (preference.getKey().equals(mSettingsView.getContext().getString(R.string.preference_view_open_source_licenses_key))) {
            viewOpenSourceLicenses();
            return true;
        } */

        return false;
    }

    private void clearImageCache() {
        MalbileManager
                .clearImageCache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        mSettingsView.toastClearedImageCache();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        // Do Nothing.
                    }
                });
    }

    private void viewOpenSourceLicenses() {
       /* if (((FragmentActivity)mSettingsView.getContext()).getSupportFragmentManager().findFragmentByTag(OpenSourceLicensesFragment.TAG) == null) {
            OpenSourceLicensesFragment openSourceLicensesFragment = new OpenSourceLicensesFragment();

            openSourceLicensesFragment.show(((FragmentActivity) mSettingsView.getContext()).getSupportFragmentManager(), OpenSourceLicensesFragment.TAG);
        } */
    }
}
