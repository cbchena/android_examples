package com.pocketdigi.slidingmenudemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.pocketdigi.fragment.FragmentA;
import com.pocketdigi.fragment.FragmentB;
import com.pocketdigi.fragment.MenuFragment;


public class MainActivity extends FragmentActivity {
	Fragment fragmentA, fragmentB, menuFragment;
	SlidingMenu menu;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.layout_content);
        afterViews();
    }

	public void afterViews() {

		//添加左侧菜单
		menuFragment = new MenuFragment();
		menu = new SlidingMenu(this);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  // 设置拖动的模式
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setShadowWidthRes(R.dimen.shadow_width); // 设置分割线的宽度

		//设置拉出菜单后，上层内容留下的宽度，即这个宽度＋菜单宽度＝屏幕宽度
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);

		//设置隐藏或显示菜单时，菜单渐变值，0，不变，1黑色，值为0－1
		menu.setFadeDegree(0.5f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.layout_menu);
		
		// menu.setMode(SlidingMenu.RIGHT); // 设置从右边拉出

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.layout_menu, menuFragment).commit();
		
		//显示FragmentA
		showFragmentA();
	}

	/**
	 * 捕捉返回键，如果当前显示菜单，刚隐藏
	 */
	@Override
	public void onBackPressed() {
		if (menu.isMenuShowing()) {
			menu.showContent();
		} else {
			super.onBackPressed();
		}
	}
	
	public void showFragmentA()
	{
		if(fragmentA==null)
		{
			fragmentA = new FragmentA();
		}

		if(!fragmentA.isVisible())
		{
			getSupportFragmentManager().beginTransaction()
			    .replace(R.id.layout_content, fragmentA).commit();
		}
		menu.showContent(true);
	}
	
	public void showFragmentB()
	{
		if(fragmentB==null)
		{
			fragmentB = new FragmentB();
		}

		if(!fragmentB.isVisible())
		{
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.layout_content, fragmentB).commit();
		}
		menu.showContent(true);
	}


}
