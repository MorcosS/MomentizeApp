package leadtools.ocrdemo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

import leadtools.ocrdemo.R;

/**
 * A dialog to display OCR text results.
 * This dialog used to show OCR text results when OcrReognizeDialog 'RECOGNITION_RESULTS_TYPE_SHOW_TEXT' is selected
 * @see leadtools.ocrdemo.dialogs.OcrSettingsDialog
 */
public class DisplayTextResultsDialog extends Dialog {
   // Text to diaply
   private String mText;

   /**
    * Construct DisplayTextResultsDialog
    * @param context The parent context.
    * @param text Text to display.
    */
   public DisplayTextResultsDialog(Context context, String text) {
      super(context, R.style.DialogTheme);
      mText = text;

      setTitle(context.getString(R.string.ocr_results));
   }

   // Init dialog views
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.ocr_txt_results);

      // Set the results text
      EditText resultsEditText = (EditText)findViewById(R.id.edittext_ocr_results);
      resultsEditText.setText(mText);

      getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
   }
}
