package com.github.onsdigital.babbage.request.handler;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.onsdigital.babbage.content.client.ContentReadException;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.error.LegacyPDFException;
import com.github.onsdigital.babbage.error.ResourceNotFoundException;
import com.github.onsdigital.babbage.response.BabbageBinaryResponse;
import com.github.onsdigital.babbage.util.RequestUtil;
import com.github.onsdigital.babbage.util.ThreadContext;

@RunWith(MockitoJUnitRunner.class)
public class GetPDFRequestHandlerTest {

    private static final String URI = "/economy/inflationandpriceindices/pdf";
    private static final String MIME_TYPE = "MIME_TYPE";
    private static final byte[] DATA = "the pdf content".getBytes();

    @Mock
    private HttpServletRequest request;

    @Mock
    private ContentResponse contentResponse;

    @Spy
    private GetPDFRequestHandler handler;

    @After
    public void cleanup() {
        ThreadContext.clear();
    }

    @Test
    public void canHandleRequestShouldReturnTrueForPdf() {
        assertTrue(handler.canHandleRequest(URI, "pdf"));
    }

    @Test
    public void canHandleRequestShouldReturnFalseForNotPdf() {
        assertFalse(handler.canHandleRequest(URI, "pdf-new"));
    }

    @Test
    public void getRequestTypeShouldReturnPdf() {
        assertEquals("pdf", handler.getRequestType());
    }

    @Test
    public void getEnglishPdfOfExistingContentWithNoName() throws Exception {
        when(contentResponse.getName()).thenReturn(null);
        when(contentResponse.getMimeType()).thenReturn(MIME_TYPE);
        when(contentResponse.getDataStream()).thenReturn(new ByteArrayInputStream(DATA));
        doReturn(contentResponse).when(handler).getResource(URI + "/page.pdf");

        BabbageBinaryResponse response = handler.get(URI, request);

        String expectedContentDisposition = "attachment";

        assertEquals(expectedContentDisposition, response.getHeader("Content-Disposition"));
        assertArrayEquals(DATA, response.getData());
        assertEquals(200, response.getStatus());
        assertEquals(MIME_TYPE, response.getMimeType());
        assertTrue(response.getErrors().isEmpty());
        assertEquals("UTF-8", response.getCharEncoding());

        verify(handler, never()).getResource(URI + "/page_cy.pdf");
    }

    @Test
    public void getEnglishPdfOfExistingContentTitleAndEdition() throws Exception {
        when(contentResponse.getName()).thenReturn("name");
        when(contentResponse.getMimeType()).thenReturn(MIME_TYPE);
        when(contentResponse.getDataStream()).thenReturn(new ByteArrayInputStream(DATA));
        doReturn(contentResponse).when(handler).getResource(URI + "/page.pdf");

        String title = "title";
        String edition = "2.0";
        stubContentDescription(title, edition);

        BabbageBinaryResponse response = handler.get(URI, request);

        String expectedContentDisposition = String.format("attachment; filename=\"%s %s.pdf\"", title, edition);

        assertEquals(expectedContentDisposition, response.getHeader("Content-Disposition"));
        assertArrayEquals(DATA, response.getData());
        assertEquals(200, response.getStatus());
        assertEquals(MIME_TYPE, response.getMimeType());
        assertTrue(response.getErrors().isEmpty());
        assertEquals("UTF-8", response.getCharEncoding());

        verify(handler, never()).getResource(URI + "/page_cy.pdf");
    }

    @Test
    public void getEnglishPdfOfExistingContentTitleAndNoEdition() throws Exception {
        when(contentResponse.getName()).thenReturn("name");
        when(contentResponse.getMimeType()).thenReturn(MIME_TYPE);
        when(contentResponse.getDataStream()).thenReturn(new ByteArrayInputStream(DATA));
        doReturn(contentResponse).when(handler).getResource(URI + "/page.pdf");

        String title = "title";
        String edition = "";
        stubContentDescription(title, edition);

        BabbageBinaryResponse response = handler.get(URI, request);

        String expectedContentDisposition = String.format("attachment; filename=\"%s.pdf\"", title);

        assertEquals(expectedContentDisposition, response.getHeader("Content-Disposition"));
        assertArrayEquals(DATA, response.getData());
        assertEquals(200, response.getStatus());
        assertEquals(MIME_TYPE, response.getMimeType());
        assertTrue(response.getErrors().isEmpty());
        assertEquals("UTF-8", response.getCharEncoding());

        verify(handler, never()).getResource(URI + "/page_cy.pdf");
    }

    @Test
    public void getWelshPdfOfExistingContentWithNoName() throws Exception {
        ThreadContext.addData(RequestUtil.LANG_KEY, "cy");
        when(contentResponse.getName()).thenReturn(null);
        when(contentResponse.getMimeType()).thenReturn(MIME_TYPE);
        when(contentResponse.getDataStream()).thenReturn(new ByteArrayInputStream(DATA));
        doReturn(contentResponse).when(handler).getResource(URI + "/page_cy.pdf");

        BabbageBinaryResponse response = handler.get(URI, request);

        String expectedContentDisposition = "attachment";

        assertEquals(expectedContentDisposition, response.getHeader("Content-Disposition"));
        assertArrayEquals(DATA, response.getData());
        assertEquals(200, response.getStatus());
        assertEquals(MIME_TYPE, response.getMimeType());
        assertTrue(response.getErrors().isEmpty());
        assertEquals("UTF-8", response.getCharEncoding());

        verify(handler, never()).getResource(URI + "/page.pdf");
    }

    @Test
    public void getWelshPdfOfExistingContentTitleAndEdition() throws Exception {
        ThreadContext.addData(RequestUtil.LANG_KEY, "cy");
        when(contentResponse.getName()).thenReturn("name");
        when(contentResponse.getMimeType()).thenReturn(MIME_TYPE);
        when(contentResponse.getDataStream()).thenReturn(new ByteArrayInputStream(DATA));
        doReturn(contentResponse).when(handler).getResource(URI + "/page_cy.pdf");

        String title = "title";
        String edition = "2.0";
        stubContentDescription(title, edition);

        BabbageBinaryResponse response = handler.get(URI, request);

        String expectedContentDisposition = String.format("attachment; filename=\"%s %s.pdf\"", title, edition);

        assertEquals(expectedContentDisposition, response.getHeader("Content-Disposition"));
        assertArrayEquals(DATA, response.getData());
        assertEquals(200, response.getStatus());
        assertEquals(MIME_TYPE, response.getMimeType());
        assertTrue(response.getErrors().isEmpty());
        assertEquals("UTF-8", response.getCharEncoding());

        verify(handler, never()).getResource(URI + "/page.pdf");
    }

    @Test
    public void getWelshPdfOfExistingContentTitleAndNoEdition() throws Exception {
        ThreadContext.addData(RequestUtil.LANG_KEY, "cy");
        when(contentResponse.getName()).thenReturn("name");
        when(contentResponse.getMimeType()).thenReturn(MIME_TYPE);
        when(contentResponse.getDataStream()).thenReturn(new ByteArrayInputStream(DATA));
        doReturn(contentResponse).when(handler).getResource(URI + "/page_cy.pdf");

        String title = "title";
        String edition = "";
        stubContentDescription(title, edition);

        BabbageBinaryResponse response = handler.get(URI, request);

        String expectedContentDisposition = String.format("attachment; filename=\"%s.pdf\"", title);

        assertEquals(expectedContentDisposition, response.getHeader("Content-Disposition"));
        assertArrayEquals(DATA, response.getData());
        assertEquals(200, response.getStatus());
        assertEquals(MIME_TYPE, response.getMimeType());
        assertTrue(response.getErrors().isEmpty());
        assertEquals("UTF-8", response.getCharEncoding());

        verify(handler, never()).getResource(URI + "/page.pdf");
    }

    @Test
    public void getEnglishPdfOfExistingContentWhenWelshUnavailable() throws Exception {
        ThreadContext.addData(RequestUtil.LANG_KEY, "cy");
        when(contentResponse.getName()).thenReturn("name");
        when(contentResponse.getMimeType()).thenReturn(MIME_TYPE);
        when(contentResponse.getDataStream()).thenReturn(new ByteArrayInputStream(DATA));
        doThrow(new ResourceNotFoundException()).when(handler).getResource(URI + "/page_cy.pdf");
        doReturn(contentResponse).when(handler).getResource(URI + "/page.pdf");

        String title = "title";
        String edition = "4";
        stubContentDescription(title, edition);

        BabbageBinaryResponse response = handler.get(URI, request);

        String expectedContentDisposition = String.format("attachment; filename=\"%s %s.pdf\"", title, edition);

        assertEquals(expectedContentDisposition, response.getHeader("Content-Disposition"));
        assertArrayEquals(DATA, response.getData());
        assertEquals(200, response.getStatus());
        assertEquals(MIME_TYPE, response.getMimeType());
        assertTrue(response.getErrors().isEmpty());
        assertEquals("UTF-8", response.getCharEncoding());
    }

    @Test(expected = LegacyPDFException.class)
    public void getShouldReturnAnExceptionIfContentNotFound() throws Exception {
        doThrow(new ResourceNotFoundException()).when(handler).getResource(URI + "/page.pdf");

        handler.get(URI, request);
    }

    private void stubContentDescription(String title, String edition) throws ContentReadException, IOException {
        ContentResponse content = Mockito.mock(ContentResponse.class);

        String descriptionJson = String.format("{\"description\":{\"title\":\"%s\",\"edition\":\"%s\"}}", title, edition);
        when(content.getDataStream()).thenReturn(new ByteArrayInputStream(descriptionJson.getBytes()));

        doReturn(content).when(handler).getContent(eq(URI), any());
    }
}
