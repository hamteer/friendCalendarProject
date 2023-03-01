package com.frcal.friendcalender.Decorators;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;

import androidx.core.content.ContextCompat;

import com.frcal.friendcalender.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class OneDayDecorator implements DayViewDecorator {

    private CalendarDay date;
    private Context context;

    public OneDayDecorator(Context context) {
        this.context = context;
        date = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new UnderlineSpan());
        view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.purple_500)));
        view.addSpan(new RelativeSizeSpan(1.9f));
    }

    /**
     * We're changing the internals, so make sure to call {@linkplain MaterialCalendarView#invalidateDecorators()}
     */
    public void setDate(int year, int month, int day) {
        this.date = CalendarDay.from(year, month, day);
    }
}
