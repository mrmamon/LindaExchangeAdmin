package com.lindaexchange.lindaexchangeadmin;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Milion on 11/24/2017.
 */

public class BranchPagerAdapter extends FragmentPagerAdapter {
    BranchDetailFragment thaiFragment;
    BranchDetailFragment englishFragment;

    public BranchPagerAdapter(FragmentManager fm, BranchDetailFragment thaiFragment, BranchDetailFragment englishFragment) {
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
        Map<String, Object> enMap = englishFragment.getMap();
        Uri thUri = (Uri) thMap.get("uri");
        if (thUri != null) {
            map.put("thUri", thUri);
            thMap.remove("uri");
        }
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
