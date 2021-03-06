package net.mgsx.ppp.widget.core;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.mgsx.ppp.pd.PdGUI;
import net.mgsx.ppp.view.PdDroidPatchView;
import net.mgsx.ppp.widget.Widget;

import org.puredata.core.PdBase;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Bang extends Widget {
	private static final String TAG = "Bang";

	volatile protected boolean bang = false;
	int interrpt,hold; //interrupt and hold time, in ms.
	
	WImage on = new WImage();
	WImage off = new WImage();
	
	ScheduledThreadPoolExecutor timer = new ScheduledThreadPoolExecutor(1);
	ScheduledFuture<?> task;
	
	public Bang(PdDroidPatchView app, String[] atomline) {
		super(app);

		float x = Float.parseFloat(atomline[2]) ;
		float y = Float.parseFloat(atomline[3]) ;
		float w = Float.parseFloat(atomline[5]) ;
		float h = Float.parseFloat(atomline[5]) ;
		
		hold = (int)Float.parseFloat(atomline[6]) ;
		interrpt = (int)Float.parseFloat(atomline[7]) ;
		init = (int)Float.parseFloat(atomline[8]) ;

		sendname = app.replaceDollarZero(atomline[9]);
		receivename = atomline[10];
		label = setLabel(atomline[11]);
		labelpos[0] = Float.parseFloat(atomline[12]) ;
		labelpos[1] = Float.parseFloat(atomline[13]) ;
		labelfont = Integer.parseInt(atomline[14]);
		labelsize = (int)(Float.parseFloat(atomline[15]));
		bgcolor = PdGUI.getColor(Integer.parseInt(atomline[16]));
		fgcolor = PdGUI.getColor(Integer.parseInt(atomline[17]));
		labelcolor = PdGUI.getColor(Integer.parseInt(atomline[18]));

		// listen out for floats from Pd
		setupreceive();

		// graphics setup
		dRect = new RectF(Math.round(x), Math.round(y), Math.round(x + w), Math.round(y + h));
		
		// try and load images
		on.load(TAG, "on", label, sendname, receivename);
		off.load(TAG, "off", label, sendname, receivename);

	}

	public Bang(PdDroidPatchView app) {
		super(app);
	}

	public void draw(Canvas canvas) {

		if (off.draw(canvas)) {
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(bgcolor);
			canvas.drawRect(dRect, paint);

			paint.setColor(fgcolor);
			paint.setStrokeWidth(1);
			canvas.drawLine(dRect.left /*+ 1*/, dRect.top, dRect.right, dRect.top, paint);
			canvas.drawLine(dRect.left + 0, dRect.bottom, dRect.right, dRect.bottom, paint);
			canvas.drawLine(dRect.left, dRect.top + 0, dRect.left, dRect.bottom, paint);
			canvas.drawLine(dRect.right, dRect.top + 0, dRect.right, dRect.bottom, paint);
			
			paint.setColor(fgcolor);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(dRect.centerX(), dRect.centerY(), Math.min(dRect.width(), dRect.height()) / 2, paint);
		}
		if (bang) {
			
			
			// parent.threadSafeInvalidate();
			if(on.draw(canvas))
			{
				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(dRect.centerX(), dRect.centerY(), Math.min(dRect.width(), dRect.height()) / 2, paint);
				paint.setColor(fgcolor);
			}
		}
		drawLabel(canvas);
	}

	// visual bang :
	private void bang() {
		bang = true;
		if(task != null && !task.isCancelled())
		{
			task.cancel(true);
		}
		task = timer.schedule(new Runnable() {
			
			@Override
			public void run() {
				bang = false;
				parent.threadSafeInvalidate();
				
			}
		}, hold, TimeUnit.MILLISECONDS);
	}

	public boolean touchdown(int pid, float x, float y)
	{
		if (dRect.contains(x, y)) {
			bang();
			PdBase.sendBang(sendname);
			return true;
		}
		
		return false;
	}
	
	/*public void receiveAny() {
		bang();
	}*/
	
	public void receiveList(Object... args) {
		bang();
	}
	
	
	public void receiveSymbol(String symbol) {
		bang();
	}
	
	public void receiveFloat(float x) {
		bang();
	}
	
	public void receiveBang() {
		bang();
	}

	
	public void receiveMessage(String symbol, Object... args) {
		if(widgetreceiveSymbol(symbol,args)) return;
		else bang();
	}

}
