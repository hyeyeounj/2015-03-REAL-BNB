package net.balbum.baby;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import net.balbum.baby.Util.EmailUtil;
import net.balbum.baby.Util.ToastUtil;
import net.balbum.baby.VO.AuthVo;
import net.balbum.baby.VO.LoginVo;
import net.balbum.baby.lib.retrofit.ServiceGenerator;
import net.balbum.baby.lib.retrofit.TaskService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static net.balbum.baby.Util.ActivityUtil.goToActivity;

/**
 * Created by hyes on 2015. 11. 15..
 */
public class LoginActivity extends FragmentActivity{
    CallbackManager callbackManager;
    TextView info;
    Context context;
    String tokenB;
    SharedPreferences sharedPreferences;
    String callValue;
    LinearLayout linear;
    Button login_button_origin;
    EditText email, password;
    String emailString, passwordString;
    TaskService taskService;
    LoginVo loginVo;

    @Override
    protected void onResume() {
        super.onResume();
        taskService = ServiceGenerator.createService(TaskService.class);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        linear = (LinearLayout)findViewById(R.id.layout);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login_activity);

        password = (EditText)findViewById(R.id.user_password);
        email = (EditText)findViewById(R.id.user_email);

        login_button_origin = (Button)findViewById(R.id.login_button_origin);

        login_button_origin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TaskService taskService = ServiceGenerator.createService(TaskService.class);

                if(getInfo()) {
                    taskService.createLogin(loginVo, new Callback<AuthVo>() {
                        @Override
                        public void success(AuthVo authVo, Response response) {
                            goToActivity(context, MainActivity.class);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                }
            }
        });

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        info = (TextView)findViewById(R.id.info);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                        //Token을 sharedprefernce에 저장해두면 되겠군!
                );

//                token = loginResult.getAccessToken().getToken();
             //   saveToken(context);

                Handler handler = new Handler();
                handler.postDelayed(runnable, 1000);

            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    private boolean getInfo() {

        if (email.getText().toString().matches("")) {
            ToastUtil.show(context, "메일을 입력하세요");
        } else if (password.getText().toString().matches("")) {
            ToastUtil.show(context, "비밀번호를 입력하세요");
        } else {
            if (!EmailUtil.isValidEmail((CharSequence) email.getText().toString())) {
                ToastUtil.show(context, "메일형식을 확인하세요");
                return false;
            }
            loginVo = new LoginVo(email.getText().toString(), password.getText().toString());
            return true;
        }
        return false;

    }

//    private void doLogin(final LoginVo loginVo) {
//        Log.i("test", "task: emil" + loginVo.email);
//        taskService.createLogin(loginVo, new Callback<AuthVo>() {
//            @Override
//            public void success(AuthVo authVo, Response response) {
//                Toast.makeText(context, "Login 성공~~~~", Toast.LENGTH_SHORT).show();
//
//                Log.i("test", "task: " + authVo.token);
//                Log.i("test", "task: " + authVo.message);
//                saveTokenBalbum(context, authVo.token);
//
//
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
//                //실패시 토스트 메시니 또는 스낵바에 내용 띄워주기 추가할 것
//
//            }
//        });
//    }


    private void saveTokenBalbum(Context context, String token) {

        Log.d("test", "saveToken~ token: " + token);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tokenB", token);
        Toast.makeText(context, "tokenB저" + token, Toast.LENGTH_SHORT).show();
        editor.commit();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
