package dyltank.touchpad;

import android.media.Image;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private float initialX, initialY, lastX, lastY;
    private String TAG = "TOUCH"
;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView panel = (ImageView)findViewById(R.id.imageView);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                initialX = lastX = event.getX();
                initialY = lastY = event.getY();

                Log.d(TAG, "Action was DOWN");
                break;

            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                float newY = event.getY();
                Log.d(TAG, "Action was MOVE");
                break;

            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                float finalY = event.getY();

                Log.d(TAG, "Action was UP");

                /*if (initialX < finalX) {
                    Log.d(TAG, "Left to Right swipe performed");
                }

                if (initialX > finalX) {
                    Log.d(TAG, "Right to Left swipe performed");
                }

                if (initialY < finalY) {
                    Log.d(TAG, "Up to Down swipe performed");
                }

                if (initialY > finalY) {
                    Log.d(TAG, "Down to Up swipe performed");
                }*/

                break;

            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG,"Action was CANCEL");
                break;

            case MotionEvent.ACTION_OUTSIDE:
                Log.d(TAG, "Movement occurred outside bounds of current screen element");
                break;
        }

        return super.onTouchEvent(event);
    }

    public static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "Down";
            case MotionEvent.ACTION_MOVE: return "Move";
            case MotionEvent.ACTION_POINTER_DOWN: return "Pointer Down";
            case MotionEvent.ACTION_UP: return "Up";
            case MotionEvent.ACTION_POINTER_UP: return "Pointer Up";
            case MotionEvent.ACTION_OUTSIDE: return "Outside";
            case MotionEvent.ACTION_CANCEL: return "Cancel";
        }
        return "";
    }
}
