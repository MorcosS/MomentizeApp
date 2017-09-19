package leadtools.ocrdemo;

import java.io.File;
import java.util.ArrayList;

import leadtools.ILeadStream;
import leadtools.LEADResourceDirectory;
import leadtools.LTLibrary;
import leadtools.LeadRect;
import leadtools.LeadStreamFactory;
import leadtools.Platform;
import leadtools.RasterColor;
import leadtools.RasterDefaults;
import leadtools.RasterException;
import leadtools.RasterExceptionCode;
import leadtools.RasterImage;
import leadtools.codecs.CodecsLoadAsyncCompletedEvent;
import leadtools.codecs.CodecsLoadAsyncCompletedListener;
import leadtools.controls.ImageViewerPanZoomInteractiveMode;
import leadtools.controls.ImageViewerSizeMode;
import leadtools.controls.RasterImageViewer;
import leadtools.demos.DeviceUtils;
import leadtools.demos.Messager;
import leadtools.demos.OpenFileDialog;
import leadtools.demos.Progress;
import leadtools.demos.SaveFileDialog;
import leadtools.demos.SplashScreen;
import leadtools.demos.Support;
import leadtools.demos.Utils;
import leadtools.forms.documentwriters.DocumentFontEmbedMode;
import leadtools.forms.documentwriters.DocumentFormat;
import leadtools.forms.documentwriters.DocumentTextMode;
import leadtools.forms.documentwriters.DocumentWriter;
import leadtools.forms.documentwriters.DocxDocumentOptions;
import leadtools.forms.documentwriters.PdfDocumentOptions;
import leadtools.forms.documentwriters.PdfDocumentType;
import leadtools.forms.documentwriters.RtfDocumentOptions;
import leadtools.forms.documentwriters.TextDocumentOptions;
import leadtools.forms.documentwriters.TextDocumentType;
import leadtools.forms.ocr.OcrAutoPreprocessPageCommand;
import leadtools.forms.ocr.OcrDocument;
import leadtools.forms.ocr.OcrEngine;
import leadtools.forms.ocr.OcrEngineManager;
import leadtools.forms.ocr.OcrEngineType;
import leadtools.forms.ocr.OcrImageSharingMode;
import leadtools.forms.ocr.OcrPage;
import leadtools.forms.ocr.OcrProgressData;
import leadtools.forms.ocr.OcrProgressListener;
import leadtools.forms.ocr.OcrProgressStatus;
import leadtools.forms.ocr.OcrSettingManager;
import leadtools.forms.ocr.OcrZone;
import leadtools.imageprocessing.IRasterCommand;
import leadtools.imageprocessing.RotateCommand;
import leadtools.imageprocessing.RotateCommandFlags;
import leadtools.imageprocessing.color.InvertCommand;
import leadtools.ocrdemo.dialogs.DisplayTextResultsDialog;
import leadtools.ocrdemo.dialogs.OcrRecogizeDialog;
import leadtools.ocrdemo.dialogs.OcrSettingsDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

public class OcrDemoActivity extends AppCompatActivity implements CodecsLoadAsyncCompletedListener, OpenFileDialog.OnFileSelectedListener {
   // Requests codes: to use for open images from gallery or files or capturing images
   // Also used to check for Permissions
   private static final int IMAGE_GALLERY         = 0x0001;
   private static final int IMAGE_CAPTURE         = 0x0002;
   private static final int IMAGE_FILE            = 0x0003;
   private static final int COPY_OCR_RUNTIME      = 0x0005;

   // OCR Settings names
   private static final String RECOGNITION_PREPROCESS_MOBILE_IMAGE = "Recognition.Preprocess.MobileImagePreprocess";
   private static final String RECOGNITION_SHARE_ORIGINAL_IMAGE = "Recognition.ShareOriginalImage";
   private static final String RECOGNITION_MODIFY_PROCESSING_IMAGE = "Recognition.ModifyProcessingImage";
   private static final String RECOGNITION_DETECT_COLORS = "Recognition.DetectColors";
   private static final String RECOGNITION_ZONING_OPTIONS = "Recognition.Zoning.Options";

   // Directory for copying the OCR Runtime files to external storage
   private static String OCR_RUNTIME_DIRECTORY;

   // Directory for copying the Shadow Fonts to external storage
   private static String SHADOW_FONTS_DIRECTORY;

   // Directory to be used when capturing images from camera 'IMAGE_CAPTURE'
   private static String CAPTURED_IMAGE_DIRECTORY;

   // LEADTOOLS uses DocumentFormat enumeration with options, this demo
   // creates a simplified enumeration then convert it to DocumentFormat and set the options
   enum OutputFormat {
      PDF                               (0),
      PDF_EMBED                         (1),
      PDFA                              (2),
      PDF_IMAGE_OVER_TEXT               (3),
      PDF_EMBED_IMAGE_OVER_TEXT         (4),
      PDFA_IMAGE_OVER_TEXT              (5),
      DOCX                              (6),
      DOCX_FRAMED                       (7),
      RTF                               (8),
      RTF_FRAMED                        (9),
      TEXT                              (10),
      TEXT_FORMATTED                    (11),
      SVG                               (12),
      ALTO_XML                          (13);

      private static ArrayList<OutputFormat> mMappings;
      @SuppressWarnings("all")
      private static ArrayList<OutputFormat> getMappings() {
         if (mMappings == null) {
            synchronized (OutputFormat.class) {
               if (mMappings == null) {
                  mMappings = new ArrayList<OutputFormat>();
               }
            }
         }
         return mMappings;
      }

      private int mValue;
      OutputFormat(int value) {
         mValue = value;

         getMappings().add(this);
      }
      @SuppressWarnings("unused")
      public int getValue() {
         return mValue;
      }

      public static OutputFormat fromInt(int value) {
         return getMappings().get(value);
      }
   }

   // Path uri for saving captured still images
   private Uri mImageCaptureUri;

   // Color filter to disable\enable button effect
   private ColorMatrixColorFilter mScaleColorFilter;

   // A flag to to enable applying 'OcrAutoPreprocessPageCommand.INVERT' before recognition
   // Can be enabled\disabled from the Demo Settings Dialog.
   private boolean mAutoInvertEnabled;

   private RasterImageViewer mImageViewer;
   private TextView mImageInformationTxtView;
   private TextView mElapsedTimeTxtView;
   private TableRow mDeskewTableRow;
   private TableRow mImageInformationTableRow;
   private TableRow mElapseTimeTableRow;

   private ProgressDialog mProgressDlg;

   // OCR demo settings dialog
   private OcrSettingsDialog mSettingsDialog;

   // OCR select save type and format dialog
   private OcrRecogizeDialog mRecognizeDialog;

   // Ocr engine
   private OcrEngine mOcrEngine;

   // Load LEADTOOLS libs
   private boolean loadLibs() {
      try {
         Platform.setLibPath(Utils.getSharedLibsPath(this));
         Platform.loadLibrary(LTLibrary.LEADTOOLS);
         Platform.loadLibrary(LTLibrary.CODECS);
         Platform.loadLibrary(LTLibrary.IMAGE_PROCESSING_CORE);
         Platform.loadLibrary(LTLibrary.IMAGE_PROCESSING_COLOR);
         Platform.loadLibrary(LTLibrary.IMAGE_PROCESSING_EFFECTS);
         Platform.loadLibrary(LTLibrary.SVG);
         Platform.loadLibrary(LTLibrary.FORMS_DOCUMENT_WRITERS);
         Platform.loadLibrary(LTLibrary.FORMS_OCR);
         return true;
      } catch (Exception ex) {
         // Show an error message and close the demo
         Messager.showErrorLoadingLibsMessage(this, ex.getMessage(), new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               finish();
            }
         });
      }

      return false;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.main);

      CAPTURED_IMAGE_DIRECTORY = DeviceUtils.getExternalStorageDirectory() + "LEADTOOLS_Demos/OCR_DEMO/";
      OCR_RUNTIME_DIRECTORY = CAPTURED_IMAGE_DIRECTORY + "OCRRuntime/";
      SHADOW_FONTS_DIRECTORY = CAPTURED_IMAGE_DIRECTORY + "ShadowFonts/";

      // Load LEADTOOLS libs
      if(!loadLibs()) {
         return;
      }

      // Set License
      Support.setLicense(this);
      if(Support.isKernelExpired()) {
         Messager.showKernelExpiredMessage(this, new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               finish();
            }
         });
         return;
      }

      //Show Splash Screen
      SplashScreen.show(this);

      // Initialize 'mScaleColorFilter' for enable\disable buttons effect
      ColorMatrix cm = new ColorMatrix();
      cm.setScale(.75f, .75f, .75f, .75f);
      mScaleColorFilter = new ColorMatrixColorFilter(cm);

      // Enable auto invert
      mAutoInvertEnabled = true;

      // Initialize the viewer
      mImageViewer = (RasterImageViewer) findViewById(R.id.imageviewer);
      mImageViewer.setTouchInteractiveMode(new ImageViewerPanZoomInteractiveMode());
      mImageViewer.setSizeMode(ImageViewerSizeMode.FIT);

      mImageInformationTxtView = (TextView) findViewById(R.id.txtview_info_image);
      mElapsedTimeTxtView = (TextView) findViewById(R.id.txtview_info_ellapsed_time);
      mDeskewTableRow = (TableRow) findViewById(R.id.tr_manual_deskew);
      mElapseTimeTableRow = (TableRow) findViewById(R.id.tr_elapse_time);
      mImageInformationTableRow = (TableRow) findViewById(R.id.tr_image);

      // Init demo (Codecs, OCR Engine)
      if(DeviceUtils.checkRWStoragePermission(this, COPY_OCR_RUNTIME)) {
         init();
      }

      updateToolbar();
   }

   private void init() {
      // Copy OCR Runtime to storage
      if(!Utils.copyOcrRuntimeFiles(this, OCR_RUNTIME_DIRECTORY)) {
         Messager.showError(this, getString(R.string.err_copying_ocr_runtime), "", new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
               finish();
            }
         });
         return;
      }

      if(!Utils.copyAssetsFiles(this, "shadow_fonts", SHADOW_FONTS_DIRECTORY)) {
         // Show an error message and continue
         Messager.showError(this, getString(R.string.err_copying_shadow_fonts), "");
      }

      try {
         // Set shadow fonts directory
         RasterDefaults.setResourceDirectory(LEADResourceDirectory.FONTS, SHADOW_FONTS_DIRECTORY);

         // Start the OCR engine
         mOcrEngine = OcrEngineManager.createEngine(OcrEngineType.ADVANTAGE);
         mOcrEngine.startup(null, null, null, OCR_RUNTIME_DIRECTORY);
         OcrSettingManager settingsManager = mOcrEngine.getSettingManager();
         settingsManager.setBooleanValue(RECOGNITION_PREPROCESS_MOBILE_IMAGE, true);
         settingsManager.setBooleanValue(RECOGNITION_SHARE_ORIGINAL_IMAGE, true);
         settingsManager.setBooleanValue(RECOGNITION_MODIFY_PROCESSING_IMAGE, true);
         settingsManager.setBooleanValue(RECOGNITION_DETECT_COLORS, false);
         int zoningOptionsValue = settingsManager.getEnumValue(RECOGNITION_ZONING_OPTIONS);
         // 0x02 is value for "Detect Graphics"
         // By ANDing with its complement, we are turning "Detect Graphics" off while keeping all other defaults
         zoningOptionsValue &= ~0x02;
         settingsManager.setEnumValue(RECOGNITION_ZONING_OPTIONS, zoningOptionsValue);

         mOcrEngine.getRasterCodecsInstance().addLoadAsyncCompletedListener(this);

         // Load the default image
         ILeadStream leadStream = LeadStreamFactory.create(this.getResources().openRawResource(R.raw.ocr1_tif), true);
         RasterImage rasterImage = mOcrEngine.getRasterCodecsInstance().load(leadStream, 1);
         setImage(rasterImage);
      } catch(Exception ex) {
         Messager.showError(this, ex.getMessage(), getString(R.string.err_error));
      }
   }

   @Override
   protected void onPause() {
      super.onPause();

      // If the app closing, then free before close
      if(isFinishing()) {
         if (mImageViewer != null)
            mImageViewer.setImage(null);

         if(mOcrEngine != null) {
            mOcrEngine.dispose();
            mOcrEngine = null;
         }
      }
   }

   // Reset the interactive mode to PanZoom
   private void resetInteractiveMode() {
      if(mImageViewer.getTouchInteractiveMode() instanceof ManualDeskewInteractiveMode){
         mDeskewTableRow.setVisibility(View.GONE);
         mElapseTimeTableRow.setVisibility(View.VISIBLE);
         mImageInformationTableRow.setVisibility(View.VISIBLE);
      }

      if(!(mImageViewer.getTouchInteractiveMode() instanceof ImageViewerPanZoomInteractiveMode))
         mImageViewer.setTouchInteractiveMode(new ImageViewerPanZoomInteractiveMode());


   }

   // Set the interative mode to 'ManualDeskewInteractiveMode'
   public void onManual3DDeskew(View v) {
      RasterImage image = mImageViewer.getImage();
      if(image == null) {
         Messager.showError(this, getString(R.string.err_no_image_loaded), null);
         return;
      }
      mDeskewTableRow.setVisibility(View.VISIBLE);
      mElapseTimeTableRow.setVisibility(View.GONE);
      mImageInformationTableRow.setVisibility(View.GONE);
      mImageViewer.setTouchInteractiveMode(new ManualDeskewInteractiveMode());
   }

   public void onManual3DDeskewApply(View v){
      try {
         if (mImageViewer.getTouchInteractiveMode() instanceof ManualDeskewInteractiveMode) {
            ManualDeskewInteractiveMode mode = (ManualDeskewInteractiveMode) mImageViewer.getTouchInteractiveMode();
            mode.applyKeyStoneCommand();
            mode.setAndFitImage();
            resetInteractiveMode();
         }
      } catch (Exception ex) {
         Messager.showError(this, ex.getMessage(), getString(R.string.err_error));
      }
   }

   public void onManual3DDeskewCancel(View v){
      resetInteractiveMode();
   }

   // Handle view effects "zoom In\Out, setting the viewer size mode"
   public void onViewAction(View v) {
      RasterImage image = mImageViewer.getImage();
      if(image == null) {
         Messager.showError(this, getString(R.string.err_no_image_loaded), null);
         return;
      }

      // Reset interactive mode to pan zoom
      resetInteractiveMode();

      int id = v.getId();
      switch (id) {
         // SizeMode
         case R.id.btn_fit:
            mImageViewer.zoom(ImageViewerSizeMode.FIT_ALWAYS, 1, mImageViewer.getDefaultZoomOrigin());
         break;
         case R.id.btn_actual_size:
            mImageViewer.zoom(ImageViewerSizeMode.ACTUAL_SIZE, 1, mImageViewer.getDefaultZoomOrigin());
         break;

         // Zoom In\Out
         case R.id.btn_zoom_in:
         {
            double scaleFactorPer = mImageViewer.getCurrentScaleFactor() * 100;
            if (scaleFactorPer < 1000)
               mImageViewer.zoom(ImageViewerSizeMode.NONE, (mImageViewer.getCurrentScaleFactor() + 0.1f), mImageViewer.getDefaultZoomOrigin());
         }
         break;
         case R.id.btn_zoom_out:
         {
            double scaleFactorPer = mImageViewer.getCurrentScaleFactor() * 100;
            if (scaleFactorPer > 10)
               mImageViewer.zoom(ImageViewerSizeMode.NONE, (mImageViewer.getCurrentScaleFactor() - 0.1f), mImageViewer.getDefaultZoomOrigin());
         }
         break;
      }
   }

   // Handle image actions (Invert, rotate CW and CCW)
   public void onImageAction(View v) {
      resetInteractiveMode();
      RasterImage image = mImageViewer.getImage();
      if(image == null) {
         Messager.showError(this, getString(R.string.err_no_image_loaded), null);
         return;
      }

      try {
         int id = v.getId();
         IRasterCommand command;
         switch (id) {
            case R.id.btn_invert:
               command = new InvertCommand();
               break;
            case R.id.btn_rotate_cw:
               // Rotate by 90 degrees CW
               command = new RotateCommand(9000, RotateCommandFlags.RESIZE.getValue(), new RasterColor(0, 0, 0));
               break;
            case R.id.btn_rotate_ccw:
               // Rotate by 90 degrees CCW
               command = new RotateCommand(-9000, RotateCommandFlags.RESIZE.getValue(), new RasterColor(0, 0, 0));
               break;
            default:
               return;
         }

         command.run(image);
      } catch(Exception ex) {
         Messager.showError(this, ex.getMessage(), "");
      }
   }

   // Image sources (gallery, file and capture still images)
   private void onSelectImageSrc(int code) {
      switch (code) {
         case IMAGE_GALLERY:
            // Pick an image from gallery
            DeviceUtils.pickImageFromGallery(this, code);
            break;
         case IMAGE_CAPTURE:
            mImageCaptureUri = DeviceUtils.captureImage(this, IMAGE_CAPTURE, CAPTURED_IMAGE_DIRECTORY);
            break;
         case IMAGE_FILE:
            // Select an image file
            DeviceUtils.pickFile(this, code, null, this);
            break;
      }
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      boolean allGranted = true;
      for(int result: grantResults) {
         if(result == PackageManager.PERMISSION_DENIED) {
            allGranted = false;
            break;
         }
      }

      switch (requestCode) {
         case COPY_OCR_RUNTIME:
            if(!allGranted) {
               // Cannot copy or read the OCR Runtime to the storage; show an error message and close the demo
               Messager.showError(this, getString(R.string.err_ocr_runtime_copy), getString(R.string.err_error), new OnDismissListener() {
                  @Override
                  public void onDismiss(DialogInterface dialogInterface) {
                     finish();
                  }
               });
            } else {
               init();
            }
            break;
         default:
            // Check if permission granted
            if(allGranted)
               onSelectImageSrc(requestCode);
            break;
      }

      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
   }

   public void onSelectImage(View v) {
      // Select image from file, gallery or capture image
      switch(v.getId()) {
         case R.id.btn_image_gallery:
            onSelectImageSrc(IMAGE_GALLERY);
            break;
         case R.id.btn_image_capture:
            onSelectImageSrc(IMAGE_CAPTURE);
            break;
         case R.id.btn_image_browse:
            onSelectImageSrc(IMAGE_FILE);
            break;
         default:
            break;
      }
   }

   // Handle onSelectImageSrc 'IMAGE_FILE' request
   @Override
   public void onFileSelected(String fileName) {
      File file = new File(fileName);
      if(file.exists())
         loadImage(fileName);
   }

   // Codecs async load event listener
   @Override
   public void onLoadAsyncCompleted(CodecsLoadAsyncCompletedEvent event) {
      Progress.close(mProgressDlg);
      RasterImage image = event.getImage();
      try {
         // Check if cancelled or an error occurred
         if (event.getCancelled()) {
            throw new RasterException(RasterExceptionCode.USER_ABORT);
         }

         RuntimeException error = event.getError();
         if (error != null) {
            throw error;
         } else {
            setImage(event.getImage());
         }
      } catch (Exception ex) {
         // Free the image
         if (image != null)
            image.dispose();

         Messager.showError(this, ex.getMessage(), getString(R.string.err_loading_file));
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      // Activity result for Capturing images or selecting images from gallery
      super.onActivityResult(requestCode, resultCode, data);
      if (resultCode == RESULT_OK) {
         String imageFileName = null;
         switch (requestCode) {
         case IMAGE_GALLERY:
            imageFileName = Utils.getGalleryPathName(this.getContentResolver(), data.getData());
            break;

         case IMAGE_CAPTURE:
            String uriPath = mImageCaptureUri.getPath();
            if(uriPath != null) {
               File file = new File(uriPath);
               if (file.exists())
                  imageFileName = uriPath;
            }
            break;

         default:
            break;
         }

         if(imageFileName != null)
            loadImage(imageFileName);
      }
   }

   // Load the image async
   private void loadImage(String imageFileName) {
      try {
         mProgressDlg = Progress.show(this, getString(R.string.msg_loading_image), getString(R.string.msg_loading));
         ILeadStream stream = LeadStreamFactory.create(imageFileName);

         // Load image using the RasterCodecs instance in the OCR engine
         mOcrEngine.getRasterCodecsInstance().loadAsync(stream, null);
      } catch (Exception ex) {
         Progress.close(mProgressDlg);
         Messager.showError(OcrDemoActivity.this, ex.getMessage(), getString(R.string.err_loading_file));
      }
   }

   /**
    * Set the image in the viewer and reset the interactive mode.
    * @param image Image to set in the viewer
    */
   private void setImage(RasterImage image) {
      try {
         mImageViewer.setImage(image);
      } catch(Exception ex) {
         Messager.showError(this, ex.getMessage(), "");
      } finally {
         updateImageInfo();
         resetInteractiveMode();
         updateToolbar();
      }
   }

   /**
    * Update image info views (size and reset recognition time if no image)
    */
   private void updateImageInfo() {
      RasterImage image = mImageViewer.getImage();
      if (image != null) {
         mImageInformationTxtView.setText(String.format("(%1$s x %2$s)", image.getWidth(), image.getHeight()));
      }
      else {
         mImageInformationTxtView.setText(getString(R.string.err_no_image_loaded));
         mElapsedTimeTxtView.setText(getString(R.string.info_zero_time));
      }
   }

   // Starts\Stops 'SelectAreaInteractivdeMode'
   public void onSelectArea(View v) {
      RasterImage image = mImageViewer.getImage();
      if(image == null) {
         Messager.showError(this, getString(R.string.err_no_image_loaded), null);
         return;
      }

      if(mImageViewer.getTouchInteractiveMode() instanceof SelectAreaInteractiveMode) {
         // Stop selecting area interactive mode and reset the Viewer interactive mode to Pan Zoom
         resetInteractiveMode();
      } else {
         // Set the viewer interactive mode to the 'SelectAreaInteractiveMode' custom demo interactive mode;
         // To select a single OCR zone for recognition
         mImageViewer.setTouchInteractiveMode(new SelectAreaInteractiveMode());
      }
   }

   // Display OCR demo settings dialog
   public void onOCRSettings(View v) {
      final ArrayList<String> supportedLanguages = mOcrEngine.getLanguageManager().getSupportedLanguages();
      if(mSettingsDialog == null) {
         mSettingsDialog = new OcrSettingsDialog(this, supportedLanguages);
         mSettingsDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
               boolean detectGraphics = mSettingsDialog.isDetectGraphicsEnabled();
               mOcrEngine.getSettingManager().setBooleanValue(RECOGNITION_DETECT_COLORS, detectGraphics);
               int zoningOptionsValue = mOcrEngine.getSettingManager().getEnumValue(RECOGNITION_ZONING_OPTIONS);
               if (detectGraphics)
                  zoningOptionsValue |= 0x02;
               else
                  zoningOptionsValue &= ~0x02;
               mOcrEngine.getSettingManager().setEnumValue(RECOGNITION_ZONING_OPTIONS, zoningOptionsValue);
               mAutoInvertEnabled = mSettingsDialog.isAutoInvertEnabled();
               mOcrEngine.getLanguageManager().enableLanguages(new String[] {supportedLanguages.get(mSettingsDialog.getSelectedLanguagePosition())});
            }
         });
      }

      mSettingsDialog.show();
      mSettingsDialog.setSelectLanguagePosition(supportedLanguages.indexOf(mOcrEngine.getLanguageManager().getEnabledLanguages()[0]));
      mSettingsDialog.setDetectGraphics(mOcrEngine.getSettingManager().getBooleanValue(RECOGNITION_DETECT_COLORS));
      mSettingsDialog.enableAutoInvert(mAutoInvertEnabled);
   }

   // Shows recognition options dialog, and recognize the current page
   public void onRecognizePage(View v) {
      // Check if image loaded
      RasterImage image = mImageViewer.getImage();
      if(image == null) {
         Messager.showError(this, getString(R.string.err_no_image_loaded), null);
         return;
      }

      // Check if a zone selected (If the current interactive mode is 'SelectAreaInteractiveMode')
      LeadRect rc = null;
      if(mImageViewer.getTouchInteractiveMode() instanceof SelectAreaInteractiveMode) {
         rc = ((SelectAreaInteractiveMode)mImageViewer.getTouchInteractiveMode()).getSelectedImageRectangle();
      }

      // reset the interactive mode to Pan Zoom
      resetInteractiveMode();

      doRecognize(image, rc);
   }

   private void doRecognize(RasterImage image, LeadRect selectedZone) {
      // No need to check for Storage write permission (Needed for saving document to disk mode) because as in the demo implementation this is should be granted
      // When copying the OCR Runtime files to storage on starting the demo.

      if(mRecognizeDialog == null) {
         mRecognizeDialog = new OcrRecogizeDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(mRecognizeDialog.getRecognitionResultsType() == OcrRecogizeDialog.RECOGNITION_RESULTS_TYPE_SAVE_TO_DOCUMENT) {
                  SaveFileDialog.OnFileSelectedListener onFileSelectedListener = new SaveFileDialog.OnFileSelectedListener() {
                     @Override
                     public void onFileSelected(String fileName) {
                        OCRRecognitionTaskParams params = (OCRRecognitionTaskParams)mRecognizeDialog.userData;
                        params.format = OutputFormat.fromInt(mRecognizeDialog.getSaveFormatIndex());
                        params.outputFileName = fileName;
                        params.image = params.image.clone();
                        new OCRRecognitionTask().execute(params);
                     }
                  };

                  SaveFileDialog saveDlg = new SaveFileDialog(OcrDemoActivity.this, null, onFileSelectedListener);
                  saveDlg.show();
               } else {
                  OCRRecognitionTaskParams params = (OCRRecognitionTaskParams)mRecognizeDialog.userData;
                  params.image = params.image.clone();

                  new OCRRecognitionTask().execute(params);
               }
            }
         });
      }

      OCRRecognitionTaskParams params = new OCRRecognitionTaskParams();
      params.image = image;
      params.zoneBounds = selectedZone;

      // Set the parameter in the user data
      mRecognizeDialog.userData = params;
      mRecognizeDialog.show();
   }

   // Just create the OCR text result dialog and set the text
   private void displayTextResults(String results) {
      new DisplayTextResultsDialog(this, results).show();
   }

   /**
    * Converts the simple Demo OutputFormat to DocumentFormat and update the documentWriter handle options
    * @param documentWriter DocumentWriter instance to update.
    * @param outputFormat Demo output format.
    * @return DocumentFormat.
    */
   private static DocumentFormat setOutputDocumentFormatOptions(DocumentWriter documentWriter, OutputFormat outputFormat) {
      DocumentFormat documentFormat;

      switch (outputFormat) {
         // PDF format
         case PDF:
         case PDF_EMBED:
         case PDFA:
         case PDF_IMAGE_OVER_TEXT:
         case PDF_EMBED_IMAGE_OVER_TEXT:
         case PDFA_IMAGE_OVER_TEXT:
            // PDF, set extra options
            documentFormat = DocumentFormat.PDF;
            PdfDocumentOptions pdfOptions = (PdfDocumentOptions)documentWriter.getOptions(documentFormat);

            // Set PDF Type
            if(outputFormat == OutputFormat.PDFA || outputFormat == OutputFormat.PDFA_IMAGE_OVER_TEXT) {
               pdfOptions.setDocumentType(PdfDocumentType.PDFA);
            }
            else {
               pdfOptions.setDocumentType(PdfDocumentType.PDF);
            }

            // Set FontEmbed Value
            pdfOptions.setFontEmbedMode(DocumentFontEmbedMode.AUTO);
            if (outputFormat == OutputFormat.PDF_EMBED || outputFormat == OutputFormat.PDF_EMBED_IMAGE_OVER_TEXT) {
               pdfOptions.setFontEmbedMode(DocumentFontEmbedMode.ALL);
            }

            // Set ImageOverText Value
            pdfOptions.setImageOverText(outputFormat == OutputFormat.PDF_IMAGE_OVER_TEXT || outputFormat == OutputFormat.PDF_EMBED_IMAGE_OVER_TEXT || outputFormat == OutputFormat.PDFA_IMAGE_OVER_TEXT);
            break;

         // DOCX format
         case DOCX:
         case DOCX_FRAMED:
            // DOCX, set extra options
            documentFormat = DocumentFormat.DOCX;
            DocxDocumentOptions docxOptions = (DocxDocumentOptions)documentWriter.getOptions(documentFormat);
            docxOptions.setTextMode(outputFormat == OutputFormat.DOCX_FRAMED ? DocumentTextMode.FRAMED : DocumentTextMode.NON_FRAMED);
            break;

         // RTF format
         case RTF:
         case RTF_FRAMED:
            // RTF, set extra options
            documentFormat = DocumentFormat.RTF;
            RtfDocumentOptions rtfOptions = (RtfDocumentOptions)documentWriter.getOptions(documentFormat);
            rtfOptions.setTextMode(outputFormat == OutputFormat.RTF_FRAMED ? DocumentTextMode.FRAMED : DocumentTextMode.NON_FRAMED);
            break;

         // Text format
         case TEXT:
         case TEXT_FORMATTED:
            // TXT, set extra options
            documentFormat = DocumentFormat.TEXT;
            TextDocumentOptions textOptions = (TextDocumentOptions)documentWriter.getOptions(documentFormat);
            textOptions.setDocumentType(TextDocumentType.UTF8);
            textOptions.setFormatted(outputFormat == OutputFormat.TEXT_FORMATTED);
            break;

         // SVG format
         case SVG:
            // SVG, no extra options
            documentFormat = DocumentFormat.SVG;
            break;

         // ALTO_XML format
         case ALTO_XML:
            // ALTO XML, no extra options
            documentFormat = DocumentFormat.ALTO_XML;
            break;

         default:
            // Something went wrong
            throw new IllegalStateException("Invalid format " + outputFormat);
      }

      return documentFormat;
   }

   // Enable\Disable toolbar image button
   private void enableToolbarImageButton(int resId, boolean enable) {
      ImageButton button = (ImageButton)findViewById(resId);
      if(button != null) {
         button.setEnabled(enable);
         // 'mScaleColorFilter' to enable\disable buttons effect
         if (enable)
            button.setColorFilter(null);
         else
            button.setColorFilter(mScaleColorFilter);
      }
   }

   // Enable\Disable toolbar button
   private void enableToolbarButton(int resId, boolean enabled) {
      Button button = (Button)findViewById(resId);
      if(button == null)
         return;

      button.setEnabled(enabled);
      Drawable[] drawbleArray = button.getCompoundDrawables();
      if(drawbleArray.length == 1)
         return;

      Drawable topDrawable = drawbleArray[1];
      // 'mScaleColorFilter' to enable\disable buttons effect
      if(enabled)
         topDrawable.setColorFilter(null);
      else
         topDrawable.setColorFilter(mScaleColorFilter);
   }

   // Disable\Enable toolbar buttons (By checking if an image loaded in the viewer)
   private void updateToolbar() {
      boolean hasImage = mImageViewer.hasImage();
      // Interactive modes
      enableToolbarImageButton(R.id.btn_pan_zoom, hasImage);
      enableToolbarImageButton(R.id.btn_deskew_3d, hasImage);

      // Size mode
      enableToolbarImageButton(R.id.btn_actual_size, hasImage);
      enableToolbarImageButton(R.id.btn_fit, hasImage);

      // View effects
      enableToolbarImageButton(R.id.btn_zoom_in, hasImage);
      enableToolbarImageButton(R.id.btn_zoom_out, hasImage);
      enableToolbarImageButton(R.id.btn_invert, hasImage);
      enableToolbarImageButton(R.id.btn_rotate_cw, hasImage);
      enableToolbarImageButton(R.id.btn_rotate_ccw, hasImage);

      // Recognize page
      enableToolbarButton(R.id.btn_recognize, hasImage);
      // Select zone (Custom interactive mode to select a zone in images)
      enableToolbarButton(R.id.btn_select_zone, hasImage);
   }

   /**
    * Data holder for passing parameters to 'OCRRecognitionTask'
    */
   private class OCRRecognitionTaskParams {
      public RasterImage image;      // Image to recognize
      public LeadRect zoneBounds;    // Optional zone bounds; In the current demo implementation, the zone bounds be selected using 'SelectAreaInteractiveMode'
      public String outputFileName;  // Optional output file name.
      public OutputFormat format;    // Optional format.
   }

   /**
    * Data holder for 'OCRRecognitionTask' results
    */
   private class OCRRecognitionTaskResult {
      String errMessage;              // Holds error message if occurred while recognition

      DocumentFormat outputFormat;   // Output document format

      String outputFileName;         /* Output file name (Same value of 'OCRRecognitionTaskParams.outputFileName'), this is used if OCRRecognitionTaskParams.outputFileName and
                                        OCRRecognitionTaskParams.outputFileName format not null; otherwise the result will be show as text (Display text result) */

      String txt;                    // Output text result (Just display a simple dialog with the ecognized text result).
   }

   /**
    * OCR recognition async task
    */
   private class OCRRecognitionTask extends AsyncTask<OCRRecognitionTaskParams, Void, OCRRecognitionTaskResult> implements OcrProgressListener, DialogInterface.OnClickListener  {
      private double mStartTime;
      private boolean mAbort;

      protected void onPreExecute () {
         // Show progress
         mProgressDlg = Progress.create(OcrDemoActivity.this, "", "", true);
         mProgressDlg.setProgress(0);
         mProgressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), this);
         mProgressDlg.show();
         mAbort = false;
      }

      protected OCRRecognitionTaskResult doInBackground(OCRRecognitionTaskParams... paramArray) {
         OCRRecognitionTaskResult result = new OCRRecognitionTaskResult();
         OCRRecognitionTaskParams params = paramArray[0];
         boolean useDocument = params.format != null && params.outputFileName != null;
         RasterImage image = null;
         OcrDocument ocrDocument = null;
         OcrPage ocrPage = null;
         try {
            mStartTime = System.currentTimeMillis();

            image = params.image;

            if(useDocument) {
               // Create OcrDocument and add the image into it.
               ocrDocument = mOcrEngine.getDocumentManager().createDocument();
               if (mAbort)
                  return result;
            }

            // Create an OcrPage from image, the image not needed anymore (using 'AUTO_DISPOSE' so the page will take care of it)
            ocrPage = mOcrEngine.createPage(image, OcrImageSharingMode.AUTO_DISPOSE);
            if(mAbort)
               return result;

            // Since AUTO_DISPOSE used, The RasterImage not needed anymore
            image = null;

            // Check if a zone selected (The zone selection in this demo done by the 'SelectAreaInteractiveMode').
            LeadRect rc = params.zoneBounds;
            if(rc != null) {
               // Create a new OcrZone
               OcrZone ocrZone = new OcrZone();
               // Set ocrZone's bound
               ocrZone.setBounds(rc);
               // Add ocrZone to the ocrPage
               ocrPage.getZones().add(ocrZone);
            }

            // Check if auto invert is enabled
            if(mAutoInvertEnabled) {
               ocrPage.autoPreprocess(OcrAutoPreprocessPageCommand.INVERT, this);
               if(mAbort)
                  return result;
            }

            ocrPage.autoPreprocess(OcrAutoPreprocessPageCommand.DESKEW, this);
            if(mAbort)
               return result;

            // Recognize the page
            ocrPage.recognize(this);

            if(useDocument) {
               // Set the output document format options and get the corresponding LEADTOOLS DocumentFormat enumeration member
               DocumentFormat documentFormat = setOutputDocumentFormatOptions(mOcrEngine.getDocumentWriterInstance(), params.format);

               // Add the ocrPage to the document
               ocrDocument.getPages().add(ocrPage);
               // Check the output file extension
               String outputFile = params.outputFileName;
               String extension = DocumentWriter.getFormatFileExtension(documentFormat);
               if(!outputFile.endsWith(extension))
                  outputFile += String.format(".%s", extension);

               // Save to the selected document format
               ocrDocument.save(outputFile, documentFormat, this);

               result.outputFormat = documentFormat;
               result.outputFileName = outputFile;
            } else {
               result.txt = ocrPage.getText(-1);
            }
            return result;
         } catch(Exception ex) {
            result.errMessage = ex.getMessage();
            return result;
         }
         finally {
            // Clean up by disposing of the objects we created
            // The page not needed anymore. The recognition data has been saved into the document, so delete it
            if(ocrPage != null)
               ocrPage.dispose();

            // In case something went wrong, dispose the image
            if (image != null)
               image.dispose();

            if (ocrDocument != null)
               ocrDocument.dispose();
         }
      }

      protected void onPostExecute(OCRRecognitionTaskResult result) {
         Progress.close(mProgressDlg);
         if(mAbort) {
            return;
         }

         double elapsedTime = (System.currentTimeMillis() - mStartTime) / 1000.0;
         mElapsedTimeTxtView.setText(String.format("%s (s)", elapsedTime));
         if (result.errMessage != null) {
            Messager.showError(OcrDemoActivity.this, result.errMessage, getString(R.string.err_error));
         }
         else if (result.txt != null) {
            // Display text results
            displayTextResults(result.txt);
         } else {
            // Saved as document
            Messager.showMessage(OcrDemoActivity.this, OcrDemoActivity.this.getString(R.string.ocr_results_saved_to, result.outputFileName), null, null);
            // Try to open the file
            try {
               // Get Mime type
               String extension = DocumentWriter.getFormatFileExtension(result.outputFormat);
               String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
               if(mimeType != null) {
                  // Start an ACTION_VIEW intent
                  File file = new File(result.outputFileName);
                  Intent intent = new Intent();
                  intent.setAction(android.content.Intent.ACTION_VIEW);
                  intent.setDataAndType(Uri.fromFile(file), mimeType);
                  startActivity(intent);
               }
            } catch (Exception ex) {
               // If failed (No default app); do nothing
               Log.e(getString(R.string.app_name), ex.getMessage());
            }
         }
      }

      @Override
      public void onProgress(OcrProgressData data) {
         // Check if the user cancelled, then 'Abort'
         data.setStatus(mAbort ? OcrProgressStatus.ABORT : OcrProgressStatus.CONTINUE);
         if(mAbort)
            return;

         final String message = Helpers.getOperationFriendlyName(OcrDemoActivity.this, data.getOperation());
         final int progress = data.getPercentage();
         // Update the progress status in the UI thread
         runOnUiThread(new Runnable() {
            @Override
            public void run() {
               mProgressDlg.setProgress(progress);
               mProgressDlg.setMessage(message);
            }
         });
      }

      // Abort recognition
      @Override
      public void onClick(DialogInterface dialog, int which) {
         mAbort = true;
         Progress.close(mProgressDlg);

         mProgressDlg = Progress.show(OcrDemoActivity.this, getString(R.string.ocr_abort), "");
      }
   }
}