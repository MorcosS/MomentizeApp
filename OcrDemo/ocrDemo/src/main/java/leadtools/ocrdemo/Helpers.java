package leadtools.ocrdemo;

import android.content.Context;
import android.graphics.PointF;

import java.util.Locale;

import leadtools.forms.ocr.OcrProgressOperation;

/**
 * OCR helper methods
 */
public class Helpers {
   /**
    * Get the equivalent friendly name of the ocr progress operation
    * @param context The context object.
    * @param operation OCR progress callback operation identification
    * @return The operation friendly name of the ocr progress operation.
    */
   public static String getOperationFriendlyName(Context context, OcrProgressOperation operation) {
      int operationValue = operation.getValue();
      String friendlyName = "";
      String[] operationsFriendlyNames = context.getResources().getStringArray(R.array.ocr_operations);
      if(operationValue < operationsFriendlyNames.length) {
         friendlyName = operationsFriendlyNames[operationValue];
      }
      return friendlyName;
   }

   /**
    * Gets the language friendly name
    * @param lanuage language name, like "en"
    * @return The locale's of language name
    */
   public static String getLanguageFriendlyName(String lanuage) {
      return new Locale(lanuage).getDisplayName();
   }

   /**
    * Calculate the distance between two points.
    * @param pt1 The first point.
    * @param pt2 The second point.
    * @return The distance between two points
    */
   public static float distanceBetweenPoints(PointF pt1, PointF pt2) {
      return (float) Math.sqrt((pt1.x - pt2.x) * (pt1.x - pt2.x) + (pt1.y - pt2.y) * (pt1.y - pt2.y));
   }
}
