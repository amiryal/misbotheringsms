/*
 * This file is part of No Stranger SMS.
 *
 * No Stranger SMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * No Stranger SMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with No Stranger SMS.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.glesik.nostrangersms;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class JunkSMSList extends Activity {

	List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
	boolean isListEmpty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_junksmslist);
		ListView smsListView = (ListView) findViewById(R.id.smsListView);
		registerForContextMenu(smsListView);
		refreshList();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    // Update messages when notification is clicked and activity is visible.
	    refreshList();
	    setIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.junk_smslist, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_all:
			// Delete all messages.
			AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
			confirmDialog.setMessage(R.string.delete_all_confirm);
			confirmDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DatabaseHandler db = new DatabaseHandler(getApplicationContext());
					db.deleteAllSms();
					refreshList();
				}
			});
			confirmDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			confirmDialog.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		// Disable 'delete all' menu if list is empty.
		MenuItem mi = menu.findItem(R.id.delete_all);
		mi.setEnabled(!isListEmpty);
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// Inflate context menu.
		getMenuInflater().inflate(R.menu.sms_list_context, menu);
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		try {
			int id = Integer.parseInt(listData.get(info.position).get("id"));
			DatabaseHandler db = new DatabaseHandler(this);
			SMSMessage sms = new SMSMessage();
			sms = db.getSms(id);
			switch (item.getItemId()) {
			case R.id.add_to_contacts:
				String sender = sms.getSender();
				// Creates a new Intent to insert or edit a contact.
				Intent intentInsertEdit = new Intent(
						Intent.ACTION_INSERT_OR_EDIT);
				// Sets the MIME type.
				intentInsertEdit.setType(Contacts.CONTENT_ITEM_TYPE);
				intentInsertEdit.putExtra(Intents.Insert.PHONE, sender);
				// Sends the Intent with an request ID.
				startActivity(intentInsertEdit);
				return true;
			case R.id.copy_text:
				String message = sms.getMessage();
				int sdk = android.os.Build.VERSION.SDK_INT;
				if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
					android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					clipboard.setText(message);
				} else {
					android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
					android.content.ClipData clip = android.content.ClipData
							.newPlainText("SMS Text", message);
					clipboard.setPrimaryClip(clip);
				}
				return true;
			case R.id.move_to_inbox:
				ContentValues values = new ContentValues();
				values.put("address", sms.getSender());
				values.put("body", sms.getMessage());
				getContentResolver().insert(Uri.parse("content://sms/inbox"),
						values);
				db.deleteSms(sms);
				refreshList();
				return true;
			case R.id.delete:
				db.deleteSms(sms);
				refreshList();
				return true;
			default:
				return super.onContextItemSelected(item);
			}
		} catch (NumberFormatException nfe) {
			return super.onContextItemSelected(item);
		}
	}

	public void refreshList() {
		listData.clear();
		// Getting all messages from DB.
		DatabaseHandler db = new DatabaseHandler(this);
		List<SMSMessage> allSms = db.getAllSms();
		for (SMSMessage sms : allSms) {
			int id = sms.getId();
			String date = sms.getDate();
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm'Z'");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			String datenew;
			try {
				Date dateD = format.parse(date);
				DateFormat usEnglishDf = DateFormat.getDateTimeInstance(
						DateFormat.SHORT, DateFormat.SHORT);
				datenew = usEnglishDf.format(dateD);
			} catch (ParseException e) {
				datenew = date;
			}
			String sender = sms.getSender();
			String message = sms.getMessage();
			Map<String, String> listDataItem = new HashMap<String, String>(3);
			listDataItem.put("id", Integer.toString(id));
			listDataItem.put("title", sender);
			listDataItem.put("text", datenew + "\n" + message);
			listData.add(listDataItem);
		}
		// Associating adapter with ListView.
		SimpleAdapter adapter = new SimpleAdapter(this, listData,
				android.R.layout.simple_list_item_2, new String[] { "title",
						"text" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		ListView smsListView = (ListView) findViewById(R.id.smsListView);
		smsListView.setAdapter(adapter);
		if (adapter.getCount() > 0) {
			isListEmpty = false;
		} else {
			isListEmpty = true;
		}
	}
}
