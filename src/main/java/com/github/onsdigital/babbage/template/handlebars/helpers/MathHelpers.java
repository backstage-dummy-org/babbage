package com.github.onsdigital.babbage.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.babbage.template.handlebars.helpers.base.BabbageHandlebarsHelper;
import com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static com.github.onsdigital.babbage.template.handlebars.helpers.util.HelperUtils.toNumber;

/**
 * Created by bren on 02/07/15.
 */
public enum MathHelpers implements BabbageHandlebarsHelper<Object> {

    increment {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Double num = toNumber(context);
            if (num == null) {
                return null;
            }
            num++;
            return new Handlebars.SafeString(String.valueOf(num.longValue()));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }

    },
    decrement {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Double num = toNumber(context);
            if (num == null) {
                return null;
            }
            num--;
            return new Handlebars.SafeString(String.valueOf(num.longValue()));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    },


    add {
        @Override
        public CharSequence apply(Object context, Options options) throws IOException {
            Double num1 = toNumber(context);
            Double num2 = toNumber(options.param(0));
            if (num1 == null || num2 == null) {
                return null;
            }

            double result = num1 + num2;
            return new Handlebars.SafeString(String.valueOf(result));
        }

        @Override
        public void register(Handlebars handlebars) {
            handlebars.registerHelper(this.name(), this);
        }
    };
}
