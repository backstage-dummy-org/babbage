package com.github.onsdigital.babbage.api.endpoint.publishing;

import com.github.onsdigital.babbage.error.BadRequestException;
import com.github.onsdigital.babbage.publishing.model.ContentDetail;
import com.github.onsdigital.babbage.publishing.model.PublishNotification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.servlet.ServletInputStream;

import static com.github.onsdigital.babbage.configuration.ApplicationConfiguration.appConfig;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class UpcomingTest {

    @Mock
    private Upcoming mockUpcoming;
    @Mock
    private javax.servlet.http.HttpServletResponse response;
    @Mock
    private javax.servlet.http.HttpServletRequest request;

    @Mock
    private PublishNotification publicationNotification;
    @InjectMocks
    @Spy
    private Upcoming endpoint;

    @Before
    public void setup() throws Exception {
       initMocks(this);

    }
    @Test
    public void testGetNoKey() throws BadRequestException {
        Exception exception = assertThrows(BadRequestException.class, () -> {
            endpoint.process(response,publicationNotification);
        });

        String expectedMessage = "Wrong key, make sure you pass in the right key";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void testGetKey() throws BadRequestException, IOException {
        Exception ex = null;
        PublishNotification pubNot = new PublishNotification();
        pubNot.setKey(appConfig().babbage().getReindexServiceKey());

        try {
            mockUpcoming.process(response, pubNot);
        } catch (Exception e) {
            ex = e;
        }
        assertEquals(null,ex);

    }

    @Test
    public void testVerifyUriList() throws BadRequestException, java.io.IOException {
        Exception ex = null;
        List<String> mockURIs = Stream.of("one", "two", "three").collect(Collectors.toList());
        List<ContentDetail> mockContents = new ArrayList<ContentDetail>();
        ContentDetail cd = new ContentDetail();
        cd.uri = "one";
        cd.type = "1";
        mockContents.add(cd);
        cd.uri = "two";
        cd.type = "2";
        mockContents.add(cd);
        cd.uri = "three";
        cd.type = "3";
        mockContents.add(cd);

        PublishNotification pubNot = new PublishNotification();
        pubNot.setKey(appConfig().babbage().getReindexServiceKey());
        pubNot.setCollectionId("collid");
        pubNot.setPublishDate("pubdate");
        pubNot.setUrisToDelete(mockContents);
        pubNot.setUrisToUpdate(mockURIs);

        try {
            mockUpcoming.verifyUriList(pubNot);
        } catch (Exception e) {
            ex = e;
        }
        assertEquals(null,ex);
        verify(mockUpcoming,atLeastOnce()).verifyUriList(any(PublishNotification.class));

    }
    @Test
    public void testPost() throws BadRequestException {
        Exception ex = null;
        try {
            endpoint.post(request,response);
        } catch (Exception e) {
            ex = e;
        }
        assertEquals(null,ex);

    }



}