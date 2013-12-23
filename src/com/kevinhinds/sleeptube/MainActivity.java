package com.kevinhinds.sleeptube;

import java.io.InputStream;

import com.kevinhinds.sleeptube.sound.SoundManager;
import com.kevinhinds.sleeptube.views.GifDecoderView;
import com.kevinhinds.sleeptube.marketplace.MarketPlace;
import com.kevinhinds.sleeptube.updates.LatestUpdates;
import com.kevinhinds.sleeptube.Channel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private SoundManager mSoundManager;
	public Channel[] myChannels;
	private boolean tvOn;
	private int currentChannel;
	private int totalChannels;
	private CountDownTimer countDownTimer = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/** get application context and setup the initial channels */
		setupChannels();

		/** wire up the TV image controls */
		wireOnOffButton();
		wireChannelChanger();

		/** defaults to the TV turned off on the zero channel instance */
		currentChannel = 0;
		setupSound();
		turnOnTV(false);
		updateChannelMessage();

		Button chooseChannelsButton = (Button) findViewById(R.id.chooseChannelsButton);
		chooseChannelsButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showChannelsDialog();
			}
		});

		TextView timerButton = (TextView) findViewById(R.id.timerButton);
		timerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showTimerDialog();
			}
		});

		/** show the latest update notes if the application was just installed */
		LatestUpdates.showFirstInstalledNotes(this);

		/** apply custom fonts to all textViews */
		applyFonts();
	}

	/**
	 * basic collection of objects that pertains to what channel has what images and sounds as well as what channel "number" it will be for the TV
	 */
	protected void setupChannels() {
		myChannels = new Channel[29];
		totalChannels = 29;
		myChannels[0] = new Channel(2, R.raw.white, R.drawable.beach, "Beach");
		myChannels[1] = new Channel(4, R.raw.white, R.drawable.boat, "Boat");
		myChannels[2] = new Channel(7, R.raw.white, R.drawable.brown, "Brown");
		myChannels[3] = new Channel(8, R.raw.white, R.drawable.fire, "Fire");
		myChannels[4] = new Channel(9, R.raw.white, R.drawable.fireflies, "Fireflies");
		myChannels[5] = new Channel(11, R.raw.white, R.drawable.flame, "Flame");
		myChannels[6] = new Channel(12, R.raw.white, R.drawable.forest_rain, "Forest");
		myChannels[7] = new Channel(14, R.raw.white, R.drawable.highway, "Highway");
		myChannels[8] = new Channel(16, R.raw.white, R.drawable.house, "House");
		myChannels[9] = new Channel(17, R.raw.white, R.drawable.lake, "Lake");
		myChannels[10] = new Channel(18, R.raw.white, R.drawable.lights, "Lights");
		myChannels[11] = new Channel(21, R.raw.white, R.drawable.moon_clouds, "Moon");
		myChannels[12] = new Channel(22, R.raw.white, R.drawable.mountain, "Mountain");
		myChannels[13] = new Channel(23, R.raw.white, R.drawable.night, "Night");
		myChannels[14] = new Channel(24, R.raw.white, R.drawable.night_sky, "Sky");
		myChannels[15] = new Channel(27, R.raw.white, R.drawable.northern, "Northern");
		myChannels[16] = new Channel(30, R.raw.white, R.drawable.pink, "Pink");
		myChannels[17] = new Channel(35, R.raw.white, R.drawable.rain, "Rain");
		myChannels[18] = new Channel(36, R.raw.white, R.drawable.relax, "Relax");
		myChannels[19] = new Channel(37, R.raw.white, R.drawable.river, "River");
		myChannels[20] = new Channel(38, R.raw.white, R.drawable.snow_dust, "Snow");
		myChannels[21] = new Channel(40, R.raw.white, R.drawable.stars, "Stars");
		myChannels[22] = new Channel(42, R.raw.white, R.drawable.sunset, "Sunset");
		myChannels[23] = new Channel(44, R.raw.white, R.drawable.venice, "Venice");
		myChannels[24] = new Channel(46, R.raw.white, R.drawable.water, "Water");
		myChannels[25] = new Channel(49, R.raw.white, R.drawable.waterfall, "Waterfall");
		myChannels[26] = new Channel(57, R.raw.white, R.drawable.waves, "Waves");
		myChannels[27] = new Channel(62, R.raw.white, R.drawable.white, "White");
		myChannels[28] = new Channel(89, R.raw.white, R.drawable.wind, "Wind");
	}

	/**
	 * have the onOff button work
	 */
	private void wireOnOffButton() {
		ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
		onOffButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
				if (!tvOn) {
					onOffButton.setImageResource(getResources().getIdentifier("on", "drawable", getPackageName()));
					playSound();
					turnOnTV(true);
				} else {
					onOffButton.setImageResource(getResources().getIdentifier("off", "drawable", getPackageName()));
					stopSound();
					turnOnTV(false);
				}
			}
		});
	}

	/**
	 * update the channel textView that the channel is now changed
	 */
	private void updateChannelMessage() {
		TextView currentChannelTV = (TextView) findViewById(R.id.currentChannel);
		currentChannelTV.setText((CharSequence) "Ch. " + Integer.toString(myChannels[currentChannel].number) + "  " + myChannels[currentChannel].channelName);
	}

	/**
	 * have the channel changer work
	 */
	private void wireChannelChanger() {
		ImageButton channelChangerButtonUp = (ImageButton) findViewById(R.id.channelChangerUp);
		channelChangerButtonUp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				currentChannel = currentChannel + 1;
				if (currentChannel >= totalChannels) {
					currentChannel = 0;
				}
				updateChannel();
			}
		});
		ImageButton channelChangerButtonDown = (ImageButton) findViewById(R.id.channelChangerDown);
		channelChangerButtonDown.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				currentChannel = currentChannel - 1;
				if (currentChannel < 0) {
					currentChannel = totalChannels - 1;
				}
				updateChannel();
			}
		});
	}

	/**
	 * turn on and off tv based on boolean parameter flag
	 * 
	 * @param on
	 */
	private void turnOnTV(boolean on) {

		/** get screen metrics */
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;

		/** adjust the TV cropped image WxH */
		float tvCroppedWidthFloat = (float) (width * .65);
		float tvCroppedHeightFloat = (float) (height * .30);
		int tvCroppedWidth = (int) tvCroppedWidthFloat;
		int tvCroppedHeight = (int) tvCroppedHeightFloat;

		/** adjust the TV cropped image WxH */
		float gifWidthFloat = (float) (width * .50);
		float gifHeightFloat = (float) (height * .30);
		int gifWidth = (int) gifWidthFloat;
		int gifHeight = (int) gifHeightFloat;

		/**
		 * get the relative view to create the animated GIF behind the TV outline
		 */
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.mainLayout2);
		int id = getResources().getIdentifier("tv_cropped", "drawable", getPackageName());
		ImageView imageView = new ImageView(this);
		RelativeLayout.LayoutParams lphw_tv = new RelativeLayout.LayoutParams(tvCroppedWidth, tvCroppedHeight);
		imageView.setImageResource(id);
		imageView.setPadding(0, 0, 0, 0);
		imageView.setLayoutParams(lphw_tv);

		/** remove any existing views currently displaying as the TV image */
		try {
			rl.removeAllViews();
		} catch (Exception e) {

		}

		/**
		 * if we're turning the TV on, then add the animated GIF behind the TV frame, else just add the TV image by itself
		 */
		if (on) {

			/**
			 * get the new animated GIF based on which channel we're on and add it to the relative layout
			 */
			InputStream stream = null;
			stream = getResources().openRawResource(myChannels[currentChannel].image);
			GifDecoderView staticView = new GifDecoderView(this, stream);
			RelativeLayout.LayoutParams lhw = new RelativeLayout.LayoutParams(gifWidth, gifHeight);
			staticView.setLayoutParams(lhw);
			staticView.setPadding(10, 0, 0, 0);
			rl.addView(staticView);
			rl.addView(imageView);

			/**
			 * cleanup any garbage collection here, the GIFs cause the application thread to run out of memory
			 */
			staticView = null;
			System.gc();
			Runtime.getRuntime().gc();
		} else {
			/** turn of the timer if we already have one running and the user turned off the TV */
			if (countDownTimer != null) {
				countDownTimer.cancel();
				TextView timerButton = (TextView) findViewById(R.id.timerButton);
				timerButton.setText("Timer");
				ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
				onOffButton.setImageResource(getResources().getIdentifier("off", "drawable", getPackageName()));
				stopSound();
			}
			rl.addView(imageView);
		}
	}

	/**
	 * play the sound looped forever / the TV is now flagged as 'on'
	 */
	private void playSound() {
		tvOn = true;
		mSoundManager.playLoopedSound(1);
	}

	/**
	 * stop sound and setup the new instance of the next sound to be played / the TV is now flagged as 'off'
	 */
	private void stopSound() {
		tvOn = false;
		mSoundManager.stopSound(1);
		setupSound();
	}

	/**
	 * setup the new soundmanager with the context being "this" and the sound being the currently mapped sound per the currently selected channel
	 */
	private void setupSound() {
		mSoundManager = new SoundManager();
		mSoundManager.initSounds(this);
		mSoundManager.addSound(1, myChannels[currentChannel].sound);
	}

	/**
	 * show the dialog of current channels to choose from
	 */
	private void showChannelsDialog() {

		final CharSequence[] channels = new CharSequence[myChannels.length];

		for (int i = 0; i < myChannels.length; i++) {
			channels[i] = myChannels[i].channelName;
		}

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setIcon(R.drawable.ic_launcher);
		alt_bld.setTitle("Select Channel");

		alt_bld.setSingleChoiceItems(channels, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				Toast.makeText(getBaseContext(), channels[item], Toast.LENGTH_LONG).show();
				currentChannel = item;
				updateChannel();
			}
		});
		alt_bld.show();
	}

	/**
	 * update the TV to the current channel that has been selected
	 */
	private void updateChannel() {
		/**
		 * the TV is automatically turned on if the user changes channels
		 */
		ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
		onOffButton.setImageResource(getResources().getIdentifier("on", "drawable", getPackageName()));

		/**
		 * stop any existing sound, turn the TV on play the new sound and update the channel textView about the channel change
		 */
		stopSound();
		turnOnTV(true);
		playSound();
		updateChannelMessage();
	}

	/**
	 * apply custom fonts to the textView elements of the application
	 */
	private void applyFonts() {
		applyTextViewFont(R.id.textViewWelcome);
		applyTextViewFont(R.id.OnOffText);
		applyTextViewFont(R.id.changeChannelText);
		applyTextViewFont(R.id.currentChannel);
		applyTextViewFont(R.id.sleepSoundly);
		applyTextViewFont(R.id.timerButton);
	}

	/**
	 * apply textview font
	 * 
	 * @param id
	 */
	private void applyTextViewFont(int id) {
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/digitaltech.otf");
		TextView tv = (TextView) findViewById(id);
		tv.setTypeface(tf);
	}

	@Override
	/**
	 * when the activity is destroyed, be sure to stop the sound manager :)
	 */
	protected void onDestroy() {
		super.onDestroy();
		mSoundManager.stopSound(1);
	}

	/**
	 * show choose timer length dialog
	 */
	private void showTimerDialog() {

		final CharSequence[] timerLengths = new CharSequence[7];

		timerLengths[0] = (CharSequence) "Cancel Timer";
		for (int i = 1; i < 7; i++) {
			int timerLength = 1800 * i;
			timerLengths[i] = (CharSequence) parseTimerDialogTime(timerLength);
		}

		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		alt_bld.setIcon(R.drawable.ic_launcher);
		alt_bld.setTitle("Select Timer Length");

		alt_bld.setSingleChoiceItems(timerLengths, 0, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				if (item == 0) {
					Toast.makeText(getBaseContext(), "Timer Cancelled", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getBaseContext(), "Timer set for ( " + timerLengths[item] + ")", Toast.LENGTH_LONG).show();
				}

				startTimer(item);
			}
		});
		alt_bld.show();
	}

	/**
	 * create a nice human readable time string based on the length of timer
	 * 
	 * @param timerLength
	 *            number of seconds of the timer
	 * @return human readable string of hours:minutes of the timer's length
	 */
	private String parseTimerDialogTime(int timerLength) {
		String timerLengthValue = "";
		String tempValue = "";
		int hours = timerLength / 3600;
		int minutes = (timerLength % 3600) / 60;
		int seconds = timerLength % 60;

		if (hours > 0) {
			tempValue = Integer.toString(hours);
			if (hours == 1) {
				timerLengthValue = timerLengthValue + tempValue + " hr ";
			} else {
				timerLengthValue = timerLengthValue + tempValue + " hrs ";
			}
		}
		if (minutes > 0) {
			tempValue = Integer.toString(minutes);
			timerLengthValue = timerLengthValue + tempValue + " min ";
		}
		if (seconds > 0) {
			tempValue = Integer.toString(seconds);
			timerLengthValue = timerLengthValue + tempValue + " sec ";
		}
		return timerLengthValue;
	}

	/**
	 * start the timer based on the chosen item in the start timer dialog
	 * 
	 * @param item
	 */
	private void startTimer(int item) {

		/**
		 * if the user selected item = 0, then it's a request to cancel the timer, so stop it
		 */
		if (item == 0) {
			if (countDownTimer != null) {
				countDownTimer.cancel();
				stopTimer();
			}
		}

		/**
		 * if the TV isn't on when they chose to start the time, then turn it on!
		 */
		if (!tvOn) {
			ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
			onOffButton.setImageResource(getResources().getIdentifier("on", "drawable", getPackageName()));
			playSound();
			turnOnTV(true);
		}

		/**
		 * create a new instance of the Android Countdown timer if another timer is running, cancel it
		 */
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
		int timeOutMilliseconds = 1800 * 1000 * item;
		countDownTimer = new CountDownTimer(timeOutMilliseconds, 1000) {

			/**
			 * set the value of the timer time left each time the timer 'ticks' per second
			 */
			public void onTick(long millisUntilFinished) {
				TextView timerButton = (TextView) findViewById(R.id.timerButton);
				timerButton.setText(getHumanReadableTimeValue(millisUntilFinished));
			}

			/**
			 * get a human readable time value to show to the user for how much time left
			 * 
			 * @param millisUntilFinished
			 * @return human readable time left for the timer
			 */
			private String getHumanReadableTimeValue(long millisUntilFinished) {
				String timerLengthValue = "";
				String tempValue = "";
				int secUntilFinished = (int) millisUntilFinished / 1000;
				int hours = secUntilFinished / 3600;
				int minutes = (secUntilFinished % 3600) / 60;

				if (hours > 0) {
					tempValue = Integer.toString(hours);
					timerLengthValue = timerLengthValue + tempValue + ":";
				}
				if (minutes > 0) {
					tempValue = Integer.toString(minutes);
					timerLengthValue = timerLengthValue + tempValue;
				}
				return timerLengthValue;
			}

			/**
			 * timer has finished on its own event
			 */
			public void onFinish() {
				stopTimer();
			}
		}.start();
	}

	/**
	 * stop timer, either based on user request or it actually finished
	 */
	private void stopTimer() {
		TextView timerButton = (TextView) findViewById(R.id.timerButton);
		timerButton.setText("Timer");
		ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
		onOffButton.setImageResource(getResources().getIdentifier("off", "drawable", getPackageName()));
		stopSound();
		turnOnTV(false);
	}

	/** handle user selecting a menu item */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_bitstreet:
			viewAllPublisherApps();
			break;
		case R.id.menu_fullversion:
			viewPremiumApp();
			break;
		}
		return true;
	}

	/** create the main menu based on if the app is the full version or not */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String isFullVersion = getResources().getString(R.string.is_full_version);
		if (isFullVersion.toLowerCase().equals("true")) {
			getMenuInflater().inflate(R.menu.main_full, menu);
		} else {
			getMenuInflater().inflate(R.menu.main, menu);
		}
		return true;
	}

	/**
	 * view all apps on the device marketplace for current publisher
	 */
	public void viewAllPublisherApps() {
		MarketPlace marketPlace = new MarketPlace(this);
		Intent intent = marketPlace.getViewAllPublisherAppsIntent(this);
		if (intent != null) {
			startActivity(intent);
		}
	}

	/**
	 * view the premium version of this app
	 */
	public void viewPremiumApp() {
		MarketPlace marketPlace = new MarketPlace(this);
		Intent intent = marketPlace.getViewPremiumAppIntent(this);
		if (intent != null) {
			startActivity(intent);
		}
	}
}