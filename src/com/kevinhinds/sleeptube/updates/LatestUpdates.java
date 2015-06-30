/**
 * Package to show the latest updates for a newly installed app
 */
package com.kevinhinds.sleeptube.updates;

import com.kevinhinds.sleeptube.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

/**
 * Show the user the latest updates with the app!
 * 
 * @author khinds
 */
public class LatestUpdates {

	/**
	 * code that will run if it's the first time we've installed the application it will show the "latest update" notes
	 */
	public static void showFirstInstalledNotes(Context context) {

		/** get what version code we have to see if we need to show update notes for this version */
		Boolean showUpdateNotes = false;
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			versionCode = 0;
		}

		/**
		 * if we have a version code different from the one already stored or it doesn't exist yet
		 * "0" then show the dialog and save the new version shown for
		 */
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(context);
		if (wmbPreference.getInt("UPDATE_NOTES_FOR_VERSION", 0) != versionCode) {
			showUpdateNotes = true;
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putInt("UPDATE_NOTES_FOR_VERSION", versionCode);
			editor.commit();
		}

		/** show update dialog if requested */
		if (showUpdateNotes) {
			showAppUpdateNotesAlert(context);
		}
	}

	/**
	 * show the latest update notes alert to the user
	 */
	private static void showAppUpdateNotesAlert(Context context) {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		alertDialog.setTitle("Welcome!");
		String latestUpdateNotes = context.getResources().getString(R.string.latest_update_notes);

		/** if we have latest update notes configured, then show the dialog */
		if (!latestUpdateNotes.equals("")) {
			alertDialog.setMessage(latestUpdateNotes);
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.show();
		}
	}
}