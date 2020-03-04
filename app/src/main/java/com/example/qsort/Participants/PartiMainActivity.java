package com.example.qsort.Participants;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.qsort.R;

import java.util.ArrayList;
import java.util.List;

public class PartiMainActivity extends FragmentActivity {

    TextView labelTextView;
    String categories, labels;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    String[] labelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parti_main);

        Intent intent = getIntent();

        categories = intent.getExtras().getString("Categories");
        labels = intent.getExtras().getString("Labels");
        labelList = labels.split("\n");
        System.out.println(labels);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);


    }

    public void backToEnterCode(View view){
        startActivity(new Intent(getApplicationContext(), PartiWelcomeActivity.class));
        finish();
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new ObjectFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putString(ObjectFragment.CATEGORY_KEY,categories);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return labelList.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return labelList[position];
        }
    }


}

