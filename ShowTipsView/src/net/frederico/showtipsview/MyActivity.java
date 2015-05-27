package net.frederico.showtipsview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    List<View> lstView = new ArrayList<View>();
    List<String> lstTitle = new ArrayList<String>();
    List<String> lstContent = new ArrayList<String>();
    int idx = 0;

    boolean hasMeasured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fy_main);

        lstView.add(this.findViewById(R.id.btnSOS));
        lstView.add(this.findViewById(R.id.btnZone));

        lstTitle.add("SOS告警");
        lstTitle.add("安全空间");

        lstContent.add("SOS告警可以在最危急的情况，尽量保证您的安全。");
        lstContent.add("安全空间可以实时传递您的安全信息。");

        ViewTreeObserver vto = lstView.get(idx).getViewTreeObserver();
        if (vto != null) {
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                         public boolean onPreDraw() {
                                             if (!hasMeasured) {
                                                 showTips();
                                                 hasMeasured = true;
                                             }
                                             return true;
                                         }
                                     }
            );
        }
    }

    private void showTips() {
        final ShowTipsView showTips = new ShowTipsBuilder(MyActivity.this)
                .setTarget(lstView.get(idx), lstView.get(idx).getWidth() / 2,
                        lstView.get(idx).getHeight() / 2, 50)
                .setTitle(lstTitle.get(idx))
                .setDescription(lstContent.get(idx))
                .build();

        idx++;
        showTips.show(MyActivity.this);
        showTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTips.removeParent();
                if (lstView.size() > idx) { // 继续Tips
                    showTips();
                } else { // 结束
                    end();
                }
            }
        });

        showTips.setCallback(new ShowTipsViewInterface() {
            @Override
            public void gotItClicked() { // 点击跳过
                end();
            }
        });
    }

    private boolean isEnd = false;
    private void end() {
        isEnd = true;
        if (isEnd)
            System.out.println("--------------  结束  --------------");
    }

    public void OnZone(View view) {
        System.out.println("=======    安全空间");
    }
}
