package tn.bfpme.test;

import org.opencv.core.Core;
import org.opencv.face.EigenFaceRecognizer;

public class OpenCVTest {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        try {
            EigenFaceRecognizer recognizer = EigenFaceRecognizer.create();
            System.out.println("EigenFaceRecognizer created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




