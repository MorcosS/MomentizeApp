package leadtools.ocrdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import leadtools.LeadEvent;
import leadtools.LeadPoint;
import leadtools.LeadPointD;
import leadtools.RasterImage;
import leadtools.controls.ControlAlignment;
import leadtools.controls.CoordinateType;
import leadtools.controls.ImageViewer;
import leadtools.controls.ImageViewerInteractiveMode;
import leadtools.controls.ImageViewerScrollMode;
import leadtools.controls.ImageViewerSizeMode;
import leadtools.controls.InteractiveService;
import leadtools.controls.InteractiveSimpleOnGestureListener;
import leadtools.controls.RasterImageViewer;
import leadtools.controls.TransformChangedListener;
import leadtools.imageprocessing.core.KeyStoneCommand;

/**
 * Custom Viewer interactive mode to apply ManualPerspectiveDeskewCommand
 */
public class ManualDeskewInteractiveMode extends ImageViewerInteractiveMode {
   /**
    * Current active point (The current selected thumb)
    */
   enum ActivePoint {
      NONE,            // None of the thumbs selected
      TOP_LEFT,        // TopLeft thumb selected
      TOP_RIGHT,       // TopRight thumb selected
      BOTTOM_LEFT,     // BottomLeft thumb selected
      BOTTOM_RIGHT,    // BottomRight thumb selected
      TOP_MID,         // TopMid thumb selected
      LEFT_MID,        // LeftMid thumb selected
      RIGHT_MID,       // RightMid thumb selected
      BOTTOM_MID       // BottomMid thumb selected
   }

   // Threshold value in pixels for hit-testing.
   private static final int HIT_TEST_BUFFER = 50;

   /**
    * Gets the name of the interactive mode.
    * @return The name of the interactive mode.
    */
   @Override
   public String getName() {
      return "Manual Deskew";
   }

   /**
    * Thumbs coordinates
    */
   private PointF mTopLeft;
   private PointF mTopRight;
   private PointF mBottomLeft;
   private PointF mBottomRight;

   // Current selected thumb
   private ActivePoint mActivePoint;

   // Original Image (Before applying ManualPerspectiveDeskewCommand)
   private RasterImage mOriginalImage;

   // Custom view to draw the deskew rect and thumbs
   private View mOverlayView;
   private Paint mPaint;

   // Simple gesture listener to handle thumbs move
   private InteractiveSimpleOnGestureListener mInteractiveSimpleOnGestureListener;

   // Disable restarting the interactive mode when the image data (RasterImage) changed
   @Override
   public boolean getRestartOnImageChange() {
      return false;
   }

   // Init and create the drawing view used to draw the Thumbs
   private void createInteractiveDrawingView(ImageViewer viewer) {
      if (mOverlayView != null)
         return;

      mOverlayView = new View(viewer.getContext()) {
         @Override
         public void onDraw(Canvas canvas) {
            drawDeskewRect(canvas);
         }
      };

      viewer.addView(mOverlayView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
   }

   // Destroy and remove the drawing view 'mOverlayView'
   private void destroyInteractiveDrawingView(ImageViewer viewer) {
      if (mOverlayView == null)
         return;

      viewer.removeView(mOverlayView);
      mOverlayView = null;
   }

   /**
    * Constructor
    */
   public ManualDeskewInteractiveMode() {
      // Init the drawing paint, used for drawing thumbs
      mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      mPaint.setStyle(Style.STROKE);
      mPaint.setColor(Color.RED);
      mPaint.setStrokeWidth(1f);

      // Init coordinates
      mTopLeft = new PointF();
      mTopRight = new PointF();
      mBottomLeft = new PointF();
      mBottomRight = new PointF();
      mActivePoint = ActivePoint.NONE;

      // Disable working on image rectangle
      setWorkOnImageRectangle(false);

      mInteractiveSimpleOnGestureListener = new InteractiveSimpleOnGestureListener() {
         @Override
         public void onDown(Object source, MotionEvent event) {
            if (!canStartWork(event))
               return;

            ImageViewer viewer = getImageViewer();
            if (!viewer.hasImage())
               return;

            if (!isWorking()) {
               onWorkStarted(LeadEvent.getEmpty(this));
            }
         }

         @Override
         public void onMove(Object source, MotionEvent event) {
            if (isWorking()) {
               // Update the thumbs and draw
               PointF pt = new PointF(event.getX(), event.getY());

               switch (mActivePoint) {
                  case TOP_LEFT:
                     setTopLeft(pt);
                     break;
                  case TOP_RIGHT:
                     setTopRight(pt);
                     break;
                  case BOTTOM_LEFT:
                     setBottomLeft(pt);
                     break;
                  case BOTTOM_RIGHT:
                     setBottomRight(pt);
                     break;
                  case TOP_MID:
                     setTopMid(pt);
                     break;
                  case BOTTOM_MID:
                     setBottomMid(pt);
                     break;
                  case LEFT_MID:
                     setLeftMid(pt);
                     break;
                  case RIGHT_MID:
                     setRightMid(pt);
                     break;
                  default:
                     break;
               }

               invalidateInteractiveView();
            }
         }
         @Override
         public void onUp(Object source, MotionEvent event) {
            if (isWorking()) {
               onWorkCompleted(LeadEvent.getEmpty(this));
               mActivePoint = ActivePoint.NONE;
               invalidateInteractiveView();
            }
         }
      };
   }

   private void invalidateInteractiveView() {
      if (mOverlayView == null)
         return;

      mOverlayView.invalidate();
   }

   // Override canStartWork
   // Returns true if the current active point is not 'NONE' (When the user is moving any of the thumbs)
   @Override
   protected boolean canStartWork(MotionEvent event) {
      if(!super.canStartWork(event))
         return false;

      PointF pt = new PointF(event.getX(), event.getY());

      ActivePoint currentActivePoint = findActivePoint(pt);
      if (currentActivePoint != ActivePoint.NONE) {
         mActivePoint = currentActivePoint;
         return true;
      } else
         return false;
   }

   @Override
   public void start(ImageViewer viewer) {
      super.start(viewer);
      InteractiveService service = super.getInteractiveService();
      // Add simple gesture listener to handle moving thumbs
      service.addSimpleGuestureListener(mInteractiveSimpleOnGestureListener);
      if (!viewer.hasImage())
         return;

      // Disable DPI to simplify calculation also image will always be Fit.
      viewer.setScrollMode(ImageViewerScrollMode.HIDDEN);
      viewer.setImageHorizontalAlignment(ControlAlignment.CENTER);
      viewer.setImageVerticalAlignment(ControlAlignment.CENTER);
      viewer.setRotateAngle(0);
      ((RasterImageViewer) viewer).setAutoFreeImages(false);

      // Save the original image (because if the image width or height > 400 we resize it)
      mOriginalImage = getRasterImage();
      setAndFitImage();
      resetDrawingView();
      createInteractiveDrawingView(viewer);

      getImageViewer().addTransformChangedListener(new TransformChangedListener() {
         @Override
         public void onTransformChanged(LeadEvent leadEvent) {
            resetDrawingView();
         }
      });

   }


   private void resetDrawingView(){

      if(getImageViewer() == null)
         return;

      int offset = ((RasterImageViewer)getImageViewer()).getImage().getWidth() / 20;
      LeadPointD topLeft = getImageViewer().getTransform().transformPoint(LeadPointD.create(offset, offset));
      LeadPointD topRight = getImageViewer().getTransform().transformPoint(LeadPointD.create(((RasterImageViewer) getImageViewer()).getImage().getWidth() - offset, offset));
      LeadPointD bottomRight = getImageViewer().getTransform().transformPoint(LeadPointD.create(((RasterImageViewer) getImageViewer()).getImage().getWidth() - offset, ((RasterImageViewer) getImageViewer()).getImage().getHeight() -offset));
      LeadPointD bottomLeft = getImageViewer().getTransform().transformPoint(LeadPointD.create(offset, ((RasterImageViewer) getImageViewer()).getImage().getHeight() -offset));

      mTopLeft = new PointF((float)topLeft.getX(), (float)topRight.getY());
      mTopRight = new PointF((float)topRight.getX(), (float)topRight.getY());
      mBottomRight = new PointF((float)bottomRight.getX(),(float)bottomRight.getY());
      mBottomLeft = new PointF((float)bottomLeft.getX(), (float)bottomLeft.getY());
   }

   @Override
   public void stop(ImageViewer viewer) {
      if (isStarted()) {
         InteractiveService service = super.getInteractiveService();
         service.removeSimpleGuestureListener(mInteractiveSimpleOnGestureListener);

         // Delete the overlay view used for drawing thumbs
         destroyInteractiveDrawingView(viewer);

         // Reset viewer settings
         viewer.beginUpdate();
         viewer.setScrollMode(ImageViewerScrollMode.AUTO);
         viewer.setImageHorizontalAlignment(ControlAlignment.CENTER);
         viewer.setImageVerticalAlignment(ControlAlignment.CENTER);
         viewer.setScaleFactor(1);
         viewer.setAspectRatioCorrection(1);
         viewer.setSizeMode(ImageViewerSizeMode.FIT_ALWAYS);
         ((RasterImageViewer) viewer).setAutoFreeImages(true);
         viewer.endUpdate();
         super.stop(viewer);
      }
   }

   private boolean isValidX(float x) {
      if (x < 0 || x > mOverlayView.getWidth())
         return false;

      return true;
   }

   private boolean isValidY(float y) {
      if (y < 0 || y > mOverlayView.getHeight())
         return false;

      return true;
   }

   public PointF getTopLeft() {
      return mTopLeft;
   }

   public void setTopLeft(PointF value) {
      boolean changeX = false;
      boolean changeY = false;
      if (value.x + 20 < mTopRight.x)
         changeX = true;

      if (value.y + 20 < mBottomLeft.y)
         changeY = true;

      if (changeX && isValidX(value.x))
         mTopLeft.x = (value.x);

      if (changeY &&isValidY(value.y))
         mTopLeft.y = (value.y);
   }

   public PointF getTopRight() {
      return mTopRight;
   }

   public void setTopRight(PointF value) {
      boolean changeX = false;
      boolean changeY = false;
      if (value.x - 20 > mTopLeft.x)
         changeX = true;

      if (value.y - 20 < mBottomRight.y)
         changeY = true;

      if (changeX && isValidX(value.x))
            mTopRight.x = (value.x);

      if (changeY && isValidY(value.y))
            mTopRight.y = (value.y);
   }

   public PointF getBottomLeft() {
      return mBottomLeft;
   }

   public void setBottomLeft(PointF value) {
      boolean changeX = false;
      boolean changeY = false;

      if (value.x + 20 < mBottomRight.x)
         changeX = true;

      if (value.y - 20 > mTopLeft.y)
         changeY = true;

      if (changeX && isValidX(value.x))
            mBottomLeft.x = (value.x);

      if (changeY && isValidY(value.y))
            mBottomLeft.y = (value.y);
   }

   public PointF getBottomRight() {
      return mBottomRight;
   }

   public void setBottomRight(PointF value) {

      boolean changeX = false;
      boolean changeY = false;

      if (value.x - 20 > mBottomLeft.x)
         changeX = true;

      if (value.y - 20 > mTopRight.y)
         changeY = true;

      if (changeX && isValidX(value.x))
         mBottomRight.x = (value.x);


      if (changeY && isValidY(value.y))
         mBottomRight.y = (value.y);
   }

   public PointF getTopMid() {
      return new PointF((mTopLeft.x + mTopRight.x) / 2,
            (mTopLeft.y + mTopRight.y) / 2);
   }

   public void setTopMid(PointF value) {
      float dX = value.x - (mTopRight.x + mTopLeft.x) / 2;
      float dY = value.y - (mTopRight.y + mTopLeft.y) / 2;

      if (isValidX(mTopRight.x + dX) && isValidX(mTopLeft.x + dX)) {
         mTopRight.x = (mTopRight.x + dX);
         mTopLeft.x = (mTopLeft.x + dX);
      }

      if ((mBottomLeft.y - mTopLeft.y - dY > 20)
            && (mBottomRight.y - mTopRight.y - dY > 20)) {
         if (isValidY(mTopRight.y + dY) && isValidY(mTopLeft.y + dY)) {
            mTopRight.y = (mTopRight.y + dY);
            mTopLeft.y = (mTopLeft.y + dY);
         }
      }
   }

   public PointF getBottomMid() {
      return new PointF((mBottomLeft.x + mBottomRight.x) / 2,
            (mBottomLeft.y + mBottomRight.y) / 2);
   }

   public void setBottomMid(PointF value) {
      PointF bottomMid = getBottomMid();
      float dX = value.x - bottomMid.x;
      float dY = value.y - bottomMid.y;

      if (isValidX(mBottomRight.x + dX) && isValidX(mBottomLeft.x + dX)) {
         mBottomRight.x = (mBottomRight.x + dX);
         mBottomLeft.x = (mBottomLeft.x + dX);
      }

      if ((mBottomLeft.y - mTopLeft.y + dY > 20)
            && (mBottomRight.y - mTopRight.y + dY > 20)) {
         if (isValidY(mBottomRight.y + dY) && isValidY(mBottomLeft.y + dY)) {
            mBottomRight.y = (mBottomRight.y + dY);
            mBottomLeft.y = (mBottomLeft.y + dY);
         }
      }
   }

   public PointF getLeftMid() {
      return new PointF((mTopLeft.x + mBottomLeft.x) / 2,
            (mTopLeft.y + mBottomLeft.y) / 2);
   }

   public void setLeftMid(PointF value) {
      float dX = value.x - getLeftMid().x;

      if ((mTopRight.x - mTopLeft.x - dX > 20)
            && (mBottomRight.x - mBottomLeft.x - dX > 20)) {
         if (isValidX(mBottomLeft.x + dX) && isValidX(mTopLeft.x + dX)) {
            mBottomLeft.x = (mBottomLeft.x + dX);
            mTopLeft.x = (mTopLeft.x + dX);
         }
      }
   }

   public PointF getRightMid() {
      return new PointF((mTopRight.x + mBottomRight.x) / 2,
            (mTopRight.y + mBottomRight.y) / 2);
   }

   public void setRightMid(PointF value) {
      float dX = value.x - getRightMid().x;

      if ((mBottomRight.x + dX - mBottomLeft.x > 20)
            && (mTopRight.x + dX - mTopLeft.x > 20)) {
         if (isValidX(mBottomRight.x + dX) && isValidX(mTopRight.x + dX)) {
            mBottomRight.x = (mBottomRight.x + dX);
            mTopRight.x = (mTopRight.x + dX);
         }
      }
   }

   // Find the active point based on the current point position and HIT_TEST_BUFFER
   private ActivePoint findActivePoint(PointF pt) {
      if (Helpers.distanceBetweenPoints(pt, mTopLeft) < HIT_TEST_BUFFER)
         return ActivePoint.TOP_LEFT;
      if (Helpers.distanceBetweenPoints(pt, mTopRight) < HIT_TEST_BUFFER)
         return ActivePoint.TOP_RIGHT;
      if (Helpers.distanceBetweenPoints(pt, mBottomLeft) < HIT_TEST_BUFFER)
         return ActivePoint.BOTTOM_LEFT;
      if (Helpers.distanceBetweenPoints(pt, mBottomRight) < HIT_TEST_BUFFER)
         return ActivePoint.BOTTOM_RIGHT;
      if (Helpers.distanceBetweenPoints(pt, getTopMid()) < HIT_TEST_BUFFER)
         return ActivePoint.TOP_MID;
      if (Helpers.distanceBetweenPoints(pt, getLeftMid()) < HIT_TEST_BUFFER)
         return ActivePoint.LEFT_MID;
      if (Helpers.distanceBetweenPoints(pt, getRightMid()) < HIT_TEST_BUFFER)
         return ActivePoint.RIGHT_MID;
      if (Helpers.distanceBetweenPoints(pt, getBottomMid()) < HIT_TEST_BUFFER)
         return ActivePoint.BOTTOM_MID;
      return ActivePoint.NONE;
   }

   public void applyKeyStoneCommand(){

      LeadPoint[] pts = new LeadPoint[4];
      pts[0] = getImageViewer().convertPoint(CoordinateType.CONTROL, CoordinateType.IMAGE, LeadPointD.create(mTopLeft.x, mTopLeft.y)).toLeadPoint();
      pts[1] = getImageViewer().convertPoint(CoordinateType.CONTROL, CoordinateType.IMAGE, LeadPointD.create(mTopRight.x, mTopRight.y)).toLeadPoint();
      pts[2] = getImageViewer().convertPoint(CoordinateType.CONTROL, CoordinateType.IMAGE, LeadPointD.create(mBottomRight.x, mBottomRight.y)).toLeadPoint();
      pts[3] = getImageViewer().convertPoint(CoordinateType.CONTROL, CoordinateType.IMAGE, LeadPointD.create(mBottomLeft.x, mBottomLeft.y)).toLeadPoint();
      KeyStoneCommand cmd = new KeyStoneCommand();
      cmd.setPolygonPoints(pts);
      cmd.run(mOriginalImage);
      mOriginalImage = cmd.getTransformedBitmap();

   }

   private RasterImage getRasterImage() {
      RasterImageViewer viewer = ((RasterImageViewer) getImageViewer());
      return viewer.getImage();
   }

   private void setRasterImage(RasterImage image) {
      RasterImageViewer viewer = ((RasterImageViewer) getImageViewer());
      viewer.setImage(image);
   }

   public void setAndFitImage() {
      setRasterImage(mOriginalImage);
      // Update the viewer settings
      getImageViewer().beginUpdate();
      getImageViewer().setSizeMode(ImageViewerSizeMode.FIT_ALWAYS);
      getImageViewer().setScaleFactor(1.0);
      getImageViewer().setRestrictHiddenScrollMode(false);
      getImageViewer().endUpdate();
   }

   // Draw deskew rect and thumbs
   private void drawDeskewRect(Canvas canvas) {
      PointF[] points = new PointF[4];
      points[0] = getTopLeft();
      points[1] = getTopRight();
      points[2] = getBottomRight();
      points[3] = getBottomLeft();

      canvas.drawLine(mTopLeft.x, mTopLeft.y, mTopRight.x, mTopRight.y, mPaint);
      canvas.drawLine(mTopRight.x, mTopRight.y, mBottomRight.x, mBottomRight.y, mPaint);
      canvas.drawLine(mBottomRight.x, mBottomRight.y, mBottomLeft.x, mBottomLeft.y, mPaint);
      canvas.drawLine(mBottomLeft.x, mBottomLeft.y, mTopLeft.x, mTopLeft.y, mPaint);

      for (int i = 0; i < 4; i++) {
         float x = points[i].x;
         float y = points[i].y;
         RectF rect = new RectF(x - 5, y - 5, x + 5, y + 5);
         canvas.drawRect(rect, mPaint);
      }

      points[0] = getTopMid();
      points[1] = getBottomMid();
      points[2] = getLeftMid();
      points[3] = getRightMid();

      for (int i = 0; i < 4; i++) {
         float x = points[i].x;
         float y = points[i].y;
         RectF rect = new RectF(x - 5, y - 5, x + 5, y + 5);
         canvas.drawOval(rect, mPaint);
      }

      if (mActivePoint != ActivePoint.NONE) {
         PointF pt;
         switch (mActivePoint) {
         case TOP_LEFT:
            pt = mTopLeft;
            break;
         case TOP_RIGHT:
            pt = mTopRight;
            break;
         case BOTTOM_LEFT:
            pt = mBottomLeft;
            break;
         case BOTTOM_RIGHT:
            pt = mBottomRight;
            break;
         case TOP_MID:
            pt = getTopMid();
            break;
         case BOTTOM_MID:
            pt = getBottomMid();
            break;
         case LEFT_MID:
            pt = getLeftMid();
            break;
         case RIGHT_MID:
            pt = getRightMid();
            break;
         default:
            return;
         }

         float x = pt.x;
         float y = pt.y;

         RectF rc = new RectF(x - 10, y - 10, x + 10, y + 10);
         canvas.drawOval(rc, mPaint);
         rc = new RectF(x - 15, y - 15, x + 15, y + 15);
         canvas.drawOval(rc, mPaint);
      }
   }
}
