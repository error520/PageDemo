package com.kinco.MotorApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.kinco.MotorApp.sys.MyApplication;
import com.kinco.MotorApp.ui.DeviceList;

import com.kinco.MotorApp.ui.firstpage.FirstpageFragment;
import com.kinco.MotorApp.ui.fourthpage.FourthpageFragment;
import com.kinco.MotorApp.ui.secondpage.SecondpageFragment;
import com.kinco.MotorApp.ui.thirdpage.ThirdpageFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    private TextView textview;
    private Button mButton3;
    private Button mButton4;
    private FirstpageFragment firstpageFragment=new FirstpageFragment(); ;
    private SecondpageFragment secondpageFragment=new SecondpageFragment();
    private ThirdpageFragment thirdpageFragment=new ThirdpageFragment();
    private FourthpageFragment fourthpageFragment=new FourthpageFragment();

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_first, R.id.navigation_second, R.id.navigation_third, R.id.navigation_fourth)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);
//
//    }
    RadioGroup mainRadioGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainRadioGroupId = (RadioGroup) findViewById(R.id.main_radioGroupId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_id, firstpageFragment);
        fragmentTransaction.commit();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(firstpageFragment);
        fragments.add(secondpageFragment);
        fragments.add(thirdpageFragment);
        fragments.add(fourthpageFragment);

        new TabFragmentUtils(mainRadioGroupId, R.id.fragment_id, fragments, getSupportFragmentManager());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //小技巧 重写这个方法  然后不重写父类的方法，可以避免程序闪退之后，几个fragment，会重叠！
        super.onSaveInstanceState(outState);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item00:
                Intent intent = new Intent(this, DeviceList.class);
                try{
                startActivity(intent);
                }catch(Exception e){
                    Log.d("device_list",e.toString());
                }
             break;
            case R.id.item01:
                Toast.makeText(MyApplication.getContext(),"Coming soon",Toast.LENGTH_SHORT);
                break;
            case R.id.item02:
                Toast.makeText(MyApplication.getContext(),"Coming soon",Toast.LENGTH_SHORT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }


}
