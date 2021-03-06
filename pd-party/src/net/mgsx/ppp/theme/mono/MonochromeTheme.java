package net.mgsx.ppp.theme.mono;

import net.mgsx.ppp.theme.Theme;
import net.mgsx.ppp.widget.Widget;
import android.graphics.Color;

/**
 * Advanced theme using color shading.
 */
@SuppressWarnings("serial")
public class MonochromeTheme implements Theme
{
	public static final float RED = 0;
	public static final float ORANGE = 30;
	public static final float YELLOW = 60;
	public static final float GREEN = 120;
	public static final float BLUE = 240;
	
	protected int fgColor, bgColor, mainBgColor;
	
	/**
	 * Simple mode : giving a hue and an aspect
	 * @param hue base color frequency from 0 to 360
	 * @param dark inverse colors for a dark theme (true) or a light theme (false)
	 */
	public MonochromeTheme(float hue, boolean dark) 
	{
		super();
		
		if(dark)
		{
			fgColor = Color.HSVToColor(255, new float[]{hue, 1, 1});
			bgColor = Color.HSVToColor(255, new float[]{hue, 1, 0.2f});
			mainBgColor = 0xFF000000;
		}
		else
		{
			fgColor = Color.HSVToColor(255, new float[]{hue, 1.0f, 0.5f});
			bgColor = Color.HSVToColor(255, new float[]{hue, 0.1f, 1.0f});
			mainBgColor = 0xFFFFFFFF;
		}
	}

	
	/**
	 * Mode custom : give all colors
	 * @param mainBgColor
	 * @param bgColor
	 * @param fgColor
	 */
	public MonochromeTheme(int mainBgColor, int bgColor, int fgColor) {
		super();
		this.mainBgColor = mainBgColor;
		this.bgColor = bgColor;
		this.fgColor = fgColor;
	}



	@Override
	public int getForegroundColor(Widget widget) {
		return fgColor;
	}

	@Override
	public int getBackgroundColor(Widget widget) {
		return bgColor;
	}

	@Override
	public int getLabelColor(Widget widget) {
		return fgColor;
	}

	@Override
	public int getBackgroundColor() {
		return mainBgColor;
	}

}
