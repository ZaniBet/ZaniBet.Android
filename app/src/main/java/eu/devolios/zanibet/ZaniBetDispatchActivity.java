package eu.devolios.zanibet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Gromat Luidgi on 09/11/2017.
 */

public abstract class ZaniBetDispatchActivity extends AppCompatActivity {

    protected abstract Class<?> getTargetClass();

    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent().setAction("Already created");
        runDispatch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String action = getIntent().getAction();
        // Prevent endless loop by adding a unique action, don't restart if action is present
        if(action == null || !action.equals("Already created")) {
            runDispatch();
        } else {
            getIntent().setAction(null);
        }
    }

    private void runDispatch() {
        Intent intent = new Intent(this, getTargetClass());
        if (getTargetClass() == LoadingActivity.class) intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }
}
