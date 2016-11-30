package com.example.saeteam.saeclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.DatePicker;
import android.content.SharedPreferences.Editor;


import java.util.Calendar;
/**
 * Created by Tiger on 2016/11/2.
 */
public class AnniView extends LinearLayout{



    private Button btnAddAnni;
    private ListView lvAnniList;
    private ArrayAdapter<AnniData> adapter;
    private static final String KEY_ANNI = "annilist";
    private AlarmManager anniManager;

    public AnniView(Context context) {
        super(context);
        init();
    }

    public AnniView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnniView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        anniManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onFinishInflate(){
        super.onFinishInflate();

        btnAddAnni = (Button)findViewById(R.id.btnAddAnni);
        lvAnniList = (ListView)findViewById(R.id.lvAnniList);

        adapter = new ArrayAdapter<AnniData>(getContext(),
                android.R.layout.simple_list_item_1);

        //adapter.add(new AnniData(System.currentTimeMillis()));
        lvAnniList.setAdapter(adapter);

        readSavedAnniList();

        btnAddAnni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAnni();
            }
        });

        lvAnniList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(getContext()).setTitle("操作选项").setItems(
                        new CharSequence[]{"删除", "全部删除"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        deleteAnni(position);
                                        break;
                                    case 1:
                                        deleteAllAnni();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                ).setNegativeButton("取消", null).show();

                return true;
            }
        });

    }

    private  void addAnni(){

        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(),new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth){
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY,8);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);


                AnniData ad = new AnniData(calendar.getTimeInMillis());
                adapter.add(ad);
                anniManager.set(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        PendingIntent.getBroadcast(getContext(),
                                ad.getId(),
                                new Intent(getContext(),
                                        AnniReceiver.class),
                                0));
                saveAnniList();
            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveAnniList(){
        Editor editor = getContext().getSharedPreferences(
                AnniView.class.getName(),
                Context.MODE_PRIVATE).edit();

        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < adapter.getCount(); i++){
            sb.append(adapter.getItem(i).getTime()).append(",");
        }

        if(sb.length() > 1){
            String content = sb.toString().substring(0,sb.length()-1);

            editor.putString(KEY_ANNI, content);

            System.out.println(content);
        }else{
            editor.putString(KEY_ANNI, null);
        }

        editor.commit();

    }

    private void readSavedAnniList(){
        SharedPreferences sp = getContext().getSharedPreferences(
                AnniView.class.getName(), Context.MODE_PRIVATE);
        String content = sp.getString(KEY_ANNI,null);

        if(content != null){
            String[] timeStrings = content.split(",");
            for(String str : timeStrings){
                adapter.add(new AnniData(Long.parseLong(str)));
            }
        }
    }

    private void deleteAnni(int position){
        AnniData ad = adapter.getItem(position);
        adapter.remove(ad);

        saveAnniList();

        anniManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),
                new Intent(getContext(), AnniReceiver.class),0));
    }

    private void deleteAllAnni(){

        int adapterCount =adapter.getCount();   // 为adapter的个数进行计数
        AnniData ad;
        for(int i = 0; i < adapterCount; i++){
            ad = adapter.getItem(0);       // 每次从第1个开始移除
            adapter.remove(ad);

            saveAnniList();       // 移除后重新保存列表

            anniManager.cancel(PendingIntent.getBroadcast(getContext(),ad.getId(),
                    new Intent(getContext(),AnniReceiver.class),0));   // 取消闹钟的广播
        }
    }

    private static class AnniData{

        private String timeLabel = "";
        private long time = 0;
        private Calendar date;

        public AnniData(long time){
            this.time = time;
            date = Calendar.getInstance();
            date.setTimeInMillis(time);
            timeLabel = String.format("%d年%d月%d日",
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH)+1,
                    date.get(Calendar.DAY_OF_MONTH));
        }

        public long getTime(){
            return time;
        }

        public String getTimeLabel(){
            return timeLabel;
        }

        public int getId(){
            return (int)(getTime()/1000/60);
        }

        @Override
        public String toString(){
            return getTimeLabel();
        }
    }
}