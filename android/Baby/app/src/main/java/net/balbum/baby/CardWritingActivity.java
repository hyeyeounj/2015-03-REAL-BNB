package net.balbum.baby;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import net.balbum.baby.Util.Define;
import net.balbum.baby.VO.BabyTagVo;
import net.balbum.baby.VO.BabyVo;
import net.balbum.baby.VO.GeneralCardVo;
import net.balbum.baby.VO.ResponseVo;
import net.balbum.baby.adapter.BabyTagAdapter;
import net.balbum.baby.lib.retrofit.ServiceGenerator;
import net.balbum.baby.lib.retrofit.TaskService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static net.balbum.baby.Util.ActivityUtil.goToActivity;

/**
 * Created by hyes on 2015. 11. 10..
 */
public class CardWritingActivity extends AppCompatActivity {

    Context context;
    List<Fragment> fragmentList = new ArrayList<>();
    List<BabyTagVo> babyTagNamesList;
    List<BabyVo> babyVoList;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_writing);

        context = this;

        Intent intent = getIntent();
        int type = intent.getIntExtra("type", 0);
        GeneralCardFragment generalCardFragment = new GeneralCardFragment();
        EventCardFragment eventCardFragment = new EventCardFragment();

        initToolbar();
        initData();
        initViewPager(generalCardFragment, eventCardFragment);
//        initBabyTag();

        if (type == Define.CARD_MODIFY) {

            GeneralCardVo cardVo = (GeneralCardVo) intent.getParcelableExtra("generalCardVo");
            Bundle bundle = new Bundle();


           // bundle.putParcelable("vo", Parcels.wrap(cardVo));
            Log.d("test", "modify start~" + cardVo.type);
            // ((OnSetCardListener) fragmentList.get(0)).setCardInfo(generalCardVo);

            if(cardVo.type == "NORMAL") {
                generalCardFragment.setArguments(bundle);
                Log.d("test", "general");

            }else if(cardVo.type == "EVENT"){
                Log.d("test", "event");
                viewPager.setCurrentItem(1);
                eventCardFragment.setArguments(bundle);

            }
        }
    }

    private void initBabyTag() {
        RecyclerView rv_baby = (RecyclerView)findViewById(R.id.rv_baby_list);
        StaggeredGridLayoutManager sgm = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        rv_baby.setLayoutManager(sgm);
        BabyTagAdapter adapter = new BabyTagAdapter(babyTagNamesList, context);
        rv_baby.setAdapter(adapter);
    }

    private void initViewPager(GeneralCardFragment generalCardFragment, EventCardFragment eventCardFragment) {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager, generalCardFragment, eventCardFragment);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private Context initData(){

        //기본 저장되어있는 아이 정보 불러와서 리스트 만들 부분
        babyTagNamesList = new ArrayList<>();
        babyVoList = new ArrayList<>();

        TaskService taskService = ServiceGenerator.createService(TaskService.class);
        taskService.getBabies("token", new Callback<ArrayList<BabyVo>>() {
              @Override
              public void success(ArrayList<BabyVo> babyVos, Response response) {
                  for(BabyVo baby : babyVos){
                      BabyTagVo babyTag = new BabyTagVo(baby.babyImg, baby.bId, false, baby.babyName);
                      babyTagNamesList.add(babyTag);
                      initBabyTag();
                  }
              }

              @Override
              public void failure(RetrofitError error) {

              }
        });
        return null;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupViewPager(ViewPager viewPager, GeneralCardFragment generalCardFragment, EventCardFragment eventCardFragment) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(generalCardFragment, "일상의 순간");
        adapter.addFrag(eventCardFragment, "특별한 순간");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.writing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_share) {
            return true;
        }

        if (id == R.id.action_save) {

            int currentItem = viewPager.getCurrentItem();
            GeneralCardVo vo =  ((OnGetCardListener)fragmentList.get(currentItem)).getCardInfo();

            String type = null;
            if(currentItem == 0){
                type = "NORMAL";
            }else if(currentItem == 1){
                type = "EVENT";
            }

            File file = new File(vo.cardImg);
            TypedFile typedFile = new TypedFile("multipart/form-data", file);

            List<Long> asd = new ArrayList<Long>();
            asd.add(new Long(2));
            asd.add(new Long(3));
            asd.add(new Long(4));

            Long l = new Long(2);


            TaskService taskService = ServiceGenerator.createService(TaskService.class);
            if((Long)vo.cid == 0) {

                taskService.createCard(typedFile, "token", asd.get(0), vo.content, vo.modifiedDate, type, new Callback<ResponseVo>() {
                    @Override
                    public void success(ResponseVo responseVo, Response response) {
                        Log.i("test", "card success" + responseVo.state + ", error: " + responseVo.error);
                        goToActivity(CardWritingActivity.this, MainActivity.class);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("test", "card error: " + error);
                    }
                });
            }else{
                taskService.updateCard(typedFile, "token", asd.get(0), vo.content, vo.modifiedDate, type, vo.cid, new Callback<ResponseVo>() {
                    @Override
                    public void success(ResponseVo responseVo, Response response) {
                        Log.i("test", "업데이트 성공!");
                        goToActivity(CardWritingActivity.this, MainActivity.class);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.i("test", "업데이트 실패!");
                    }
                });

            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        List<String> fragmentTitleList = new ArrayList<String>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}


