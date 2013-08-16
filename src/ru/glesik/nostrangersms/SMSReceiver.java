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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	public SMSReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				// Get SMS objects.
				Object[] pdus = (Object[]) bundle.get("pdus");
				if (pdus.length == 0) {
					return;
				}
				// Large message might be broken into many.
				SmsMessage[] messages = new SmsMessage[pdus.length];
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					sb.append(messages[i].getMessageBody());
				}
				String sender = messages[0].getOriginatingAddress();
				if (!(getContactDisplayNameByNumber(context, sender).equals(""))) {
					// Contact with this number exists in address book: do nothing.
				} else {
					// Contact not in address book: log message and don't let it through.
					String message = sb.toString();
					// Prevent other broadcast receivers from receiving broadcast.
					abortBroadcast();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd'T'HH:mm'Z'"); // ISO 8601, Local time zone.
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					String date = dateFormat.format(new Date()); // Current time in UTC.
					DatabaseHandler db = new DatabaseHandler(context);
					db.addSms(new SMSMessage(date, sender, message), 0); // 0 is for no limit.
					// TODO: delete oldest messages
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);
					int icon;
					icon = R.drawable.ic_stat_sad_sms;
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context).setSmallIcon(icon).setContentTitle(sender)
							.setContentText(message)
							.setPriority(NotificationCompat.PRIORITY_DEFAULT)
							.setOnlyAlertOnce(true);
					// TODO: Optional light notification.
					Intent ni = new Intent(context, JunkSMSList.class);
					ni.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_SINGLE_TOP);
					PendingIntent pi = PendingIntent.getActivity(context, 0,
							ni, 0);
					mBuilder.setContentIntent(pi);
					mBuilder.setAutoCancel(true);
					mNotificationManager.notify(777, mBuilder.build());
				}
			}
		}
	}

	public String getContactDisplayNameByNumber(Context context, String number) {
		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(number));
		String name = "";
		ContentResolver contentResolver = context.getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] {
				BaseColumns._ID, ContactsContract.PhoneLookup.DISPLAY_NAME },
				null, null, null);
		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				name = contactLookup.getString(contactLookup
						.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();
			}
		}
		return name;
	}
}
