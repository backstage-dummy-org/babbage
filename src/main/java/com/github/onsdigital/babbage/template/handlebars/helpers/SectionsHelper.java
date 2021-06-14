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
            if (options.isFalsy(context) || ((List<Object>) context).isEmpty()) {
                return null;
            }

            CustomMarkdownHelper mdHelper = new CustomMarkdownHelper();
            Integer runningWordCount = 0;
            for (Object section : (List<Object>) context) {
                Map<String, String> sectionText = (Map<String, String>) section;

                String md = sectionText.get("markdown");
                String mdWordCount = "0";
                if(md != null && !md.isEmpty()) {
                    String mdHtml = mdHelper.apply(md, options).toString();
                    mdWordCount = StringHelper.wordCount.apply(mdHtml, options).toString();
                }

                String titleWordCount = "0";
                String title = sectionText.get("title");
                if(title != null && !title.isEmpty()) {
                    titleWordCount = StringHelper.wordCount.apply(title, options).toString();
                }

                runningWordCount = Integer.parseInt(titleWordCount) + Integer.parseInt(mdWordCount) + runningWordCount;
            }
            return runningWordCount.toString();
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    }
}

