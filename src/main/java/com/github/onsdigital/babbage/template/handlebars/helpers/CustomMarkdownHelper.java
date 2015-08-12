package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.MarkdownHelper;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;

import java.io.IOException;

/**
 * Created by bren on 28/07/15.
 *
 * Extending functionality of Handlebars java markdown helper
 */
public class CustomMarkdownHelper extends MarkdownHelper  implements BabbageHandlebarsHelper<Object>{

    private final static String SUBSCRIPT_PATTERN = "~(?=\\S)(\\S*)~";
    private final static String SUPER_SCRIPT_PATTERN = "\\^(?=\\S)(\\S*)\\^";
    private final String HELPER_NAME = "md";

    @Override
    public CharSequence apply(Object context, Options options) throws IOException {
        if(options.isFalsy(context)) {
            return "";
        }
        String markdown = context.toString();
        markdown = markdown.replaceAll(SUBSCRIPT_PATTERN, "<sub>$1</sub>");
        markdown = markdown.replaceAll(SUPER_SCRIPT_PATTERN, "<sup>$1</sup>");
        return super.apply(markdown, options);
    }

    @Override
    public String getHelperName() {
        return HELPER_NAME;
    }
}