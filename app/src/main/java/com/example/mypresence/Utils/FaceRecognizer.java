package com.example.mypresence.Utils;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceRec;

public class FaceRecognizer {
    private static FaceRec mFaceRec;

    public static FaceRec getInstance()
    {
        if (mFaceRec == null){
            mFaceRec = new FaceRec(Constants.getDLibDirectoryPath());
            return mFaceRec;
        }
        return mFaceRec;
    }

    public void releaseInstance(){
        mFaceRec.release();
    }
}
