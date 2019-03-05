package com.nuig.philip.projectenda.Tasks;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nuig.philip.projectenda.R;

public class Toasts extends AppCompatActivity {


    public static void infoToast(String message, Activity activity, int duration) {
        // Inflate toast XML layout
        View layout = activity.getLayoutInflater().inflate(R.layout.toast_info, (ViewGroup) activity.findViewById(R.id.info_toast_layout));
        // Fill in the message into the textview
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        // Construct the toast, set the view and display
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setView(layout);
        toast.setDuration(duration);
        toast.show();
    }

    public static void failToast(String message, Activity activity, int duration) {
        // Inflate toast XML layout
        View layout = activity.getLayoutInflater().inflate(R.layout.toast_fail, (ViewGroup) activity.findViewById(R.id.fail_toast_layout));
        // Fill in the message into the textview
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        // Construct the toast, set the view and display
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setView(layout);
        toast.setDuration(duration);
        toast.show();
    }

    public static void successToast(String message, Activity activity, int duration) {
        // Inflate toast XML layout
        View layout = activity.getLayoutInflater().inflate(R.layout.toast_success, (ViewGroup) activity.findViewById(R.id.success_toast_layout));
        // Fill in the message into the textview
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);
        // Construct the toast, set the view and display
        Toast toast = new Toast(activity.getApplicationContext());
        toast.setView(layout);
        toast.setDuration(duration);
        toast.show();
    }
}
