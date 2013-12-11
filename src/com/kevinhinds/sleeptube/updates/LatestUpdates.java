/**
 * Package to show the latest updates for a newly installed app
 */
package com.kevinhinds.sleeptube.updates;

import com.kevinhinds.sleeptube.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(context);
		if (wmbPreference.getBoolean("SHOWNUPDATENOTES", true)) {
			SharedPreferences.Editor editor = wmbPreference.edit();
			showAppUpdateNotesAlert(context);
			editor.putBoolean("SHOWNUPDATENOTES", false);
			editor.commit();
		}
	}

	/**
	 * show the latest update notes alert to the user
	 */
	private static void showAppUpdateNotesAlert(Context context) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle("Welcome!");
		String latestUpdateNotes = context.getResources().getString(R.string.latest_update_notes);

		/** if we have latest update notes configured, then show the dialog */
		if (!latestUpdateNotes.equals("")) {
			alertDialog.setMessage(latestUpdateNotes);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.show();
		}
	}
}