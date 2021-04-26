package flower.app.flowers.io.impl

import com.xuggle.xuggler.*
import com.xuggle.xuggler.video.ConverterFactory
import com.xuggle.xuggler.video.IConverter
import org.slf4j.LoggerFactory
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object MediaSnapshotCreator {
    private val logger = LoggerFactory.getLogger(MediaSnapshotCreator::class.java)

    /**
     * @param inputFilename  Input video file
     * @param timestamps     List with the video timestamps (millis) to make snapshots for
     * @param outputDir      Directory where the snapshots should be stored
     * @param snapshotPrefix Prefix for the snapshots.
     * @param maxWidth       Optional max width to the snapshots
     * @param maxHeight      Optional max height to the snapshots
     * @throws
     */
    @Throws(IllegalArgumentException::class)
    fun makeSnapshots(inputFilename: String, timestamps: List<Long>, outputDir: File?, snapshotPrefix: String?, maxWidth: Int?, maxHeight: Int?) {
        var snapshotPrefix = snapshotPrefix
        if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
            logger.info("IVideoResampler not available")
            throw IllegalArgumentException("you must install the GPL version of Xuggler (with IVideoResampler support")
        }
        snapshotPrefix = snapshotPrefix ?: "snapshot_"

        //Resources that must be closed
        var videoCoder: IStreamCoder? = null
        var container: IContainer? = null
        container = IContainer.make()
        require(container.open(FileServiceImpl.filePath + "/" + inputFilename, IContainer.Type.READ, null) >= 0) { "could not open file: $inputFilename" }
        try {
            val streamIndex: Long = findVideoStreamIndex(container)
            require(streamIndex >= 0) { "could not find video stream in container: $inputFilename" }
            val stream: IStream = container.getStream(streamIndex)

            // get the pre-configured decoder that can decode this stream;
            videoCoder = stream.getStreamCoder()

            // Now we have found the video stream in this file. Let's open up
            // our decoder so it can do work
            require(videoCoder.open(null, null) >= 0) { "could not open video decoder for container: $inputFilename" }

            //Calculate the final output dimensions if maximum values for width and height have been specified
            val outputDim: Dimension = computeOutputDimension(videoCoder.getWidth(), videoCoder.getHeight(), maxWidth, maxHeight)

            //If resize is required we will use a IVideoResampler
            val resizeRequired = videoCoder.getWidth() !== outputDim.width || videoCoder.getHeight() !== outputDim.height
            if (resizeRequired && logger.isInfoEnabled) {
                logger.info("Video snapshots will be resized to {}x{}", outputDim.width, outputDim.height)
            }
            val converter: IConverter = ConverterFactory.createConverter(ConverterFactory.XUGGLER_BGR_24, videoCoder.getPixelType(),
                    videoCoder.getWidth(), videoCoder.getHeight(),
                    outputDim.width, outputDim.height)
            /*
             * Reminder
             * PTS : Presentation Time Stamp (in Microseconds). Not necessarily the same timebase used in the stream (seeking)
             */
            //This is the timebase used for seeking
            val videoTimeBase: IRational = stream.getTimeBase()
            //This is the timebase of our timestamps.
            val timestampsTimeBase: IRational = IRational.make(1, 1000)
            val frameRate: Double = videoCoder.getFrameRate().getDouble()
            val frameInterval = (Global.DEFAULT_PTS_PER_SECOND / frameRate) as Long // In PTS
            val startTime: Long = container.getStartTime() // In microseconds
            if (logger.isDebugEnabled) {
                logger.debug("Video time base = {}", videoTimeBase)
                logger.debug("Video frame rate = {}", frameRate)
                logger.debug("Video start time = {}", startTime)
            }
            val it = timestamps.iterator()
            // We allocate a new picture to get the data out of Xuggle
            val picture: IVideoPicture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight())
            //And a IPacket to keep the packets
            val packet: IPacket = IPacket.make()
            while (it.hasNext()) {
                //in milliseconds
                val requestedTimestamp = it.next()
                //In the video timebase. We convert from the timestamps timebase to the video timebase
                val seekTimestamp: Long = startTime + videoTimeBase.rescale(requestedTimestamp, timestampsTimeBase)
                //In PTS units. Used to find the matching frame
                val targetPTS = Math.round(requestedTimestamp / 1000.0 * Global.DEFAULT_PTS_PER_SECOND)
                var decodeVideo = true
                if (logger.isDebugEnabled) {
                    logger.debug("Making snapshot at {}ms. Seeking to {} ts", requestedTimestamp, seekTimestamp)
                }
                seekVideo(container, streamIndex.toInt(), seekTimestamp)

                // Now, we start walking through the container looking at each packet.
                while (decodeVideo && container.readNextPacket(packet) >= 0) {

                    // Now we have a packet, let's see if it belongs to our video stream
                    if (packet.streamIndex.toLong() != streamIndex.toLong()) {
                        continue  //Skip this packet
                    }
                    var offset = 0
                    while (decodeVideo && offset < packet.getSize()) {
                        // Now, we decode the video, checking for any errors.
                        val bytesDecoded: Int = videoCoder.decodeVideo(picture, packet, offset)
                        if (bytesDecoded < 0) {
                            convertErrorToException(bytesDecoded, "Error decoding video. Reason:")
                        }
                        offset += bytesDecoded

                        // Some decoders will consume data in a packet, but will not
                        // be able to construct a full video picture yet. Therefore
                        // you should always check if you got a complete picture
                        // from the decoder.
                        if (picture.isComplete()) {

                            //Check if the current picture is within the desired time frame
                            val diff: Long = picture.getPts() - targetPTS
                            if (Math.abs(diff) <= frameInterval) {
                                if (logger.isDebugEnabled) {
                                    val actual: Double = picture.getPts() as Double / Global.DEFAULT_PTS_PER_SECOND
                                    val requested = requestedTimestamp / 1000.0
                                    logger.debug("Video within desired position. Requested= {}s Actual={}", requested, actual)
                                }
                                val image: BufferedImage = converter.toImage(picture)
                                val filename = "$snapshotPrefix.jpg"
                                val targetFile = File(outputDir, filename)
                                logger.info("Saving snapshot as {}", targetFile)
                                try {
                                    ImageIO.write(image, "jpg", targetFile)
                                } catch (e: IOException) {
                                    throw IllegalArgumentException("Failed to save snapshot", e)
                                }
                                decodeVideo = false
                            } //Check if we are far ahead of the desired timestamp. Using one second threshold
                            else if (diff > frameInterval * frameRate) {
                                val actual: Long = picture.getPts() / Global.DEFAULT_PTS_PER_SECOND
                                logger.warn("Failed to seek to desired position. Current position is {}s", actual)
                                decodeVideo = false
                            }
                        }
                    }
                }
            }
        } finally {
            if (videoCoder != null) {
                videoCoder.close()
                videoCoder = null
            }
            if (container != null) {
                container.close()
                container = null
            }
        }
    }

    private fun findVideoStreamIndex(container: IContainer): Long {
        val numStreams: Int = container.numStreams
        for (i in 0 until numStreams) {
            val stream: IStream = container.getStream(i.toLong())

            // get the pre-configured decoder that can decode this stream;
            val coder: IStreamCoder = stream.streamCoder
            if (coder.codecType === ICodec.Type.CODEC_TYPE_VIDEO) {
                return i.toLong()
            }
        }
        return -1
    }

    private fun computeOutputDimension(originalWidth: Int, originalHeight: Int, maxWidth: Int?, maxHeight: Int?): Dimension {
        val outputWidth: Int
        val outputHeight: Int
        if (maxWidth != null && maxHeight != null) {
            val videoWidth = originalWidth.toDouble()
            val videoHeight = originalHeight.toDouble()
            val ratio = Math.min(maxWidth / videoWidth, maxHeight / videoHeight)
            outputWidth = (videoWidth * ratio).toInt()
            outputHeight = (videoHeight * ratio).toInt()
        } else {
            outputWidth = originalWidth
            outputHeight = originalHeight
        }
        return Dimension(outputWidth, outputHeight)
    }

    @Throws(IllegalArgumentException::class)
    private fun seekVideo(container: IContainer, videoStreamIndex: Int, seekTimestamp: Long) {
        var seekRes = 0
        seekRes = container.seekKeyFrame(videoStreamIndex, Long.MIN_VALUE, seekTimestamp, seekTimestamp, 0)
        if (seekRes < 0) {
            convertErrorToException(seekRes, "Error performing seek. Reason:")
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun convertErrorToException(errorCode: Int, message: String) {
        val error: IError = IError.make(errorCode)
        throw IllegalArgumentException(message + error.getDescription())
    }
}
