package com.github.onsdigital.babbage.api.endpoint.publishing;

import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.publishing.model.ContentDetail;
import com.github.onsdigital.babbage.publishing.model.PublishNotification;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpcomingTest {

    @Mock
    private Upcoming mockUpcoming;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private PublishNotification publicationNotification;
    @InjectMocks
    @Spy
    private Upcoming endpoint;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        mockUpcoming.verifyUriList = true;
    }

    @Test
    public void testGetNoKey() throws BadRequestException {
        Exception exception = assertThrows(BadRequestException.class, () -> {
            endpoint.process(mockResponse, publicationNotification);
        });

        String expectedMessage = "Wrong key, make sure you pass in the right key";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void testVerifyKey() {
        Exception ex = new Exception();
        try {
            mockUpcoming.verifyKey(setPubNot());
        } catch (Exception e) {
            ex = e;
        }
        assertEquals(null, ex.getMessage());
        assertEquals(null, ex.getCause());
    }

    @Test
    public void testVerifyUriList() {
        Exception ex = new Exception();
        try {
            mockUpcoming.verifyUriList(setPubNot());
        } catch (Exception e) {
            ex = e;
        }
        assertEquals(null, ex.getMessage());
        assertEquals(null, ex.getCause());
    }

    private PublishNotification setPubNot() {
        List<String> mockURIs = Stream.of("one", "two", "three").collect(Collectors.toList());
        List<ContentDetail> mockContents = new ArrayList<ContentDetail>();
        ContentDetail cd = new ContentDetail();
        cd.uri = "one";
        cd.type = "1";
        mockContents.add(cd);
        PublishNotification pubNot = new PublishNotification();
        pubNot.setKey(appConfig().babbage().getReindexServiceKey());
        pubNot.setCollectionId("collid");
        pubNot.setPublishDate("pubdate");
        pubNot.setUrisToDelete(mockContents);
        pubNot.setUrisToUpdate(mockURIs);
        return pubNot;
    }

}