package com.lindaexchange.lindaexchangeadmin;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Milion on 11/4/2017.
 */

public class NewsPagerAdapter extends FragmentStatePagerAdapter {
    NewsDetailFragment thaiFragment;
    NewsDetailFragment englishFragment;

    public NewsPagerAdapter(FragmentManager fm, NewsDetailFragment thaiFragment, NewsDetailFragment englishFragment) {
        super(fm);
        this.thaiFragment = thaiFragment;
        this.englishFragment = englishFragment;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return thaiFragment;
        } else if (position == 1) {
            return englishFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public List<Map<String, Object>> getMap() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> thMap = thaiFragment.getMap();
        Uri thUri = (Uri) thMap.get("uri");
        if (thUri != null) {
            map.put("thUri", thUri);
            thMap.remove("uri");
        }
        Map<String, Object> enMap = englishFragment.getMap();
        Uri enUri = (Uri) enMap.get("uri");
        if (enUri != null) {
            map.put("enUri", enUri);
            enMap.remove("uri");
        }

        mapList.add(map);
        mapList.add(thMap);
        mapList.add(enMap);

        return mapList;
    }
}
