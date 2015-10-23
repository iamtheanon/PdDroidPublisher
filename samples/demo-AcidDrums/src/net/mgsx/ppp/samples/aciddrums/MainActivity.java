package net.mgsx.ppp.samples.aciddrums;

import net.mgsx.ppp.PdDroidPartyConfig;
import net.mgsx.ppp.PdDroidPartyLauncher;
import net.mgsx.ppp.theme.mono.MonochromeTheme;
import net.mgsx.ppp.widget.core.Bang;
import net.mgsx.ppp.widget.core.Slider;
import net.mgsx.ppp.widget.custom.RibbonSlider;
import net.mgsx.ppp.widget.custom.SimpleBang;
import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        PdDroidPartyConfig config = new PdDroidPartyConfig();
        config.guiKeepAspectRatio = true;
        config.theme = new MonochromeTheme(MonochromeTheme.YELLOW,true);
        
       
        config.typeOverrides.put(Slider.class, RibbonSlider.class);
        config.typeOverrides.put(Bang.class, SimpleBang.class);
       
       // config.patches.put("Full Stack", "Acid_Drums/aciddrums_fullstack.pd");
        config.guiPatches.put("Sequencer", "Acid_Drums/aciddrums_sequencer.pd");
        config.guiPatches.put("Audio Controls", "Acid_Drums/aciddrums_controls.pd");
        config.corePatches.add("Acid_Drums/aciddrums_audiocore.pd");
        
        config.presetsPaths.add("Acid_Drums/presets_pattern");
        config.presetsPaths.add("Acid_Drums/presets_synth");
        
        PdDroidPartyLauncher.launch(this, config);
        //PdDroidPartyLauncher.launch(this,"Acid_Drums/AcidDrums.pd", config);
    }
    



}
