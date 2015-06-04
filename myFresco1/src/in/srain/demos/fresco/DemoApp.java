package in.srain.demos.fresco;

import android.app.Application;
import com.facebook.drawee.backends.pipeline.Fresco;

public class DemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
