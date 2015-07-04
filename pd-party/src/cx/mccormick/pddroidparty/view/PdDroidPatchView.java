package cx.mccormick.pddroidparty.view;

import java.io.File;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.larvalabs.svgandroid.SVGParser;

import cx.mccormick.pddroidparty.PdDroidParty;
import cx.mccormick.pddroidparty.R;
import cx.mccormick.pddroidparty.svg.SVGRenderer;
import cx.mccormick.pddroidparty.widget.Bang;
import cx.mccormick.pddroidparty.widget.Canvasrect;
import cx.mccormick.pddroidparty.widget.Comment;
import cx.mccormick.pddroidparty.widget.Display;
import cx.mccormick.pddroidparty.widget.DroidNetClient;
import cx.mccormick.pddroidparty.widget.DroidNetReceive;
import cx.mccormick.pddroidparty.widget.DroidSystem;
import cx.mccormick.pddroidparty.widget.Knob;
import cx.mccormick.pddroidparty.widget.LoadSave;
import cx.mccormick.pddroidparty.widget.MenuBang;
import cx.mccormick.pddroidparty.widget.Numberbox;
import cx.mccormick.pddroidparty.widget.Numberbox2;
import cx.mccormick.pddroidparty.widget.Numberboxfixed;
import cx.mccormick.pddroidparty.widget.Slider;
import cx.mccormick.pddroidparty.widget.Taplist;
import cx.mccormick.pddroidparty.widget.Toggle;
import cx.mccormick.pddroidparty.widget.Touch;
import cx.mccormick.pddroidparty.widget.Widget;
import cx.mccormick.pddroidparty.widget.Wordbutton;

public class PdDroidPatchView extends View implements OnTouchListener {
	
	Paint paint = new Paint();
	public int patchwidth;
	public int patchheight;
	//view port :
	public int viewX = 0;
	public int viewY = 0;
	public int viewW = 1;
	public int viewH = 1;

	public int fontsize;
	ArrayList<Widget> widgets = new ArrayList<Widget>();
	private PdDroidParty app;
	private int splash_res = 0;
	private Resources res = null;
	private Picture background = null;
	private Bitmap bgbitmap = null;
	private RectF bgrect = new RectF();
	
	public PdDroidPatchView(Context context, PdDroidParty parent) {
		super(context);
		
		// disable graphic acceleration to have SVG properly rendered
		// not needed prior to API level 17
		// not possible prior to API level 11
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			setSoftwareMode();
		}
		
		app = parent;

		setFocusable(true);
		setFocusableInTouchMode(true);
		
		this.setOnTouchListener(this);
		this.setId(R.id.patch_view);
		// default background color settings
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		
		res = parent.getResources();
		
		// if there is a splash image, use it
		splash_res = res.getIdentifier("splash", "raw", parent.getPackageName());
		if (splash_res != 0) {
			// Get a drawable from the parsed SVG and set it as the drawable for the ImageView
			background = SVGParser.getSVGFromResource(res, splash_res).getPicture();
		} else {
			loadBackground();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setSoftwareMode()
	{
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
	private static Bitmap picture2Bitmap(Picture picture){
	    PictureDrawable pictureDrawable = new PictureDrawable(picture);
	    Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(), pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
	    //Log.e(TAG, "picture size: " + pictureDrawable.getIntrinsicWidth() + " " + pictureDrawable.getIntrinsicHeight());
	    Canvas canvas = new Canvas(bitmap);
	    canvas.drawPicture(pictureDrawable.getPicture());
	    return bitmap;
	}

	private void loadBackground() {
		// if we have a splash_res or we don't have a background
		if (bgbitmap == null || splash_res != 0) {
			// load the background image
			SVGRenderer renderer = SVGRenderer.getSVGRenderer(this, "background");
			if (renderer != null) {
				background = renderer.getPicture();
				bgbitmap = picture2Bitmap(background);
			}
			else {
				File f = new File(app.getPatchFile().getParent() + "/" + "background" + ".png");
				if (f.exists() && f.canRead() && f.isFile()) {
					bgbitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
				}
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawPaint(paint);
		canvas.save();

		canvas.scale(getWidth() / (float)viewW, getHeight() / (float)viewH);
		canvas.translate(-viewX, -viewY );

		bgrect.set(0, 0, patchwidth, patchheight);
		if (bgbitmap != null) {
			canvas.drawBitmap(bgbitmap, null, bgrect, null);
		}
	
		if (widgets != null) for (Widget widget: widgets) {
			widget.draw(canvas);
		}

		canvas.restore();
	}
	
	public float PointerX(float x){
		return (x * ((float)viewW) / getWidth() + viewX);
	}
	
	public float PointerY(float y){
		return (y * ((float)viewH) / getHeight() + viewY);
	}
	
	@Override
	public boolean performClick() 
	{
		return super.performClick();
	}
	
	public boolean onTouch(View view, MotionEvent event) {
		int index, pid, action;
		float x, y;
		
		if (widgets != null) {
			action = event.getActionMasked();
			switch(action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					index = event.getActionIndex();
					pid = event.getPointerId(index);
					x = PointerX(event.getX(index));
					y = PointerY(event.getY(index));
					for (Widget widget: widgets) {
						if( widget.touchdown(pid,x,y)) break;
					}
					break;
				case MotionEvent.ACTION_UP:
					view.performClick();
				case MotionEvent.ACTION_POINTER_UP:
					index = event.getActionIndex();
					pid = event.getPointerId(index);
					x = PointerX(event.getX(index));
					y = PointerY(event.getY(index));
					for (Widget widget: widgets) {
						if( widget.touchup(pid,x,y)) break;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					int pointerCount = event.getPointerCount();
					for (int p = 0; p < pointerCount; p++) {
						pid = event.getPointerId(p);
						x = PointerX(event.getX(p));
						y = PointerY(event.getY(p));
						for (Widget widget: widgets) {
							if( widget.touchmove(pid,x,y)) break;
						}
					}
					break;
				default:
			}
		}
		invalidate();
		return true;
	}
	
	/** Lets us invalidate this view from the audio thread */
	public void threadSafeInvalidate() {
		final PdDroidPatchView me = this;
		app.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				me.invalidate();
			}
		});
	}
	
	/** Main patch is done loading, now we should change the background from the splash. **/
	public void loaded() {
		loadBackground();
	}
	
	/** build a user interface using the lines of atoms found in the patch by the pd file parser */
	public void buildUI(ArrayList<String[]> atomlines) {
		//ArrayList<String> canvases = new ArrayList<String>();
		int level = 0;
		
		for (String[] line: atomlines) {
			if (line.length >= 4) {
				// find canvas begin and end lines
				if (line[1].equals("canvas")) {
					/*if (canvases.length == 0) {
						canvases.add(0, "self");
					} else {
						canvases.add(0, line[6]);
					}*/
					level += 1;
					if (level == 1) {
						viewW = patchwidth = Integer.parseInt(line[4]);
						viewH = patchheight = Integer.parseInt(line[5]);
						fontsize = Integer.parseInt(line[6]);
					}
				} else if (line[1].equals("restore")) {
					//canvases.remove(0);
					level -= 1;
				// find different types of UI element in the top level patch
				} else if (level == 1) {
					if (line.length >= 2) {
						// builtin pd things
						if (line[1].equals("text")) {
							widgets.add(new Comment(this, line));
						} else if (line[1].equals("floatatom")) {
							widgets.add(new Numberbox(this, line));
						} else if (line.length >= 5) {
							// pd objects
							if (line[4].equals("vsl")) {
								widgets.add(new Slider(this, line, false));
							} else if (line[4].equals("hsl")) {
								widgets.add(new Slider(this, line, true));
							} else if (line[4].equals("tgl")) {
								widgets.add(new Toggle(this, line));
							} else if (line[4].equals("bng")) {
								widgets.add(new Bang(this, line));
							} else if (line[4].equals("nbx")) {
								widgets.add(new Numberbox2(this, line));
							} else if (line[4].equals("cnv")) {
								widgets.add(new Canvasrect(this, line));
							} else if (line[4].equals("mknob")) {
								widgets.add(new Knob(this, line));
							}
							// special PdDroidParty abstractions
							else if (line[4].equals("wordbutton")) {
								widgets.add(new Wordbutton(this, line));
							} else if (line[4].equals("numberbox")) {
								widgets.add(new Numberboxfixed(this, line));
							} else if (line[4].equals("taplist")) {
								widgets.add(new Taplist(this, line));
							} else if (line[4].equals("display")) {
								widgets.add(new Display(this, line));
							} else if (line[4].equals("touch")) {
								widgets.add(new Touch(this, line));
							}
						}
					}
				}
				
				// things that can be found at any depth and still work
				if (line.length >= 5) {
					if (line[4].equals("droidnetreceive")) {
						widgets.add(new DroidNetReceive(this, line));
					} else if (line[4].equals("droidnetclient")) {
						widgets.add(new DroidNetClient(this, line));
					} else if (line[4].equals("menubang")) {
						new MenuBang(this, line);
					} else if (line[4].equals("loadsave")) {
						new LoadSave(this, line);
					} else if (line[4].equals("droidsystem")) {
						new DroidSystem(this, line);
					}
				}
			}
		}
		threadSafeInvalidate();
	}

	public String replaceDollarZero(String name) 
	{
		return app.replaceDollarZero(name);
	}

	public Object getSystemService(String name) {
		return app.getSystemService(name);
	}

	public void startActivity(Intent intent) 
	{
		app.startActivity(intent);
	}

	public void registerReceiver(String name, Widget w) {
		app.registerReceiver(name, w);
	}

	public String getPatchRelativePath(String dir) {
		return app.getPatchRelativePath(dir);
	}

	public void launchDialog(Widget which, int type) {
		app.launchDialog(which, type);
	}

	public File getPatchFile() {
		return app.getPatchFile();
	}

	public void send(String dest, String s) {
		app.send(dest, s);
	}
}
