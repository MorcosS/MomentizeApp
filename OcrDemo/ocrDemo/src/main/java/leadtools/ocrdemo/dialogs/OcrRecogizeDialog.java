package leadtools.ocrdemo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import leadtools.ocrdemo.R;

/**
 * A dialog to select OCR recognition options.
 */
public class OcrRecogizeDialog extends Dialog {
   // Values for results type.
   // The results can be saved to storage as document or just display the recognition text in a DisplayTextResultsDialog, depending on the selected value of mResultsTypeSpinner
   public static final int RECOGNITION_RESULTS_TYPE_SAVE_TO_DOCUMENT = 0;
   public static final int RECOGNITION_RESULTS_TYPE_SHOW_TEXT        = 1;

   private Spinner mResultsTypeSpinner;
   private Spinner mSaveFormatSpinner;

   // On OK listener to handle dialog OK button click
   private View.OnClickListener mOnOKClickListener;

   /**
    * User Data
    */
   public Object userData;

   /**
    * Constructor
    * @param context The parent context.
    * @param onOKclickListener On OK listener to handle OK button click.
    */
   public OcrRecogizeDialog(Context context, View.OnClickListener onOKclickListener) {
      super(context, R.style.DialogTheme);
      mOnOKClickListener = onOKclickListener;
   }

   // Init dialog views
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.ocr_recognize);

      // Set OK button
      Button okButton = (Button) findViewById(R.id.btn_recognize_ok);
      okButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mOnOKClickListener.onClick(view);
            dismiss();
         }
      });

      // Set Cancel button
      Button cancelButton = (Button) findViewById(R.id.btn_recognize_cancel);
      cancelButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            dismiss();
         }
      });

      mResultsTypeSpinner = (Spinner)findViewById(R.id.spnr_recognition_result_type);
      mSaveFormatSpinner = (Spinner)findViewById(R.id.spnr_recognition_formats);

      mResultsTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            // if the position is 0 'RECOGNITION_RESULTS_TYPE_SAVE_TO_DOCUMENT', then enable mSaveFormatSpinner, otherwise disable it
            mSaveFormatSpinner.setEnabled(position == RECOGNITION_RESULTS_TYPE_SAVE_TO_DOCUMENT);
         }

         @Override
         public void onNothingSelected(AdapterView<?> adapterView) {

         }
      });

      setTitle(getContext().getString(R.string.ocr_recognition_options));
   }

   /**
    * Gets the selected recognition type
    * @return RECOGNITION_RESULTS_TYPE_SAVE_TO_DOCUMENT or RECOGNITION_RESULTS_TYPE_SHOW_TEXT
    */
   public int getRecognitionResultsType() {
      return mResultsTypeSpinner.getSelectedItemPosition();
   }

   /**
    * Gets the selected demo OutputFormat index.
    * @return The demo output format index.
    */
   public int getSaveFormatIndex() {
      return mSaveFormatSpinner.getSelectedItemPosition();
   }
}
