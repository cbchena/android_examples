package dicomviewer.android.idot.lisa.ulb.ac.be.myglide;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView img = (ImageView) this.findViewById(R.id.img);

        // 加载网络图片 2016/3/14 15:16
        Glide.with(this)
                .load("http://www.bz55.com/uploads/allimg/130803/1-130P3112Q0.jpg")
                .error(R.mipmap.logo)// load失敗的Drawable
                .placeholder(R.mipmap.home_mp_on) // loading時候的Drawable
//                .animate(R.mipmap.home_pb_on_today) // 设置加载完成的动画
                .centerCrop()   // 中心切圆, 会填滿
                .fitCenter()    // 中心fit, 以原本圆片的长宽為主
                .listener(new RequestListener<String, GlideDrawable>() { // 下载完成监听 2016/3/14 15:24
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        System.out.println("=============");
                        return false;
                    }
                })
                .crossFade() // 淡入淡出
                .into(img);

        // 做为缩略图，为原图的十分之一 2016/3/14 15:15
//        Glide.with(this)
//                .load("http://www.bz55.com/uploads/allimg/130803/1-130P3112Q0.jpg")
//                .thumbnail(0.1f)
//                .into(img);
    }

}
