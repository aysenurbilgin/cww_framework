package fatSecret;
import com.sun.media.jai.codec.SeekableStream;

import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;
import java.awt.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageResize {
    /**
     * The JAI.create action name for handling a stream.
     */
    private static final String JAI_STREAM_ACTION = "stream";

    /**
     * The JAI.create action name for handling a resizing using a subsample averaging technique.
     */
    private static final String JAI_SUBSAMPLE_AVERAGE_ACTION = "SubsampleAverage";

    /**
     * The JAI.create encoding format name for JPEG.
     */
    private static final String JAI_ENCODE_FORMAT_JPEG = "JPEG";

    /**
     * The JAI.create action name for encoding image data.
     */
    private static final String JAI_ENCODE_ACTION = "encode";

    /**
     * The http content type/mime-type for JPEG images.
     */
    private static final String JPEG_CONTENT_TYPE = "image/jpeg";

    private int mMaxWidth = 800;

    private int mMaxWidthThumbnail = 150;

    /**
     * This method takes in an image as a byte array (currently supports GIF, JPG, PNG and
     * possibly other formats) and
     * resizes it to have a width no greater than the pMaxWidth parameter in pixels.
     * It converts the image to a standard
     * quality JPG and returns the byte array of that JPG image.
     *
     * @param pImageData
     *                the image data.
     * @param pMaxWidth
     *                the max width in pixels, 0 means do not scale.
     * @return the resized JPG image.
     * @throws IOException
     *                 if the image could not be manipulated correctly.
     */
    public byte[] resizeImageAsJPG(byte[] pImageData, int pMaxWidth) throws IOException {
        InputStream imageInputStream = new ByteArrayInputStream(pImageData);
        // read in the original image from an input stream
        SeekableStream seekableImageStream = SeekableStream.wrapInputStream(imageInputStream, true);
        RenderedOp originalImage = JAI.create(JAI_STREAM_ACTION, seekableImageStream);
        ((OpImage) originalImage.getRendering()).setTileCache(null);
        int origImageWidth = originalImage.getWidth();
        // now resize the image
        double scale = 1.0;
        if (pMaxWidth > 0 && origImageWidth > pMaxWidth) {
            scale = (double) pMaxWidth / originalImage.getWidth();
        }
        ParameterBlock paramBlock = new ParameterBlock();
        paramBlock.addSource(originalImage); // The source image
        paramBlock.add(scale); // The xScale
        paramBlock.add(scale); // The yScale
        paramBlock.add(0.0); // The x translation
        paramBlock.add(0.0); // The y translation

        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        RenderedOp resizedImage = JAI.create(JAI_SUBSAMPLE_AVERAGE_ACTION, paramBlock, qualityHints);

        // lastly, write the newly-resized image to an output stream, in a specific encoding
        ByteArrayOutputStream encoderOutputStream = new ByteArrayOutputStream();
        JAI.create(JAI_ENCODE_ACTION, resizedImage, encoderOutputStream, JAI_ENCODE_FORMAT_JPEG, null);
        // Export to Byte Array
        byte[] resizedImageByteArray = encoderOutputStream.toByteArray();
        return resizedImageByteArray;
    }

}
