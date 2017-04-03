package ru.belokonalexander.yta;

import android.graphics.Rect;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;


import butterknife.BindView;
import butterknife.ButterKnife;
import ru.belokonalexander.yta.GlobalShell.StaticHelpers;
import ru.belokonalexander.yta.Views.CustomViewPager;

public class MainActivity extends AppCompatActivity {

    final int STANDART_SCROLL_DURATION_FACTOR = 2;
    final int QUICK_SCROLL_DURATION_FACTOR = 1;


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
        mainViewPager.setScrollDurationFactor(STANDART_SCROLL_DURATION_FACTOR);
        fragments = new Fragment[]{new ActionFragment(), new FragmentHistory(), new FragmentFavorites() };

        mainViewPager.setOffscreenPageLimit(fragments.length);

        setGlobalLayout();

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
                        clearFocus();;
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

    public void openActionFragment(){
        mainViewPager.setScrollDurationFactor(QUICK_SCROLL_DURATION_FACTOR);
        mainViewPager.setCurrentItem(0);
        mainViewPager.setScrollDurationFactor(STANDART_SCROLL_DURATION_FACTOR);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
                 return fragments[position];
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.translate_title);
                case 1:
                    return getString(R.string.history_title);
                case 2:
                    return getString(R.string.favorites_title);

            }
            return null;
        }
    }





}
