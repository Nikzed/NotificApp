package space.zakharchuk.notificapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;

public class MainActivity extends AppCompatActivity {

    int count = 1;
    private NotificationManager notificationManager;
    private static final int NOTIFY_ID = 1;
    private static final String CHANNEL_ID = "CHANNEL_ID";
    ObjectAnimator scale;

    TextView mainValue;
    TextView minus;
    TextView plus;

    NotificationManager nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mainValue = findViewById(R.id.mainValue);
        mainValue.setText(String.valueOf(count));

        minus = findViewById(R.id.minus);
        minus.setVisibility(View.INVISIBLE);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseValue();
            }
        });

        plus = findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseValue();
            }
        });



        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        TextView circle = findViewById(R.id.circle);
        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // добавление анимации
        scale = ObjectAnimator.ofPropertyValuesHolder(
                circle,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));

        // уведомление при нажатии на круг и сопутствующая анимация
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAction();
            }
        });
    }
    // функция для корректной работы с API 26+
    public static void createChannelIfNeeded(NotificationManager manager){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

    private void decreaseValue(){
        count--;
        if (count==1) minus.setVisibility(View.INVISIBLE);
        mainValue.setText(String.valueOf(count));
    }

    private void increaseValue(){
        count++;
        if (count>1) minus.setVisibility(View.VISIBLE);
        mainValue.setText(String.valueOf(count));
    }

    private void notificationAction(){
        scale.setDuration(150);
        scale.setRepeatCount(ObjectAnimator.RESTART);
        scale.setRepeatMode(ObjectAnimator.REVERSE);
        scale.start();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Chat heads active")
                        .setContentText("Notification " + count)
                        .setPriority(PRIORITY_DEFAULT);
        createChannelIfNeeded(notificationManager);
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
    }

}