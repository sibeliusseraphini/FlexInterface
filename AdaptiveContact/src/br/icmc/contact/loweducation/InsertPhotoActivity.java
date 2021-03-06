package br.icmc.contact.loweducation;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import br.icmc.contact.Contact;
import br.icmc.contact.ContactOpenCOM;
import br.icmc.contact.ElderlyActivity;
import br.icmc.contact.R;
import br.icmc.contact.Util;
import br.icmc.flexinterface.InteractionLogging;

public class InsertPhotoActivity extends ElderlyActivity {
	private static final int IMAGE_PICK = 0;

	private ImageButton btnPhoto;
	private Button mFinished;
	private Bitmap mPhoto = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.le_insert_photo);

		btnPhoto = (ImageButton) findViewById(R.id.btn_photo);
		mFinished = (Button) findViewById(R.id.finished);
		mFinished.setEnabled(false);

		Util.getUtil().say(getResources().getString(R.string.error_photo));
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	public void click_finished(View view) {
		//InteractionLogging.getInstance().logClick((Button) view);

		
		String msg;
		if (mPhoto == null) {
			msg = getResources().getString(R.string.error_photo);
			InteractionLogging.getInstance().log(InteractionLogging.ERROR, msg);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg).setNeutralButton("Ok", null).show();
			Util.getUtil().say(msg);
		} else {

			DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						// A��o quando o usu�rio clicar no bot�o sim
						Util.getUtil().vibrate();
						click_back(null);
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						// A��o quando o usu�rio clicar no bot�o n�o
						save_contact();
						break;
					}
				}
			};

			Contact.getContact().setPhoto(mPhoto);

			msg = getResources().getString(R.string.add_more_info);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(msg)
					.setPositiveButton(getResources().getString(R.string.yes),
							dialogClickListener)
					.setNegativeButton(getResources().getString(R.string.no),
							dialogClickListener).show();
			Util.getUtil().say(msg);
		}
	}

	public void click_photo(View view) {
		//InteractionLogging.getInstance().logClick((Button) view);
		InteractionLogging.getInstance().log(InteractionLogging.ONCLICK, "Escolher foto");
		
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.setType("image/*");
		startActivityForResult(i, IMAGE_PICK);
	}

	public void click_back(View view) {
		InteractionLogging.getInstance().logClick((Button) view);
		
		
		DialogInterface.OnClickListener dialogClickListener = new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					// A��o quando o usu�rio clicar no bot�o sim
					ContactOpenCOM.getInstance().getFlexAndroid().previousScreen();
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					// A��o quando o usu�rio clicar no bot�o n�o
					break;
				}
			}
		};

		String msg = getResources().getString(R.string.cancel_send_photo);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
				.setPositiveButton(getResources().getString(R.string.yes),
						dialogClickListener)
				.setNegativeButton(getResources().getString(R.string.no),
						dialogClickListener).show();
		Util.getUtil().say(msg);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case IMAGE_PICK:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				mPhoto = BitmapFactory.decodeStream(imageStream);

				btnPhoto.setImageBitmap(mPhoto);

				mFinished.setEnabled(true);
			} else {
				if (mPhoto == null)
					mFinished.setEnabled(false);
			}
		}
	}
}
