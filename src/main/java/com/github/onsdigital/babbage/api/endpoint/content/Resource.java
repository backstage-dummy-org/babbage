package com.github.onsdigital.babbage.api.endpoint.content;

import com.github.davidcarboni.restolino.framework.Api;
import com.github.onsdigital.babbage.api.error.ErrorHandler;
import com.github.onsdigital.babbage.content.client.ContentClient;
import com.github.onsdigital.babbage.content.client.ContentResponse;
import com.github.onsdigital.babbage.response.BabbageContentBasedBinaryResponse;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ExceptionMapper;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bren on 01/07/15.
 * <p/>
 * Serves resource files from content service with no content disposition
 */
@Api
public class Resource {

    @GET
    public void get(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        try {
            String uri = request.getParameter("uri");
            String width = request.getParameter("width");

            ContentResponse contentResponse = ContentClient.getInstance().getResource(uri);
            String contentDispositionHeader = "inline; ";
            contentDispositionHeader += contentResponse.getName() == null ? "" : "filename=\"" + contentResponse.getName() + "\"";
            response.setHeader("Content-Disposition", contentDispositionHeader);

            try ( InputStream contentResponseBody = contentResponse.getDataStream() ) {

                BufferedImage image = null;

                if (width != null) {

                    try {
                        Integer w = Integer.parseInt(width, 10);

                        if (uri.toLowerCase().endsWith(".png") || uri.toLowerCase().endsWith(".jpg") || uri.toLowerCase().endsWith(".jpeg")) {
                            image = ImageIO.read(contentResponseBody);
                            Integer clampedWidth = Math.max(1, Math.min(image.getWidth(), w));

                            Double w2 = (double) clampedWidth;
                            Double ratio = w2 / (double) image.getWidth();
                            Double h = (double) image.getHeight() * ratio;

                            Image newImage = image.getScaledInstance(clampedWidth, h.intValue(), Image.SCALE_SMOOTH);
                            image = new BufferedImage(newImage.getWidth(null), newImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
                            image.getGraphics().drawImage(newImage, 0, 0, null);
                        }

                    } catch (IOException e) {
                        // intentionally swallowing exception so we can return original image if resize fails
                        e.printStackTrace();
                    }

                }


                if (image != null) {


                    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                        ImageIO.write(image, "png", os);
                        try ( InputStream input = new ByteArrayInputStream(os.toByteArray())) {
                            new BabbageContentBasedBinaryResponse(contentResponse, input, contentResponse.getMimeType()).apply(request, response);
                            return;
                        }

                    }
                }


                new BabbageContentBasedBinaryResponse(contentResponse, contentResponseBody, contentResponse.getMimeType()).apply(request, response);
            }
        } catch (Throwable t) {
            ErrorHandler.handle(request, response, t);
        }
    }

}
