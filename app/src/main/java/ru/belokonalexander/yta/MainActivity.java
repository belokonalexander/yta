package ru.belokonalexander.yta;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.container)
    ViewPager mainViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    SectionsPagerAdapter sectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(mainViewPager);




    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position)
            {
                case 0:
                    fragment = new ActionFragment();

                    break;
                case 1:
                    fragment = new LibraryFragment();

                    break;

                case 2:
                    fragment = new SettingsFragment();
            }

            return fragment;
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
                    return "0";
                case 1:
                    return "1";
                case 2:
                    return "2";

            }
            return null;
        }
    }
}
