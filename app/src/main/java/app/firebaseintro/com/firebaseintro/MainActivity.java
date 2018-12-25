package app.firebaseintro.com.firebaseintro;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private static final String TAG = "MainActivity";

    private EditText email;
    private EditText password;
    private Button login;
    private Button signOut;
    private Button createAcc;

    //Auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.email_input);
        password = (EditText) findViewById(R.id.password_input);
        login = (Button) findViewById(R.id.login);
        signOut = (Button) findViewById(R.id.signOut);
        createAcc = (Button) findViewById(R.id.createAcc);




        //we instantiate firebase
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("message");

        //auth
        mAuth = FirebaseAuth.getInstance();

   //     databaseReference.setValue("Hello ");

        //Allows us to update real time
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String value = dataSnapshot.getValue(Customer.class);
//                Toast.makeText(MainActivity.this, value , Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        // get the current user if he is login or not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null){
                    //USER IS SIGNED IN
                    Log.d(TAG, "user is signed in");
                    Log.d(TAG, "userName: " + user.getEmail());
                }else{

                    //USER IS SIGNED OUT
                    Log.d(TAG, "user is signed out");
                }
            }
        };

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString = email.getText().toString();
                String pwd = password.getText().toString();

                if (!emailString.isEmpty() && !pwd.isEmpty()){

                    mAuth.signInWithEmailAndPassword(emailString, pwd).addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Failed to sign in", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this,  "sign in successful", Toast.LENGTH_SHORT).show();

                                        Customer customer = new Customer("uthman", "ayinde", emailString, 30 );

                                        databaseReference.setValue(customer);
                                    }
                                }
                            }
                    );
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();

                Toast.makeText(MainActivity.this, "user signed out", Toast.LENGTH_SHORT).show();
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailString = email.getText().toString();
                String pwd = password.getText().toString();

                if (!emailString.isEmpty() && !pwd.isEmpty()){
                    mAuth.createUserWithEmailAndPassword(emailString, pwd).addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Account not created", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(MainActivity.this,  "account created", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }



            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
