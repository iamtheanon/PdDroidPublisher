package cx.mccormick.pddroidparty.test;

import android.app.Activity;
import android.os.Bundle;
import cx.mccormick.pddroidparty.PdDroidPartyConfig;
import cx.mccormick.pddroidparty.PdDroidPartyLauncher;


public class PersistTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        PdDroidPartyConfig config = new PdDroidPartyConfig();
        config.guiKeepAspectRatio = true;
        config.presetsPaths.add("PersistTest/presets");
        PdDroidPartyLauncher.launch(this, "PersistTest/PersistTestAndroid.pd", config);
    }
    



}
