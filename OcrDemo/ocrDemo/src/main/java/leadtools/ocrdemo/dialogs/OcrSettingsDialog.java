package leadtools.ocrdemo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;

import leadtools.ocrdemo.Helpers;
import leadtools.ocrdemo.R;

/**
 * OCR settings dialog
 * Provides simple settings handling
 */
public class OcrSettingsDialog extends Dialog {
   private Spinner mLanguagesSpinner;
   private ToggleButton mDetectGraphicsButton;
   private ToggleButton mAutoInvertButton;

   // Current supported languages
   private ArrayList<String> mLanguages;

   /**
    * Constructs OcrSettingsDialog
    * @param context The parent context.
    * @param languages The OCR supported languages.
    */
   public OcrSettingsDialog(Context context, ArrayList<String> languages) {
      super(context, R.style.DialogTheme);
      mLanguages = languages;
   }

   // Init dialog views
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.ocr_settings);

      Context context = getContext();
      // Set close button to dismiss the dialog on click
      Button closeButton = (Button)findViewById(R.id.btn_ocr_settings_close);
      closeButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            dismiss();
         }
      });


      // Set languages list
      mLanguagesSpinner = (Spinner) findViewById(R.id.spnr_languages);
      String[] languagesFriendlyNames = new String[mLanguages.size()];
      for(int i = 0; i < mLanguages.size(); i++)
         languagesFriendlyNames[i] = Helpers.getLanguageFriendlyName(mLanguages.get(i));

      ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item, languagesFriendlyNames);
      arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      mLanguagesSpinner.setAdapter(arrayAdapter);

      mDetectGraphicsButton = (ToggleButton) findViewById(R.id.btn_detect_graphics);
      mAutoInvertButton = (ToggleButton) findViewById(R.id.btn_enable_auto_invert);

      setTitle(context.getString(R.string.dlg_ocr_settings));
   }

   /**
    * Sets the current selected language index(position).
    * @param position The index of the current selected language.
    */
   public void setSelectLanguagePosition(int position) {
      mLanguagesSpinner.setSelection(position);
   }

   /**
    * Gets the current selected language
    * @return The current selected language index.
    */
   public int getSelectedLanguagePosition() {
      return mLanguagesSpinner.getSelectedItemPosition();
   }

   /**
    * Enables mobile image OCR settings
    * @param detectGraphics True to enable mobile image processing mode; otherwise, False.
    */
   public void setDetectGraphics(boolean detectGraphics) {
      mDetectGraphicsButton.setChecked(detectGraphics);
   }

   /**
    * Indicates whether mobile image enabled.
    * @return True if the mobile image processing mode enabled; otherwise, False.
    */
   public boolean isDetectGraphicsEnabled() {
      return mDetectGraphicsButton.isChecked();
   }

   /**
    * Enables auto invert images before recognition.
    * @param enableAutoInvert True to enable auto invert images before recognition; otherwise, False.
    */
   public void enableAutoInvert(boolean enableAutoInvert) {
      mAutoInvertButton.setChecked(enableAutoInvert);
   }

   /**
    * Indicates whether auto invert images enabled.
    * @return True if auto invert images enabled; otherwise, False.
    */
   public boolean isAutoInvertEnabled() {
      return mAutoInvertButton.isChecked();
   }
}
