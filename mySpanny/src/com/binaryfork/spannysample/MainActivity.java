package com.binaryfork.spannysample;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.style.*;
import android.widget.TextView;
import com.binaryfork.spanny.Spanny;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView);
        Spanny spanny = new Spanny("StyleSpan", new StyleSpan(Typeface.BOLD_ITALIC))
                .append("CustomAlignmentSpan", new CustomAlignmentSpan(CustomAlignmentSpan.RIGHT_TOP))
                .append("\n\nUnderlineSpan, ", new UnderlineSpan())
                .append(" TypefaceSpan, ", new TypefaceSpan("serif"))
                .append("URLSpan, ", new URLSpan("www.baidu.com"))
                .append("StrikethroughSpan", new StrikethroughSpan())
                .append("\nQuoteSpan", new QuoteSpan(Color.RED))
                .appendText("\nPlain text")
                .append("SubscriptSpan", new SubscriptSpan())
                .append("SuperscriptSpan", new SuperscriptSpan())
                .append("\n\nBackgroundSpan", new BackgroundColorSpan(Color.LTGRAY))
                .append("\n\nCustomBackgroundSpan", new CustomBackgroundSpan(Color.DKGRAY, dp(16)))
                .append("\n\nForegroundColorSpan", new ForegroundColorSpan(Color.LTGRAY))
                .append("\nAlignmentSpan", new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER))
                .append("\nTextAppearanceSpan\n", new TextAppearanceSpan(this, android.R.style.TextAppearance_Medium))
                .append("ImageSpan", new ImageSpan(getApplicationContext(), R.drawable.ic_launcher))
                .append("\nRelativeSizeSpan", new RelativeSizeSpan(1.5f))
                .append("\n\nMultiple spans", new StyleSpan(Typeface.ITALIC), new UnderlineSpan(),
                        new TextAppearanceSpan(this, android.R.style.TextAppearance_Large),
                        new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), new BackgroundColorSpan(Color.LTGRAY));
        textView.setText(spanny);

        spanny = new Spanny("\n\nFind and span the word. All appearances of the word will be spanned.");
        spanny.findAndSpan("word", new Spanny.GetSpan() {
            @Override public Object getSpan() {
                return new UnderlineSpan();
            }
        });
        textView.append(spanny);
    }

    private int dp(int value) {
        return (int) Math.ceil(getResources().getDisplayMetrics().density * value);
    }
}
