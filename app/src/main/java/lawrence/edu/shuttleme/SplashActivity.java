package lawrence.edu.shuttleme;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        /*
        Typeface typeface = Typeface.createFromAsset(getAssets(), "SortsMillGoudy-Regular.ttf");
        TextView title = (TextView) findViewById(R.id.splash_title);
        TextView names = (TextView) findViewById(R.id.textView4);
        title.setTypeface(typeface);
        names.setTypeface(typeface);
        */

        //getSupportActionBar().hide();

        Thread thread = new Thread() {
            @Override
            public void run(){
                try{
                    sleep(3000);

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }

        };
        thread.start();

    }
}