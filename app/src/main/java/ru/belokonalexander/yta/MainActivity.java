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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;


import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomViewPager;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    final int STANDART_SCROLL_DURATION_FACTOR = 2;



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

            StaticHelpers.LogThisFt("MAIN ACT: " + savedInstanceState);


                ButterKnife.bind(this);

                sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mainViewPager.setAdapter(sectionsPagerAdapter);


                tabLayout.setupWithViewPager(mainViewPager);


                mainViewPager.setScrollDurationFactor(STANDART_SCROLL_DURATION_FACTOR);
                fragments = new Fragment[]{new ActionFragment(), new FragmentHistory(), new FragmentFavorites()};

                mainViewPager.setOffscreenPageLimit(fragments.length);

                setTabItems();
                setGlobalLayout();




    }

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

    private void setGlobalLayout() {
        // Threshold for minimal keyboard height.
        final int MIN_KEYBOARD_HEIGHT_PX = 150;

        // Top-level window decor view.
        final View decorView = getWindow().getDecorView();

        // Register global layout listener.
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                // Retrieve visible rectangle inside window.
                decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                // Decide whether keyboard is visible from changing decor view height.
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        // Calculate current keyboard height (this includes also navigation bar height when in fullscreen mode).
                        int currentKeyboardHeight = decorView.getHeight() - windowVisibleDisplayFrame.bottom;
                        // Notify listener about keyboard being shown.
                        //listener.onKeyboardShown(currentKeyboardHeight);
                        StaticHelpers.LogThis(" SHOWN ");
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        // Notify listener about keyboard being hidden.
                        StaticHelpers.LogThis(" HIDE ");
                        clearFocus();
                    }
                }
                // Save current decor view height for the next call.
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        });
    }

    public void clearFocus(){
        root.requestFocus();
    }

    public void openActionFragment(int fromPosition){
        onOpenAnotherFragment();
        int slideSpeed = Math.max(1,STANDART_SCROLL_DURATION_FACTOR+1-fromPosition);
        mainViewPager.setScrollDurationFactor(slideSpeed);
        mainViewPager.setCurrentItem(0,true);
        mainViewPager.setScrollDurationFactor(STANDART_SCROLL_DURATION_FACTOR);
    }

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



       /* @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: {

                    return getString(R.string.translate_title);
                }
                case 1:
                    return getString(R.string.history_title);
                case 2:
                    return getString(R.string.favorites_title);

            }
            return null;
        }*/
    }





}
