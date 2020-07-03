package com.waynegames.deliverance;

public interface AdInterface {

	void showInterstitial();
	void setAdShown(boolean adShown);
	boolean isAdShown();
	void showLeaderboard();
	void submitScore(int score);
	boolean isSignedIn();
	void startSignInIntent();
	void signOut();
	void signInSilently(boolean asked);

}
