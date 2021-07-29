package customer.gajamove.com.gajamove_customer.auth.camera;

import android.graphics.Rect;
import android.graphics.RectF;

public interface CaptureReadyCallback
{
    void onFaceReadyInCenter(String path);

    void onFaceReadyInCenter(RectF rectF);
    void onFaceReadyInCenter(Rect rectF);
    void onFaceNoReady();
    void onFaceQuickReady();
}
