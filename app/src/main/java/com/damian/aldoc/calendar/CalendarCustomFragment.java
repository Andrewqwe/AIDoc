package com.damian.aldoc.calendar;

/**
 * Created by Damian on 14.05.2017.
 */

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

public class CalendarCustomFragment extends CaldroidFragment {

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        // TODO Auto-generated method stub
        return new CalendarCustomAdapter(getActivity(), month, year,
                getCaldroidData(), extraData);
    }

}