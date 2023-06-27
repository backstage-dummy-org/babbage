package com.github.onsdigital.babbage.api.endpoint.publishing;

import com.github.onsdigital.babbage.publishing.model.PublishNotification;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Spy;
import com.github.onsdigital.babbage.error.BadRequestException;

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
    private PublishNotification publicationNotification;
    @InjectMocks
    @Spy
    private com.github.onsdigital.babbage.api.endpoint.publishing.Upcoming endpoint;


    @Before
    public void setup() throws Exception {
       initMocks(this);

    }
    @Test
    public void testGetNoKey() throws com.github.onsdigital.babbage.error.BadRequestException {
        Exception exception = assertThrows(BadRequestException.class, () -> {
            endpoint.process(response,publicationNotification);
        });

        String expectedMessage = "Wrong key, make sure you pass in the right key";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }
    @Test
    public void testGetKey() throws com.github.onsdigital.babbage.error.BadRequestException, java.io.IOException {
        Exception ex = null;
        PublishNotification pubNot = new com.github.onsdigital.babbage.publishing.model.PublishNotification();
        pubNot.setKey(appConfig().babbage().getReindexServiceKey());

        try {
            mockUpcoming.process(response, pubNot);
        } catch (Exception e) {
            ex = e;
        }
        assertEquals(null,ex);

    }

    @Test
    public void testVerifyUriList() throws com.github.onsdigital.babbage.error.BadRequestException, java.io.IOException {
        Exception ex = null;
        java.util.List<String> mockURIs = java.util.stream.Stream.of("one", "two", "three").collect(java.util.stream.Collectors.toList());
        java.util.List<com.github.onsdigital.babbage.publishing.model.ContentDetail> mockContents = new java.util.ArrayList<com.github.onsdigital.babbage.publishing.model.ContentDetail>();
        com.github.onsdigital.babbage.publishing.model.ContentDetail cd = new com.github.onsdigital.babbage.publishing.model.ContentDetail();
        cd.uri = "one";
        cd.type = "1";
        mockContents.add(cd);
        cd.uri = "two";
        cd.type = "2";
        mockContents.add(cd);
        cd.uri = "three";
        cd.type = "3";
        mockContents.add(cd);

        PublishNotification pubNot = new com.github.onsdigital.babbage.publishing.model.PublishNotification();
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
}