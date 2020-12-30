package br.scaylart.malbile.controllers.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;

public class AccountService extends Service {
    private static Account account;
    private static Context context;
    private Authenticator mAuthenticator;

    public static void create(Context context) {
        AccountService.context = context;
    }

    /**
     * Get the provider whose behavior is being controlled.
     *
     * @return String The provider
     */
    public static String getAuth() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        if (TextUtils.isDigitsOnly(pInfo.versionName.replace(".", "")))
            return ".account.Provider";
        else
            return ".beta.account.Provider";
    }

    /**
     * Get the username of an account.
     *
     * @return String The username
     */
    public static String getUsername() {
        String username = getAccount().name;
        return username;
    }

    /**
     * Get the password of an account.
     *
     * @return String The password
     */
    public static String getPassword() {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount();
        return accountManager.getPassword(account);
    }

    /**
     * Get an Account on the device.
     *
     * @return Account The account
     */
    public static Account getAccount() {
        if (account == null) {
            AccountManager accountManager = AccountManager.get(context);
            Account[] myAccount = accountManager.getAccountsByType(".malbile.SyncAdapter.account");
            account = myAccount.length > 0 ? myAccount[0] : null;
        }
        return account;
    }

    /**
     * Removes an account from the accountmanager.
     */
    public static void deleteAccount() {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.removeAccount(getAccount(), null, null);
        account = null;
    }

    /**
     * Add an account in the accountmanager.
     *
     * @param username The username of the account that will be saved
     * @param password The password of the account that will be saved
     */
    public static void addAccount(String username, String password) {
        AccountManager accountManager = AccountManager.get(context);
        final Account account = new Account(username, ".malbile.SyncAdapter.account");
        accountManager.addAccountExplicitly(account, password, null);
        accountManager.setUserData(account, "accountType", "MyAnimeList");
    }

    /**
     * Add an accesToken to the Account data.
     *
     * @param token The AccesToken which should be stored
     * @param time  The time till the token will expire
     * @return String The token
     */
    public static String setAccesToken(String token, Long time) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.setUserData(getAccount(), "accesToken", token);
        accountManager.setUserData(getAccount(), "accesTokenTime", Long.toString((System.currentTimeMillis() / 1000) + (time - 60)));
        return token;
    }

    /**
     * Get the accesToken.
     * <p/>
     * Note: this method will return null if the accesToken is expired!
     *
     * @return String accesToken
     */
    public static String getAccesToken() {
        AccountManager accountManager = AccountManager.get(context);
        String token = accountManager.getUserData(getAccount(), "accesToken");
        Long expireTime = Long.parseLong(accountManager.getUserData(getAccount(), "accesTokenTime"));
        Long time = System.currentTimeMillis() / 1000;
        Long timeLeft = expireTime - time;
        return timeLeft >= 0 ? token : null;
    }

    /**
     * Set an auth token in the accountmanager.
     *
     * @param refreshToken The auth token of the account that will be saved
     */
    public static void setRefreshToken(String refreshToken) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.setUserData(getAccount(), "refreshToken", refreshToken);
    }

    /**
     * Set an refresh token in the accountmanager.
     */
    public static String getRefreshToken() {
        AccountManager accountManager = AccountManager.get(context);
        return accountManager.getUserData(getAccount(), "refreshToken");
    }

    /**
     * Update a password of an account.
     *
     * @param password The new password for an account
     */
    public static void updatePassword(String password) {
        AccountManager accountManager = AccountManager.get(context);
        accountManager.setPassword(getAccount(), password);
    }

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

    public class Authenticator extends AbstractAccountAuthenticator {
        public Authenticator(Context context) {
            super(context);
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse accountAuthenticatorResponse, String s, String s2, String[] strings, Bundle bundle) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getAuthTokenLabel(String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
            throw new UnsupportedOperationException();
        }
    }
}
