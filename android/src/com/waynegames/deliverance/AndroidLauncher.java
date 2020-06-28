package com.waynegames.deliverance;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AndroidLauncher extends AndroidApplication {

	GoogleSignInAccount signedInAccount;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		//signInSilently();

		initialize(new Deliverance(), config);
	}

	private void signInSilently() {

		GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken("226616651429-oit0968tbhm0d4sdt5ml565a5010c9nl.apps.googleusercontent.com").build();
		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

		if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
			// Already signed in.
			// The signed in account is stored in the 'account' variable.
			signedInAccount = account;
		} else {
			// Haven't been signed-in before. Try the silent sign-in first.
			GoogleSignInClient signInClient = GoogleSignIn.getClient(this, signInOptions);

			signInClient.silentSignIn().addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {

				@Override
				public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
					if (task.isSuccessful()) {
						// The signed in account is stored in the task's result.
						signedInAccount = task.getResult();
					} else {
						startSignInIntent();
					}
				}

			});
		}

	}

	private void startSignInIntent() {
		GoogleSignInClient signInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestIdToken("226616651429-oit0968tbhm0d4sdt5ml565a5010c9nl.apps.googleusercontent.com").build());
		Intent intent = signInClient.getSignInIntent();
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			int statusCode = result.getStatus().getStatusCode();

			Log.d("Logg", "***********************" + statusCode);

			if (result.isSuccess()) {
				// The signed in account is stored in the result.
				signedInAccount = result.getSignInAccount();
			} else {
				String message = result.getStatus().getStatusMessage();

				if (message == null || message.isEmpty()) {
					message = "Unknown Google Sign-In Error!";
				}

				new AlertDialog.Builder(this).setMessage(message).setNeutralButton(android.R.string.ok, null).show();
			}
		}
	}

	private void signOut() {
		GoogleSignInClient signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

		signInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				// at this point, the user is signed out.
			}
		});
	}


}
