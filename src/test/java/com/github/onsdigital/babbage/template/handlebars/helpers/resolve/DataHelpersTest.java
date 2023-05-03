package com.github.onsdigital.babbage.template.handlebars.helpers.resolve;

import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Context.Builder;
import com.github.jknack.handlebars.Context;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonParser;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import com.github.onsdigital.babbage.configuration.Babbage;
import com.github.onsdigital.babbage.configuration.ApplicationConfiguration;




import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataHelpersTest extends TestCase {
    private static ApplicationConfiguration INSTANCE;
    @Mock
    private DataHelpers mockDH;

    @Mock
    private ApplicationConfiguration mockConfig;
    @Before
    public void setup() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);


    }


    @Test
    public void testDataHelpers_resolveTaxonomy_nullValues_thrownExcption() throws Exception {

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            DataHelpers.resolveTaxonomy.apply(null, null);
        });
        assertEquals(java.lang.NullPointerException.class, exception.getClass());

    }

    @Test
    public void testDataHelpers_resolveTaxonomy_nullOption_thrownException() throws Exception {
        Object uri = "/navigation";

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            DataHelpers.resolveTaxonomy.apply(uri, null);
        });
        assertEquals(java.lang.NullPointerException.class, exception.getClass());

    }
    @Test
    public void testDataHelpers_resolveTaxonomy() throws Exception {
        Babbage mockBabbage = mock(Babbage.class);
        when(mockBabbage.isPublishing()).thenReturn(false);
        when(mockBabbage.isNavigationEnabled()).thenReturn(true);

//        build options
        File file = new File("src/test/resources/TestSectiondata.json");
        JsonParser parser = new JsonParser();
        Object uri = parser.parse(new java.io.FileReader(file));
        Options options = setOptions();
        assertEquals(options.helperName, "resolveTaxonomy");
        assertTrue( options.<Integer>hash("depth") == 2);





        CharSequence resolve = DataHelpers.resolveTaxonomy.apply(uri, options);

        System.out.print("\n----- resolve ----- " + resolve + "\n");

    }


    private Options setOptions() throws Exception {
        Map<String, String> uriMock = new java.util.LinkedHashMap<>();
        uriMock.put("uri", "/navigation");

        Map<String, Object> optionMap = new HashMap<String,Object>();
        optionMap.put("depth", 2);

        Handlebars handlebars = mock(Handlebars.class);
        TagType tagType = mock(TagType.class);
        Template template = mock(Template.class);

        Context.Builder parentContext = Context.newBuilder(uriMock);

        Context.Builder contextBuilder = Context.newBuilder(parentContext.build(), "test");

        Options.Builder builder = new Options.Builder(
                handlebars,
                "resolveTaxonomy",
                tagType,
                contextBuilder.build(),
                template);
        builder.setHash(optionMap);
        return builder.build();
    }

}