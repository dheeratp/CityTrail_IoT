package name.bagi.levente.pedometer;

/**
 * Created by Dheean on 4/20/15.
 */


import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import android.graphics.Point;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class AnimationView extends View {

    Paint paint;
    Paint paint2;

    Bitmap bm;
    Bitmap bmChild;
    int bmparent_offsetX, bmparent_offsetY;
    int bmchild_offsetX, bmchild_offsetY;


    Path animPathParent;
    Path animPathChild;
    PathMeasure pathMeasureParent;
    PathMeasure pathMeasureChild;
    float pathLengthParent;
    float pathLengthChild;

    float step;   //distance each step
    float distance;  //distance moved

    float[] pos;
    float[] tan;

    Matrix matrix;
    Canvas canvas;
    Bitmap alteredBitmap;
    Bitmap resizedBitmap;
    private static final String TAG = "Pedometer";
    float x1, y1, x2, y2;
    SharedPreferences mState;
    //values for child
    float stepChild;
    float distanceChild;
    float[] posChild;
    float[] tanChild;

    Matrix matrixChild;
    float childx1, childy1, childx2, childy2;


    public AnimationView(Context context) {
        super(context);
        Log.i("Dheera - ","Inside AnimationView constructor");
        initMyView(context);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("Dheera - ","Inside AnimationView constructor 2 attr");
        initMyView(context);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("Dheera - ","Inside AnimationView constructor 3 attr");
        initMyView(context);
    }

    public void initMyView(Context context){

        //
    	Log.i(TAG, "[ACTIVITY] Inside initMyView function");
  

//        Drawable myDrawable = getResources().getDrawable(R.drawable.mapitaly);
//        alteredBitmap = ((BitmapDrawable) myDrawable).getBitmap();
//
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width=size.x;
//        int height=size.y;
////        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
////        float dp = 350f;
////        float fpixels = metrics.density * dp;
////        int pixels = (int) (fpixels + 0.5f);
//        resizedBitmap = Bitmap.createScaledBitmap(alteredBitmap, 3*width/4, height, false);



        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(70);
        paint.setStyle(Paint.Style.STROKE);

        //paint object for main line
        paint2 = new Paint();
        paint2.setColor(Color.parseColor("#E0555B")); //Color of pizza on the map is #E0555B
        paint2.setStrokeWidth(70);
        paint2.setStyle(Paint.Style.STROKE);

        //avatar for parent
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.pet2);
        bmparent_offsetX = bm.getWidth()/2;
        bmparent_offsetY = bm.getHeight()/2;
        //avatar for child
        bmChild = BitmapFactory.decodeResource(getResources(), R.drawable.pet1);
        bmchild_offsetX = bmChild.getWidth()/2;
        bmchild_offsetY = bmChild.getHeight()/2;

        animPathParent = new Path();
        animPathParent.moveTo(615, 1400);
      //  animPath.lineTo(740, 1270);
        //animPath.lineTo(1330, 1270);
        
        //animPath.close();

       

        //Toast.makeText(getContext(), "pathLength: " + pathLengthParent, Toast.LENGTH_LONG).show();

        step = 5;
        distance = 0;
        pos = new float[2];
        tan = new float[2];

        matrix = new Matrix();
        
        //values for child
        
        animPathChild = new Path();
        animPathChild.moveTo(785, 1400);
        
        stepChild = 5;
        distanceChild = 0;
        posChild = new float[2];
        tanChild = new float[2];
       
        matrixChild = new Matrix();
         Log.i("!!!!!!!!!!Dheera - ", "bm height="+bm.getHeight()+" Parent step count =" + Pedometer.mStepValue +" Child step count ="+Pedometer.childstepcount);
    }

    @Override
    protected void onDraw(Canvas canvas) {

       //canvas.drawBitmap(resizedBitmap,0,0,null);
       // Log.i("Dheera - ","Inside onDraw() .. pathLength="+pathLength+" distance="+distance+ " bm_offsetX="+bm_offsetX+" bm_offsetY="+bm_offsetY);

        //Draw lines for main path
    	
//        canvas.drawLine(740,1400,740,1370,paint2);
//        canvas.drawLine(740,1270,1360,1270,paint2);
//        canvas.drawLine(1360-bm_offsetX/4,1270,1360-bm_offsetX/4,955,paint2);
//        canvas.drawLine(1360-bm_offsetX/4,955,155-bm_offsetX/4,955,paint2);
    	 // let's calibrate the limit as per recommended number of steps per day- 10000 steps/day 
    	//And we have 5950 coordinates to move over for both the avatars on the map
    	//Which means, for each step taken, lets move the avatar by 0.5950px over the map
    	
    	//1200 is the diameter of the circle..let's say
    	// Log.i("#####Dheera - ","In onDraw()...parent....x1="+x1+" x2="+x2);
    	//VALUES FOR PARENT
        x1=615f;
    	y1=1400f;
    	x2=615f;
    	
    	if(Pedometer.mStepValue<100)
    	{
     		float temp=Float.parseFloat(Integer.toString(Pedometer.mStepValue))*11;
    		y2=y1-temp;
    	}
    	
    	canvas.drawLine(x1,y1,x2,y2,paint2);
    	
    	
    	//VALUES FOR CHILD
    	childx1=785f;
     	childy1=1400f;
     	childx2=785f;
     	if(Pedometer.childstepcount<100)
    	{
     		float tempchild=Float.parseFloat(Integer.toString(Pedometer.childstepcount))*11;
     		childy2=childy1-tempchild;
    	}
     	
    	
    	
    	
    	//canvas.drawCircle(740, 700, 600, paint);
    	
       
        //Draw path for the avatar's workout
    	
    	//Part where im drawing along circle
    	/*float oX=740; float cX=740; float oY=1400; float cY=700;
    	float deltaX = oX-cX; 
    	float deltaY = oY-cY;
    	double radius = Math.sqrt(deltaX*deltaX+deltaY*deltaY);
    	double d=100;
    	double orthoX = -deltaY*d/radius;
    	double orthoY = deltaX*d/radius;
    	double newDeltaX = deltaX+orthoX; 
    	double newDeltaY = deltaY+orthoY;
    	double newLength = Math.sqrt(newDeltaX*newDeltaX+newDeltaY*newDeltaY);
    	double aX = cX+newDeltaX*radius/newLength; 
    	double aY = cY+newDeltaY*radius/newLength;
    	*/
    
    	//end of part
    	
    	//one more experiment
    	
    	/*final RectF oval = new RectF();
    	
    	//Below is the desc of API for parameters of "set"
    	//public Rect (int left, int top, int right, int bottom)
		//Added in API level 1
		//Create a new rectangle with the specified coordinates. Note: no range checking is performed, so the caller must ensure that left <= right and top <= bottom.
		//
		//Parameters
		//left	The X coordinate of the left side of the rectangle
		//top	The Y coordinate of the top of the rectangle
		//right	The X coordinate of the right side of the rectangle
		//bottom	The Y coordinate of the bottom of the rectangle
    	radius=600;

		oval.set((float)cX- (float)radius, (float)cY-(float)radius, (float)cX + (float)radius, (float)cY +(float)radius);
		animPath.lineTo((float)aX, (float)aY);
		animPath.arcTo(oval, 0, 15);
		*/
    	//end of experiment
    	
    	animPathParent.lineTo(x2, y2);
    	animPathChild.lineTo(childx2, childy2);
    	
		 pathMeasureParent = new PathMeasure(animPathParent, false);
	     pathLengthParent = pathMeasureParent.getLength();
	     
	     pathMeasureChild = new PathMeasure(animPathChild, false);
	     pathLengthChild = pathMeasureChild.getLength();
        
	    
	        canvas.drawPath(animPathParent, paint2);
	        canvas.drawPath(animPathChild, paint2);
	    


        if(distance < pathLengthParent){

            pathMeasureParent.getPosTan(distance, pos, tan);

            matrix.reset();
            //float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees = 0;
           // Log.i("Dheera - ", "Inside AnimationView distance < pathLength...degrees= " + degrees);
            matrix.postRotate(degrees, bmparent_offsetX, bmparent_offsetY);

           
            	matrix.postTranslate(pos[0]-bmparent_offsetX, pos[1]-bmparent_offsetY);
            
            
            
            //Log.i("Dheera in onDraw...", " coordi="+Float.toString(pos[0]-bmparent_offsetX)+" , "+Float.toString(pos[1]-bmparent_offsetY));

            canvas.drawBitmap(bm, matrix, null);

            distance += step;


        }else{
            //distance = 0;
            matrix.reset();
           // float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees=0;
          //  Log.i("Dheera - ","Inside else distance >= pathLength...degrees= "+degrees);
            matrix.postRotate(degrees, bmparent_offsetX, bmparent_offsetY);
            
            
            	matrix.postTranslate(pos[0]-bmparent_offsetX, pos[1]-bmparent_offsetY);
            

            canvas.drawBitmap(bm, matrix, null);

        }
        
        
        // ANIMATING AVATAR  FOR CHILD
        

        if(distanceChild < pathLengthChild){

            pathMeasureChild.getPosTan(distanceChild, posChild, tanChild);

            matrixChild.reset();
            //float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees = 0;
           //Log.i("Dheera - ", "Inside AnimationView distance < pathLength...degrees= " + degrees);
            matrixChild.postRotate(degrees, bmchild_offsetX, bmchild_offsetY);

            
            	matrixChild.postTranslate(posChild[0]-bmchild_offsetX, posChild[1]-bmchild_offsetY);
            

            canvas.drawBitmap(bmChild, matrixChild, null);

            distanceChild += stepChild;
           

        }else{
            //distance = 0;
            matrixChild.reset();
           // float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
            float degrees=0;
          //  Log.i("Dheera - ","Inside else distance >= pathLength...degrees= "+degrees);
            matrixChild.postRotate(degrees, bmchild_offsetX, bmchild_offsetY);

           
            	matrixChild.postTranslate(posChild[0]-bmchild_offsetX, posChild[1]-bmchild_offsetY);
            

            canvas.drawBitmap(bmChild, matrixChild, null);
           
        }
       // Log.i("Dheera - ","Inside AnimationView before invalidate() ");
        invalidate();
    }

}