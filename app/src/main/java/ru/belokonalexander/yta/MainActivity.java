package ru.belokonalexander.yta;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;


import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomViewPager;

/**
 * главное активити приложения - ViewPager + 3 Фрагмента
 */
public class MainActivity extends AppCompatActivity {


    final int STANDARD_SCROLL_DURATION_FACTOR = 2;

    @BindView(R.id.container)
    CustomViewPager mainViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.activity_main)
    View root;

    SectionsPagerAdapter sectionsPagerAdapter;

    Fragment[] fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);

            sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mainViewPager.setAdapter(sectionsPagerAdapter);
            tabLayout.setupWithViewPager(mainViewPager);
            mainViewPager.setScrollDurationFactor(STANDARD_SCROLL_DURATION_FACTOR);
            fragments = new Fragment[]{new FragmentAction(), new FragmentHistory(), new FragmentFavorites()};
            mainViewPager.setOffscreenPageLimit(fragments.length);
            setTabItems();





    }

    /**
     * инициализация табов для TabLayout
     */
    private void setTabItems() {


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this.getBaseContext(), R.color.normal_text_color_white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(MainActivity.this.getBaseContext(), R.color.pre_primary);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        TabLayout.Tab tab = tabLayout.getTabAt(0);
        Drawable icon =  ContextCompat.getDrawable(getBaseContext(),R.drawable.ic_translate_white_24dp);
        icon.setColorFilter(ContextCompat.getColor(MainActivity.this.getBaseContext(), R.color.normal_text_color_white),PorterDuff.Mode.SRC_IN);
        tab.setIcon(icon);


        icon =  ContextCompat.getDrawable(getBaseContext(),R.drawable.ic_history_white_24dp);
        icon.setColorFilter(ContextCompat.getColor(MainActivity.this.getBaseContext(), R.color.pre_primary),PorterDuff.Mode.SRC_IN);
        tab = tabLayout.getTabAt(1);
        tab.setIcon(icon);

        icon =  ContextCompat.getDrawable(getBaseContext(),R.drawable.ic_bookmark_white_24dp);
        icon.setColorFilter(ContextCompat.getColor(MainActivity.this.getBaseContext(), R.color.pre_primary),PorterDuff.Mode.SRC_IN);
        tab = tabLayout.getTabAt(2);
        tab.setIcon(icon);



    }


    public void openActionFragment(int fromPosition){
        onOpenAnotherFragment();
        int slideSpeed = Math.max(1, STANDARD_SCROLL_DURATION_FACTOR +1-fromPosition);
        mainViewPager.setScrollDurationFactor(slideSpeed);
        mainViewPager.setCurrentItem(0,true);
        mainViewPager.setScrollDurationFactor(STANDARD_SCROLL_DURATION_FACTOR);
    }

    /**
     * прячу клавиатуру при переходах
     */
    private void onOpenAnotherFragment() {
        try { // hide keyboard
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            onOpenAnotherFragment();
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 3;
        }

    }





}
