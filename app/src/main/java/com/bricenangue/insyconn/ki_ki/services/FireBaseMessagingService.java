package com.bricenangue.insyconn.ki_ki.services;


import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by bricenangue on 12/08/16.
 */
public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private final static String GROUP_KEY_MESSAGES = "group_key_messages";
    private final static String GROUP_KEY_PUBLICATION = "group_key_publication";
    private final static String GROUP_KEY_DEAL = "group_key_deal";
    public static final int NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;

    private int incomingNotifiId;
    public static int notificationId=0;
    public static int notificationIdOffer=0;
    public static int notificationIdDeal=0;
    public static int notificationIdPublication=0;


    public static ArrayList<String> incomingmessages=new ArrayList<>();



    private FirebaseAuth auth;
    private FirebaseUser user;
    private int UNIQUE_REQUEST_CODE=0;
    public static int number=0;
    public static int numberDeal=0;
    private static final String GROUP_KEY_MESSAGES_OFFERS="group_key_offer";
    public static int notificationIdFollower=0;
    private static int notificationIdStuffFollower=0;


    public FireBaseMessagingService() {

    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Displaying data in log
        //It is optional
        remoteMessage.getData();

//        Log.d(TAG, "From: " + remoteMessage.getFrom());
       // Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());


        Map<String,String > maps=remoteMessage.getData();
        if(maps.containsKey("title")){
            String title=maps.get("title");


            if(title.contains("New chat message")){



            }else if (title.contains("New Publication")){
                if(maps.get("sender_uid")!=null && !maps.get("sender_uid").equals(user.getUid())){
                   // sendPostNotification(maps);
                }


            }else if (title.contains("New Deal")){
                if(maps.get("sender_uid")!=null && !maps.get("sender_uid").equals(user.getUid())){
                   // sendDealNotification(maps);
                }

            }else if (title.contains("New Offer")){
               // if (SingleDealActivityActivity.singledeal){
               //     sendNewOfferNotification(maps);
               // }

            }else if (title.contains("New Follower")){
            //    sendNewFollowerNotification(maps);
            }else if (title.contains(ConfigApp.FIREBASE_APP_FOLLOWERS_MESSAGE_NOTIFICATION)){
              //  sendNewStuffForFollowerNotification(maps);
            }else if (title.contains("From Console")){
                //sendConsoleNotification(maps);
            }
        }else {
          //sendNotification(maps);

        }

        /**
        else{
            if(!LiveChatActivity.messageshowed && maps.get("sender").equals(LiveChatActivity.receiverName)){
                sendOrderedBroadcast(i, null);
            }else if(LiveChatActivity.messageshowed){
                sendChatNotification(maps);
               // LiveChatBroadcastReceiver.completeWakefulIntent(intent);
            }else if(!LiveChatActivity.messageshowed && !maps.get("sender").equals(LiveChatActivity.receiverName)){
                sendChatNotification(maps);
               // LiveChatBroadcastReceiver.completeWakefulIntent(intent);
            }

        }
        **/
        //Calling method to generate notification

    }

    @Override
    public void onCreate() {
        super.onCreate();

        auth= FirebaseAuth.getInstance();
        if(auth!=null){
            user=auth.getCurrentUser();
        }

    }



    /**

     //This method is only generating push notification
     private void sendNotification(Map<String,String> stringMap) {
     Map<String,String> map=stringMap;

     Intent intent = new Intent(this, ChatActivity.class);
     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
     PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
     PendingIntent.FLAG_ONE_SHOT);

     Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
     NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
     .setSmallIcon(R.drawable.ic_stat_primary_notify)
     .setContentTitle(map.get("title"))
     .setContentText(map.get("message"))
     .setAutoCancel(true)
     .setSound(defaultSoundUri)
     .setContentIntent(pendingIntent);

     NotificationManager notificationManager =
     (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

     notificationManager.notify(0, notificationBuilder.build());
     }
    private void sendChatNotification(Map<String,String> stringMap) {

        Map<String,String> map =stringMap;
        notificationId++;

        //String chattingFrom = extras.getString("chattingFrom");
        String sender = map.get("sender");// will be user as receiver name in current Device getting the notifiction
        String msg = map.get("message");
        String title=map.get("title");
        String reciever=map.get("receiver");
        String senderuid=map.get("foreign_uid");
        String creator_uid=map.get("creator_uid");
        String post_id=map.get("post_id");
        String is_deal=map.get("is_deal");
        String sender_uid=map.get("sender_uid");

        String message=sender+": " +msg;
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_message);
        Bitmap largeIcon_API22 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_message)
                .setContentTitle(title)
                .setAutoCancel(true)

                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText( getString(R.string.Chat_Message_recieved_from)+" " +sender);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion>=23){
            builder  .setLargeIcon(largeIcon);
        }else if (currentapiVersion>=21 && currentapiVersion<23){
            builder  .setLargeIcon(largeIcon_API22);
        }else {

        }

        //intent.putExtra("chattingFrom", chattingFrom);
        intent.putExtra("key",senderuid);
        intent.putExtra("post_id", post_id);
        intent.putExtra("creator_uid",creator_uid);
        intent.putExtra("is_deal",is_deal);

        createMychat(creator_uid,post_id,senderuid,msg,System.currentTimeMillis(),sender,reciever,is_deal,sender_uid);

        Intent backIntent = new Intent(getApplicationContext(), MainPageActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), UNIQUE_REQUEST_CODE++,
                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        incomingmessages.add(message);
        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        for(int i=0;i<incomingmessages.size();i++){
            style.addLine(incomingmessages.get(i));
            style.setBigContentTitle(notificationId + " new messages");
            style.setSummaryText("messages");
        }
        if(notificationId>=2){
            builder.setContentIntent(pendingIntent)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY_MESSAGES)
                    .setGroupSummary(true);
            incomingmessages.clear();
            //notificationId=0;
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean bool = settings.getBoolean("notifications_new_message", true);

        if(bool){
            builder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
            builder.setContentIntent(pendingIntent);
            mNotificationManager.cancelAll();
            mNotificationManager.notify(notificationId, builder.build());

        }


    }

    private void sendPostNotification(Map<String,String> stringMap) {

        number++;
        notificationIdPublication++;
        Map<String,String> map =stringMap;
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_logo);

        String title = getString(R.string.fcm_Notification_new_publication_translatable);
        String msg=getString(R.string.fcm_Notification_new_publication_message);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_primary_notify);
        Bitmap largeIcon_API22 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_primary_notify)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(msg)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);



        Intent backIntent = new Intent(getApplicationContext(), CategoryActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion>=23){
            builder  .setLargeIcon(largeIcon);
        }else if (currentapiVersion>=21 && currentapiVersion<23){
            builder  .setLargeIcon(largeIcon_API22);
        }else {

        }

        final PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), UNIQUE_REQUEST_CODE++,
                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

            style.setBigContentTitle(notificationIdPublication + " "+getString(R.string.fcm_Notification_new_publication_translatable));
            style.setSummaryText(title);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean bool = settings.getBoolean("notifications_new_publication", true);

        if(number>=5 && bool){


            builder.setContentIntent(pendingIntent)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY_PUBLICATION)
                    .setGroupSummary(true);
            builder.setContentIntent(pendingIntent);
            mNotificationManager.cancelAll();
            mNotificationManager.notify(notificationIdPublication, builder.build());
            number=0;
        }


    }

    // deal, publication, topic ( categories, [city + arraynumber])


    private void sendDealNotification(Map<String,String> stringMap) {

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_primary_notify);
        Bitmap largeIcon_API22 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no);
        numberDeal++;
        notificationIdDeal++;
        Map<String,String> map =stringMap;

        //String chattingFrom = extras.getString("chattingFrom");
      // will be user as receiver name in current Device getting the notifiction
        String msg = getString(R.string.new_deal_in_your_area);
        String title=getString(R.string.notification_new_deal_title);


        String message=msg;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_primary_notify)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                ;
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,ViewDealsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion>=23){
            builder  .setLargeIcon(largeIcon);
        }else if (currentapiVersion>=21 && currentapiVersion<23){
            builder  .setLargeIcon(largeIcon_API22);
        }else {

        }

        Intent backIntent = new Intent(getApplicationContext(), MainPageActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), UNIQUE_REQUEST_CODE++,
                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

     if (notificationIdDeal==1){
         style.setBigContentTitle(notificationIdDeal + " "+getString(R.string.notification_new_deal_title_singular));
     }else {
         style.setBigContentTitle(notificationIdDeal + " "+getString(R.string.notification_new_deal_title));
     }
        style.setSummaryText(title);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean bool = settings.getBoolean("notifications_new_publication", true);

        if(numberDeal>=1 && bool){

            builder.setContentIntent(pendingIntent)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY_DEAL)
                    .setGroupSummary(true);

        }
        builder.setContentIntent(pendingIntent);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(notificationIdDeal, builder.build());
    }


    private void sendNewOfferNotification(Map<String,String> stringMap) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_primary_notify);
        Bitmap largeIcon_API22 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no);

        Map<String,String> map =stringMap;
        notificationIdOffer++;

        String title=getString(R.string.fcm_Notification_new_offers_translatable);
        String post_id=map.get("post_id");

        String message=getString(R.string.fcm_Notification_new_offers_message);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_primary_notify)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText(message);

        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion>=23){
            builder  .setLargeIcon(largeIcon);
        }else if (currentapiVersion>=21 && currentapiVersion<23){
            builder  .setLargeIcon(largeIcon_API22);
        }else {

        }


        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,SingleDealActivityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("dealid", post_id);

        /**
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT

                ); //


        Intent backIntent = new Intent(getApplicationContext(), MyDealsActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), UNIQUE_REQUEST_CODE++,
                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);

        if (notificationIdOffer==1){
            builder.setContentTitle(notificationIdOffer + " "+ getString(R.string.fcm_Notification_new_offers_translatables_singular));
        }else {
            builder.setContentTitle(notificationIdOffer + " "+ getString(R.string.fcm_Notification_new_offers_translatables));
        }

        style.setSummaryText(message);

        if(notificationIdOffer>=2){

            builder.setContentIntent(pendingIntent)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setGroup(GROUP_KEY_MESSAGES_OFFERS)
                    .setGroupSummary(true);

        }
        builder.setContentIntent(pendingIntent);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(notificationIdOffer, builder.build());

    }


    private void sendConsoleNotification(Map<String,String> stringMap) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_primary_notify);
        Bitmap largeIcon_API22 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no);

        Map<String,String> map =stringMap;

        String title=getString(R.string.fcm_Notification_console_translatable);
        String message=map.get("message");


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_primary_notify)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText(message);

        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion>=23){
            builder  .setLargeIcon(largeIcon);
        }else if (currentapiVersion>=21 && currentapiVersion<23){
            builder  .setLargeIcon(largeIcon_API22);
        }else {

        }


        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,FirebaseConsoleNotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("message", message);

        Intent backIntent = new Intent(getApplicationContext(), MainPageActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), UNIQUE_REQUEST_CODE++,
                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);


        builder.setContentIntent(pendingIntent);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(0, builder.build());

    }


    private void sendNewFollowerNotification(Map<String,String> stringMap) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_primary_notify);
        Bitmap largeIcon_API22 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no);

        Map<String,String> map =stringMap;
        notificationIdFollower++;

        String title=getString(R.string.fcm_Notification_new_follower_translatable);
        String completor=getString(R.string.fcm_Notification_new_follower_is);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_primary_notify)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText(getString(R.string.fcm_Notification_new_follower_is,notificationIdFollower));

        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion>=23){
            builder  .setLargeIcon(largeIcon);
        }else if (currentapiVersion>=21 && currentapiVersion<23){
            builder  .setLargeIcon(largeIcon_API22);
        }else {

        }


        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

       // intent.putExtra("dealid", post_id);

        /**
         TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
         // Adds the Intent that starts the Activity to the top of the stack
         stackBuilder.addNextIntent(intent);
         PendingIntent resultPendingIntent =
         stackBuilder.getPendingIntent(
         0,
         PendingIntent.FLAG_UPDATE_CURRENT

         );//


        Intent backIntent = new Intent(getApplicationContext(), CategoryActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), UNIQUE_REQUEST_CODE++,
                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.fcm_Notification_new_follower_is,notificationIdFollower)))
                .setContentText(getString(R.string.fcm_Notification_new_follower_is,notificationIdFollower));

        style.setSummaryText(getString(R.string.fcm_Notification_new_follower_is,notificationIdFollower));

        if(notificationIdFollower>=2){

            builder.setContentIntent(pendingIntent)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setContentText(getString(R.string.fcm_Notification_new_follower_are,notificationIdFollower))
                    .setGroup(GROUP_KEY_MESSAGES_OFFERS)
                    .setGroupSummary(true);

        }
        builder.setContentIntent(pendingIntent);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(notificationIdFollower, builder.build());

    }


    private void sendNewStuffForFollowerNotification(Map<String,String> stringMap) {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_primary_notify);
        Bitmap largeIcon_API22 = BitmapFactory.decodeResource(getResources(), R.drawable.logo_no);

        Map<String,String> map =stringMap;
        notificationIdStuffFollower++;

        String title = getString(R.string.fcm_Notification_new_publication_translatable);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_stat_primary_notify)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setContentText(getString(R.string.fcm_Notification_new_stuff_follower_is,notificationIdStuffFollower));

        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setAutoCancel(true);
        builder.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion>=23){
            builder  .setLargeIcon(largeIcon);
        }else if (currentapiVersion>=21 && currentapiVersion<23){
            builder  .setLargeIcon(largeIcon_API22);
        }else {

        }


        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,MainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // intent.putExtra("dealid", post_id);

        /**
         TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
         // Adds the Intent that starts the Activity to the top of the stack
         stackBuilder.addNextIntent(intent);
         PendingIntent resultPendingIntent =
         stackBuilder.getPendingIntent(
         0,
         PendingIntent.FLAG_UPDATE_CURRENT

         );//


        Intent backIntent = new Intent(getApplicationContext(), CategoryActivity.class);
        backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        final PendingIntent pendingIntent = PendingIntent.getActivities(getApplicationContext(), UNIQUE_REQUEST_CODE++,
                new Intent[] {backIntent, intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle style=new NotificationCompat.InboxStyle();
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(getString(R.string.fcm_Notification_new_stuff_follower_is,notificationIdStuffFollower)))
                .setContentText(getString(R.string.fcm_Notification_new_stuff_follower_is,notificationIdStuffFollower));

        style.setSummaryText(getString(R.string.fcm_Notification_new_stuff_follower_is,notificationIdStuffFollower));

        if(notificationIdStuffFollower>=2){

            builder.setContentIntent(pendingIntent)
                    .setStyle(style)
                    .setAutoCancel(true)
                    .setContentText(getString(R.string.fcm_Notification_new_stuff_follower_are,notificationIdStuffFollower))
                    .setGroup(GROUP_KEY_MESSAGES_OFFERS)
                    .setGroupSummary(true);

        }
        builder.setContentIntent(pendingIntent);
        mNotificationManager.cancelAll();
        mNotificationManager.notify(notificationIdStuffFollower, builder.build());

    }


    // check path
    private void createMychat(final String creator_uid, final String post_id, final String foreign_uid
            , final String last_message, final long time, final String namebyuer, final String namecreator
            , final String is_deal,final String sender_uid){
        //to save chat load keys
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child(ConfigApp.FIREBASE_APP_URL_MY_CHAT)
                .child(user.getUid())
                .child(creator_uid)
                .child(post_id)
                .child(foreign_uid);

        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child(ConfigApp.FIREBASE_APP_URL_CHAT_USER)
                .child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()){
                    final DataSnapshot snapshot =dataSnapshot.getChildren().iterator().next();
                    Map<String,Object> map=new HashMap<String, Object>();
                    map.put("lastmessage",last_message);
                    map.put("lastmessage_timestamp",time);
                    map.put("buyer_name",namebyuer);

                    map.put("path_creator_uid",creator_uid);
                    map.put("path_post_id",post_id);
                    map.put("path_buyer",foreign_uid);

                    map.put("buyer_name",namebyuer);
                    map.put("creator_name",namecreator);
                    map.put("is_deal",is_deal);

                    ref.child(snapshot.getKey()).updateChildren(map);

                }else {
                    String key=reference.push().getKey();
                    Map<String,Object> map=new HashMap<String, Object>();
                    map.put("lastmessage",last_message);
                    map.put("lastmessage_timestamp",time);
                    map.put("buyer_name",namebyuer);

                    map.put("path_creator_uid",creator_uid);
                    map.put("path_post_id",post_id);
                    map.put("path_buyer",foreign_uid);

                    map.put("buyer_name",namebyuer);
                    map.put("creator_name",namecreator);
                    map.put("is_deal",is_deal);

                    ref.child(key).updateChildren(map);
                    reference.child(key).setValue(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public static void setBadge(Context context, int count) {
        String launcherClassName = getLauncherClassName(context);
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count);
        intent.putExtra("badge_count_package_name", context.getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        context.sendBroadcast(intent);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    **/
}
