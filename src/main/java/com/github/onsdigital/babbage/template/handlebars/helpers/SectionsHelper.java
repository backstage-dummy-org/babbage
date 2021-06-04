package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.markdown.CustomMarkdownHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by angela on 04/06/21.
 */
public enum SectionsHelper implements BabbageHandlebarsHelper<Object> {

    totalWordCount {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            CustomMarkdownHelper mdHelper = new CustomMarkdownHelper();
            Integer runningWordCount = 0;
            if (context != null) {
                for (Object section : (List<Object>) context) {
                    Map<String, String> sectionText = (Map<String, String>) section;
                    String mdHtml = mdHelper.apply(sectionText.get("markdown"), options).toString();
                    String mdWordCount = StringHelper.wordCount.apply(mdHtml, options).toString();
                    String titleWordCount = StringHelper.wordCount.apply(sectionText.get("title"), options).toString();
                    runningWordCount = Integer.parseInt(titleWordCount) + Integer.parseInt(mdWordCount) + runningWordCount;
                }
            }
            return runningWordCount.toString();
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    }
}

