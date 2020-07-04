package com.waynegames.deliverance;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class AndroidLauncher extends AndroidApplication implements AdInterface {

	GoogleSignInAccount signedInAccount;

	InterstitialAd interstitialAd;

	private boolean adShown;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		adShown = false;

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		// Test id: ca-app-pub-3940256099942544/1033173712
		this.interstitialAd = new InterstitialAd(this);
		this.interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

		interstitialAd.setAdListener(new AdListener() {

			@Override
			public void onAdClosed() {
				super.onAdClosed();
				loadInterstitialAd();
				adShown = true;
			}
		});

		loadInterstitialAd();

		initialize(new Deliverance(this), config);
	}

	private void loadInterstitialAd() {
		AdRequest interstitialRequest = new AdRequest.Builder().build();
		interstitialAd.loadAd(interstitialRequest);
	}

	public void showInterstitial() {
		runOnUiThread(new Runnable() {
			public void run() {
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
				} else {
					loadInterstitialAd();
				}
			}
		});
	}

	public void signInSilently(final boolean asked) {

		GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).requestEmail().build();
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
						if(!asked) {
							startSignInIntent();
						}
					}
				}

			});
		}

	}

	public void startSignInIntent() {
		GoogleSignInClient signInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).requestEmail().build());
		Intent intent = signInClient.getSignInIntent();
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 1) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

			if (result.isSuccess()) {
				// The signed in account is stored in the result.
				signedInAccount = result.getSignInAccount();
			} else {
				String message = result.getStatus().getStatusMessage();

				if (message == null || message.isEmpty()) {
					message = "Unknown Google Sign-In Error!";
				}

				//new AlertDialog.Builder(this).setMessage(message).setNeutralButton(android.R.string.ok, null).show();
			}
		}
	}

	public void signOut() {
		GoogleSignInClient signInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

		signInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				// at this point, the user is signed out.
			}
		});
	}

	public void showLeaderboard() {
		if(isSignedIn()) {
			Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)).getLeaderboardIntent("CgkIpd2em8wGEAIQAA").addOnSuccessListener(new OnSuccessListener<Intent>() {
				@Override
				public void onSuccess(Intent intent) {
					startActivityForResult(intent, 9004);
				}
			});
		} else{
			startSignInIntent();
		}
	}

	public void submitScore(int score) {
		if(isSignedIn()) {
			Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this)).submitScore("CgkIpd2em8wGEAIQAA", score);
		}
	}

	public String getSampleRate() {
		AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
	}

	public boolean isSignedIn() {
		return !(GoogleSignIn.getLastSignedInAccount(this) == null);
	}

	public boolean isAdShown() {
		return adShown;
	}

	public void setAdShown(boolean adShown) {
		this.adShown = adShown;
	}
}
