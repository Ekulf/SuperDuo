package it.jaschke.alexandria;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class CameraSelectorDialogFragment extends DialogFragment {
    private static final String ARG_CAMERA_ID = "CameraSelectorDialogFragment.CAMERA_ID";

    public interface CameraSelectorDialogListener {
        void onCameraSelected(int cameraId);
    }

    private int mCameraId;
    private CameraSelectorDialogListener mListener;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);
    }

    public static CameraSelectorDialogFragment newInstance(int cameraId) {
        CameraSelectorDialogFragment fragment = new CameraSelectorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CAMERA_ID, cameraId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCameraId = getArguments().getInt(ARG_CAMERA_ID);

        int numberOfCameras = Camera.getNumberOfCameras();
        String[] cameraNames = new String[numberOfCameras];
        int checkedIndex = 0;

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraNames[i] = "Front Facing";
            } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraNames[i] = "Rear Facing";
            } else {
                cameraNames[i] = "Camera ID: " + i;
            }

            if (i == mCameraId) {
                checkedIndex = i;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.select_camera)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(cameraNames, checkedIndex,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCameraId = which;
                            }
                        })
                        // Set the action buttons
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedIndices results somewhere
                        // or return them to the component that opened the dialog
                        if (mListener != null) {
                            mListener.onCameraSelected(mCameraId);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CameraSelectorDialogListener) {
            mListener = (CameraSelectorDialogListener)activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}