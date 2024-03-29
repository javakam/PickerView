package ando.widget.pickerview.view;

import android.util.Log;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ando.widget.pickerview.R;
import ando.widget.pickerview.adapter.ArrayWheelAdapter;
import ando.widget.pickerview.adapter.NumericWheelAdapter;
import ando.widget.pickerview.listener.ISelectTimeCallback;
import ando.widget.pickerview.utils.ChinaDate;
import ando.widget.pickerview.utils.LunarCalendar;
import ando.widget.wheelview.WheelView;
import ando.widget.wheelview.listener.OnItemSelectedListener;

public class WheelTime {
    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private View view;
    private WheelView wv_year;
    private WheelView wv_month;
    private WheelView wv_day;
    private WheelView wv_hours;
    private WheelView wv_minutes;
    private WheelView wv_seconds;
    private int gravity;

    private boolean[] type;
    private Calendar currentDate;
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFAULT_START_DAY = 1;
    private int startYear = DEFAULT_START_YEAR;
    private int startMonth = DEFAULT_START_MONTH;
    private int startDay = DEFAULT_START_DAY;
    private int startHour = 0;
    private int startMinute = 0;
    private int startSecond = 0;

    private static final int DEFAULT_END_YEAR = 2100;
    private static final int DEFAULT_END_MONTH = 12;
    private static final int DEFAULT_END_DAY = 31;
    private int endYear = DEFAULT_END_YEAR;
    private int endMonth = DEFAULT_END_MONTH;
    private int endDay = DEFAULT_END_DAY; //表示31天的
    private int endHour = 0;
    private int endMinute = 0;
    private int endSecond = 0;

    private int currentYear;

    private int textSize;
    private boolean isSetDateInRange = false;//setDate 是否在 setRangDate 范围之中
    private boolean isLunarCalendar = false;
    private ISelectTimeCallback mSelectChangeCallback;

    public WheelTime(View view, boolean[] type, int gravity, int textSize) {
        super();
        this.view = view;
        this.type = type;
        this.gravity = gravity;
        this.textSize = textSize;
    }

    public void setLunarMode(boolean isLunarCalendar) {
        this.isLunarCalendar = isLunarCalendar;
    }

    public boolean isLunarMode() {
        return isLunarCalendar;
    }

    public void setPicker(int year, int month, int day) {
        this.setPicker(year, month, day, 0, 0, 0);
    }

    public void setPicker(int year, final int month, int day, int h, int m, int s) {
        if (isLunarCalendar) {
            int[] lunar = LunarCalendar.solarToLunar(year, month + 1, day);
            setLunar(lunar[0], lunar[1] - 1, lunar[2], lunar[3] == 1, h, m, s);
        } else {
            setSolar(year, month, day, h, m, s);
        }
    }

    /**
     * 设置农历
     */
    private void setLunar(int year, final int month, int day, boolean isLeap, int h, int m, int s) {
        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new ArrayWheelAdapter(ChinaDate.getYears(startYear, endYear)));// 设置"年"的显示数据
        wv_year.setLabel("");// 添加文字
        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
        wv_year.setGravity(gravity);

        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year)));
        wv_month.setLabel("");

        int leapMonth = ChinaDate.leapMonth(year);
        if (leapMonth != 0 && (month > leapMonth - 1 || isLeap)) { //选中月是闰月或大于闰月
            wv_month.setCurrentItem(month + 1);
        } else {
            wv_month.setCurrentItem(month);
        }

        wv_month.setGravity(gravity);

        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (ChinaDate.leapMonth(year) == 0) {
            wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year, month))));
        } else {
            wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year))));
        }
        wv_day.setLabel("");
        wv_day.setCurrentItem(day - 1);
        wv_day.setGravity(gravity);

        wv_hours = (WheelView) view.findViewById(R.id.hour);
        //wv_hours.setLabel(context.getString(R.string.pickerview_hours));// 添加文字
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setCurrentItem(h);
        wv_hours.setGravity(gravity);

        wv_minutes = (WheelView) view.findViewById(R.id.min);
        wv_minutes.setAdapter(new NumericWheelAdapter(0, 59));
        //wv_minutes.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_minutes.setCurrentItem(m);
        wv_minutes.setGravity(gravity);

        wv_seconds = (WheelView) view.findViewById(R.id.second);
        wv_seconds.setAdapter(new NumericWheelAdapter(0, 59));
        //wv_seconds.setLabel(context.getString(R.string.pickerview_minutes));// 添加文字
        wv_seconds.setCurrentItem(m);
        wv_seconds.setGravity(gravity);

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year_num = index + startYear;
                // 判断是不是闰年,来确定月和日的选择
                wv_month.setAdapter(new ArrayWheelAdapter(ChinaDate.getMonths(year_num)));
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    wv_month.setCurrentItem(wv_month.getCurrentItem() + 1);
                } else {
                    wv_month.setCurrentItem(wv_month.getCurrentItem());
                }

                int currentIndex = wv_day.getCurrentItem();
                int maxItem = 29;
                if (ChinaDate.leapMonth(year_num) != 0 && wv_month.getCurrentItem() > ChinaDate.leapMonth(year_num) - 1) {
                    if (wv_month.getCurrentItem() == ChinaDate.leapMonth(year_num) + 1) {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(year_num))));
                        maxItem = ChinaDate.leapDays(year_num);
                    } else {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, wv_month.getCurrentItem()))));
                        maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem());
                    }
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1))));
                    maxItem = ChinaDate.monthDays(year_num, wv_month.getCurrentItem() + 1);
                }

                if (currentIndex > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });
        // 添加"月"监听
        wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                final int monthNum = index;
                final int yearNum = wv_year.getCurrentItem() + startYear;
                final int currentIndex = wv_day.getCurrentItem();
                int maxItem = 29;
                if (ChinaDate.leapMonth(yearNum) != 0 && monthNum > ChinaDate.leapMonth(yearNum) - 1) {
                    if (wv_month.getCurrentItem() == ChinaDate.leapMonth(yearNum) + 1) {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.leapDays(yearNum))));
                        maxItem = ChinaDate.leapDays(yearNum);
                    } else {
                        wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(yearNum, monthNum))));
                        maxItem = ChinaDate.monthDays(yearNum, monthNum);
                    }
                } else {
                    wv_day.setAdapter(new ArrayWheelAdapter(ChinaDate.getLunarDays(ChinaDate.monthDays(yearNum, monthNum + 1))));
                    maxItem = ChinaDate.monthDays(yearNum, monthNum + 1);
                }

                if (currentIndex > maxItem - 1) {
                    wv_day.setCurrentItem(maxItem - 1);
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });

        setChangedListener(wv_day);
        setChangedListener(wv_hours);
        setChangedListener(wv_minutes);
        setChangedListener(wv_seconds);

        if (type.length != 6) {
            throw new RuntimeException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
        setContentTextSize();
    }

    /**
     * 设置公历
     */
    private void setSolar(int year, final int month, int day, int h, int m, int s) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        currentYear = year;
        // 年
        wv_year = (WheelView) view.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(startYear, endYear));// 设置"年"的显示数据

        wv_year.setCurrentItem(year - startYear);// 初始化时显示的数据
        wv_year.setGravity(gravity);
        // 月
        wv_month = (WheelView) view.findViewById(R.id.month);
        if (startYear == endYear) {//开始年等于终止年
            wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth));
            wv_month.setCurrentItem(month + 1 - startMonth);
        } else if (year == startYear) {
            //起始日期的月份控制
            wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));
            wv_month.setCurrentItem(month + 1 - startMonth);
        } else if (year == endYear) {
            //终止日期的月份控制
            wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
            wv_month.setCurrentItem(month);
        } else {
            wv_month.setAdapter(new NumericWheelAdapter(1, 12));
            wv_month.setCurrentItem(month);
        }
        wv_month.setGravity(gravity);
        // 日
        wv_day = (WheelView) view.findViewById(R.id.day);

        boolean leapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
        if (startYear == endYear && startMonth == endMonth) {
            if (list_big.contains(String.valueOf(month + 1))) {
                if (endDay > 31) {
                    endDay = 31;
                }
                wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
            } else if (list_little.contains(String.valueOf(month + 1))) {
                if (endDay > 30) {
                    endDay = 30;
                }
                wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
            } else {
                // 闰年
                if (leapYear) {
                    if (endDay > 29) {
                        endDay = 29;
                    }
                } else {
                    if (endDay > 28) {
                        endDay = 28;
                    }
                }
                wv_day.setAdapter(new NumericWheelAdapter(startDay, endDay));
            }
            wv_day.setCurrentItem(day - startDay);
        } else if (year == startYear && month + 1 == startMonth) {
            // 起始日期的天数控制
            if (list_big.contains(String.valueOf(month + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(startDay, 31));
            } else if (list_little.contains(String.valueOf(month + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(startDay, 30));
            } else {
                // 闰年 29，平年 28
                wv_day.setAdapter(new NumericWheelAdapter(startDay, leapYear ? 29 : 28));
            }
            wv_day.setCurrentItem(day - startDay);
        } else if (year == endYear && month + 1 == endMonth) {
            // 终止日期的天数控制
            if (list_big.contains(String.valueOf(month + 1))) {
                if (endDay > 31) {
                    endDay = 31;
                }
                wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
            } else if (list_little.contains(String.valueOf(month + 1))) {
                if (endDay > 30) {
                    endDay = 30;
                }
                wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
            } else {
                // 闰年
                if (leapYear) {
                    if (endDay > 29) {
                        endDay = 29;
                    }
                } else {
                    if (endDay > 28) {
                        endDay = 28;
                    }
                }
                wv_day.setAdapter(new NumericWheelAdapter(1, endDay));
            }
            wv_day.setCurrentItem(day - 1);
        } else {
            // 判断大小月及是否闰年,用来确定"日"的数据
            if (list_big.contains(String.valueOf(month + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 31));
            } else if (list_little.contains(String.valueOf(month + 1))) {
                wv_day.setAdapter(new NumericWheelAdapter(1, 30));
            } else {
                // 闰年 29，平年 28
                wv_day.setAdapter(new NumericWheelAdapter(startDay, leapYear ? 29 : 28));
            }
            wv_day.setCurrentItem(day - 1);
        }
        wv_day.setGravity(gravity);
        wv_hours = (WheelView) view.findViewById(R.id.hour);
        wv_hours.setGravity(gravity);
        wv_minutes = (WheelView) view.findViewById(R.id.min);
        wv_minutes.setGravity(gravity);
        wv_seconds = (WheelView) view.findViewById(R.id.second);
        wv_seconds.setGravity(gravity);

        resetHour(false, isStartDay(), h);
        resetMinute(false, isStartDayHour(), m);
        resetSecond(false, isStartDayHourMinute(), s);

        // 添加"年"监听
        wv_year.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int year_num = index + startYear;
                currentYear = year_num;
                int currentMonthItem = wv_month.getCurrentItem();
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (startYear == endYear) {
                    //重新设置月份
                    wv_month.setAdapter(new NumericWheelAdapter(startMonth, endMonth));
                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                    }
                    wv_month.setCurrentItem(Math.max(1, currentMonthItem));

                    int monthNum = currentMonthItem + startMonth;
                    if (startMonth == endMonth) {
                        //重新设置日
                        setReDay(year_num, monthNum, startDay, endDay, list_big, list_little);
                    } else if (monthNum == startMonth) {
                        setReDay(year_num, monthNum, startDay, 31, list_big, list_little);
                    } else if (monthNum == endMonth) {
                        setReDay(year_num, monthNum, 1, endDay, list_big, list_little);
                    } else {
                        setReDay(year_num, monthNum, 1, 31, list_big, list_little);
                    }
                } else if (year_num == startYear) {//等于开始的年
                    wv_month.setAdapter(new NumericWheelAdapter(startMonth, 12));
                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                    }
                    wv_month.setCurrentItem(Math.max(1, currentMonthItem));

                    int month = currentMonthItem + startMonth;
                    if (month == startMonth) {
                        setReDay(year_num, month, startDay, 31, list_big, list_little);
                    } else {
                        setReDay(year_num, month, 1, 31, list_big, list_little);
                    }
                } else if (year_num == endYear) {
                    wv_month.setAdapter(new NumericWheelAdapter(1, endMonth));
                    if (currentMonthItem > wv_month.getAdapter().getItemsCount() - 1) {
                        currentMonthItem = wv_month.getAdapter().getItemsCount() - 1;
                    }
                    wv_month.setCurrentItem(Math.max(1, currentMonthItem));

                    int monthNum = currentMonthItem + 1;
                    if (monthNum == endMonth) {
                        setReDay(year_num, monthNum, 1, endDay, list_big, list_little);
                    } else {
                        setReDay(year_num, monthNum, 1, 31, list_big, list_little);
                    }
                } else {
                    wv_month.setAdapter(new NumericWheelAdapter(1, 12));
                    setReDay(year_num, wv_month.getCurrentItem() + 1, 1, 31, list_big, list_little);
                }

                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });
        // 添加"月"监听
        wv_month.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                int month_num = index + 1;
                if (startYear == endYear) {
                    month_num = month_num + startMonth - 1;
                    if (startMonth == endMonth) {
                        //重新设置日
                        setReDay(currentYear, month_num, startDay, endDay, list_big, list_little);
                    } else if (startMonth == month_num) {
                        //重新设置日
                        setReDay(currentYear, month_num, startDay, 31, list_big, list_little);
                    } else if (endMonth == month_num) {
                        setReDay(currentYear, month_num, 1, endDay, list_big, list_little);
                    } else {
                        setReDay(currentYear, month_num, 1, 31, list_big, list_little);
                    }
                } else if (currentYear == startYear) {
                    month_num = month_num + startMonth - 1;
                    if (month_num == startMonth) {
                        //重新设置日
                        setReDay(currentYear, month_num, startDay, 31, list_big, list_little);
                    } else {
                        //重新设置日
                        setReDay(currentYear, month_num, 1, 31, list_big, list_little);
                    }
                } else if (currentYear == endYear) {
                    if (month_num == endMonth) {
                        //重新设置日
                        setReDay(currentYear, wv_month.getCurrentItem() + 1, 1, endDay, list_big, list_little);
                    } else {
                        setReDay(currentYear, wv_month.getCurrentItem() + 1, 1, 31, list_big, list_little);
                    }
                } else {
                    //重新设置日
                    setReDay(currentYear, month_num, 1, 31, list_big, list_little);
                }
                if (mSelectChangeCallback != null) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            }
        });
        // 添加"天"监听
        wv_day.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                final boolean isStartDay = (isStartMonth() && (startDay > 0 && index == 0));//0->startDay
                Log.e("12345", "onItemSelected111: " + isStartDay + ";" + isStartDayHour() + ";" + isStartDayHourMinute());
                resetHour(true, isStartDay, h);
                resetMinute(true, isStartDayHour(), m);
                resetSecond(true, isStartDayHourMinute(), s);
            }
        });
        // 添加"时"监听
        wv_hours.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                final boolean isStartDayHour = (isStartDay() && (startHour > 0 && index == 0));//0->startHour
                Log.e("12345", "onItemSelected222: " + isStartDay() + ";" + isStartDayHour + ";" + isStartDayHour() + ";" + isStartDayHourMinute());

                resetMinute(true, isStartDayHour, m);
                resetSecond(true, isStartDayHourMinute(), s);
            }
        });
        // 添加"分"监听
        wv_minutes.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                final boolean isStartDayHourMinute = (isStartDayHour() && (startMinute > 0 && index == 0));//0->startMinute
                resetSecond(true, isStartDayHourMinute, s);
            }
        });

        if (type.length != 6) {
            throw new IllegalArgumentException("type[] length is not 6");
        }
        wv_year.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        wv_month.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        wv_day.setVisibility(type[2] ? View.VISIBLE : View.GONE);
        wv_hours.setVisibility(type[3] ? View.VISIBLE : View.GONE);
        wv_minutes.setVisibility(type[4] ? View.VISIBLE : View.GONE);
        wv_seconds.setVisibility(type[5] ? View.VISIBLE : View.GONE);
        setContentTextSize();
    }

    private boolean isStartMonth() {
        boolean isStartMonth = false;
        if (wv_month.getItemsCount() > 0) {
            isStartMonth = (currentYear == startYear && wv_month.getCurrentItem() == 0);
        }
        return isStartMonth;
    }

    private boolean isStartDay() {
        boolean isStartDay = false;
        if (wv_day.getItemsCount() > 0) {
            isStartDay = (isStartMonth() && wv_day.getCurrentItem() == 0);//或者 wv_day.getCurrentItemValueInt();
        }
        return isStartDay;
    }

    private boolean isStartDayHour() {
        boolean isStartDayHour = false;
        if (wv_hours.getItemsCount() > 0) {
            isStartDayHour = (isStartDay() && wv_hours.getCurrentItem() == 0);
        }
        return isStartDayHour;
    }

    private boolean isStartDayHourMinute() {
        boolean isStartDayHourMinute = false;
        if (wv_minutes.getItemsCount() > 0) {
            isStartDayHourMinute = (isStartDayHour() && wv_minutes.getCurrentItem() == 0);
        }
        return isStartDayHourMinute;
    }

    private boolean isEndDay() {
        return (currentYear == endYear && wv_month.getCurrentItemValueInt() == endMonth && wv_day.getCurrentItemValueInt() == endDay);
    }

    private boolean isEndDayHour() {
        return (isEndDay() && wv_hours.getCurrentItemValueInt() == endHour);
    }

    private boolean isEndDayHourMinute() {
        return (isEndDayHour() && wv_minutes.getCurrentItemValueInt() == endMinute);
    }

    private void resetHour(boolean isUserScroll, boolean isStartDay, int h) {
//        Log.e("12345", "resetHour h=" + h + ";isSetDateInRange=" + isSetDateInRange +
//                ";isStartDay=" + isStartDay() + ";" + isStartDay + ";isEndDay=" + isEndDay() + ";" + startHour);
        final boolean isEndDay = isEndDay();
        int realEndHour = isEndDay ? endHour : 23;
        int realStartHour = isStartDay ? startHour : 0;
        final NumericWheelAdapter adapter;

        if (isStartDay && isEndDay) {//同一天
            adapter = new NumericWheelAdapter(realStartHour, realEndHour);
            wv_hours.setCurrentItem(isUserScroll ? wv_hours.getCurrentItem() : (isSetDateInRange ? Math.abs(h - realStartHour) : Math.min(0, realStartHour)));
        } else if (isStartDay) {
            adapter = new NumericWheelAdapter(realStartHour, 23);
            wv_hours.setCurrentItem(isUserScroll ? wv_hours.getCurrentItem() : (isSetDateInRange ? Math.abs(h - realStartHour) : 0));
        } else if (isEndDay) {
            adapter = new NumericWheelAdapter(0, realEndHour);
            wv_hours.setCurrentItem(isUserScroll ? wv_hours.getCurrentItem() : Math.min(h, realEndHour));
        } else {
            adapter = new NumericWheelAdapter(0, 23);
            wv_hours.setCurrentItem(isUserScroll ? wv_hours.getCurrentItem() : h);
        }
        wv_hours.setAdapter(adapter);
    }

    private void resetMinute(boolean isUserScroll, boolean isStartDayHour, int m) {
        final boolean isEndDayHour = isEndDayHour();
        int realEndHourMinute = isEndDayHour ? endMinute : 59;
        int realStartHourMinute = isStartDayHour ? startMinute : 0;

        final NumericWheelAdapter adapter;
        if (isStartDayHour && isEndDayHour) {//同一天同一小时
            adapter = new NumericWheelAdapter(realStartHourMinute, realEndHourMinute);
            wv_minutes.setCurrentItem(isUserScroll ? wv_minutes.getCurrentItem() : (isSetDateInRange ? Math.abs(m - realStartHourMinute) : Math.min(0, realStartHourMinute)));
        } else if (isStartDayHour) {
            adapter = new NumericWheelAdapter(realStartHourMinute, 59);
            wv_minutes.setCurrentItem(isUserScroll ? wv_minutes.getCurrentItem() : (isSetDateInRange ? Math.abs(m - realStartHourMinute) : 0));
        } else if (isEndDayHour) {
            adapter = new NumericWheelAdapter(0, realEndHourMinute);
            wv_minutes.setCurrentItem(isUserScroll ? wv_minutes.getCurrentItem() : Math.min(m, realEndHourMinute));
        } else {
            adapter = new NumericWheelAdapter(0, 59);
            wv_minutes.setCurrentItem(isUserScroll ? wv_minutes.getCurrentItem() : m);
        }
        wv_minutes.setAdapter(adapter);
    }

    private void resetSecond(boolean isUserScroll, boolean isStartDayHourMinute, int s) {
        final boolean isEndDayHourMinute = isEndDayHourMinute();
        int realEndHourMinuteSecond = isEndDayHourMinute() ? endSecond : 59;
        int realStartHourMinuteSecond = isStartDayHourMinute ? startSecond : 0;

        final NumericWheelAdapter adapter;
        if (isStartDayHourMinute && isEndDayHourMinute) {//同一天同一小时同一分钟
            adapter = new NumericWheelAdapter(realStartHourMinuteSecond, realEndHourMinuteSecond);
            wv_seconds.setCurrentItem(isUserScroll ? wv_seconds.getCurrentItem() : (isSetDateInRange ? Math.abs(s - realStartHourMinuteSecond) : Math.min(0, realStartHourMinuteSecond)));
        } else if (isStartDayHourMinute) {
            adapter = new NumericWheelAdapter(realStartHourMinuteSecond, 59);
            wv_seconds.setCurrentItem(isUserScroll ? wv_seconds.getCurrentItem() : (isSetDateInRange ? Math.abs(s - realStartHourMinuteSecond) : 0));
        } else if (isEndDayHourMinute) {
            adapter = new NumericWheelAdapter(0, realEndHourMinuteSecond);
            wv_seconds.setCurrentItem(isUserScroll ? wv_seconds.getCurrentItem() : Math.min(s, realEndHourMinuteSecond));
        } else {
            adapter = new NumericWheelAdapter(0, 59);
            wv_seconds.setCurrentItem(isUserScroll ? wv_seconds.getCurrentItem() : s);
        }
        wv_seconds.setAdapter(adapter);
    }

    private void setChangedListener(WheelView wheelView) {
        if (mSelectChangeCallback != null) {
            wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(int index) {
                    mSelectChangeCallback.onTimeSelectChanged();
                }
            });
        }
    }

    private void setReDay(int year_num, int monthNum, int startD, int endD, List<String> list_big, List<String> list_little) {
        int currentItem = wv_day.getCurrentItem();
//        int maxItem;
        if (list_big.contains(String.valueOf(monthNum))) {
            if (endD > 31) {
                endD = 31;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
//            maxItem = endD;
        } else if (list_little.contains(String.valueOf(monthNum))) {
            if (endD > 30) {
                endD = 30;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
//            maxItem = endD;
        } else {
            if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0) {
                if (endD > 29) {
                    endD = 29;
                }
                //maxItem = endD;
            } else {
                if (endD > 28) {
                    endD = 28;
                }
                //maxItem = endD;
            }
            wv_day.setAdapter(new NumericWheelAdapter(startD, endD));
        }
        if (currentItem > wv_day.getAdapter().getItemsCount() - 1) {
            currentItem = wv_day.getAdapter().getItemsCount() - 1;
            wv_day.setCurrentItem(currentItem);
        }

        final boolean isStartDay = isStartDay();
        resetHour(true, isStartDay, wv_hours.getCurrentItem());
        resetMinute(true, isStartDay, wv_minutes.getCurrentItem());
        resetSecond(true, isStartDay, wv_seconds.getCurrentItem());
    }

    private void setContentTextSize() {
        wv_day.setTextSize(textSize);
        wv_month.setTextSize(textSize);
        wv_year.setTextSize(textSize);
        wv_hours.setTextSize(textSize);
        wv_minutes.setTextSize(textSize);
        wv_seconds.setTextSize(textSize);
    }

    public void setLabels(String label_year, String label_month, String label_day, String label_hours, String label_mins, String label_seconds) {
        if (isLunarCalendar) {
            return;
        }
        if (label_year != null) {
            wv_year.setLabel(label_year);
        } else {
            wv_year.setLabel(view.getContext().getString(R.string.pickerview_year));
        }
        if (label_month != null) {
            wv_month.setLabel(label_month);
        } else {
            wv_month.setLabel(view.getContext().getString(R.string.pickerview_month));
        }
        if (label_day != null) {
            wv_day.setLabel(label_day);
        } else {
            wv_day.setLabel(view.getContext().getString(R.string.pickerview_day));
        }
        if (label_hours != null) {
            wv_hours.setLabel(label_hours);
        } else {
            wv_hours.setLabel(view.getContext().getString(R.string.pickerview_hours));
        }
        if (label_mins != null) {
            wv_minutes.setLabel(label_mins);
        } else {
            wv_minutes.setLabel(view.getContext().getString(R.string.pickerview_minutes));
        }
        if (label_seconds != null) {
            wv_seconds.setLabel(label_seconds);
        } else {
            wv_seconds.setLabel(view.getContext().getString(R.string.pickerview_seconds));
        }
    }

    public void setTextXOffset(int x_offset_year, int x_offset_month, int x_offset_day,
                               int x_offset_hours, int x_offset_minutes, int x_offset_seconds) {
        wv_year.setTextXOffset(x_offset_year);
        wv_month.setTextXOffset(x_offset_month);
        wv_day.setTextXOffset(x_offset_day);
        wv_hours.setTextXOffset(x_offset_hours);
        wv_minutes.setTextXOffset(x_offset_minutes);
        wv_seconds.setTextXOffset(x_offset_seconds);
    }

    /**
     * 设置是否循环滚动
     */
    public void setCyclic(boolean cyclic) {
        wv_year.setCyclic(cyclic);
        wv_month.setCyclic(cyclic);
        wv_day.setCyclic(cyclic);
        wv_hours.setCyclic(cyclic);
        wv_minutes.setCyclic(cyclic);
        wv_seconds.setCyclic(cyclic);
    }

    public String getTime() {
        if (isLunarCalendar) {
            //如果是农历 返回对应的公历时间
            return getLunarTime();
        }
        StringBuilder sb = new StringBuilder();
        if (currentYear == startYear) {
           /* int i = wv_month.getCurrentItem() + startMonth;
            System.out.println("i:" + i);*/
            if ((wv_month.getCurrentItem() + startMonth) == startMonth) {
                sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                        .append((wv_month.getCurrentItem() + startMonth)).append("-")
                        .append((wv_day.getCurrentItem() + startDay));
            } else {
                sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                        .append((wv_month.getCurrentItem() + startMonth)).append("-")
                        .append((wv_day.getCurrentItem() + 1));
            }
        } else {
            sb.append((wv_year.getCurrentItem() + startYear)).append("-")
                    .append((wv_month.getCurrentItem() + 1)).append("-")
                    .append((wv_day.getCurrentItem() + 1));
        }

        sb.append(" ");
        if (type[3]) {
            sb.append(wv_hours.getCurrentItemValue());
        } else {
            sb.append("00");
        }
        sb.append(":");

        if (type[4]) {
            sb.append(wv_minutes.getCurrentItemValue());
        } else {
            sb.append("00");
        }
        sb.append(":");

        if (type[5]) {
            sb.append(wv_seconds.getCurrentItemValue());
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    /**
     * 农历返回对应的公历时间
     */
    private String getLunarTime() {
        StringBuilder sb = new StringBuilder();
        int year = wv_year.getCurrentItem() + startYear;
        int month = 1;
        boolean isLeapMonth = false;
        if (ChinaDate.leapMonth(year) == 0) {
            month = wv_month.getCurrentItem() + 1;
        } else {
            if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) <= 0) {
                month = wv_month.getCurrentItem() + 1;
            } else if ((wv_month.getCurrentItem() + 1) - ChinaDate.leapMonth(year) == 1) {
                month = wv_month.getCurrentItem();
                isLeapMonth = true;
            } else {
                month = wv_month.getCurrentItem();
            }
        }
        int day = wv_day.getCurrentItem() + 1;
        int[] solar = LunarCalendar.lunarToSolar(year, month, day, isLeapMonth);

        sb.append(solar[0]).append("-")
                .append(solar[1]).append("-")
                .append(solar[2]);

        sb.append(" ");
        if (type[3]) {
            sb.append(wv_hours.getCurrentItemValue());
        } else {
            sb.append("00");
        }
        sb.append(":");

        if (type[4]) {
            sb.append(wv_minutes.getCurrentItemValue());
        } else {
            sb.append("00");
        }
        sb.append(":");

        if (type[5]) {
            sb.append(wv_seconds.getCurrentItemValue());
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    public View getView() {
        return view;
    }

    public void setDate(Calendar calendar) {
        this.currentDate = calendar;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public void setRangDate(Calendar startDate, Calendar endDate) {
        if (startDate != null && endDate != null && currentDate != null && currentDate.getTimeInMillis() >= startDate.getTimeInMillis()
                && currentDate.getTimeInMillis() <= endDate.getTimeInMillis()) {
            this.isSetDateInRange = true;
        } else {
            this.isSetDateInRange = false;
        }

        if (startDate == null && endDate != null) {
            int year = endDate.get(Calendar.YEAR);
            int month = endDate.get(Calendar.MONTH) + 1;
            int day = endDate.get(Calendar.DAY_OF_MONTH);
            int hour = endDate.get(Calendar.HOUR_OF_DAY);
            int minute = endDate.get(Calendar.MINUTE);
            int second = endDate.get(Calendar.SECOND);
            if (year > startYear) {
                this.endYear = year;
                this.endMonth = month;
                this.endDay = day;
                this.endHour = hour;
                this.endMinute = minute;
                this.endSecond = second;
            } else if (year == startYear) {
                if (month > startMonth) {
                    this.endYear = year;
                    this.endMonth = month;
                    this.endDay = day;
                    this.endHour = hour;
                    this.endMinute = minute;
                    this.endSecond = second;
                } else if (month == startMonth) {
                    if (day > startDay) {
                        this.endYear = year;
                        this.endMonth = month;
                        this.endDay = day;
                        this.endHour = hour;
                        this.endMinute = minute;
                        this.endSecond = second;
                    }
                }
            }
        } else if (startDate != null && endDate == null) {
            int year = startDate.get(Calendar.YEAR);
            int month = startDate.get(Calendar.MONTH) + 1;
            int day = startDate.get(Calendar.DAY_OF_MONTH);
            int hour = startDate.get(Calendar.HOUR_OF_DAY);
            int minute = startDate.get(Calendar.MINUTE);
            int second = startDate.get(Calendar.SECOND);
            if (year < endYear) {
                this.startMonth = month;
                this.startDay = day;
                this.startYear = year;
                this.startHour = hour;
                this.startMinute = minute;
                this.startSecond = second;
            } else if (year == endYear) {
                if (month < endMonth) {
                    this.startMonth = month;
                    this.startDay = day;
                    this.startYear = year;
                    this.startHour = hour;
                    this.startMinute = minute;
                    this.startSecond = second;
                } else if (month == endMonth) {
                    if (day < endDay) {
                        this.startMonth = month;
                        this.startDay = day;
                        this.startYear = year;
                        this.startHour = hour;
                        this.startMinute = minute;
                        this.startSecond = second;
                    }
                }
            }
        } else if (startDate != null && endDate != null) {
            this.startYear = startDate.get(Calendar.YEAR);
            this.startMonth = startDate.get(Calendar.MONTH) + 1;
            this.startDay = startDate.get(Calendar.DAY_OF_MONTH);
            this.startHour = startDate.get(Calendar.HOUR_OF_DAY);
            this.startMinute = startDate.get(Calendar.MINUTE);
            this.startSecond = startDate.get(Calendar.SECOND);
            this.endYear = endDate.get(Calendar.YEAR);
            this.endMonth = endDate.get(Calendar.MONTH) + 1;
            this.endDay = endDate.get(Calendar.DAY_OF_MONTH);
            this.endHour = endDate.get(Calendar.HOUR_OF_DAY);
            this.endMinute = endDate.get(Calendar.MINUTE);
            this.endSecond = endDate.get(Calendar.SECOND);
        }
    }

    /**
     * 设置间距倍数,但是只能在1.0-4.0f之间
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        wv_day.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_month.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_year.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_hours.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_minutes.setLineSpacingMultiplier(lineSpacingMultiplier);
        wv_seconds.setLineSpacingMultiplier(lineSpacingMultiplier);
    }

    /**
     * 设置分割线的颜色
     */
    public void setDividerColor(int dividerColor) {
        wv_day.setDividerColor(dividerColor);
        wv_month.setDividerColor(dividerColor);
        wv_year.setDividerColor(dividerColor);
        wv_hours.setDividerColor(dividerColor);
        wv_minutes.setDividerColor(dividerColor);
        wv_seconds.setDividerColor(dividerColor);
    }

    /**
     * 设置分割线的类型
     */
    public void setDividerType(WheelView.DividerType dividerType) {
        wv_day.setDividerType(dividerType);
        wv_month.setDividerType(dividerType);
        wv_year.setDividerType(dividerType);
        wv_hours.setDividerType(dividerType);
        wv_minutes.setDividerType(dividerType);
        wv_seconds.setDividerType(dividerType);
    }

    /**
     * 设置分割线之间的文字的颜色
     */
    public void setTextColorCenter(int textColorCenter) {
        wv_day.setTextColorCenter(textColorCenter);
        wv_month.setTextColorCenter(textColorCenter);
        wv_year.setTextColorCenter(textColorCenter);
        wv_hours.setTextColorCenter(textColorCenter);
        wv_minutes.setTextColorCenter(textColorCenter);
        wv_seconds.setTextColorCenter(textColorCenter);
    }

    /**
     * 设置分割线以外文字的颜色
     */
    public void setTextColorOut(int textColorOut) {
        wv_day.setTextColorOut(textColorOut);
        wv_month.setTextColorOut(textColorOut);
        wv_year.setTextColorOut(textColorOut);
        wv_hours.setTextColorOut(textColorOut);
        wv_minutes.setTextColorOut(textColorOut);
        wv_seconds.setTextColorOut(textColorOut);
    }

    /**
     * @param isCenterLabel 是否只显示中间选中项的
     */
    public void isCenterLabel(boolean isCenterLabel) {
        wv_day.isCenterLabel(isCenterLabel);
        wv_month.isCenterLabel(isCenterLabel);
        wv_year.isCenterLabel(isCenterLabel);
        wv_hours.isCenterLabel(isCenterLabel);
        wv_minutes.isCenterLabel(isCenterLabel);
        wv_seconds.isCenterLabel(isCenterLabel);
    }

    public void setCenterLabelSpacing(float centerLabelSpacing) {
        wv_day.setCenterLabelSpacing(centerLabelSpacing);
        wv_month.setCenterLabelSpacing(centerLabelSpacing);
        wv_year.setCenterLabelSpacing(centerLabelSpacing);
        wv_hours.setCenterLabelSpacing(centerLabelSpacing);
        wv_minutes.setCenterLabelSpacing(centerLabelSpacing);
        wv_seconds.setCenterLabelSpacing(centerLabelSpacing);
    }

    public void setSelectChangeCallback(ISelectTimeCallback mSelectChangeCallback) {
        this.mSelectChangeCallback = mSelectChangeCallback;
    }

    public void setItemsVisible(int itemsVisibleCount) {
        wv_day.setItemsVisibleCount(itemsVisibleCount);
        wv_month.setItemsVisibleCount(itemsVisibleCount);
        wv_year.setItemsVisibleCount(itemsVisibleCount);
        wv_hours.setItemsVisibleCount(itemsVisibleCount);
        wv_minutes.setItemsVisibleCount(itemsVisibleCount);
        wv_seconds.setItemsVisibleCount(itemsVisibleCount);
    }

    public void setAlphaGradient(boolean isAlphaGradient) {
        wv_day.setAlphaGradient(isAlphaGradient);
        wv_month.setAlphaGradient(isAlphaGradient);
        wv_year.setAlphaGradient(isAlphaGradient);
        wv_hours.setAlphaGradient(isAlphaGradient);
        wv_minutes.setAlphaGradient(isAlphaGradient);
        wv_seconds.setAlphaGradient(isAlphaGradient);
    }

    public void setOuterTextScale(boolean isOuterTextScale) {
        wv_day.setOuterTextScale(isOuterTextScale);
        wv_month.setOuterTextScale(isOuterTextScale);
        wv_year.setOuterTextScale(isOuterTextScale);
        wv_hours.setOuterTextScale(isOuterTextScale);
        wv_minutes.setOuterTextScale(isOuterTextScale);
        wv_seconds.setOuterTextScale(isOuterTextScale);
    }
}