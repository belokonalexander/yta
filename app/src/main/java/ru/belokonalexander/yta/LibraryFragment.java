package ru.belokonalexander.yta;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alexander on 16.03.2017.
 */

public class LibraryFragment extends Fragment {

    LibraryPagerAdapter sectionsPagerAdapter;

    @BindView(R.id.container)
    ViewPager mainViewPager;

    @BindView(R.id.tabs)
    TabLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library,container,false);
        ButterKnife.bind(this, view);

        sectionsPagerAdapter = new LibraryPagerAdapter(getChildFragmentManager());
        mainViewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(mainViewPager);

        return view;
    }


    public class LibraryPagerAdapter extends FragmentPagerAdapter {

        LibraryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position)
            {
                case 0:
                    fragment = new FragmentHistory();

                    break;
                case 1:
                    fragment = new FragmentFavorites();
                    break;

            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "0";
                case 1:
                    return "1";

            }
            return null;
        }
    }
}
