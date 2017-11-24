package local.viettel.minhbq.newactivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private EditText textUserName;
    private EditText textPassword;
    private Button btnLogin;
    private TextView textCount;
    private final String sPASS = "12345678";
    private final String sUSER = "admin";
    private int iLoginAttempt = 5;
/*    private static int iPASS;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
/*        initPassword ();*/
        updateAttemptCount();
        btnLoginClick ();

    }

    public void btnLoginClick () {
        textUserName = (EditText) findViewById(R.id.editTextUsername);
        textPassword = (EditText) findViewById(R.id.editTextPwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String sUsername = textUserName.getText().toString();
                        String sPass = textPassword.getText().toString();

                        if ( (sUsername.equals(sUSER) ) && (sPass.equals(sPASS)) ) {
                            Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                            iLoginAttempt = 5;

                        } else {
                            textUserName.setText("");
                            textPassword.setText("");
                            Toast.makeText(LoginActivity.this, "Login fail!\nUser: " + sUsername + "\nPass: " + sPass,Toast.LENGTH_SHORT).show();
                            iLoginAttempt --;
                            if (iLoginAttempt ==0) {
                                btnLogin.setEnabled(false);
                            }
                        }
                        updateAttemptCount();
                    }
                }
        );
    }
    private void updateAttemptCount () {
        textCount = (TextView) findViewById(R.id.textViewAttempt);
        textCount.setText(Integer.toString(iLoginAttempt));
    }
/*
    private void initPassword () {
        iPASS = sPASS.hashCode();
    }*/
}
