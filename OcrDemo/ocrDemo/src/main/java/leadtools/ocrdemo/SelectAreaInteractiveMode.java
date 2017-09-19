package leadtools.ocrdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import leadtools.LeadEvent;
import leadtools.LeadRect;
import leadtools.LeadRectD;
import leadtools.LeadSizeD;
import leadtools.controls.CoordinateType;
import leadtools.controls.ImageViewer;
import leadtools.controls.ImageViewerInteractiveMode;
import leadtools.controls.ImageViewerSizeMode;
import leadtools.controls.InteractiveService;
import leadtools.controls.InteractiveSimpleOnGestureListener;

/**
 * Custom Viewer interactive mode for selecting an OCR recognition area(zone)
 */
public class SelectAreaInteractiveMode extends ImageViewerInteractiveMode {
   /**
    * Current active point (Like the current selected thumb, or the rectangle body)
    */
   private enum RectangleActivePoint {
      NONE,          // None of the points selected
      TOP_LEFT,      // TopLeft point is selected
      TOP_RIGHT,     // TopRight point is selected
      BOTTOM_LEFT,   // BottomLeft point is selected
      BOTTOM_RIGHT,  // BottomRight point is selected
      RECT_BODY      // The rectangle body is selected (Used to move the rectangle)
   }

   // Threshold value in pixels for hit-testing.
   private static final int HIT_TEST_BUFFER = 20;
   // Thumbnail length (Value to use for thumnail width and height)
   private static final int THUMB_LENGTH = 10;

   /**
    * Gets the name of the interactive mode.
    * @return The name of the interactive mode.
    */
   @Override
   public String getName() {
      return "Select Area";
   }

   // Current active point
   private RectangleActivePoint mActivePoint;

   // Image and View area bounds
   private LeadRectD mImageRectangle;
   private RectF mSelectedArea;

   // Paint objects to draw the selected area and thumbs
   private Paint mPaint;
   private Paint mThumbsPaint;

   // Custom view to draw the selected area
   private View mOverlayView;

   // Simple gesture listener to handle touch events
   private InteractiveSimpleOnGestureListener mInteractiveSimpleOnGestureListener;

   // Init and create the drawing view used to draw the selected area and the Thumbs
   private void createInteractiveDrawingView(ImageViewer viewer) {
      if (mOverlayView != null)
         return;

      mOverlayView = new View(viewer.getContext()) {
         @Override
         protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            ImageViewer viewer = getImageViewer();
            if (!viewer.hasImage())
               return;
            LeadRectD selectedArea = viewer.convertRect(CoordinateType.IMAGE, CoordinateType.CONTROL, mImageRectangle);
            // Convert to RectF and set the value in mSelectedArea
            mSelectedArea = new RectF((float)selectedArea.getLeft(), (float)selectedArea.getTop(), (float)selectedArea.getRight(), (float)selectedArea.getBottom());
         }

         @Override
         public void onDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(mSelectedArea,  Op.DIFFERENCE);
            canvas.drawARGB(128, 128, 128, 128);
            canvas.restore();
            
            // Draw selected area rectangle 
            canvas.drawRect(mSelectedArea, mPaint);
            
            // Draw thumbs
            canvas.drawRect(mSelectedArea.left - THUMB_LENGTH, mSelectedArea.top - THUMB_LENGTH, mSelectedArea.left + THUMB_LENGTH, mSelectedArea.top + THUMB_LENGTH, mThumbsPaint);
            canvas.drawRect(mSelectedArea.left - THUMB_LENGTH, mSelectedArea.bottom - THUMB_LENGTH, mSelectedArea.left + THUMB_LENGTH, mSelectedArea.bottom + THUMB_LENGTH, mThumbsPaint);
            canvas.drawRect(mSelectedArea.right - THUMB_LENGTH, mSelectedArea.top - THUMB_LENGTH, mSelectedArea.right + THUMB_LENGTH, mSelectedArea.top + THUMB_LENGTH, mThumbsPaint);
            canvas.drawRect(mSelectedArea.right - THUMB_LENGTH, mSelectedArea.bottom - THUMB_LENGTH, mSelectedArea.right + THUMB_LENGTH, mSelectedArea.bottom + THUMB_LENGTH, mThumbsPaint);
         }
      };

      mOverlayView.setBackgroundColor(Color.TRANSPARENT);
      viewer.addView(mOverlayView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
   public SelectAreaInteractiveMode() {
      // Selected rectangle paint
      mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      mPaint.setStyle(Style.STROKE);
      mPaint.setColor(Color.RED);
      mPaint.setStrokeWidth(2f);
      
      // Thumbs paint
      mThumbsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      mThumbsPaint.setStyle(Style.FILL_AND_STROKE);
      mThumbsPaint.setColor(Color.BLUE);

      mSelectedArea = new RectF();
      mImageRectangle = new LeadRectD();
      mActivePoint = RectangleActivePoint.NONE;

      // Create a simple gesture listener to listen for down, move and up events
      mInteractiveSimpleOnGestureListener = new InteractiveSimpleOnGestureListener() {
         private PointF mLastPoint;

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

            mLastPoint = new PointF(event.getX(), event.getY());
         }

         @Override
         public void onMove(Object source, MotionEvent event) {
            if (isWorking()) {
               ImageViewer viewer = getImageViewer();

               float x = event.getX();
               float y = event.getY();

               LeadSizeD size = viewer.getImageSize();
               LeadRectD rect = viewer.convertRect(CoordinateType.IMAGE, CoordinateType.CONTROL, new LeadRectD(0, 0, size.getWidth(), size.getHeight()));
               RectF newSelectedArea = new RectF(mSelectedArea);
               // Update rectangle bounds
               switch (mActivePoint) {
               case TOP_LEFT:
                  newSelectedArea.top = y;
                  newSelectedArea.left = x;
                  break;
               case TOP_RIGHT:
                  newSelectedArea.top = y;
                  newSelectedArea.right = x;
                  break;
               case BOTTOM_LEFT:
                  newSelectedArea.bottom = y;
                  newSelectedArea.left = x;
                  break;
               case BOTTOM_RIGHT:
                  newSelectedArea.bottom = y;
                  newSelectedArea.right = x;
                  break;
               case RECT_BODY:
                  float xDiff = x - mLastPoint.x;
                  float yDiff = y - mLastPoint.y;
                  newSelectedArea.top += yDiff;
                  newSelectedArea.bottom += yDiff;
                  newSelectedArea.left += xDiff;
                  newSelectedArea.right += xDiff;
               default:
                  break;
               }

               // Limit the selected area rectangle to image bounds
               if(newSelectedArea.top > rect.getTop() && newSelectedArea.bottom < rect.getBottom() && newSelectedArea.top < newSelectedArea.bottom) {
                  mSelectedArea.top = newSelectedArea.top;
                  mSelectedArea.bottom = newSelectedArea.bottom;
               }
               if(newSelectedArea.left > rect.getLeft() && newSelectedArea.right < rect.getRight() && newSelectedArea.left < newSelectedArea.right) {
                  mSelectedArea.left = newSelectedArea.left;
                  mSelectedArea.right = newSelectedArea.right;
               }

               mImageRectangle = viewer.convertRect(CoordinateType.CONTROL, CoordinateType.IMAGE, LeadRectD.fromLTRB(mSelectedArea.left, mSelectedArea.top, mSelectedArea.right, mSelectedArea.bottom));
               mLastPoint = new PointF(x, y);
               invalidateInteractiveView();
            }
         }

         @Override
         public void onUp(Object source, MotionEvent event) {
            if (isWorking()) {
               onWorkCompleted(LeadEvent.getEmpty(this));
               mActivePoint = RectangleActivePoint.NONE;
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
   // Returns true if the current active point is not 'NONE' (When the user is moving any of the thumbs, or the rectangle body)
   @Override
   protected boolean canStartWork(MotionEvent event) {
      if(!super.canStartWork(event))
         return false;

      ImageViewer viewer = getImageViewer();
      if (!viewer.hasImage())
         return false;

      PointF pt = new PointF(event.getX(), event.getY());

      RectangleActivePoint currentActivePoint = findActivePoint(pt);
      if (currentActivePoint != RectangleActivePoint.NONE) {
         mActivePoint = currentActivePoint;
         return true;
      } else
         return false;
   }

   @Override
   public void start(ImageViewer viewer) {
      super.start(viewer);
      // Add simple gesture listener to handle touch events (For moving the rectangle and the thumbs)
      InteractiveService service = super.getInteractiveService();
      service.addSimpleGuestureListener(mInteractiveSimpleOnGestureListener);
      if (!viewer.hasImage())
         return;

      setWorkOnImageRectangle(false);
      // Set the viewer size mode to fit (To show the whole image for selection)
      viewer.zoom(ImageViewerSizeMode.FIT, 1, viewer.getDefaultZoomOrigin());


      LeadSizeD size = viewer.getImageSize();
      // Set the default are to the center
      mImageRectangle = new LeadRectD(size.getWidth() * 0.25, size.getHeight() * 0.25, size.getWidth() * 0.5, size.getHeight() * 0.5);
      LeadRectD selectedArea = viewer.convertRect(CoordinateType.IMAGE, CoordinateType.CONTROL, mImageRectangle);
      // Convert to RectF
      mSelectedArea = new RectF((float)selectedArea.getLeft(), (float)selectedArea.getTop(), (float)selectedArea.getRight(), (float)selectedArea.getBottom());
      
      createInteractiveDrawingView(viewer);
   }

   @Override
   public void stop(ImageViewer viewer) {
      if (isStarted()) {
         InteractiveService service = super.getInteractiveService();
         service.removeSimpleGuestureListener(mInteractiveSimpleOnGestureListener);

         // Delete the overlay view
         destroyInteractiveDrawingView(viewer);
         viewer.postInvalidate();
         super.stop(viewer);
      }
   }

   /**
    * Gets the selected area.
    * @return The selected area.
    */
   public LeadRect getSelectedImageRectangle() {
      return  mImageRectangle.toLeadRect();
   }

   // Find the active point based on the current point position and HIT_TEST_BUFFER
   private RectangleActivePoint findActivePoint(PointF pt) {
      // Check the point
      if (Helpers.distanceBetweenPoints(pt, new PointF(mSelectedArea.left, mSelectedArea.top)) < HIT_TEST_BUFFER)
         return RectangleActivePoint.TOP_LEFT;
      else if (Helpers.distanceBetweenPoints(pt, new PointF(mSelectedArea.right, mSelectedArea.top)) < HIT_TEST_BUFFER)
         return RectangleActivePoint.TOP_RIGHT;
      else if (Helpers.distanceBetweenPoints(pt, new PointF(mSelectedArea.left, mSelectedArea.bottom)) < HIT_TEST_BUFFER)
         return RectangleActivePoint.BOTTOM_LEFT;
      else if (Helpers.distanceBetweenPoints(pt, new PointF(mSelectedArea.right, mSelectedArea.bottom)) < HIT_TEST_BUFFER)
         return RectangleActivePoint.BOTTOM_RIGHT;
      else if(mSelectedArea.contains(pt.x, pt.y))
         return RectangleActivePoint.RECT_BODY;
         
      return RectangleActivePoint.NONE;
   }
}
