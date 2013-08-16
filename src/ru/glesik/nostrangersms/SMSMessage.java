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

public class SMSMessage {
	int smsId;
	String smsDate;
    String smsSender;
    String smsMessage;
    
    // Constructors
    public SMSMessage() {
    }

    public SMSMessage(int id, String date, String sender, String message) {
        this.smsId = id;
        this.smsDate = date;
        this.smsSender = sender;
        this.smsMessage = message;
    }

    public SMSMessage(String date, String sender, String message) {
    	this.smsDate = date;
        this.smsSender = sender;
        this.smsMessage = message;
    }

    // Id
    public int getId() {
        return this.smsId;
    }

    public void setId(int id) {
        this.smsId = id;
    }

    // Date
    public String getDate() {
    	return this.smsDate;
    }

    public void setDate(String date) {
    	this.smsDate = date;
	}

    // Sender
    public String getSender() {
        return this.smsSender;
    }
     
    public void setSender(String sender){
        this.smsSender = sender;
    }

    // Message text
    public String getMessage(){
        return this.smsMessage;
    }

    public void setMessage(String message){
        this.smsMessage = message;
    }
}
