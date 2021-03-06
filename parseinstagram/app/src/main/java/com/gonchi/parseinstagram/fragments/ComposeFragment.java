package com.gonchi.parseinstagram.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.gonchi.parseinstagram.LoginActivity;
import com.gonchi.parseinstagram.MainActivity;
import com.gonchi.parseinstagram.Post;
import com.gonchi.parseinstagram.R;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class ComposeFragment extends Fragment {
  public static final String TAG = "ComposeFragment";

  private ConstraintLayout viewContainer;

  private Button btnLogout;
  private EditText etDescription;
  private Button btnCaptureImage;
  private ImageView ivPostImage;
  private Button btnSubmit;

  public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
  public String photoFileName = "photo.jpg";
  File photoFile;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_compose, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    etDescription = view.findViewById(R.id.etDescription);
    btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
    ivPostImage = view.findViewById(R.id.ivPostImage);
    btnSubmit = view.findViewById(R.id.btnSubmit);
    viewContainer = view.findViewById(R.id.mainContainer);
    btnLogout = view.findViewById(R.id.btnLogout);
    btnCaptureImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        launchCamera();
      }
    });


    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String description = etDescription.getText().toString();
        ParseUser user = ParseUser.getCurrentUser();
        savePost(description, user, photoFile);
      }
    });

    btnLogout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ParseUser.logOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
      }
    });
  }

  private void launchCamera() {
    // create Intent to take a picture and return control to the calling application
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    // Create a File reference to access to future access
    photoFile = getPhotoFileUri(photoFileName);

    // wrap File object into a content provider
    // required for API >= 24
    // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
    Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.gonchi.fileprovider", photoFile);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

    // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
    // So as long as the result is not null, it's safe to use the intent.
    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
      // Start the image capture intent to take photo
      startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
  }

  // Returns the File for a photo stored on disk given the fileName
  public File getPhotoFileUri(String fileName) {
    // Get safe storage directory for photos
    // Use `getExternalFilesDir` on Context to access package-specific directories.
    // This way, we don't need to request external read/write runtime permissions.
    File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
      Log.d(TAG, "failed to create directory");
    }

    // Return the file target for the photo based on filename
    return new File(mediaStorageDir.getPath() + File.separator + fileName);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == Activity.RESULT_OK) {
        // by this point we have the camera photo on disk
        Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        // RESIZE BITMAP, see section below
        // Load the taken image into a preview
        ivPostImage.setImageBitmap(takenImage);
      } else { // Result was a failure
        Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void savePost(String description, ParseUser user, File photoFile) {
    Post post = new Post();
    post.setDescription(description);
    post.setUser(user);
    if (photoFile == null || ivPostImage.getDrawable() == null) {
      Log.e(TAG, "No photo to submit");
      Toast.makeText(getContext(), "Oosp! There is no photo", Toast.LENGTH_SHORT).show();
      return;
    }
    post.setImage(new ParseFile(photoFile));
    post.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e != null) {
          Log.e(TAG, "Error while saving");
          e.printStackTrace();
          Toast.makeText(getContext(), "Oosp! There was an error while saving", Toast.LENGTH_SHORT).show();

//          Snackbar.make(viewContainer, "Oosp! There was an error while saving", Snackbar.LENGTH_LONG).show();
          return;
        }
        Toast.makeText(getContext(), "Posted added!", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Succeeded");
        etDescription.setText("");
        ivPostImage.setImageResource(0);
      }
    });
  }




}
