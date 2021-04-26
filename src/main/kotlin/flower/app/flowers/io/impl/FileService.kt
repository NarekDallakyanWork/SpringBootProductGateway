package flower.app.flowers.io.impl

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface FileService {

    fun store(file: MultipartFile): String

    fun getThumbnailFromVideo(fileName: String): String

    fun storeToTemp(file: MultipartFile): String

    fun storeFromTempToLocalDirectory(fileName: String, directoryName: String): String

    fun load(filename: String): Path?

    fun loadAsResource(filename: String): Resource

    fun deleteByName(filename: String): Boolean
}
