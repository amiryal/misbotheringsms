# Misbothering SMS Receiver

Stop spam SMS alerts by only playing a notification sound and vibrating your
phone for senders in your contacts.

To make this app work as intended, disable sound and vibration in your normal
Messaging app, and let this app do it only for senders in your contacts list.

## Background

If you are using a version of Android earlier than 4.4, you may like to try the
original [No Stranger SMS][1] app, which intercepts spam SMS messages and
prevents them from getting to the default Messaging app entirely.  It manages
its own list of intercepted messages, which you can delete in bulk, add sender
to contacts or move to the normal Messaging app.  However, with Android’s [API
change in version 4.4][2] (API level 19 KITKAT), interception of SMS messages is
no longer possible unless the app is to be made the default messaging app.

Since the functionality and behaviour of this fork is so different from the
original, the author has decided to keep it as a separate project, not offering
it as a patch upstream.  You are welcome to visit the [upstream project’s
homepage][1] anyway, and donate to the cause of submitting it to Google Play.

[1]: https://github.com/glesik/nostrangersms
[2]: http://android-developers.blogspot.co.il/2013/10/getting-your-sms-apps-ready-for-kitkat.html

## Roadmap

### x.0.0

 * Advanced filtering using whitelist or blacklist.

### 0.x.0

 * Add preferences screen to choose whether to play a notification sound,
 vibrate or both.
 * Ability to pick notification sound and vibration pattern.
 * Improve the About screen.

### 0.1.x

 * Fix bug: intents don’t start the app as soon as it’s installed.  Workaround:
 start the app manually from launcher for the first time.

## Changelog

### 0.1.0

 * Fork project for Android 4.4 functionality.
 * Strip down to only making sound and vibration for senders in the contacts
 list.

## License

Copyright © 2013, 2014, 2015 Alexander Inglessi (http://glsk.net)
<br>
Copyright © 2015 Amir Yalon

Code licensed under [GNU GPL][gpl] version 3 or any later version.  Artwork
licensed under [CC BY-NC-SA][cc] 3 or any later version.

[gpl]: https://www.gnu.org/licenses/gpl.html
[cc]: https://creativecommons.org/licenses/by-nc-sa/3.0/
