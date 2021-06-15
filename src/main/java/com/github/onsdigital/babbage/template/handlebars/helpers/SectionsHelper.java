package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Context;
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
            if (isEmpty(context, options)) {
                return null;
            }

            Integer runningWordCount = 0;
            for (Object section : (List<Object>) context) {
                Map<String, String> sectionText = (Map<String, String>) section;

                String md = sectionText.get("markdown");
                String mdWordCount = calculateWordCountFromMarkdown(md, options);

                String title = sectionText.get("title");
                String titleWordCount = calculateWordCountFromSection(title, options);

                runningWordCount = Integer.parseInt(titleWordCount) + Integer.parseInt(mdWordCount) + runningWordCount;
            }
            return runningWordCount.toString();
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

        private String calculateWordCountFromMarkdown(String md, Options options) throws IOException {
            if (md == null || md.isEmpty()) {
                return "0";
            }

            CustomMarkdownHelper mdHelper = new CustomMarkdownHelper();
            String mdHtml = mdHelper.apply(md, options).toString();
            return StringHelper.wordCount.apply(mdHtml, options).toString();
        }

        private String calculateWordCountFromSection(String title, Options options) throws IOException {
            if (title == null || title.isEmpty()) {
                return "0";
            }

            return StringHelper.wordCount.apply(title, options).toString();
        }

        private boolean isEmpty(Object context, Options options) {
            return options.isFalsy(context) || ((List<Object>) context).isEmpty();
        }
    }
}

