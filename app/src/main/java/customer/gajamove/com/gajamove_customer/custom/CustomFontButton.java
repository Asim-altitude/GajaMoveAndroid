package customer.gajamove.com.gajamove_customer.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;



import androidx.annotation.Nullable;
import customer.gajamove.com.gajamove_customer.R;

/**
 * Created by PC-GetRanked on 7/11/2018.
 */

public class CustomFontButton extends androidx.appcompat.widget.AppCompatButton {

    public CustomFontButton(Context context) {
        super(context);
        init(null);
    }

    public CustomFontButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomFontButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {

        if (attrs != null) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomEditText);

            String fontName = a.getString(R.styleable.CustomEditText_font_text);



            try {

                if (fontName != null) {

                    Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);

                    setTypeface(myTypeface);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }



            a.recycle();

        }

    }



}
