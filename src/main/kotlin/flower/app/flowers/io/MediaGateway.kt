package flower.app.flowers.io

import flower.app.flowers.io.impl.FileService
import org.apache.commons.io.FilenameUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/media")
class MediaGateway(
        private val fileService: FileService
) {

    @PostMapping("/upload")
    fun uploadMedia(@RequestParam("file") media: MultipartFile): MediaInfo {

        // Saving MultipartFile
        val fileName = fileService.store(media)
        // Getting file information
        val fileMimeType = FilenameUtils.getExtension(media.originalFilename)
        val fileSize = media.size
        return MediaInfo(fileName, fileMimeType, fileSize)
    }
}
