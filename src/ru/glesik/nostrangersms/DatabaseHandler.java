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

import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHandler extends SQLiteOpenHelper {
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "smsDB";
 
    // Contacts table name
    private static final String TABLE_MESSAGES = "smsMessages";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_MESSAGE = "message";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
        		+ KEY_SENDER + " TEXT,"
                + KEY_MESSAGE + " TEXT" + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new message
    void addSms(SMSMessage sms, int limit) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(KEY_DATE, sms.getDate());
        values.put(KEY_SENDER, sms.getSender());
        values.put(KEY_MESSAGE, sms.getMessage());
 
        // Inserting Row
        db.insert(TABLE_MESSAGES, null, values);
        
        if ((limit > 0) && (this.getSmsCount() > limit)) {
        	// TODO: delete oldest messages
        }
        
        db.close(); // Closing database connection
    }
 
    // Getting single message by id
    SMSMessage getSms(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_MESSAGES, new String[] { KEY_ID, KEY_DATE,
                KEY_SENDER, KEY_MESSAGE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        SMSMessage sms = new SMSMessage(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return message
        return sms;
    }
     
    // Getting all messages
    public List<SMSMessage> getAllSms() {
        List<SMSMessage> smsList = new ArrayList<SMSMessage>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MESSAGES;
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToLast()) {
            do {
            	SMSMessage sms = new SMSMessage();
                sms.setId(Integer.parseInt(cursor.getString(0)));
                sms.setDate(cursor.getString(1));
                sms.setSender(cursor.getString(2));
                sms.setMessage(cursor.getString(3));
                // Adding contact to list
                smsList.add(sms);
            } while (cursor.moveToPrevious());
        }
 
        // return contact list
        return smsList;
    }
 
    // Deleting single message
    public void deleteSms(SMSMessage sms) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, KEY_ID + " = ?",
                new String[] { String.valueOf(sms.getId()) });
        db.close();
    }
    
 // Deleting single message
    public void deleteAllSms() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DATE + " TEXT,"
        		+ KEY_SENDER + " TEXT,"
                + KEY_MESSAGE + " TEXT" + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);
        db.close();
    }
 
    // Getting contacts Count
    public int getSmsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
}
