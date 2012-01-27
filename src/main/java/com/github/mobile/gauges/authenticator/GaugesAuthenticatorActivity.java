package com.github.mobile.gauges.authenticator;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.text.TextUtils.isEmpty;
import static com.github.kevinsawicki.http.HttpRequest.post;
import static com.github.mobile.gauges.authenticator.AuthConstants.GAUGES_ACCOUNT_TYPE;
import static com.github.mobile.gauges.core.GaugesConstants.URL_AUTH;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mobile.gauges.R.id;
import com.github.mobile.gauges.R.layout;
import com.github.mobile.gauges.R.string;
import com.github.mobile.gauges.validation.LeavingBlankTextFieldWarner;
import com.github.mobile.gauges.validation.TextWatcherAdapter;
import com.google.inject.Inject;

import roboguice.activity.RoboAccountAuthenticatorActivity;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

/**
 * Activity to authenticate the user against gaug.es
 */
public class GaugesAuthenticatorActivity extends
		RoboAccountAuthenticatorActivity {

	/**
	 * PARAM_CONFIRMCREDENTIALS
	 */
	public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";

	/**
	 * PARAM_PASSWORD
	 */
	public static final String PARAM_PASSWORD = "password";

	/**
	 * PARAM_USERNAME
	 */
	public static final String PARAM_USERNAME = "username";

	/**
	 * PARAM_AUTHTOKEN_TYPE
	 */
	public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";

	private static final String TAG = "GaugesAuthActivity";

	private AccountManager accountManager;

	@InjectView(id.message)
	private TextView messageText;

	@InjectView(id.et_email)
	private EditText emailText;

	@InjectView(id.et_password)
	private EditText passwordText;

	@InjectView(id.b_signin)
	private Button signinButton;

	@Inject
	private LeavingBlankTextFieldWarner leavingBlankTextFieldWarner;

	private TextWatcher watcher = validationTextWatcher();

	private RoboAsyncTask<Boolean> authenticationTask;
	private String authToken;
	private String authTokenType;

	/**
	 * If set we are just checking that the user knows their credentials; this
	 * doesn't cause the user's password to be changed on the device.
	 */
	private Boolean confirmCredentials = false;

	private String email;

	private String password;

	/**
	 * Was the original caller asking for an entirely new account?
	 */
	protected boolean requestNewAccount = false;

	@Override
	public void onCreate(Bundle icicle) {
		Log.i(TAG, "onCreate(" + icicle + ")");
		super.onCreate(icicle);
		accountManager = AccountManager.get(this);
		Log.i(TAG, "loading data from Intent");
		final Intent intent = getIntent();
		email = intent.getStringExtra(PARAM_USERNAME);
		authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
		requestNewAccount = email == null;
		confirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS,
				false);

		Log.i(TAG, "request new: " + requestNewAccount);

		setContentView(layout.login_activity);

		setNonBlankValidationFor(emailText);
		setNonBlankValidationFor(passwordText);

		TextView signupText = (TextView) findViewById(id.tv_signup);
		signupText.setMovementMethod(LinkMovementMethod.getInstance());
		signupText.setText(Html.fromHtml(getString(string.signup_link)));
	}

	private void setNonBlankValidationFor(EditText editText) {
		editText.addTextChangedListener(watcher);
		editText.setOnFocusChangeListener(leavingBlankTextFieldWarner);
	}

	private TextWatcher validationTextWatcher() {
		return new TextWatcherAdapter() {
			public void afterTextChanged(Editable gitDirEditText) {
				updateUIWithValidation();
			}

		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateUIWithValidation();
	}

	private void updateUIWithValidation() {
		boolean populated = populated(emailText) && populated(passwordText);
		signinButton.setEnabled(populated);
	}

	private boolean populated(EditText editText) {
		return editText.length() > 0;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(getText(string.login_activity_authenticating));
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				Log.i(TAG, "dialog cancel has been invoked");
				if (authenticationTask != null) {
					authenticationTask.cancel(true);
					finish();
				}
			}
		});
		return dialog;
	}

	/**
	 * Handles onClick event on the Submit button. Sends username/password to
	 * the server for authentication.
	 * <p/>
	 * Specified by android:onClick="handleLogin" in the layout xml
	 *
	 * @param view
	 */
	public void handleLogin(View view) {
		Log.d(TAG, "handleLogin hit on" + view);
		if (requestNewAccount)
			email = emailText.getText().toString();
		password = passwordText.getText().toString();
		if (isEmpty(email) || isEmpty(password))
			messageText.setText(getMessage());
		else {
			showProgress();

			authenticationTask = new RoboAsyncTask<Boolean>(this) {
				public Boolean call() throws Exception {
					int response = post(URL_AUTH).send(
							"email=" + email + "&password=" + password).code();
					Log.d(TAG, "response=" + response);
					return response == 200;
				}

				@Override
				protected void onException(Exception e) throws RuntimeException {
					messageText.setText(e.getMessage());
				}

				public void onSuccess(Boolean authSuccess) {
					onAuthenticationResult(authSuccess);
				}

				@Override
				protected void onFinally() throws RuntimeException {
					hideProgress();
				}
			};
			authenticationTask.execute();
		}
	}

	/**
	 * Called when response is received from the server for confirm credentials
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller.
	 *
	 * @param result
	 */
	protected void finishConfirmCredentials(boolean result) {
		Log.i(TAG, "finishConfirmCredentials()");
		final Account account = new Account(email, GAUGES_ACCOUNT_TYPE);
		accountManager.setPassword(account, password);

		final Intent intent = new Intent();
		intent.putExtra(KEY_BOOLEAN_RESULT, result);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Called when response is received from the server for authentication
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller. Also sets
	 * the authToken in AccountManager for this account.
	 */

	protected void finishLogin() {
		Log.i(TAG, "finishLogin()");
		final Account account = new Account(email, GAUGES_ACCOUNT_TYPE);

		if (requestNewAccount)
			accountManager.addAccountExplicitly(account, password, null);
		else
			accountManager.setPassword(account, password);
		final Intent intent = new Intent();
		authToken = password;
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, email);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, GAUGES_ACCOUNT_TYPE);
		if (authTokenType != null
				&& authTokenType.equals(AuthConstants.AUTHTOKEN_TYPE)) {
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, authToken);
		}
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * Hide progress dialog
	 */
	protected void hideProgress() {
		dismissDialog(0);
	}

	/**
	 * Show progress dialog
	 */
	protected void showProgress() {
		showDialog(0);
	}

	/**
	 * Called when the authentication process completes (see attemptLogin()).
	 *
	 * @param result
	 */
	public void onAuthenticationResult(boolean result) {
		Log.i(TAG, "onAuthenticationResult(" + result + ")");
		if (result)
			if (!confirmCredentials)
				finishLogin();
			else
				finishConfirmCredentials(true);
		else {
			Log.e(TAG, "onAuthenticationResult: failed to authenticate");
			if (requestNewAccount)
				messageText.setText("Please enter a valid username/password.");
			else
				messageText.setText("Please enter a valid password.");
		}
	}

	/**
	 * Returns the message to be displayed at the top of the login dialog box.
	 */
	private CharSequence getMessage() {
		return null;
	}
}
