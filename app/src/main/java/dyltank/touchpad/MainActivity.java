package dyltank.touchpad;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private float initialX, initialY, lastX, lastY;
    public float dx, dy;

    private String TAG = "TOUCH";

    private long lastClickTime;

    public DatagramSocket clientSocket;
    public InetAddress IPAddress;

    public int currentNumFingers = 1;

    private DecimalFormat df = new DecimalFormat("#.#####");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ImageView panel = (ImageView)findViewById(R.id.imageView);
        new AsyncMouseMove().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void move(int numFingers, float dx, float dy) throws IOException{
        //System.out.println(numFingers + " finger move -> X:" + dx + " | Y:" + dy);

        //message = String.format("%d:%f:%f", numFingers, dx, dy);
        currentNumFingers = numFingers;
        this.dx += dx;
        this.dy += dy;
        df.format(this.dx);
        df.format(this.dy);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                initialX = lastX = event.getX();
                initialY = lastY = event.getY();

                lastClickTime = System.currentTimeMillis();

                Log.d(TAG, "Action was DOWN");
                break;

            case MotionEvent.ACTION_MOVE:
                float newX = event.getX();
                float newY = event.getY();

                try{
                    move(event.getPointerCount(), newX - lastX, newY - lastY);
                } catch (IOException e){
                    e.printStackTrace();
                }
                lastX = newX;
                lastY = newY;

                //System.out.println("TIMEDIFF " + (int)(lastClickTime - System.currentTimeMillis()));
                //Log.d(TAG, "Action was MOVE");
                break;

            case MotionEvent.ACTION_UP:
                float finalX = event.getX();
                float finalY = event.getY();

                if(lastClickTime - System.currentTimeMillis() > -150){
                    System.out.println("CLICK!");
                    new AsyncMouseClick().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

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

    class AsyncMouseMove extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("NETWORKCLIENT", "Connection started");

            try{
                clientSocket = new DatagramSocket();
                IPAddress = InetAddress.getByName("192.168.1.70");

                while(true){
                    if(dx == 0 && dy == 0) continue;
                    if(dx > -3 && dx < 3 && dy > -3 && dy < 3) continue;
                    byte[] sendData = null;
                    if(currentNumFingers == 1){
                        sendData = ("MOVE" + dx + ":" + dy).getBytes();
                    } else if(currentNumFingers == 2){
                        sendData = ("SCROLL" + dx + ":" + dy).getBytes();
                    } else {

                    }
                    dx = dy = 0;
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6969);

                    clientSocket.send(sendPacket);
                }

            } catch (IOException e){
                e.printStackTrace();
            }

            clientSocket.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }

    }

    class AsyncMouseClick extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try{
                byte[] sendData = ("CLICK").getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6969);

                clientSocket.send(sendPacket);
                System.out.println("SENT CLICK PACKET");

            } catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

    }

}
