package com.kevinhinds.sleeptube;

import java.io.InputStream;

import com.kevinhinds.sleeptube.sound.SoundManager;
import com.kevinhinds.sleeptube.views.GifDecoderView;
import com.kevinhinds.sleeptube.Channel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
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
	private int timerLengthSelection;
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

		/** apply custom fonts to all textViews */
		applyFonts();
	}

	/**
	 * basic collection of objects that pertains to what channel has what images and sounds as well as what channel "number" it will be for the TV
	 */
	protected void setupChannels() {
		myChannels = new Channel[12];
		totalChannels = 12;
		myChannels[0] = new Channel(2, R.raw.white, R.drawable.white, "White Noise");
		myChannels[1] = new Channel(3, R.raw.waterfall, R.drawable.waterfall, "Waterfall");
		myChannels[2] = new Channel(4, R.raw.rain, R.drawable.rain, "Rain");
		myChannels[3] = new Channel(5, R.raw.river, R.drawable.river, "River");
		myChannels[4] = new Channel(6, R.raw.waves, R.drawable.waves, "Waves");
		myChannels[5] = new Channel(7, R.raw.pink, R.drawable.pink, "Pink Noise");
		myChannels[6] = new Channel(8, R.raw.forest, R.drawable.forest, "Forest");
		myChannels[7] = new Channel(9, R.raw.wind, R.drawable.wind, "Wind");
		myChannels[8] = new Channel(10, R.raw.sunset, R.drawable.sunset, "Sunset");
		myChannels[9] = new Channel(11, R.raw.beach, R.drawable.beach, "Beach");
		myChannels[10] = new Channel(12, R.raw.brown, R.drawable.brown, "Brown Noise");
		myChannels[11] = new Channel(13, R.raw.night, R.drawable.night, "Night");
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
		currentChannelTV.setText((CharSequence) "Ch. " + Integer.toString(myChannels[currentChannel].number) + " " + myChannels[currentChannel].channelName);
	}

	/**
	 * have the channel changer work
	 */
	private void wireChannelChanger() {
		ImageButton channelChangerButton = (ImageButton) findViewById(R.id.channelChanger);
		channelChangerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				currentChannel = currentChannel + 1;
				if (currentChannel >= totalChannels) {
					currentChannel = 0;
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
	 * show choose timer length dialog
	 */
	private void showTimerDialog() {

		final CharSequence[] timerLengths = new CharSequence[6];

		timerLengths[0] = (CharSequence) "Cancel Timer";
		for (int i = 1; i < 6; i++) {
			String timerLengthValue = "";
			String tempValue = "";
			int timerLength = 1800 * i;
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

			timerLengths[i] = (CharSequence) timerLengthValue;
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

	private void startTimer(int item) {

		if (item == 0) {
			if (countDownTimer != null) {
				countDownTimer.cancel();
				stopTimer();
			}
		}

		ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
		onOffButton.setImageResource(getResources().getIdentifier("on", "drawable", getPackageName()));
		playSound();
		turnOnTV(true);

		int timerLength = 1800 * item * 1000;
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}
		countDownTimer = new CountDownTimer(timerLength, 1000) {

			public void onTick(long millisUntilFinished) {

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
				TextView timerButton = (TextView) findViewById(R.id.timerButton);
				timerButton.setText(timerLengthValue);
			}

			public void onFinish() {
				stopTimer();
			}
		}.start();
	}

	private void stopTimer() {
		TextView timerButton = (TextView) findViewById(R.id.timerButton);
		timerButton.setText("Timer");
		ImageButton onOffButton = (ImageButton) findViewById(R.id.onOffButton);
		onOffButton.setImageResource(getResources().getIdentifier("off", "drawable", getPackageName()));
		stopSound();
		turnOnTV(false);
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
		 * the channel changer button must change because the user changed the channel
		 */
		ImageButton channelChangerButton = (ImageButton) findViewById(R.id.channelChanger);
		int currentChannelImage = currentChannel;
		channelChangerButton.setImageResource(getResources().getIdentifier("channel" + Integer.toString(currentChannelImage), "drawable", getPackageName()));

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
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Waree-Bold.ttf");
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
}