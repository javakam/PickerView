package ando.app.picker;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ando.app.picker.bean.CardBean;
import ando.app.picker.bean.ProvinceBean;
import ando.widget.pickerview.builder.OptionsPickerBuilder;
import ando.widget.pickerview.builder.TimePickerBuilder;
import ando.widget.pickerview.listener.CustomListener;
import ando.widget.pickerview.listener.OnOptionsSelectChangeListener;
import ando.widget.pickerview.listener.OnOptionsSelectListener;
import ando.widget.pickerview.listener.OnTimeSelectChangeListener;
import ando.widget.pickerview.listener.OnTimeSelectListener;
import ando.widget.pickerview.view.OptionsPickerView;
import ando.widget.pickerview.view.TimePickerView;
import ando.widget.wheelview.WheelView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    private Button btn_Time;
    private Button btn_Options;
    private Button btn_CustomOptions;
    private Button btn_CustomTime;
    private Button btn_CustomTime2;

    private TimePickerView pvTime, pvCustomTime, pvCustomTime2, pvCustomLunar;
    private OptionsPickerView pvOptions, pvCustomOptions, pvNoLinkOptions;
    private ArrayList<CardBean> cardItem = new ArrayList<>();

    private ArrayList<String> food = new ArrayList<>();
    private ArrayList<String> clothes = new ArrayList<>();
    private ArrayList<String> computer = new ArrayList<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //等数据加载完毕再初始化并显示Picker,以免还未加载完数据就显示,造成APP崩溃。
        getOptionData();

        initTimePicker();//时间选择器
        initCustomTimePicker();//时间选择器自定义布局
        initCustomTimePicker2();//时间选择器自定义布局-时分
        initLunarPicker();//公农历切换
        initOptionPicker();//条件选择器
        initCustomOptionPicker();//条件选择器自定义布局
        initNoLinkOptionsPicker();//条件选择器(不联动)

        btn_Time = (Button) findViewById(R.id.btn_Time);
        btn_Options = (Button) findViewById(R.id.btn_Options);
        btn_CustomOptions = (Button) findViewById(R.id.btn_CustomOptions);
        btn_CustomTime = (Button) findViewById(R.id.btn_CustomTime);
        btn_CustomTime2 = (Button) findViewById(R.id.btn_CustomTime2);
        Button btn_no_linkage = (Button) findViewById(R.id.btn_no_linkage);
        Button btn_to_Fragment = (Button) findViewById(R.id.btn_fragment);
        Button btn_circle = (Button) findViewById(R.id.btn_circle);

        btn_Time.setOnClickListener(this);
        btn_Options.setOnClickListener(this);
        btn_CustomOptions.setOnClickListener(this);
        btn_CustomTime.setOnClickListener(this);
        btn_CustomTime2.setOnClickListener(this);
        btn_no_linkage.setOnClickListener(this);
        btn_to_Fragment.setOnClickListener(this);
        btn_circle.setOnClickListener(this);

        findViewById(R.id.btn_GotoJsonData).setOnClickListener(this);
        findViewById(R.id.btn_lunar).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_Time && pvTime != null) {
            // pvTime.setDate(Calendar.getInstance());
            /* pvTime.show(); //show timePicker*/
            pvTime.show(v, true);//弹出时间选择器，传递参数过去，回调的时候则可以绑定此view
        } else if (v.getId() == R.id.btn_Options && pvOptions != null) {
            pvOptions.show(); //弹出条件选择器
        } else if (v.getId() == R.id.btn_CustomOptions && pvCustomOptions != null) {
            pvCustomOptions.show(); //弹出自定义条件选择器
        } else if (v.getId() == R.id.btn_CustomTime && pvCustomTime != null) {
            pvCustomTime.show(); //弹出自定义时间选择器
        } else if (v.getId() == R.id.btn_CustomTime2 && pvCustomTime2 != null) {
            pvCustomTime2.show(); //弹出自定义时间选择器-时分
        } else if (v.getId() == R.id.btn_no_linkage && pvNoLinkOptions != null) {//不联动数据选择器
            pvNoLinkOptions.show();
        } else if (v.getId() == R.id.btn_GotoJsonData) {//跳转到 省市区解析示例页面
            startActivity(new Intent(MainActivity.this, JsonDataActivity.class));
        } else if (v.getId() == R.id.btn_fragment) {//跳转到 fragment
            startActivity(new Intent(MainActivity.this, FragmentTestActivity.class));
        } else if (v.getId() == R.id.btn_lunar) {
            pvCustomLunar.show();
        } else if (v.getId() == R.id.btn_circle) {
            startActivity(new Intent(MainActivity.this, TestCircleWheelViewActivity.class));
        }
    }

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private void initLunarPicker() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2069, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
            }
        })
                .setDate(Calendar.getInstance())//系统当前时间
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {

                    @Override
                    public void customLayout(final View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.returnData();
                                pvCustomLunar.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.dismiss();
                            }
                        });
                        //公农历切换
                        CheckBox cb_lunar = (CheckBox) v.findViewById(R.id.cb_lunar);
                        cb_lunar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                pvCustomLunar.setLunarCalendar(!pvCustomLunar.isLunarCalendar());
                                //自适应宽
                                setTimePickerChildWeight(v, isChecked ? 0.8f : 1f, isChecked ? 1f : 1.1f);
                            }
                        });
                    }

                    /*
                     * 公农历切换后调整宽
                     */
                    private void setTimePickerChildWeight(View v, float yearWeight, float weight) {
                        ViewGroup timePicker = (ViewGroup) v.findViewById(R.id.timepicker);
                        View year = timePicker.getChildAt(0);
                        LinearLayout.LayoutParams lp = ((LinearLayout.LayoutParams) year.getLayoutParams());
                        lp.weight = yearWeight;
                        year.setLayoutParams(lp);
                        for (int i = 1; i < timePicker.getChildCount(); i++) {
                            View childAt = timePicker.getChildAt(i);
                            LinearLayout.LayoutParams childLp = ((LinearLayout.LayoutParams) childAt.getLayoutParams());
                            childLp.weight = weight;
                            childAt.setLayoutParams(childLp);
                        }
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }

    //Dialog 模式下，在底部弹出
    private void initTimePicker() {
        /*
        2022年9月27日 13:58:42
        注: Calendar.getInstance() 初始化时是用的当前时间 setTimeInMillis(System.currentTimeMillis()) ,
        如果有的变量未设置初始值, 如"小时"字段, 系统默认赋予当前时间的"小时"。
        所以, 当需要限定到"时分秒"时, Calendar.set 和 setType(new boolean[]{...}) 必须要对应上。
         */
//        Log.i("123", "initTimePicker=" + endDate.get(Calendar.HOUR_OF_DAY));

        //默认时间范围是 1900 - 2100年, 可以通过 setRangDate 重新设置范围
        Calendar startDate = Calendar.getInstance();
        startDate.set(2023, 7, 23, 15, 25, 35);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2035, 2, 5, 11, 22, 33);

        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                btn_Time.setText(getTime(date));
                Toast.makeText(MainActivity.this, getTime(date), Toast.LENGTH_SHORT).show();
                Log.i("pvTime", "onTimeSelect");
            }
        })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                //.setRangDate(startDate, endDate)
                .setType(new boolean[]{true, true, true, true, true, true}) //分别控制“年”“月”“日”“时”“分”“秒”的显示或隐藏
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                .setItemVisibleCount(7) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setLineSpacingMultiplier(2.5f)
                .setPadding(25, 0, 25, 0)
                .setContentTextSize(17)
                .setRangDate(startDate, endDate)//替换掉默认的时间范围 1900 - 2100年
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(ando.widget.pickerview.R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }

    //时间选择器 ，自定义布局
    private void initCustomTimePicker() {
        /*
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar startDate = Calendar.getInstance();
        startDate.set(2022, 8, 28, 15, 25, 35);//2022-09-28 15:25:35.122

        Calendar endDate = Calendar.getInstance();
        endDate.set(2035, 2, 5, 11, 22, 33);//非同一天测试通过
        //2022年9月27日 14:12:08 挨个测试通过
        //endDate.set(2022, 8, 30, 18, 28, 38);//同月不同天测试通过
        //endDate.set(2022, 8, 28, 18, 28, 38);//同一天测试通过
        //endDate.set(2022, 8, 28, 15, 28, 38);//同一天同一小时测试通过
        //endDate.set(2022, 8, 28, 15, 25, 38);//同一天同一小时同一分钟测试通过
        //Log.d("123", "startDate= " + startDate.getTimeInMillis() + " ; " + endDate.getTimeInMillis());

        pvCustomTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                btn_CustomTime.setText(getTime(date));
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentTextSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
                /*.animGravity(Gravity.RIGHT)// default is center*/

                //注:当同时设置 setDate 和 setRangDate 时, 尽量保证 setDate 值在范围之中
                .setDate(Calendar.getInstance())//系统当前时间
                .setRangDate(startDate, endDate)//设置时间范围
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(17)//默认18
                .setType(new boolean[]{true, true, true, true, true, true})//分别控制“年”“月”“日”“时”“分”“秒”的显示或隐藏
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(2F)
                .setTextXOffset(20, 0, 0, 0, 0, -20)

                //2023年9月13日 16:59:04 v1.8.0 修复原版`isCenterLabel=true`显示异常问题并增加`setCenterLabelSpacing(float)`控制文本和"年月日"等单位的间距
                //控制"年月日"等单位只在中间显示还是全部条目都显示
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setCenterLabelSpacing(5F) //设置中间文字和单位(label文字)的间距。仅在 isCenterLabel(true) 时生效。

                .isCyclic(false)//是否循环滚动
                .setDividerColor(0xFF24AD9D)
                .setPadding(15, 0, 15, 0)
                //.setMinHeight(580)
                .setItemVisibleCount(7)
                .isDialog(false)//中间弹窗样式

                //非中间字体关闭缩放时, 会强制开启颜色渐变, 否则会有文字重叠的问题; 当开启缩放时, !建议!关闭颜色渐变, 显示效果更好
                .setOuterTextScale(false)//关闭字体3D效果(默认开启) -> PickerOptions.isOuterTextScale
//                .setOuterTextLineSpacing(20)
                //.isAlphaGradient(true)//此时内部已强制为 true

                .build();
    }

    //时间选择器 ，自定义布局 - 时分
    private void initCustomTimePicker2() {
        Calendar startDate = Calendar.getInstance();
        startDate.set(2022, 8, 28, 15, 25, 35);//2022-09-28 15:25:35.122

        Calendar endDate = Calendar.getInstance();
        endDate.set(2035, 2, 5, 11, 22, 33);//非同一天测试通过
        pvCustomTime2 = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                btn_CustomTime2.setText(getTime(date));
            }
        })
                .setDate(Calendar.getInstance())//系统当前时间
                //.setRangDate(startDate, endDate)//设置时间范围
                .setLayoutRes(R.layout.pickerview_custom_time_hour_minute, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime2.returnData();
                                pvCustomTime2.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime2.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(17)//默认18
                .setType(new boolean[]{false, false, false, true, true, false})//分别控制“年”“月”“日”“时”“分”“秒”的显示或隐藏
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(2.5F)//拉大该值, 显示的更扁平
                //控制"年月日"等单位只在中间显示还是全部条目都显示
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setCenterLabelSpacing(15F) //设置中间文字和单位(label文字)的间距。仅在 isCenterLabel(true) 时生效。

                .isCyclic(false)//是否循环滚动
                .setDividerColor(0xFF24AD9D)
                .setPadding(30, 0, 30, 0)
                //.setMinHeight(580)
                .setItemVisibleCount(7)
                .isDialog(false)//中间弹窗样式
                .setDividerType(WheelView.DividerType.FILL)

                //非中间字体关闭缩放时, 会强制开启颜色渐变, 否则会有文字重叠的问题; 当开启缩放时, !建议!关闭颜色渐变, 显示效果更好
                .setOuterTextScale(false)//关闭字体3D效果(默认开启) -> PickerOptions.isOuterTextScale
//                .setOuterTextLineSpacing(20)
                //.isAlphaGradient(true)//此时内部已强制为 true
                .build();
    }

    private void initOptionPicker() {//条件选择器初始化
        /*
         * 注意 ：如果是三级联动的数据(省市区等)，请参照 JsonDataActivity 类里面的写法。
         */
        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = options1Items.get(options1).getPickerViewText()
                        + options2Items.get(options1).get(options2)
                        /* + options3Items.get(options1).get(options2).get(options3).getPickerViewText()*/;
                btn_Options.setText(tx);
            }
        })
                .setTitleText("城市选择")
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0, 1)//默认选中项
                .setBgColor(Color.BLACK)
                .setTitleBgColor(Color.DKGRAY)
                .setTitleColor(Color.LTGRAY)
                .setCancelColor(Color.YELLOW)
                .setSubmitColor(Color.YELLOW)
                .setTextColorCenter(Color.LTGRAY)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("省", "市", "区")
                .setOutSideColor(0x00000000) //设置外部遮罩颜色
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
                        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        /*pvOptions.setPicker(options1Items);//一级选择器*/
        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
    }

    private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        pvCustomOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = cardItem.get(options1).getPickerViewText();
                btn_CustomOptions.setText(tx);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        final TextView tvAdd = (TextView) v.findViewById(R.id.tv_add);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });

                        tvAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getCardData();
                                pvCustomOptions.setPicker(cardItem);
                            }
                        });

                    }
                })
                .isDialog(true)
                .setOutSideCancelable(false)
                .build();
        pvCustomOptions.setPicker(cardItem);//添加数据
    }

    private void initNoLinkOptionsPicker() {// 不联动的多级选项
        pvNoLinkOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {

                String str = "food:" + food.get(options1)
                        + "\nclothes:" + clothes.get(options2)
                        + "\ncomputer:" + computer.get(options3);

                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        })
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                        String str = "options1: " + options1 + "\noptions2: " + options2 + "\noptions3: " + options3;
                        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
                    }
                })
                .setItemVisibleCount(5)
                // .setSelectOptions(0, 1, 1)
                .build();
        pvNoLinkOptions.setNPicker(food, clothes, computer);
        pvNoLinkOptions.setSelectOptions(0, 1, 1);


    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        Log.d("123", "getTime() choice date millis: " + date.getTime());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    private void getOptionData() {
        /*
         * 注意：如果是添加JavaBean实体数据，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        getCardData();
        getNoLinkData();

        //选项1
        options1Items.add(new ProvinceBean(0, "广东", "描述部分", "其他数据"));
        options1Items.add(new ProvinceBean(1, "湖南", "描述部分", "其他数据"));
        options1Items.add(new ProvinceBean(2, "广西", "描述部分", "其他数据"));

        //选项2
        ArrayList<String> options2Items_01 = new ArrayList<>();
        options2Items_01.add("广州");
        options2Items_01.add("佛山");
        options2Items_01.add("东莞");
        options2Items_01.add("珠海");
        ArrayList<String> options2Items_02 = new ArrayList<>();
        options2Items_02.add("长沙");
        options2Items_02.add("岳阳");
        options2Items_02.add("株洲");
        options2Items_02.add("衡阳");
        ArrayList<String> options2Items_03 = new ArrayList<>();
        options2Items_03.add("桂林");
        options2Items_03.add("玉林");
        options2Items.add(options2Items_01);
        options2Items.add(options2Items_02);
        options2Items.add(options2Items_03);
        /*--------数据源添加完毕---------*/
    }

    private void getCardData() {
        for (int i = 0; i < 5; i++) {
            cardItem.add(new CardBean(i, "No.ABC12345 " + i));
        }

        for (int i = 0; i < cardItem.size(); i++) {
            if (cardItem.get(i).getCardNo().length() > 6) {
                String str_item = cardItem.get(i).getCardNo().substring(0, 6) + "...";
                cardItem.get(i).setCardNo(str_item);
            }
        }
    }

    private void getNoLinkData() {
        food.add("KFC");
        food.add("MacDonald");
        food.add("Pizza hut");

        clothes.add("Nike");
        clothes.add("Adidas");
        clothes.add("Armani");

        computer.add("ASUS");
        computer.add("Lenovo");
        computer.add("Apple");
        computer.add("HP");
    }
}